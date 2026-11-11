package lach_01298.qmd.item.player;

import com.nred.nuclearcraft.radiation.RadSources;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public final class LuminousPaintItem extends BlockItem {

    public static final double RADIATION = RadSources.RADIUM / 16D;

    public LuminousPaintItem(Block block, Properties properties) {
        super(block, properties);
    }
}
