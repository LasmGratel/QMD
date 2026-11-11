package lach_01298.qmd.block.entity;

import com.nred.nuclearcraft.block_entity.internal.fluid.Tank;
import com.nred.nuclearcraft.block_entity.turbine.TurbineControllerEntity;
import com.nred.nuclearcraft.block_entity.turbine.TurbineRotorBladeEntity;
import com.nred.nuclearcraft.handler.AbstractRecipeHandler;
import com.nred.nuclearcraft.multiblock.turbine.TurbineRotorBladeUtil;
import com.nred.nuclearcraft.recipe.BasicRecipe;
import com.nred.nuclearcraft.recipe.NCRecipes;
import com.nred.nuclearcraft.registration.BlockRegistration;
import lach_01298.qmd.registry.QMDBlockEntities;
import lach_01298.qmd.registry.QMDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;

@GameTestHolder("qmd")
@PrefixGameTestTemplate(false)
public final class TurbineCompatGameTests {
    @GameTest(template = "empty", batch = "qmd.turbine")
    public static void bladeTypeAndRenderDirectionSurvivePersistence(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(2, 2, 2));
        helper.getLevel().setBlock(pos, QMDBlocks.SUPER_ALLOY_TURBINE_BLADE.get().defaultBlockState(), 3);
        var blade = (SuperAlloyBladeBlockEntity) helper.getLevel().getBlockEntity(pos);
        helper.assertTrue(blade.getType() == QMDBlockEntities.SUPER_ALLOY_TURBINE_BLADE.get() && blade.getType().isValid(blade.getBlockState()),
                "Native rotor subclass must use its own QMD block entity registration");
        for (var dir : TurbineRotorBladeUtil.TurbinePartDir.values()) {
            blade.setDir(dir);
            var restored = BlockEntity.loadStatic(pos, blade.getBlockState(),
                    blade.saveWithFullMetadata(helper.getLevel().registryAccess()), helper.getLevel().registryAccess());
            helper.assertTrue(restored instanceof SuperAlloyBladeBlockEntity, "Saved native blade must reload as the QMD subclass");
            var copy = (SuperAlloyBladeBlockEntity) restored;
            helper.assertTrue(copy.getDir() == dir && copy.getRenderState().is(QMDBlocks.SUPER_ALLOY_TURBINE_BLADE.get())
                            && copy.getRenderState().getValue(TurbineRotorBladeUtil.DIR) == dir,
                    "All native render directions retain the QMD model after reload");
            helper.assertTrue(copy.getBladeType() == QMDBlocks.SUPER_ALLOY_BLADE_TYPE
                            && copy.getBladeType().getEfficiency() == 1.1 && copy.getBladeType().getExpansionCoefficient() == 1.3,
                    "Reloaded QMD blades retain their native efficiency and expansion variant");
        }
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.turbine", timeoutTicks = 400)
    public static void nativeTurbineAssemblesQmdBladesAndProcessesMercury(GameTestHelper helper) {
        for (int x = 0; x <= 4; x++) for (int y = 0; y <= 4; y++) for (int z = 0; z <= 4; z++) {
            boolean shell = x == 0 || x == 4 || y == 0 || y == 4 || z == 0 || z == 4;
            helper.setBlock(new BlockPos(x, y, z), shell ? BlockRegistration.TURBINE_MAP.get("turbine_casing").get() : Blocks.AIR);
        }
        BlockPos controllerPos = new BlockPos(0, 2, 2);
        helper.setBlock(controllerPos, BlockRegistration.TURBINE_MAP.get("turbine_controller").get());
        for (int z : new int[]{0, 4}) {
            helper.setBlock(new BlockPos(2, 2, z), BlockRegistration.TURBINE_MAP.get("turbine_rotor_bearing").get());
            helper.setBlock(new BlockPos(3, 2, z), BlockRegistration.TURBINE_MAP.get("magnesium_turbine_dynamo_coil").get());
        }
        helper.setBlock(new BlockPos(1, 2, 0), BlockRegistration.TURBINE_MAP.get("turbine_inlet").get());
        helper.setBlock(new BlockPos(1, 2, 4), BlockRegistration.TURBINE_MAP.get("turbine_outlet").get());
        for (int z = 1; z <= 3; z++) {
            helper.setBlock(new BlockPos(2, 2, z), BlockRegistration.TURBINE_MAP.get("turbine_rotor_shaft").get());
            for (BlockPos blade : new BlockPos[]{new BlockPos(1, 2, z), new BlockPos(3, 2, z), new BlockPos(2, 1, z), new BlockPos(2, 3, z)})
                helper.setBlock(blade, QMDBlocks.SUPER_ALLOY_TURBINE_BLADE.get());
        }
        boolean[] started = {false};
        helper.succeedWhen(() -> {
            var controller = (TurbineControllerEntity) helper.getBlockEntity(controllerPos);
            var turbine = controller.getMultiblockController().orElse(null);
            helper.assertTrue(turbine != null && turbine.isAssembled(), "Native ZeroCore turbine must assemble the QMD rotor fixture");
            helper.assertTrue(turbine.getPartCount(TurbineRotorBladeEntity.class) == 12
                            && turbine.getBlade(helper.absolutePos(new BlockPos(1, 2, 1))) instanceof SuperAlloyBladeBlockEntity,
                    "Native rotor tracing must recognize QMD subclasses as all twelve operating blades");
            helper.assertTrue(turbine.flowDir == Direction.SOUTH && turbine.noBladeSets == 3
                            && Math.abs(turbine.totalExpansionLevel - Math.pow(1.3, 3)) < 1E-10,
                    "Native rotor validation uses the QMD expansion coefficient in all three stages");
            helper.assertTrue(turbine.conductivity > 0 && turbine.dynamoCoilCount == 1 && turbine.dynamoCoilCountOpposite == 1,
                    "Both actual magnesium dynamos must form conductive native bearing connections");
            if (!started[0]) {
                // The native turbine initializes recipe power after its first spin-up tick.
                // Supply more than the twelve-blade rotor can consume in one tick.
                turbine.tanks.get(0).setFluidStored(fluid("high_pressure_mercury", 2000));
                turbine.computerActivated = true;
                turbine.getLogic().setIsTurbineOn();
                started[0] = true;
            }
            helper.assertTrue(FluidStack.matches(turbine.tanks.get(1).getFluid(), fluid("exhaust_mercury", 6000))
                            && turbine.tanks.get(0).isEmpty(),
                    "Actual native world ticking consumes 2000 mB of mercury vapor and produces exactly 6000 mB exhaust");
            helper.assertTrue(turbine.basePowerPerMB == 192 && turbine.rotorEfficiency > 0 && turbine.energyStorage.getEnergyStoredLong() > 0,
                    "QMD blades drive the native mercury energy cycle through real dynamos: base=" + turbine.basePowerPerMB
                            + ", efficiency=" + turbine.rotorEfficiency + ", stored=" + turbine.energyStorage.getEnergyStoredLong());
        });
    }

