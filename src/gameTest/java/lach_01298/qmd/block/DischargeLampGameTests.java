package lach_01298.qmd.block;

import lach_01298.qmd.registry.QMDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("qmd")
@PrefixGameTestTemplate(false)
public final class DischargeLampGameTests {
    @GameTest(template = "empty", batch = "qmd.lamps")
    public static void allGasVariantsKeepInvertedRedstoneAndProductionRoutes(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(2, 2, 2));
        for (var entry : QMDBlocks.DISCHARGE_LAMPS.entrySet()) {
            helper.getLevel().setBlock(pos, entry.getValue().get().defaultBlockState(), 3);
            helper.assertTrue(helper.getLevel().getBlockState(pos).getValue(DischargeLampBlock.LIT), "Unpowered lamp starts lit");
            helper.getLevel().setBlock(pos.above(), Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
            helper.assertTrue(!helper.getLevel().getBlockState(pos).getValue(DischargeLampBlock.LIT), "Redstone suppresses the lamp");
            helper.getLevel().removeBlock(pos.above(), false);
            helper.assertTrue(helper.getLevel().getBlockState(pos).getValue(DischargeLampBlock.LIT), "Removing redstone restores the lamp");
            if (!entry.getKey().equals("empty")) helper.assertTrue(helper.getLevel().getRecipeManager().byKey(
                    net.minecraft.resources.ResourceLocation.parse("qmd:fluid_infuser/discharge_lamp_" + entry.getKey())).isPresent(),
                    "Each gas lamp must have its native infuser production recipe");
        }
        helper.succeed();
    }
}
