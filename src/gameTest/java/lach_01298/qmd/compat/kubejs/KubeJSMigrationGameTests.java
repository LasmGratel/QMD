package lach_01298.qmd.compat.kubejs;

import lach_01298.qmd.recipe.CollectorRecipe;
import lach_01298.qmd.recipe.OreLeacherRecipe;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.gametest.GameTestHolder;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/** Optional integration coverage without linking KubeJS classes in the base runtime. */
@GameTestHolder("qmd")
public final class KubeJSMigrationGameTests {
    private static final ResourceLocation SMOKE = ResourceLocation.parse("qmd:smoke/ore_leacher");
    private static final ResourceLocation REMOVED = ResourceLocation.parse("qmd:smoke/removed_ore_leacher");
    private static final ResourceLocation COLLECTOR = ResourceLocation.parse("qmd:smoke/collector");
    private static final ResourceLocation REMOVED_COLLECTOR = ResourceLocation.parse("qmd:smoke/collector_removed");
    private static final ResourceLocation COLLECTOR_ONLY = ResourceLocation.parse("qmd:smoke/collector_only");
    private static final ResourceLocation SALT_WATER = ResourceLocation.parse("qmd:salt_water");
    // GameTestServer runs unthrottled; tick counts cannot measure background reload duration.
    private static final int ASYNC_TICK_LIMIT = 1_000_000;
    private static final long ASYNC_TIMEOUT_NANOS = TimeUnit.SECONDS.toNanos(30);

    private KubeJSMigrationGameTests() {
    }

    @GameTestGenerator
    public static Collection<TestFunction> optionalKubeJSTests() {
        if (!ModList.get().isLoaded("kubejs")) return List.of();
        return List.of(new TestFunction("qmd.kubejs.reload", "kubejsmigrationgametests.recipesAndReload",
                "qmd:empty", ASYNC_TICK_LIMIT, 0, true, KubeJSMigrationGameTests::recipesAndReload));
    }

    private static void recipesAndReload(GameTestHelper helper) {
        helper.assertTrue(KubeJSMigrationSmokeBridge.startupPasses() == 1,
                "Tracked KubeJS startup fixture must run once and pass its binding/energy assertions");
        assertNoScriptErrors(helper);
        AtomicBoolean reloadStarted = new AtomicBoolean();
        AtomicBoolean reloadFinished = new AtomicBoolean();
        AtomicReference<Throwable> reloadFailure = new AtomicReference<>();
        AtomicReference<OreLeacherRecipe> initialRecipe = new AtomicReference<>();
        AtomicReference<CollectorRecipe> initialCollector = new AtomicReference<>();
        AtomicReference<RecipeManager> initialManager = new AtomicReference<>();
        int[] initialGeneration = {0};
        long startedNanos = System.nanoTime();

        // Executing assertions record permanent failures; waiting assertions only retry pending work.
        helper.startSequence().thenExecuteFor(ASYNC_TICK_LIMIT, () -> {
            helper.assertTrue(System.nanoTime() - startedNanos <= ASYNC_TIMEOUT_NANOS,
                    "KubeJS reload did not verify within thirty seconds: server="
                        + KubeJSMigrationSmokeBridge.serverGeneration() + ", recipe="
                        + KubeJSMigrationSmokeBridge.recipeGeneration() + ", manager="
                        + KubeJSMigrationSmokeBridge.managerGeneration() + ", reloadFinished="
                        + reloadFinished.get() + ", reloadFailure=" + reloadFailure.get()
                        + ", scriptFailures=" + KubeJSMigrationSmokeBridge.failures());
            helper.assertTrue(KubeJSMigrationSmokeBridge.failures().isEmpty(),
                    "KubeJS smoke script failed: " + KubeJSMigrationSmokeBridge.failures());
            assertNoScriptErrors(helper);
            helper.assertTrue(reloadFailure.get() == null,
                    "Server resource reload failed: " + reloadFailure.get());
        });

        // A sequence polls once per tick without preallocating a callback for every allowed tick.
        helper.startSequence().thenWaitUntil(() -> {
            if (!reloadStarted.get()) {
                int generation = KubeJSMigrationSmokeBridge.serverGeneration();
                helper.assertTrue(generation > 0 && KubeJSMigrationSmokeBridge.recipeGeneration() == generation
                                && KubeJSMigrationSmokeBridge.managerGeneration() == generation,
                        "Waiting for initial server-script, recipe-event and loaded-manager verification");
                initialGeneration[0] = generation;
                initialRecipe.set(assertLoadedRecipe(helper, generation));
                initialCollector.set(assertLoadedCollectorRecipe(helper, generation));
                initialManager.set(helper.getLevel().getRecipeManager());
                reloadStarted.set(true);
                var server = helper.getLevel().getServer();
                var selectedPacks = List.copyOf(server.getPackRepository().getSelectedIds());
                // Call outside the server thread so reloadResources does not block the GameTest tick.
                CompletableFuture.runAsync(() -> server.reloadResources(selectedPacks).join())
                        .whenComplete((ignored, failure) -> {
                            reloadFailure.set(failure);
                            reloadFinished.set(true);
                        });
            }
            helper.assertTrue(reloadFinished.get(), "Waiting for the asynchronous KubeJS resource reload");
            int generation = KubeJSMigrationSmokeBridge.serverGeneration();
            helper.assertTrue(generation > initialGeneration[0] && KubeJSMigrationSmokeBridge.recipeGeneration() == generation
                            && KubeJSMigrationSmokeBridge.managerGeneration() == generation,
                    "Waiting for this reload's server-script, recipe-event and loaded-manager verification");
        }).thenExecute(() -> {
            helper.assertTrue(KubeJSMigrationSmokeBridge.startupPasses() == 1,
                    "Server resource reload must not re-run startup scripts");
            OreLeacherRecipe reloaded = assertLoadedRecipe(helper, KubeJSMigrationSmokeBridge.serverGeneration());
            helper.assertTrue(initialRecipe.get() != reloaded,
                    "Reload must replace the actual ore-leacher recipe instance");
            CollectorRecipe reloadedCollector = assertLoadedCollectorRecipe(helper, KubeJSMigrationSmokeBridge.serverGeneration());
            helper.assertTrue(initialCollector.get() != reloadedCollector,
                    "Reload must decode a new collector recipe instance rather than retain the previous generation");
            helper.assertTrue(initialManager.get() != helper.getLevel().getRecipeManager(),
                    "Reload must replace the actual server recipe manager");
        }).thenSucceed();
    }

