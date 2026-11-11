package lach_01298.qmd.particle;

import lach_01298.qmd.config.QMDConfig;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SpecializedParticleStorageTest {

    @BeforeAll
    static void initializeCatalog() {
        Particles.initialize();
    }

    @Test
    void sourceRejectsInsertionAndEveryExtractionIsAnIndependentNonConsumingSample() {
        ParticleStorageSource source = new ParticleStorageSource();
        ParticleStack input = new ParticleStack(Particles.proton, 100, Long.MAX_VALUE - 5, 8);
        source.setParticleStack(input);
        input.setAmount(1);
        assertFalse(source.canReceiveParticle(null, input));
        assertFalse(source.receiveParticle(null, input));
        assertFalse(source.canReceiveParticle(null, null));
        assertFalse(source.receiveParticle(null, null));

        List<ParticleStack> samples = List.of(
            source.extractParticle(Direction.UP),
            source.extractParticle(null, Particles.proton),
            source.extractParticle(null, 4),
            source.extractParticle(null, Particles.proton, 7));
        assertEquals(
            List.of(100, 100, 4, 7),
            samples.stream()
                .map(ParticleStack::getAmount)
                .toList());
        for (ParticleStack sample : samples) {
            assertEquals(Long.MAX_VALUE - 5, sample.getMeanEnergy());
            assertEquals(8, sample.getFocus());
            sample.setAmount(1);
            sample.setFocus(0);
        }
        assertEquals(
            100,
            source.getParticle()
                .getAmount());
        assertEquals(
            8,
            source.getParticle()
                .getFocus());
        assertNull(source.extractParticle(null, Particles.electron));
        assertNull(source.extractParticle(null, Particles.electron, 4));
        assertNull(source.extractParticle(null, 0));
        assertNull(source.extractParticle(null, -1));
        assertEquals(
            100,
            source.extractParticle(null, 1000)
                .getAmount());
        source.setParticleStack(null);
        assertFalse(source.canExtractParticle(null));
        assertNull(source.extractParticle(null));
    }

    @Test
    void acceleratorReplacesSpeciesWithoutAccumulationAndCanExplicitlyClearItsSnapshot() {
        ParticleStorageAccelerator accelerator = new ParticleStorageAccelerator();
        accelerator.setMinEnergy(20);
        accelerator.setMaxEnergy(100);
        accelerator.setCapacity(10);
        ParticleStack first = new ParticleStack(Particles.proton, 10, 20, 4);
        assertTrue(accelerator.canReceiveParticle(null, first));
        assertNull(accelerator.getParticle());
        assertTrue(accelerator.receiveParticle(null, first));
        first.setAmount(1);
        ParticleStack next = new ParticleStack(Particles.electron, 5, 100, 3);
        assertTrue(accelerator.canReceiveParticle(null, next));
        assertTrue(accelerator.receiveParticle(null, next));
        next.setMeanEnergy(1);
        assertSame(
            Particles.electron,
            accelerator.getParticle()
                .getParticle());
        assertEquals(
            5,
            accelerator.getParticle()
                .getAmount());
        assertEquals(
            100,
            accelerator.getParticle()
                .getMeanEnergy());
        accelerator.extractParticle(null, Particles.electron, 2)
            .setFocus(0);
        assertEquals(
            5,
            accelerator.getParticle()
                .getAmount());
        assertEquals(
            3,
            accelerator.getParticle()
                .getFocus());

        for (ParticleStack invalid : List.of(
            new ParticleStack(),
            new ParticleStack(Particles.proton, 11, 50),
            new ParticleStack(Particles.proton, 1, 19),
            new ParticleStack(Particles.proton, 1, 101))) {
            assertFalse(accelerator.canReceiveParticle(null, invalid));
            assertFalse(accelerator.receiveParticle(null, invalid));
            assertSame(
                Particles.electron,
                accelerator.getParticle()
                    .getParticle());
        }
        assertTrue(accelerator.canReceiveParticle(null, null));
        assertNotNull(accelerator.getParticle());
        assertTrue(accelerator.receiveParticle(null, null));
        assertNull(accelerator.getParticle());
    }

    @Test
    void acceleratorSamplesAllOverloadsWithoutConsumingTheOutput() {
        ParticleStorageAccelerator accelerator = new ParticleStorageAccelerator();
        assertTrue(accelerator.receiveParticle(null, new ParticleStack(Particles.muon, 12, 1L << 40, 4)));
        for (ParticleStack extracted : List.of(
            accelerator.extractParticle(null),
            accelerator.extractParticle(null, Particles.muon),
            accelerator.extractParticle(null, 3),
            accelerator.extractParticle(null, Particles.muon, 5))) {
            extracted.setAmount(1);
            assertEquals(
                12,
                accelerator.getParticle()
                    .getAmount());
        }
    }

    @Test
    void beamlineReplacesItsIncomingBeamAndRejectsInvalidFocusWithoutChangingThePreviousBeam() {
        ParticleStorageBeamline beamline = new ParticleStorageBeamline(4);
        ParticleStack first = new ParticleStack(Particles.proton, 100, Long.MAX_VALUE, 10);
        assertTrue(beamline.canReceiveParticle(null, first));
        assertNull(beamline.getParticle());
        assertTrue(beamline.receiveParticle(null, first));
        first.setAmount(1);
        assertEquals(
            100,
            beamline.getParticle()
                .getAmount());
        assertTrue(beamline.receiveParticle(null, new ParticleStack(Particles.electron, 20, 42, 6)));
        assertSame(
            Particles.electron,
            beamline.getParticle()
                .getParticle());
        assertEquals(
            20,
            beamline.getParticle()
                .getAmount());
        assertFalse(beamline.canReceiveParticle(null, null));
        assertFalse(beamline.receiveParticle(null, null));
        for (ParticleStack invalid : List.of(
            new ParticleStack(),
            new ParticleStack(Particles.proton, 1, 100, 0),
            new ParticleStack(Particles.proton, 1, 100, Double.NaN))) {
            assertFalse(beamline.canReceiveParticle(null, invalid));
            assertFalse(beamline.receiveParticle(null, invalid));
            assertSame(
                Particles.electron,
                beamline.getParticle()
                    .getParticle());
        }
    }

    @Test
    void beamlineUsesTheLegacyChargeAndBeamAmountDependentFocusLoss() {
        int length = 5;
        ParticleStorageBeamline beamline = new ParticleStorageBeamline(length);
        double rate = QMDConfig.BEAM_ATTENUATION_RATE.get();
        int scale = QMDConfig.BEAM_SCALING.get();
        ParticleStack incoming = new ParticleStack(Particles.calcium_48_ion, scale * 4, 42, 10);
        assertTrue(beamline.receiveParticle(null, incoming));
        ParticleStack extracted = beamline.extractParticle(null);
        assertNotNull(extracted);
        assertEquals(
            10 - rate * length * (1 + Math.abs(Particles.calcium_48_ion.getCharge()) * 2),
            extracted.getFocus(),
            1.0E-12);
        assertEquals(incoming.getAmount(), extracted.getAmount());
        assertEquals(42, extracted.getMeanEnergy());
        assertNull(beamline.getParticle());
        assertEquals(10, incoming.getFocus());

        assertTrue(beamline.receiveParticle(null, new ParticleStack(Particles.neutron, scale, 42, 10)));
        assertEquals(
            10 - rate * length,
            beamline.extractParticle(null, Particles.neutron)
                .getFocus(),
            1.0E-12);
        assertNull(beamline.getParticle());
    }

    @Test
    void beamlinePartialAndTypedExtractionCannotBypassAttenuationOrTheStrictFocusGate() {
        ParticleStorageBeamline beamline = new ParticleStorageBeamline(10);
        double rate = QMDConfig.BEAM_ATTENUATION_RATE.get();
        double gate = 10 * rate;
        beamline.setParticleStack(new ParticleStack(Particles.proton, 100, 42, gate));
        assertFalse(beamline.canExtractParticle(null));
        assertNull(beamline.extractParticle(null));
        assertNull(beamline.extractParticle(null, Particles.proton));
        assertNull(beamline.extractParticle(null, 3));
        assertNull(beamline.extractParticle(null, Particles.proton, 3));
        assertEquals(
            100,
            beamline.getParticle()
                .getAmount());

        beamline.setParticleStack(new ParticleStack(Particles.proton, 100, 42, 2));
        assertNull(beamline.extractParticle(null, Particles.electron, 4));
        assertNull(beamline.extractParticle(null, 0));
        ParticleStack partial = beamline.extractParticle(null, Particles.proton, 4);
        assertEquals(4, partial.getAmount());
        assertEquals(2 - gate * (1 + Math.sqrt(4D / QMDConfig.BEAM_SCALING.get())), partial.getFocus(), 1.0E-12);
        assertEquals(
            96,
            beamline.getParticle()
                .getAmount());
        assertEquals(
            2,
            beamline.getParticle()
                .getFocus());
        partial.setAmount(1);
        assertEquals(
            96,
            beamline.getParticle()
                .getAmount());
        assertNotNull(beamline.extractParticle(null, 100));
        assertNull(beamline.getParticle());
    }

    @Test
    void beamlineLengthAndStoragePersistAndMalformedLengthDoesNotBecomeNegative() {
        ParticleStorageBeamline beamline = new ParticleStorageBeamline(13);
        assertTrue(beamline.receiveParticle(null, new ParticleStack(Particles.proton, 30, 1L << 40, 9)));
        CompoundTag saved = beamline.writeToNBT(new CompoundTag());
        ParticleStorageBeamline restored = new ParticleStorageBeamline(1);
        restored.readFromNBT(saved);
        assertEquals(13, restored.getLength());
        assertEquals(
            30,
            restored.getParticle()
                .getAmount());
        assertEquals(
            1L << 40,
            restored.getParticle()
                .getMeanEnergy());
        assertEquals(
            9,
            restored.getParticle()
                .getFocus());
        saved.putInt("length", -7);
        restored.readFromNBT(saved);
        assertEquals(0, restored.getLength());
        saved.remove("length");
        restored.setLength(4);
        restored.readFromNBT(saved);
        assertEquals(4, restored.getLength());
        assertThrows(IllegalArgumentException.class, () -> new ParticleStorageBeamline(-1));
        assertThrows(IllegalArgumentException.class, () -> restored.setLength(-1));
    }

    @Test
    void chargeDependentLossSaturatesAtZeroFocusAsInTheParticleValueModel() {
        ParticleStorageBeamline beamline = new ParticleStorageBeamline(1);
        double rate = QMDConfig.BEAM_ATTENUATION_RATE.get();
        assertTrue(
            beamline.receiveParticle(
                null,
                new ParticleStack(Particles.proton, QMDConfig.BEAM_SCALING.get(), 1, rate * 1.5)));
        ParticleStack extracted = beamline.extractParticle(null);
        assertNotNull(extracted);
        assertEquals(0, extracted.getFocus());
        assertNull(beamline.getParticle());
    }
}
