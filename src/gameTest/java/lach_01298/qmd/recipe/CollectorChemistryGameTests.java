package lach_01298.qmd.recipe;

import com.nred.nuclearcraft.block_entity.processor.ProcessorEntityImpl.BasicUpgradableEnergyProcessorEntity;
import com.nred.nuclearcraft.recipe.BasicRecipe;
import com.nred.nuclearcraft.recipe.ProcessorRecipe;
import com.nred.nuclearcraft.recipe.SizedChanceFluidIngredient;
import com.nred.nuclearcraft.recipe.machine.MultiblockDistillerRecipe;
import com.nred.nuclearcraft.registration.BlockRegistration;
import com.nred.nuclearcraft.registration.ItemRegistration;
import lach_01298.qmd.config.QMDConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;

/** Check loaded chemistry and actual Neohaul processors, without substituting test recipes. */
@GameTestHolder("qmd")
@PrefixGameTestTemplate(false)
public final class CollectorChemistryGameTests {
    private static final String SALT = "qmd:chemical_dust_sodium_chloride";

    private CollectorChemistryGameTests() {
    }

    @GameTest(template = "empty")
    public static void loadedSaltAndAirChemistryRetainsLegacyRatios(GameTestHelper helper) {
        ProcessorRecipe saltWater = recipe(helper, "qmd:crystallizer/salt_from_salt_water", ProcessorRecipe.class);
        assertCrystallizer(helper, saltWater, "qmd:salt_water", 10000, 2, 4);
        ProcessorRecipe solution = recipe(helper, "qmd:crystallizer/salt_from_sodium_chloride_solution", ProcessorRecipe.class);
        assertCrystallizer(helper, solution, "qmd:sodium_chloride_solution", 666, 1, 1);

        MultiblockDistillerRecipe brine = recipe(helper,
                "qmd:multiblock_distiller/sodium_chloride_solution_from_salt_water", MultiblockDistillerRecipe.class);
        helper.assertTrue(brine.getFluidIngredients().size() == 1 && brine.getFluidProducts().size() == 2,
                "Salt-water distillation must have one input and both liquid products");
        assertFluid(helper, brine.getFluidIngredients().getFirst(), "qmd:salt_water", 10000);
        assertFluid(helper, brine.getFluidProducts().getFirst(), "qmd:sodium_chloride_solution", 666);
        assertFluid(helper, brine.getFluidProducts().get(1), "minecraft:water", 9000);
        helper.assertTrue(brine.getProcessTimeMultiplier() == 1 && brine.getProcessPowerMultiplier() == 1,
                "Salt-water distillation must retain its time and power modifiers");
        helper.assertTrue(brine.getDistillerSieveTrayCount() == 1,
                "Separating the two liquid products must require one sieve tray");

        MultiblockDistillerRecipe air = recipe(helper,
                "qmd:multiblock_distiller/gases_from_liquid_air", MultiblockDistillerRecipe.class);
        helper.assertTrue(air.getFluidIngredients().size() == 1 && air.getFluidProducts().size() == 5,
                "Liquid-air distillation must retain all five gases");
        assertFluid(helper, air.getFluidIngredients().getFirst(), "qmd:liquid_air", 1000);
        String[] gases = {"nuclearcraftneohaul:nitrogen", "nuclearcraftneohaul:oxygen", "qmd:argon",
                "qmd:neon", "nuclearcraftneohaul:helium"};
        int[] amounts = {38400, 12800, 6400, 5120, 1280};
        for (int gas = 0; gas < gases.length; ++gas) {
            assertFluid(helper, air.getFluidProducts().get(gas), gases[gas], amounts[gas]);
        }
        helper.assertTrue(air.getProcessTimeMultiplier() == 1 && air.getProcessPowerMultiplier() == 1,
                "Liquid-air distillation must retain its time and power modifiers");
        helper.assertTrue(air.getDistillerSieveTrayCount() == 0,
                "The five gaseous products must not be counted as liquid sieve-tray products");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void defaultCoolingReplacesNeohaulRecipesWithoutDuplicates(GameTestHelper helper) {
        helper.assertTrue(QMDConfig.OVERRIDE_NC_RECIPES.get(), "This fixture checks the default enabled override policy");
        String[] gases = {"compressed_air", "nitrogen", "helium"};
        double[] powers = {1, 2.5, 5};
        for (int gas = 0; gas < gases.length; ++gas) {
            String name = gases[gas];
            String sourceNamespace = gas == 0 ? "qmd:" : "nuclearcraftneohaul:";
            String product = gas == 0 ? "qmd:liquid_air" : "nuclearcraftneohaul:liquid_" + name;
            String path = "supercooler/liquid_" + (gas == 0 ? "air" : name) + "_from_" + name;
            ProcessorRecipe cooling = recipe(helper, "qmd:" + path + "_override", ProcessorRecipe.class);
            helper.assertTrue(cooling.getFluidIngredients().size() == 1 && cooling.getFluidProducts().size() == 1,
                    "Each enabled cooling recipe must have one input and one product");
            assertFluid(helper, cooling.getFluidIngredients().getFirst(), sourceNamespace + name, 64);
            assertFluid(helper, cooling.getFluidProducts().getFirst(), product, 1);
            assertModifiers(helper, cooling, 2D / 150D, powers[gas]);
            helper.assertTrue(helper.getLevel().getRecipeManager().byKey(id("qmd:" + path + "_standard")).isEmpty(),
                    "The disabled standard cooling alternative must not remain loaded for " + name);
            if (gas > 0) {
                helper.assertTrue(helper.getLevel().getRecipeManager().byKey(id("nuclearcraftneohaul:" + path)).isEmpty(),
                        "Neohaul's qmd-disabled original must be replaced rather than duplicated for " + name);
            }
        }
        long replacements = helper.getLevel().getRecipeManager().getRecipes().stream()
                .filter(holder -> holder.id().getNamespace().equals("qmd")
                        && holder.id().getPath().startsWith("supercooler/")).count();
        helper.assertTrue(replacements == 3, "Exactly the three enabled replacement cooling recipes must load");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 2400)
    public static void realCrystallizersSupplySaltToTheAcidProductionChain(GameTestHelper helper) {
        var first = processor(helper, new BlockPos(1, 1, 1), "crystallizer");
        var second = processor(helper, new BlockPos(3, 1, 1), "crystallizer");
        var concentrated = processor(helper, new BlockPos(1, 1, 3), "crystallizer");
        var enricher = processor(helper, new BlockPos(3, 1, 3), "fluid_enricher");
        var electrolyzer = processor(helper, new BlockPos(1, 3, 1), "electrolyzer");
        var reactor = processor(helper, new BlockPos(3, 3, 1), "chemical_reactor");
        List<BasicUpgradableEnergyProcessorEntity<?, ?>> machines =
                List.of(first, second, concentrated, enricher, electrolyzer, reactor);
        for (var crystal : List.of(first, second)) {
            crystal.getTanks().getFirst().setFluidStored(fluid("qmd:salt_water", 10000));
            expectRecipe(helper, crystal, "qmd:crystallizer/salt_from_salt_water");
        }
        concentrated.getTanks().getFirst().setFluidStored(fluid("qmd:sodium_chloride_solution", 666));
        expectRecipe(helper, concentrated, "qmd:crystallizer/salt_from_sodium_chloride_solution");
        int[] stage = {0};
        long deadline = System.nanoTime() + 45_000_000_000L;

        helper.succeedWhen(() -> {
            if (System.nanoTime() > deadline) {
                helper.fail("Chemistry chain exceeded its 45-second wall-clock deadline at stage " + stage[0]);
            }
            // Supply power through the fixture; actual world ticks advance every processor normally.
            machines.forEach(machine -> machine.getEnergyStorage().setEnergyStored(machine.getMaxEnergyStoredLong()));
            if (stage[0] == 0) {
                for (var crystal : List.of(first, second, concentrated)) {
                    assertSalt(helper, crystal.getItem(crystal.getContainerInfo().itemOutputSlots[0]));
                    helper.assertTrue(crystal.getTanks().getFirst().isEmpty(),
                            "A completed crystallizer cycle must consume exactly its liquid input");
                }
                ItemStack salt = first.removeItem(first.getContainerInfo().itemOutputSlots[0], 1);
                salt.grow(second.removeItem(second.getContainerInfo().itemOutputSlots[0], 1).getCount());
                enricher.getTanks().getFirst().setFluidStored(fluid("minecraft:water", 2000));
                enricher.setItem(enricher.getContainerInfo().itemInputSlots[0], salt);
                expectRecipe(helper, enricher, "qmd:fluid_enricher/sodium_chloride_solution");
                stage[0] = 1;
            } else if (stage[0] == 1) {
                assertTank(helper, enricher, 1, "qmd:sodium_chloride_solution", 1332);
                helper.assertTrue(enricher.getItem(enricher.getContainerInfo().itemInputSlots[0]).isEmpty()
                                && enricher.getTanks().getFirst().isEmpty(),
                        "Two real salt items and two water buckets must produce two solution batches");
                electrolyzer.getTanks().getFirst().setFluidStored(enricher.getTanks().get(1).getFluid().copy());
                enricher.clearTank(1);
                expectRecipe(helper, electrolyzer, "qmd:electrolyzer/sodium_chloride_solution");
                stage[0] = 2;
            } else if (stage[0] == 2) {
                assertTank(helper, electrolyzer, 1, "nuclearcraftneohaul:hydrogen", 1000);
                assertTank(helper, electrolyzer, 2, "qmd:chlorine", 1000);
                assertTank(helper, electrolyzer, 3, "nuclearcraftneohaul:sodium_hydroxide_solution", 1332);
                helper.assertTrue(electrolyzer.getTanks().getFirst().isEmpty(),
                        "Electrolysis must consume exactly the solution produced by the enricher");
                reactor.getTanks().get(0).setFluidStored(electrolyzer.getTanks().get(1).getFluid().copy());
                reactor.getTanks().get(1).setFluidStored(electrolyzer.getTanks().get(2).getFluid().copy());
                electrolyzer.clearTank(1);
                electrolyzer.clearTank(2);
                expectRecipe(helper, reactor, "qmd:chemical_reactor/hydrochloric_acid");
                stage[0] = 3;
            }
            helper.assertTrue(stage[0] == 3, "Waiting for the produced salt to reach the acid reactor");
            assertTank(helper, reactor, 2, "qmd:hydrochloric_acid", 2000);
            helper.assertTrue(reactor.getTanks().get(0).isEmpty() && reactor.getTanks().get(1).isEmpty(),
                    "The acid reactor must consume the transferred hydrogen and chlorine");
            assertSalt(helper, concentrated.getItem(concentrated.getContainerInfo().itemOutputSlots[0]));
        });
    }

    private static BasicUpgradableEnergyProcessorEntity<?, ?> processor(GameTestHelper helper, BlockPos pos, String name) {
        helper.setBlock(pos, BlockRegistration.PROCESSOR_MAP.get(name).get());
        var tile = helper.getBlockEntity(pos);
        helper.assertTrue(tile instanceof BasicUpgradableEnergyProcessorEntity<?, ?>,
                "Placing the actual Neohaul " + name + " must create its processor entity");
        var machine = (BasicUpgradableEnergyProcessorEntity<?, ?>) tile;
        var info = machine.getContainerInfo();
        machine.setItem(info.speedUpgradeSlot, ItemRegistration.UPGRADE_MAP.get("speed").toStack(8));
        machine.setItem(info.energyUpgradeSlot, ItemRegistration.UPGRADE_MAP.get("energy").toStack(8));
        return machine;
    }

    private static void expectRecipe(GameTestHelper helper, BasicUpgradableEnergyProcessorEntity<?, ?> machine, String recipeId) {
        machine.refreshAll();
        BasicRecipe loaded = recipe(helper, recipeId, BasicRecipe.class);
        helper.assertTrue(machine.getRecipeInfo() != null && machine.getRecipeInfo().recipe == loaded,
                "The real processor must select the loaded recipe " + recipeId);
        helper.assertTrue(machine.getCanProcessInputs(), "The real processor must accept inputs for " + recipeId);
    }

    private static void assertCrystallizer(GameTestHelper helper, ProcessorRecipe recipe, String input, int amount,
                                          double time, double power) {
        helper.assertTrue(recipe.getItemIngredients().isEmpty() && recipe.getFluidIngredients().size() == 1
                        && recipe.getItemProducts().size() == 1 && recipe.getFluidProducts().isEmpty(),
                "Salt crystallization must retain one fluid input and one item product");
        assertFluid(helper, recipe.getFluidIngredients().getFirst(), input, amount);
        assertSalt(helper, recipe.getItemProducts().getFirst().getStackRaw());
        assertModifiers(helper, recipe, time, power);
    }

    private static void assertModifiers(GameTestHelper helper, ProcessorRecipe recipe, double time, double power) {
        helper.assertTrue(recipe.getProcessTimeMultiplier() == time && recipe.getProcessPowerMultiplier() == power
                        && recipe.getBaseProcessRadiation() == 0,
                "Loaded recipe must preserve exact time, power and radiation modifiers");
    }

    private static void assertSalt(GameTestHelper helper, ItemStack salt) {
        helper.assertTrue(salt.getCount() == 1 && BuiltInRegistries.ITEM.getKey(salt.getItem()).equals(id(SALT)),
                "Crystallization must produce exactly one QMD sodium-chloride salt item");
    }

    private static void assertFluid(GameTestHelper helper, SizedChanceFluidIngredient ingredient, String name, int amount) {
        helper.assertTrue(ingredient.amount() == amount && ingredient.chancePercent() == 100
                        && FluidStack.matches(ingredient.getStackRaw(), fluid(name, amount)),
                "Loaded recipe must retain deterministic " + amount + " mB of " + name);
    }

    private static void assertTank(GameTestHelper helper, BasicUpgradableEnergyProcessorEntity<?, ?> machine,
                                   int tank, String name, int amount) {
        helper.assertTrue(FluidStack.matches(machine.getTanks().get(tank).getFluid(), fluid(name, amount)),
                "Processor tank " + tank + " must contain exactly " + amount + " mB of " + name);
    }

    private static <T extends BasicRecipe> T recipe(GameTestHelper helper, String name, Class<T> expectedType) {
        var holder = helper.getLevel().getRecipeManager().byKey(id(name))
                .orElseThrow(() -> new IllegalStateException("Required chemistry recipe did not load: " + name));
        helper.assertTrue(expectedType.isInstance(holder.value()), "Loaded recipe has the wrong type: " + name);
        return expectedType.cast(holder.value());
    }

    private static FluidStack fluid(String name, int amount) {
        ResourceLocation key = id(name);
        if (!BuiltInRegistries.FLUID.containsKey(key)) {
            throw new IllegalStateException("Required chemistry fluid is not registered: " + name);
        }
        return new FluidStack(BuiltInRegistries.FLUID.get(key), amount);
    }

    private static ResourceLocation id(String name) {
        return ResourceLocation.parse(name);
    }
}
