package lach_01298.qmd.physics;

import lach_01298.qmd.particle.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ParticlePhysicsTest {

    @BeforeAll
    static void particles() {
        Particles.initialize();
    }

    @Test
    void collisionEnergyDoesNotOverflowTheIntermediateProduct() {
        assertEquals(12, ParticlePhysics.collisionEnergy(4, 9));
        assertEquals(2_000_000_000_000L, ParticlePhysics.collisionEnergy(1_000_000_000_000L, 1_000_000_000_000L));
        assertEquals(Long.MAX_VALUE, ParticlePhysics.collisionEnergy(Long.MAX_VALUE, Long.MAX_VALUE));
        assertEquals(0, ParticlePhysics.collisionEnergy(0, Long.MAX_VALUE));
    }

    @Test
    void asymmetricCollisionsKeepTheirContinuousEnergyPenalty() {
        assertEquals(1000, ParticlePhysics.particleOutputAmountCollisionChamber(1000, 1000, 100, 100, 1, 1, 1));
        assertEquals(500, ParticlePhysics.particleOutputAmountCollisionChamber(1000, 1000, 100, 300, 1, 1, 1));
        assertEquals(0, ParticlePhysics.particleOutputAmountCollisionChamber(1000, 1000, 0, 0, 1, 1, 1));
    }

    @Test
    void signedQAndCountsCannotWrapAround() {
        assertEquals(0, ParticlePhysics.particleOutputEnergy(5, -6, 1));
        assertEquals(4, ParticlePhysics.particleOutputEnergy(5, 3, 2));
        assertEquals(Long.MAX_VALUE, ParticlePhysics.particleOutputEnergy(Long.MAX_VALUE, Long.MAX_VALUE, 1));
        assertEquals(Long.MAX_VALUE, ParticlePhysics.particleOutputAmount(Long.MAX_VALUE, Long.MAX_VALUE, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> ParticlePhysics.particleOutputEnergy(1, 0, 0));
    }

    @Test
    void chargeAndIntensityGovernAttenuationAndMagneticLimits() {
        var proton = new ParticleStack(Particles.proton, 10000, 1000, 10);
        assertEquals(.4, ParticlePhysics.focusLoss(10, proton, .02, 10000), 1e-10);
        assertEquals(.2, ParticlePhysics.focusGain(.2, proton), 1e-10);
        assertEquals(200, ParticlePhysics.linacEnergyGain(200, proton));
        assertEquals(0, ParticlePhysics.ringEnergyMaxEnergy(1, 200, 3, new ParticleStack(Particles.photon, 1, 1, 10)));
        assertEquals(0, ParticlePhysics.ringEnergyMaxEnergy(1, 200, 3, new ParticleStack(Particles.neutron, 1, 1, 10)));
        assertEquals(0, ParticlePhysics.cornerEnergyLoss(new ParticleStack(Particles.photon, 1, 1, 10), 1));
    }

    @Test
    void malformedPhysicalInputsAreRejectedBeforeProducingNan() {
        assertThrows(IllegalArgumentException.class, () -> ParticlePhysics.ringEnergyMaxEnergyFromDipole(1, 0, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> ParticlePhysics.focusLoss(1, null, Double.NaN, 1));
        assertThrows(IllegalArgumentException.class, () -> ParticlePhysics.particleOutputAmount(1, 1, -1, 1));
        assertThrows(
            IllegalArgumentException.class,
            () -> ParticlePhysics.particleOutputAmount(1, 1, 1, Double.POSITIVE_INFINITY));
    }
}
