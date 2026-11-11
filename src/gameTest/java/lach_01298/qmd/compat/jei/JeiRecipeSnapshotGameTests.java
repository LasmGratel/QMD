package lach_01298.qmd.compat.jei;

import lach_01298.qmd.compat.jei.recipe.CollectorWorldInfo;
import lach_01298.qmd.compat.kubejs.KubeJSMigrationSmokeBridge;
import lach_01298.qmd.recipe.CollectorRecipe;
import lach_01298.qmd.recipe.WorldCondition;
import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

/** Common recipe-viewer contracts without loading JEI or Minecraft client classes on the server. */
@GameTestHolder("qmd")
@PrefixGameTestTemplate(false)
public final class JeiRecipeSnapshotGameTests {
    private static final ResourceLocation LEACHER = ResourceLocation.parse("qmd:smoke/ore_leacher");
    private static final ResourceLocation REMOVED_LEACHER = ResourceLocation.parse("qmd:smoke/removed_ore_leacher");
    private static final ResourceLocation COLLECTOR = ResourceLocation.parse("qmd:smoke/collector");
    private static final ResourceLocation REMOVED_COLLECTOR = ResourceLocation.parse("qmd:smoke/collector_removed");
    private static final int ASYNC_TICK_LIMIT = 1_000_000;

    private JeiRecipeSnapshotGameTests() {}

