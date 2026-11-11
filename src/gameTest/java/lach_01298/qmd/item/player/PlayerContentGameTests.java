package lach_01298.qmd.item.player;

import com.nred.nuclearcraft.radiation.RadSources;
import com.nred.nuclearcraft.radiation.RadiationHelper;
import lach_01298.qmd.registry.QMDBlocks;
import lach_01298.qmd.registry.QMDItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder("qmd")
@PrefixGameTestTemplate(false)
public final class PlayerContentGameTests {
    private static final int TABLET_TIME = 400;

    private PlayerContentGameTests() {}

    @GameTest(template = "empty", batch = "qmd.player.content")
    public static void tabletConsumesOneAndAppliesLegacyImmunity(GameTestHelper helper) {
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var radiation = RadiationHelper.getEntityRadiation(player);
        helper.assertTrue(radiation != null, "Players must expose Neohaul's radiation state");
        radiation.setRadiationImmunityTime(0D);
        player.removeAllEffects();

        ItemStack tablets = new ItemStack(QMDItems.POTASSIUM_IODINE_TABLET.get(), 2);
        ItemStack remainder = tablets.getItem().finishUsingItem(tablets, helper.getLevel(), player);
        var weakness = player.getEffect(MobEffects.WEAKNESS);

        helper.assertTrue(remainder == tablets && remainder.getCount() == 1,
                "Finishing a potassium iodine tablet must consume exactly one item");
        helper.assertTrue(radiation.getRadiationImmunityTime() == TABLET_TIME,
                "The first tablet must grant the legacy default of 400 immunity ticks");
        helper.assertTrue(weakness != null && weakness.getDuration() == TABLET_TIME,
                "The first tablet must apply Weakness for its complete immunity duration");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.player.content")
    public static void tabletOverdoseBeginsStrictlyAboveOneAndAHalfDoses(GameTestHelper helper) {
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var radiation = RadiationHelper.getEntityRadiation(player);
        helper.assertTrue(radiation != null, "Players must expose Neohaul's radiation state");

        radiation.setRadiationImmunityTime(TABLET_TIME * 1.5D);
        player.removeAllEffects();
        helper.assertTrue(PotassiumIodineTabletItem.applyDose(player, TABLET_TIME),
                "A registered player radiation state must accept a tablet dose");
        helper.assertTrue(player.getEffect(MobEffects.HUNGER) == null,
                "Exactly one and a half existing doses must not cross the legacy overdose threshold");

        radiation.setRadiationImmunityTime(TABLET_TIME * 1.5D + 1D);
        player.removeAllEffects();
        helper.assertTrue(PotassiumIodineTabletItem.applyDose(player, TABLET_TIME),
                "The overdose dose must still extend radiation immunity");
        helper.assertTrue(player.getEffect(MobEffects.HUNGER) != null,
                "Existing immunity above one and a half doses must apply the legacy Hunger penalty");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.player.content")
    public static void luminousPaintPlacesEveryColorAndNeedsItsSupport(GameTestHelper helper) {
        Item[] items = {
                QMDItems.GREEN_LUMINOUS_PAINT_ITEM.get(),
                QMDItems.BLUE_LUMINOUS_PAINT_ITEM.get(),
                QMDItems.ORANGE_LUMINOUS_PAINT_ITEM.get()
        };
        Block[] blocks = {
                QMDBlocks.GREEN_LUMINOUS_PAINT_BLOCK.get(),
                QMDBlocks.BLUE_LUMINOUS_PAINT_BLOCK.get(),
                QMDBlocks.ORANGE_LUMINOUS_PAINT_BLOCK.get()
        };
        var player = helper.makeMockPlayer(GameType.SURVIVAL);

        for (int color = 0; color < items.length; color++) {
            BlockPos support = helper.absolutePos(new BlockPos(1 + color, 1, 1));
            BlockPos paintPos = support.above();
            helper.getLevel().setBlock(support, Blocks.STONE.defaultBlockState(), 3);
            player.setPos(support.getX() + 0.5D, support.getY() + 1D, support.getZ() + 0.5D);

            ItemStack paint = new ItemStack(items[color], 2);
            player.setItemInHand(InteractionHand.MAIN_HAND, paint);
            BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(support), Direction.UP, support, false);
            var result = items[color].useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));
            BlockState state = helper.getLevel().getBlockState(paintPos);

            helper.assertTrue(result.consumesAction() && state.is(blocks[color]),
                    "Each luminous paint item must place its matching paint block");
            helper.assertTrue(state.getValue(LuminousPaintBlock.FACING) == Direction.UP,
                    "Paint placed on top of a block must retain the clicked face");
            helper.assertTrue(paint.getCount() == 1,
                    "Survival placement must consume exactly one luminous paint item");
            helper.assertTrue(state.getLightEmission(helper.getLevel(), paintPos) == LuminousPaintBlock.LIGHT_LEVEL,
                    "Placed luminous paint must emit the legacy light level of 12");
            helper.assertTrue(Math.abs(RadiationHelper.getRadiationFromStack(new ItemStack(items[color]), 1D)
                            - RadSources.RADIUM / 16D) < 1E-15D,
                    "Every luminous paint color must expose its legacy radium radiation through Neohaul");

            helper.getLevel().destroyBlock(support, false);
            helper.assertTrue(helper.getLevel().getBlockState(paintPos).isAir(),
                    "Luminous paint must break when its supporting face is removed");
        }
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "qmd.player.content")
    public static void fleshKeepsLegacyFoodValues(GameTestHelper helper) {
        ItemStack flesh = new ItemStack(QMDItems.FLESH.get());
        var food = flesh.get(DataComponents.FOOD);
        helper.assertTrue(food != null, "The existing qmd:flesh item must remain edible");
        helper.assertTrue(food.nutrition() == 4,
                "QMD flesh must retain its legacy nutrition value of 4");
        helper.assertTrue(Math.abs(food.saturation() - 0.8F) < 1E-6F,
                "QMD flesh must retain saturation modifier 0.1, producing 0.8 saturation points");
        helper.succeed();
    }
}
