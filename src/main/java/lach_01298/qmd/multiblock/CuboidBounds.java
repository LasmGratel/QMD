package lach_01298.qmd.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

/** Inclusive immutable bounds shared by machine validators; all chunk checks are non-loading. */
public record CuboidBounds(BlockPos min, BlockPos max) {

    public CuboidBounds {
        min = min.immutable();
        max = max.immutable();
        if (min.getX() > max.getX() || min.getY() > max.getY() || min.getZ() > max.getZ()) {
            throw new IllegalArgumentException("Inverted multiblock bounds");
        }
    }

    public int sizeX() {
        return max.getX() - min.getX() + 1;
    }

    public int sizeY() {
        return max.getY() - min.getY() + 1;
    }

    public int sizeZ() {
        return max.getZ() - min.getZ() + 1;
    }

    public long volume() {
        return (long) sizeX() * sizeY() * sizeZ();
    }

    public boolean contains(BlockPos pos) {
        return pos.getX() >= min.getX() && pos.getX() <= max.getX()
            && pos.getY() >= min.getY()
            && pos.getY() <= max.getY()
            && pos.getZ() >= min.getZ()
            && pos.getZ() <= max.getZ();
    }

    public int boundaryAxes(BlockPos pos) {
        if (!contains(pos)) return 0;
        return (pos.getX() == min.getX() || pos.getX() == max.getX() ? 1 : 0)
            + (pos.getY() == min.getY() || pos.getY() == max.getY() ? 1 : 0)
            + (pos.getZ() == min.getZ() || pos.getZ() == max.getZ() ? 1 : 0);
    }

    public boolean isShell(BlockPos pos) {
        return contains(pos) && boundaryAxes(pos) > 0;
    }

    public boolean isEdge(BlockPos pos) {
        return boundaryAxes(pos) >= 2;
    }

    public boolean isInterior(BlockPos pos) {
        return contains(pos) && boundaryAxes(pos) == 0;
    }

    public CuboidBounds interior() {
        if (sizeX() < 3 || sizeY() < 3 || sizeZ() < 3) throw new IllegalStateException("Cuboid has no interior");
        return new CuboidBounds(min.offset(1, 1, 1), max.offset(-1, -1, -1));
    }

    public Iterable<BlockPos> positions() {
        return BlockPos.betweenClosed(min, max);
    }

    public boolean chunksLoaded(LevelReader level) {
        for (int x = min.getX() >> 4; x <= max.getX() >> 4; x++) {
            for (int z = min.getZ() >> 4; z <= max.getZ() >> 4; z++) {
                if (!level.hasChunk(x, z)) return false;
            }
        }
        return true;
    }
}
