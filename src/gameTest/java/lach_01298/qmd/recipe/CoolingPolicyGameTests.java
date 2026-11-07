package lach_01298.qmd.recipe;

import com.mojang.serialization.JsonOps;
import com.nred.nuclearcraft.recipe.ProcessorRecipe;
import lach_01298.qmd.Config;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/** Exercise actual conditional recipe selection through both policies and a restoring reload. */
@GameTestHolder("qmd")
@PrefixGameTestTemplate(false)
public final class CoolingPolicyGameTests {
    private CoolingPolicyGameTests() {}

    @GameTest(template = "empty", batch = "qmd.cooling.reload", timeoutTicks = 1_000_000)
    public static void resourceReloadSelectsOneCoolingPolicyAndRestoresIt(GameTestHelper helper) {
        boolean original = Config.OVERRIDE_NC_RECIPES.get();
        assertPolicy(helper, original);
        var ops = helper.getLevel().registryAccess().createSerializationContext(JsonOps.INSTANCE);
        ICondition condition = new OverrideNCRecipesCondition(original);
        var encoded = ICondition.CODEC.encodeStart(ops, condition).getOrThrow();
        ICondition decoded = ICondition.CODEC.parse(ops, encoded).getOrThrow();
        helper.assertTrue(decoded.test(ICondition.IContext.EMPTY)
                        && !new OverrideNCRecipesCondition(!original).test(ICondition.IContext.EMPTY),
                "Registered condition codec must preserve which config value enables a recipe");

        AtomicReference<CompletableFuture<Void>> reload = new AtomicReference<>();
        AtomicLong deadline = new AtomicLong(System.nanoTime() + TimeUnit.SECONDS.toNanos(30));
        AtomicReference<Object> previousManager = new AtomicReference<>(helper.getLevel().getRecipeManager());
        helper.startSequence().thenExecuteFor(1_000_000, () -> {
            helper.assertTrue(System.nanoTime() <= deadline.get(), "Cooling-policy resource reload exceeded thirty seconds");
            CompletableFuture<Void> current = reload.get();
            if (current != null && current.isCompletedExceptionally()) {
                Config.OVERRIDE_NC_RECIPES.set(original);
                helper.fail("Cooling-policy resource reload failed: " + current.handle((unused, failure) -> failure).join());
            }
        });

        helper.startSequence().thenExecute(() -> {
            Config.OVERRIDE_NC_RECIPES.set(!original);
            reload.set(reload(helper));
        }).thenWaitUntil(() -> helper.assertTrue(reload.get().isDone(), "Waiting for alternate cooling policy"))
                .thenExecute(() -> {
                    try {
                        helper.assertTrue(helper.getLevel().getRecipeManager() != previousManager.get(),
                                "Changing policy must replace the recipe manager");
                        assertPolicy(helper, !original);
                    } finally {
                        Config.OVERRIDE_NC_RECIPES.set(original);
                    }
                    previousManager.set(helper.getLevel().getRecipeManager());
                    deadline.set(System.nanoTime() + TimeUnit.SECONDS.toNanos(30));
                    reload.set(reload(helper));
                }).thenWaitUntil(() -> helper.assertTrue(reload.get().isDone(), "Waiting to restore original cooling policy"))
                .thenExecute(() -> {
                    helper.assertTrue(helper.getLevel().getRecipeManager() != previousManager.get(),
                            "Restoring policy must decode another recipe manager");
                    assertPolicy(helper, original);
                }).thenSucceed();
    }

    private static CompletableFuture<Void> reload(GameTestHelper helper) {
        var server = helper.getLevel().getServer();
        var selected = List.copyOf(server.getPackRepository().getSelectedIds());
        return CompletableFuture.runAsync(() -> server.reloadResources(selected).join());
    }

    private static void assertPolicy(GameTestHelper helper, boolean override) {
        var manager = helper.getLevel().getRecipeManager();
        String selected = override ? "override" : "standard";
        String absent = override ? "standard" : "override";
        for (String gas : List.of("compressed_air", "nitrogen", "helium")) {
            String liquid = gas.equals("compressed_air") ? "air" : gas;
            String base = "qmd:supercooler/liquid_" + liquid + "_from_" + gas + "_";
            var holder = manager.byKey(ResourceLocation.parse(base + selected));
            helper.assertTrue(holder.isPresent() && holder.get().value() instanceof ProcessorRecipe,
                    "Selected cooling policy must load: " + base + selected);
            helper.assertTrue(manager.byKey(ResourceLocation.parse(base + absent)).isEmpty(),
                    "Opposite policy must not remain as a competing recipe: " + base + absent);
            ProcessorRecipe recipe = (ProcessorRecipe) holder.orElseThrow().value();
            helper.assertTrue(recipe.getFluidIngredients().getFirst().amount() == (override ? 64 : 8000)
                            && recipe.getFluidProducts().getFirst().amount() == (override ? 1 : 25),
                    "Cooling policy must preserve its actual input/output quantities for " + gas);
            if (override) {
                double power = switch (gas) { case "helium" -> 5D; case "nitrogen" -> 2.5D; default -> 1D; };
                helper.assertTrue(recipe.getProcessTimeMultiplier() == 2D / 150D
                                && recipe.getProcessPowerMultiplier() == power,
                        "Legacy cooling policy must preserve time and power for " + gas);
            }
        }
    }
}
