package lach_01298.qmd.capability;

import lach_01298.qmd.particle.IParticleStackHandler;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

/** Particle transfers use the receiving block face as their direction context. */
public final class QMDParticleCapabilities {

    public static final BlockCapability<IParticleStackHandler, @Nullable Direction> BLOCK = BlockCapability
        .createSided(ResourceLocation.fromNamespaceAndPath("qmd", "particle_handler"), IParticleStackHandler.class);

    private QMDParticleCapabilities() {}
}