    @GameTest(template = "empty", batch = "qmd.jei")
    public static void snapshotsContainEveryAuthoritativeRecipeInStableFamilies(GameTestHelper helper) {
        var manager = helper.getLevel().getRecipeManager();
        var snapshot = QMDJeiRecipes.from(manager);
        assertAuthoritative(helper, manager, snapshot);
        assertImmutable(helper, snapshot.oreLeacher());
        assertImmutable(helper, snapshot.liquidCollector());
        assertImmutable(helper, snapshot.atmosphereCollector());

        var mutableLeacher = new ArrayList<>(snapshot.oreLeacher());
        var mutableLiquid = new ArrayList<>(snapshot.liquidCollector());
        var mutableAtmosphere = new ArrayList<>(snapshot.atmosphereCollector());
        var copied = new QMDJeiRecipes(mutableLeacher, mutableLiquid, mutableAtmosphere);
        mutableLeacher.clear();
        mutableLiquid.clear();
        mutableAtmosphere.clear();
        helper.assertTrue(copied.equals(snapshot),
                "Recipe snapshots must not alias caller-owned lists that change during recipe reloads");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.jei")
    public static void collectorDescriptionsPreserveAllWorldAlternativesAndStateProperties(GameTestHelper helper) {
        var biomes = alternativeIds("biome");
        var tags = alternativeIds("biome_tag");
        var dimensions = alternativeIds("dimension");
        var blocks = List.of(new WorldCondition.BlockMatch(Blocks.WATER, Map.of("level", "0")),
                new WorldCondition.BlockMatch(Blocks.OAK_STAIRS,
                        Map.of("waterlogged", "false", "shape", "outer_left", "half", "top", "facing", "east")));
        var id = ResourceLocation.parse("qmd:jei_test/all_world_predicates");
        var recipe = new CollectorRecipe(CollectorRecipe.Kind.LIQUID,
                new WorldCondition(blocks, biomes, tags, dimensions), new FluidStack(Fluids.WATER, 17), -243);
        var description = CollectorWorldInfo.describe(new RecipeHolder<>(id, recipe));
        var expected = new ArrayList<String>();
        expected.add("  minecraft:water[level=0]");
        expected.add("  minecraft:oak_stairs[facing=east,half=top,shape=outer_left,waterlogged=false]");
        biomes.forEach(value -> expected.add("  " + value));
        tags.forEach(value -> expected.add("  #" + value));
        dimensions.forEach(value -> expected.add("  " + value));
        var actual = description.stream().filter(value -> !(value.getContents() instanceof TranslatableContents))
                .map(Component::getString).toList();
        helper.assertTrue(actual.equals(expected),
                "The scrollable collector description must retain every accepted biome, tag and dimension alternative,"
                        + " full block IDs and sorted state properties without truncation");
        helper.assertTrue(description.size() == expected.size() + 6
                        && translation(description.getFirst(), "logic")
                        && translation(description.get(description.size() - 2), "priority", -243)
                        && translation(description.getLast(), "recipe", id.toString()),
                "Descriptions must explain predicate logic, retain priority and identify the exact data/script recipe");
        assertImmutable(helper, description);

        var unrestricted = CollectorWorldInfo.describe(new RecipeHolder<>(id,
                new CollectorRecipe(CollectorRecipe.Kind.ATMOSPHERE, WorldCondition.ANY,
                        new FluidStack(Fluids.WATER, 1), 0)));
        for (String section : List.of("blocks", "biomes", "dimensions")) {
            var line = unrestricted.stream().filter(value -> translation(value, section)).findFirst().orElseThrow();
            var contents = (TranslatableContents) line.getContents();
            helper.assertTrue(contents.getArgs().length == 1 && contents.getArgs()[0] instanceof Component argument
                            && translation(argument, "any"),
                    "Unrestricted collector " + section + " must explicitly say Any");
        }
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.jei.reload", timeoutTicks = ASYNC_TICK_LIMIT)
    public static void freshSnapshotsFollowResourceReloadAndOptionalKubeJSChanges(GameTestHelper helper) {
        boolean kubejs = ModList.get().isLoaded("kubejs");
        var previousManager = new AtomicReference<RecipeManager>();
        var previousSnapshot = new AtomicReference<QMDJeiRecipes>();
        var reloadFinished = new AtomicBoolean();
        var reloadFailure = new AtomicReference<Throwable>();
        int[] previousGeneration = {0};
        int[] previousOutputCount = {0};
        int[] previousFluidAmount = {0};
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(30);

        helper.startSequence().thenExecuteFor(ASYNC_TICK_LIMIT, () -> {
            helper.assertTrue(System.nanoTime() <= deadline, "JEI recipe snapshot reload exceeded thirty seconds");
            helper.assertTrue(reloadFailure.get() == null, "JEI recipe snapshot reload failed: " + reloadFailure.get());
            helper.assertTrue(!kubejs || KubeJSMigrationSmokeBridge.failures().isEmpty(),
                    "KubeJS fixture failed: " + KubeJSMigrationSmokeBridge.failures());
        });
        helper.startSequence().thenWaitUntil(() ->
                helper.assertTrue(!kubejs || scriptsReady(), "Waiting for initial KubeJS manager verification"))
                .thenExecute(() -> {
                    var manager = helper.getLevel().getRecipeManager();
                    var snapshot = QMDJeiRecipes.from(manager);
                    assertAuthoritative(helper, manager, snapshot);
                    previousManager.set(manager);
                    previousSnapshot.set(snapshot);
                    previousGeneration[0] = KubeJSMigrationSmokeBridge.serverGeneration();
                    previousOutputCount[0] = find(snapshot.oreLeacher(), LEACHER).value().getItemProducts().getFirst().getStackRaw().getCount();
                    previousFluidAmount[0] = find(snapshot.liquidCollector(), COLLECTOR).value().output().getAmount();
                    if (kubejs) assertScriptGeneration(helper, snapshot, previousGeneration[0]);
                    var server = helper.getLevel().getServer();
                    var selected = List.copyOf(server.getPackRepository().getSelectedIds());
                    CompletableFuture.runAsync(() -> server.reloadResources(selected).join()).whenComplete((unused, failure) -> {
                        reloadFailure.set(failure);
                        reloadFinished.set(true);
                    });
                }).thenWaitUntil(() -> {
            helper.assertTrue(reloadFinished.get(), "Waiting for JEI recipe snapshot resource reload");
            helper.assertTrue(!kubejs || scriptsReady()
                            && KubeJSMigrationSmokeBridge.serverGeneration() > previousGeneration[0],
                    "Waiting for the reloaded KubeJS manager generation");
        }).thenExecute(() -> {
            var manager = helper.getLevel().getRecipeManager();
            var before = previousSnapshot.get();
            var after = QMDJeiRecipes.from(manager);
            assertAuthoritative(helper, manager, after);
            helper.assertTrue(manager != previousManager.get()
                            && find(before.oreLeacher(), LEACHER).value() != find(after.oreLeacher(), LEACHER).value()
                            && find(before.liquidCollector(), COLLECTOR).value() != find(after.liquidCollector(), COLLECTOR).value(),
                    "Each viewer refresh must use newly decoded recipes from the replacement server recipe manager");
            helper.assertTrue(find(before.oreLeacher(), LEACHER).value().getItemProducts().getFirst().getStackRaw().getCount()
                            == previousOutputCount[0]
                            && find(before.liquidCollector(), COLLECTOR).value().output().getAmount() == previousFluidAmount[0],
                    "A retained previous snapshot must preserve its recipe values after the manager is replaced");
            if (kubejs) {
                assertScriptGeneration(helper, after, KubeJSMigrationSmokeBridge.serverGeneration());
                helper.assertTrue(find(after.oreLeacher(), LEACHER).value().getItemProducts().getFirst().getStackRaw().getCount()
                                != previousOutputCount[0]
                                && find(after.liquidCollector(), COLLECTOR).value().output().getAmount() != previousFluidAmount[0],
                        "Fresh viewer snapshots must include KubeJS replacement values from the new script generation");
            }
        }).thenSucceed();
    }

    private static void assertAuthoritative(GameTestHelper helper, RecipeManager manager, QMDJeiRecipes snapshot) {
        assertFamily(helper, snapshot.oreLeacher(), manager.getAllRecipesFor(QMDRecipes.ORE_LEACHER_TYPE.get()));
        assertFamily(helper, snapshot.liquidCollector(), manager.getAllRecipesFor(QMDRecipes.LIQUID_COLLECTOR_TYPE.get()));
        assertFamily(helper, snapshot.atmosphereCollector(), manager.getAllRecipesFor(QMDRecipes.ATMOSPHERE_COLLECTOR_TYPE.get()));
        helper.assertTrue(snapshot.liquidCollector().stream().allMatch(value -> value.value().kind() == CollectorRecipe.Kind.LIQUID)
                        && snapshot.atmosphereCollector().stream().allMatch(value -> value.value().kind() == CollectorRecipe.Kind.ATMOSPHERE),
                "World recipe families must not leak between the liquid and atmosphere viewer categories");
    }

    private static <R extends Recipe<?>> void assertFamily(GameTestHelper helper, List<RecipeHolder<R>> actual,
                                                          List<RecipeHolder<R>> authoritative) {
        var expected = authoritative.stream().sorted(Comparator.comparing(value -> value.id().toString())).toList();
        helper.assertTrue(!actual.isEmpty() && actual.equals(expected),
                "A viewer family must contain every authoritative holder exactly once in stable recipe-ID order");
    }

    private static void assertScriptGeneration(GameTestHelper helper, QMDJeiRecipes snapshot, int generation) {
        helper.assertTrue(find(snapshot.oreLeacher(), LEACHER).value().getItemProducts().getFirst().getStackRaw().getCount() == generation
                        && find(snapshot.oreLeacher(), LEACHER).value().getProcessTimeMultiplier() == 0.01D * generation
                        && find(snapshot.liquidCollector(), COLLECTOR).value().output().getAmount() == 137 * generation
                        && find(snapshot.liquidCollector(), COLLECTOR).value().priority() == -10000 - generation,
                "Viewer snapshots must expose decoded KubeJS item counts, process time, fluid amount and priority");
        helper.assertTrue(snapshot.oreLeacher().stream().noneMatch(value -> value.id().equals(REMOVED_LEACHER))
                        && snapshot.liquidCollector().stream().noneMatch(value -> value.id().equals(REMOVED_COLLECTOR)),
                "Recipes removed by KubeJS must be absent from the viewer snapshot");
    }

    private static boolean scriptsReady() {
        int generation = KubeJSMigrationSmokeBridge.serverGeneration();
        return generation > 0 && KubeJSMigrationSmokeBridge.recipeGeneration() == generation
                && KubeJSMigrationSmokeBridge.managerGeneration() == generation;
    }

    private static <R extends Recipe<?>> RecipeHolder<R> find(List<RecipeHolder<R>> recipes, ResourceLocation id) {
        return recipes.stream().filter(value -> value.id().equals(id)).findFirst().orElseThrow();
    }

    private static List<ResourceLocation> alternativeIds(String prefix) {
        return IntStream.range(0, WorldCondition.MAX_ALTERNATIVES)
                .mapToObj(index -> ResourceLocation.fromNamespaceAndPath("qmd", "jei_test/" + prefix + "_" + index)).toList();
    }

    private static boolean translation(Component component, String suffix, Object... arguments) {
        if (!(component.getContents() instanceof TranslatableContents contents)
                || !contents.getKey().equals("gui.qmd.jei.collector." + suffix)) return false;
        return arguments.length == 0 || java.util.Arrays.equals(contents.getArgs(), arguments);
    }

    private static void assertImmutable(GameTestHelper helper, List<?> list) {
        try {
            list.clear();
            helper.fail("Viewer recipe and description lists must reject mutation");
        } catch (UnsupportedOperationException expected) {
            // Immutable snapshots remain safe while JEI retains them across asynchronous resource reloads.
        }
    }
}