    private static OreLeacherRecipe assertLoadedRecipe(GameTestHelper helper, int generation) {
        var manager = helper.getLevel().getRecipeManager();
        var optionalRecipe = manager.byKey(SMOKE);
        helper.assertTrue(optionalRecipe.isPresent(), "KubeJS smoke recipe must load in the actual recipe manager");
        var holder = optionalRecipe.orElseThrow();
        helper.assertTrue(holder.value() instanceof OreLeacherRecipe,
                "KubeJS event.custom must decode through the real QMD serializer");
        OreLeacherRecipe recipe = (OreLeacherRecipe) holder.value();
        helper.assertTrue(recipe.getItemIngredients().size() == 1
                        && recipe.getItemIngredients().getFirst().test(new ItemStack(Items.COBBLESTONE))
                        && recipe.getFluidIngredients().size() == 3 && recipe.getItemProducts().size() == 3
                        && recipe.getItemProducts().get(1).isEmpty() && recipe.getItemProducts().get(2).isEmpty(),
                "KubeJS custom recipe must retain inputs and three padded output slots");
        ItemStack output = recipe.getItemProducts().getFirst().getStackRaw();
        helper.assertTrue(output.is(Items.BRICK) && output.getCount() == generation
                        && recipe.getProcessTimeMultiplier() == 0.01D * generation,
                "Recipe manager must contain this script generation's replacement values");
        helper.assertTrue(manager.byKey(REMOVED).isEmpty(),
                "KubeJS event.remove must remove the existing test datapack recipe on every load");
        long copies = manager.getAllRecipesFor(QMDRecipes.ORE_LEACHER_TYPE.get()).stream()
                .filter(value -> value.id().equals(SMOKE)).count();
        helper.assertTrue(copies == 1, "Recipe reload must contain exactly one replacement recipe");
        return recipe;
    }

    private static CollectorRecipe assertLoadedCollectorRecipe(GameTestHelper helper, int generation) {
        var manager = helper.getLevel().getRecipeManager();
        var optionalRecipe = manager.byKey(COLLECTOR);
        helper.assertTrue(optionalRecipe.isPresent(), "KubeJS collector recipe must load in the actual recipe manager");
        var holder = optionalRecipe.orElseThrow();
        helper.assertTrue(holder.value() instanceof CollectorRecipe,
                "KubeJS event.custom must decode through the real collector serializer");
        CollectorRecipe recipe = (CollectorRecipe) holder.value();
        helper.assertTrue(recipe.kind() == CollectorRecipe.Kind.LIQUID
                        && recipe.getType() == QMDRecipes.LIQUID_COLLECTOR_TYPE.get()
                        && BuiltInRegistries.FLUID.getKey(recipe.output().getFluid()).equals(SALT_WATER)
                        && recipe.output().getAmount() == 137 * generation
                        && recipe.priority() == -10000 - generation,
                "The collector must contain this script generation's decoded kind, output and priority");
        var world = recipe.world();
        helper.assertTrue(world.blocks().size() == 1 && world.blocks().getFirst().block() == Blocks.WATER
                        && world.blocks().getFirst().properties().equals(Map.of("level", "0"))
                        && world.biomes().isEmpty() && world.biomeTags().equals(List.of(COLLECTOR_ONLY))
                        && world.dimensions().equals(List.of(Level.OVERWORLD.location())),
                "KubeJS must preserve the real block-state, biome-tag and dimension predicate fields");
        helper.assertTrue(!world.matches(Blocks.WATER.defaultBlockState(),
                        helper.getLevel().getBiome(helper.absolutePos(BlockPos.ZERO)), Level.OVERWORLD),
                "The missing smoke-only biome tag must isolate this fixture from normal collector world tests");
        helper.assertTrue(manager.byKey(REMOVED_COLLECTOR).isEmpty(),
                "KubeJS event.remove must remove the existing collector fixture on every load");
        long copies = manager.getAllRecipesFor(QMDRecipes.LIQUID_COLLECTOR_TYPE.get()).stream()
                .filter(value -> value.id().equals(COLLECTOR)).count();
        helper.assertTrue(copies == 1, "Collector reload must contain exactly one decoded replacement recipe");
        return recipe;
    }

    private static void assertNoScriptErrors(GameTestHelper helper) {
        try {
            Class<?> consoleClass = Class.forName("dev.latvian.mods.kubejs.script.ConsoleJS");
            for (String scope : List.of("STARTUP", "SERVER")) {
                Object console = consoleClass.getField(scope).get(null);
                helper.assertTrue(console != null, "KubeJS " + scope + " console must be initialized");
                Collection<?> errors = (Collection<?>) consoleClass.getField("errors").get(console);
                helper.assertTrue(errors.isEmpty(), "KubeJS " + scope + " script errors: " + errors);
            }
        } catch (ReflectiveOperationException exception) {
            helper.fail("Cannot inspect KubeJS script error state: " + exception);
        }
    }
}