    @GameTest(template = "empty", batch = "qmd.turbine")
    public static void nativeHandlersMatchEveryMercuryCycleRecipe(GameTestHelper helper) {
        var cold = match(helper, NCRecipes.fission_heating, "fission_heating/mercury", "mercury", 1, "high_pressure_mercury", 2);
        var warm = match(helper, NCRecipes.fission_heating, "fission_heating/hot_mercury", "hot_mercury", 1, "high_pressure_mercury", 2);
        helper.assertTrue(cold.getFissionHeatingHeatPerInputMB() == 512 && warm.getFissionHeatingHeatPerInputMB() == 256,
                "Native fission boilers retain both legacy mercury heating costs");
        var turbine = match(helper, NCRecipes.turbine, "turbine/high_pressure_mercury", "high_pressure_mercury", 1, "exhaust_mercury", 3);
        helper.assertTrue(turbine.getTurbinePowerPerMB() == 192 && turbine.getTurbineExpansionLevel() == 3,
                "Native mercury turbine matching retains generation and ideal expansion");
        var exchanger = match(helper, NCRecipes.heat_exchanger, "heat_exchanger/exhaust_mercury", "exhaust_mercury", 6, "hot_mercury", 1);
        helper.assertTrue(exchanger.getHeatExchangerHeatDifference() == 128 && !exchanger.getHeatExchangerIsHeating()
                        && exchanger.getHeatExchangerInputTemperature() == 700 && exchanger.getHeatExchangerOutputTemperature() == 700,
                "Equal-temperature exhaust condensation must remain an exothermic native exchange recipe");
        var exhaust = match(helper, NCRecipes.condenser, "condenser/exhaust_mercury", "exhaust_mercury", 6, "hot_mercury", 1);
        var condensate = match(helper, NCRecipes.condenser, "condenser/hot_mercury", "hot_mercury", 1, "mercury", 1);
        helper.assertTrue(exhaust.getCondenserCoolingRequired() == 128 && exhaust.getCondenserInputTemperature() == 700
                        && exhaust.getCondenserOutputTemperature() == 700 && condensate.getCondenserCoolingRequired() == 256
                        && condensate.getCondenserInputTemperature() == 700 && condensate.getCondenserOutputTemperature() == 300,
                "Both native condenser routes retain exact heat and temperature requirements");
        helper.succeed();
    }

    private static <T extends BasicRecipe> T match(GameTestHelper helper, AbstractRecipeHandler<T> handler, String path,
                                                   String input, int inputAmount, String output, int outputAmount) {
        Tank tank = new Tank(1000, null);
        tank.setFluidStored(fluid(input, inputAmount));
        var match = handler.getRecipeInfoFromInputs(helper.getLevel(), List.of(), List.of(tank));
        var expected = helper.getLevel().getRecipeManager().byKey(ResourceLocation.fromNamespaceAndPath("qmd", path)).orElseThrow().value();
        helper.assertTrue(match != null && match.recipe == expected, "Native handler must select loaded qmd:" + path + " from actual registered fluid");
        helper.assertTrue(match.recipe.getFluidIngredient().amount() == inputAmount
                        && FluidStack.matches(match.recipe.getFluidProduct().getStackRaw(), fluid(output, outputAmount)),
                "Native loaded recipe must retain exact mercury fluid identity and amount: " + path);
        return match.recipe;
    }

    private static FluidStack fluid(String name, int amount) {
        return new FluidStack(BuiltInRegistries.FLUID.get(ResourceLocation.fromNamespaceAndPath("qmd", name)), amount);
    }
}
