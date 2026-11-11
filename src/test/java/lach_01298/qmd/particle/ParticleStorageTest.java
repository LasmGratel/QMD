package lach_01298.qmd.particle;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ParticleStorageTest {

    @BeforeAll
    static void initializeCatalog() {
        Particles.initialize();
    }

    @Test
    void catalogPreservesAllLegacySpeciesAndStableIdentity() {
        assertEquals(
            64,
            Particles.getRegisteredParticles()
                .size());
        Particle proton = Particles.proton;
        Particles.initialize();
        assertSame(proton, Particles.proton);
        assertSame(proton, Particles.getParticleFromName("proton"));
        assertSame(proton, Particles.getParticleFromName("qmd:proton"));
        assertNull(Particles.getParticleFromName("other:proton"));
        assertNull(Particles.getParticleFromName("missing"));
        assertNull(Particles.getParticleFromName(null));
        assertThrows(UnsupportedOperationException.class, () -> Particles.list.clear());
    }

    @Test
    void antiparticlesAndCompositeConstituentsRemainPaired() {
        for (Particle particle : Particles.getRegisteredParticles()) {
            assertSame(
                particle,
                particle.getAntiParticle()
                    .getAntiParticle(),
                particle.getName());
        }
        assertEquals(
            2,
            Particles.proton.getComponentParticles()
                .get(Particles.up));
        assertEquals(
            1,
            Particles.proton.getComponentParticles()
                .get(Particles.down));
        assertEquals(
            2,
            Particles.antiproton.getComponentParticles()
                .get(Particles.antiup));
        assertEquals(
            1,
            Particles.antiproton.getComponentParticles()
                .get(Particles.antidown));
        assertEquals(
            20,
            Particles.calcium_48_ion.getComponentParticles()
                .get(Particles.proton));
        assertEquals(
            28,
            Particles.calcium_48_ion.getComponentParticles()
                .get(Particles.neutron));
        assertEquals(
            19,
            Particles.calcium_48_ion.getComponentParticles()
                .get(Particles.electron));
        assertEquals(938D, Particles.proton.getMass());
        assertEquals(-1D, Particles.antiproton.getCharge());
        assertFalse(Particles.electron.interactsWithStrong());
        assertTrue(Particles.electron.interactsWithWeak());
        assertFalse(Particles.photon.interactsWithWeak());
    }

    @Test
    void everyRegisteredParticleRetainsItsTexture() {
        for (Particle particle : Particles.getRegisteredParticles()) {
            String path = "/assets/" + particle.getTexture()
                .getNamespace()
                + "/"
                + particle.getTexture()
                    .getPath();
            assertNotNull(getClass().getResource(path), path);
        }
    }

    @Test
    void duplicateSpeciesCannotReplaceExistingDefinitions() {
        Particle replacement = new Particle("proton", Particles.proton.getTexture(), 0, 0, 0);
        assertThrows(IllegalArgumentException.class, () -> Particles.registerParticle(replacement));
        assertSame(Particles.proton, Particles.getParticleFromName("proton"));
    }

    @Test
    void registeredParticlePhysicsCannotBeChangedThroughLookupResults() {
        Particle proton = Particles.getParticleFromName("proton");
        Particle antiproton = proton.getAntiParticle();
        Map<Particle, Integer> components = proton.getComponentParticles();
        assertThrows(IllegalStateException.class, () -> proton.setAntiParticle(Particles.photon));
        assertThrows(IllegalStateException.class, () -> proton.addComponentParticle(Particles.gluon));
        assertThrows(IllegalStateException.class, () -> proton.setComponentParticles(Map.of(Particles.gluon, 1)));
        proton.getComponentParticles()
            .clear();
        assertSame(antiproton, proton.getAntiParticle());
        assertSame(proton, antiproton.getAntiParticle());
        assertEquals(components, proton.getComponentParticles());
        Particles.initialize();
        assertSame(proton, Particles.getParticleFromName("proton"));
    }

    @Test
    void anUnregisteredParticleCannotMutateItsRegisteredAntiparticlePartner() {
        Particle custom = new Particle("custom", Particles.proton.getTexture(), 1, 1, 0);
        Particle originalAntiparticle = Particles.proton.getAntiParticle();
        assertThrows(IllegalStateException.class, () -> custom.setAntiParticle(Particles.proton));
        assertSame(custom, custom.getAntiParticle());
        assertSame(originalAntiparticle, Particles.proton.getAntiParticle());
    }

    @Test
    void unregisteredParticleConstructionRemainsMutable() {
        Particle custom = new Particle("custom", Particles.proton.getTexture(), 1, 1, 0);
        Particle anti = new Particle("anti_custom", Particles.proton.getTexture(), 1, -1, 0);
        custom.addComponentParticle(Particles.up);
        custom.addComponentParticle(Particles.up, 2);
        assertEquals(
            3,
            custom.getComponentParticles()
                .get(Particles.up));
        custom.setComponentParticles(Map.of(Particles.down, 2));
        assertEquals(Map.of(Particles.down, 2), custom.getComponentParticles());
        custom.setAntiParticle(anti);
        assertSame(anti, custom.getAntiParticle());
        assertSame(custom, anti.getAntiParticle());
    }

    @Test
    void stackRoundTripsLegacyNbtKeysAndLongEnergy() {
        ParticleStack original = new ParticleStack(Particles.proton, 45, Long.MAX_VALUE - 13, 7.25);
        CompoundTag tag = original.writeToNBT(new CompoundTag());
        assertEquals("proton", tag.getString("particle"));
        assertEquals(45, tag.getInt("amount"));
        assertEquals(Long.MAX_VALUE - 13, tag.getLong("meanEnergy"));
        assertEquals(7.25, tag.getDouble("focus"));
        ParticleStack loaded = ParticleStack.loadParticleStackFromNBT(tag);
        assertNotNull(loaded);
        assertSame(Particles.proton, loaded.getParticle());
        assertEquals(original.getAmount(), loaded.getAmount());
        assertEquals(original.getMeanEnergy(), loaded.getMeanEnergy());
        assertEquals(original.getFocus(), loaded.getFocus());
    }

    @Test
    void malformedOrEmptyNbtNeverCreatesExtractableUnknownParticles() {
        assertNull(ParticleStack.loadParticleStackFromNBT(null));
        assertNull(ParticleStack.loadParticleStackFromNBT(new CompoundTag()));
        CompoundTag tag = new CompoundTag();
        tag.putString("particle", "missing");
        tag.putInt("amount", 10);
        assertNull(ParticleStack.loadParticleStackFromNBT(tag));
        tag.putString("particle", "proton");
        tag.putInt("amount", -4);
        assertNull(ParticleStack.loadParticleStackFromNBT(tag));
        tag.putInt("amount", 1);
        tag.putLong("meanEnergy", -10);
        tag.putDouble("focus", Double.NaN);
        ParticleStack loaded = ParticleStack.loadParticleStackFromNBT(tag);
        assertNotNull(loaded);
        assertEquals(0, loaded.getMeanEnergy());
        assertEquals(0, loaded.getFocus());
    }

    @Test
    void writingEmptyStackRemovesStaleSpecies() {
        CompoundTag reused = new ParticleStack(Particles.electron, 5).writeToNBT(new CompoundTag());
        new ParticleStack().writeToNBT(reused);
        assertFalse(reused.contains("particle"));
        assertNull(ParticleStack.loadParticleStackFromNBT(reused));
    }

    @Test
    void stackArithmeticSaturatesWithoutWrappingOrAddingOnNegativeRemoval() {
        ParticleStack stack = new ParticleStack(Particles.proton, Integer.MAX_VALUE - 2, Long.MAX_VALUE - 2);
        stack.addAmount(Long.MAX_VALUE);
        stack.addMeanEnergy(Long.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, stack.getAmount());
        assertEquals(Long.MAX_VALUE, stack.getMeanEnergy());
        stack.removeAmount(-1);
        assertEquals(Integer.MAX_VALUE, stack.getAmount());
        stack.addAmount(Long.MIN_VALUE);
        stack.addMeanEnergy(Long.MIN_VALUE);
        assertEquals(0, stack.getAmount());
        assertEquals(0, stack.getMeanEnergy());
        stack.setAmount(2);
        stack.removeAmount(Long.MAX_VALUE);
        assertTrue(stack.isEmpty());
    }

    @Test
    void copiesAndStorageInfoDoNotAliasTheOriginalBeam() {
        ParticleStack original = new ParticleStack(Particles.proton, 7, 42, 3);
        ParticleStorage storage = new ParticleStorage(original, 1000, 20);
        original.setAmount(1);
        assertEquals(
            7,
            storage.getParticleStack()
                .getAmount());
        ParticleStack copy = storage.getParticle();
        ParticleStorageInfo snapshot = storage.getInfo();
        copy.setAmount(1);
        snapshot.particleStack.setAmount(2);
        assertEquals(
            7,
            storage.getParticleStack()
                .getAmount());
    }

    @Test
    void firstInsertionChecksCapacityAndBothEnergyLimits() {
        ParticleStorage storage = new ParticleStorage(null, 100, 10, 20);
        for (ParticleStack invalid : List.of(
            new ParticleStack(Particles.proton, 11, 50),
            new ParticleStack(Particles.proton, 5, 19),
            new ParticleStack(Particles.proton, 5, 101))) {
            assertFalse(storage.canReceiveParticle(Direction.NORTH, invalid));
            assertFalse(storage.receiveParticle(Direction.NORTH, invalid));
            assertNull(storage.getParticleStack());
        }
        assertFalse(storage.receiveParticle(null, null));
        assertFalse(storage.receiveParticle(null, new ParticleStack()));
        assertFalse(storage.receiveParticle(null, new ParticleStack(Particles.proton, 0, 50)));
    }

    @Test
    void insertionAndSimulationAgreeAtInclusiveEnergyBoundaries() {
        ParticleStorage storage = new ParticleStorage(null, 100, 10, 20);
        ParticleStack low = new ParticleStack(Particles.proton, 5, 20);
        assertTrue(storage.canReceiveParticle(null, low));
        assertNull(storage.getParticleStack());
        assertTrue(storage.receiveParticle(null, low));
        ParticleStack high = new ParticleStack(Particles.proton, 5, 100);
        assertTrue(storage.canReceiveParticle(null, high));
        assertTrue(storage.receiveParticle(null, high));
        assertEquals(
            60,
            storage.getParticleStack()
                .getMeanEnergy());
        assertEquals(
            10,
            storage.getParticleStack()
                .getAmount());
        assertFalse(storage.receiveParticle(null, new ParticleStack(Particles.proton, 1, 50)));
    }

    @Test
    void insertionsRejectDifferentSpeciesAndNeverOverflowCapacity() {
        ParticleStorage storage = new ParticleStorage(
            new ParticleStack(Particles.proton, Integer.MAX_VALUE - 2, 10),
            100);
        assertFalse(storage.receiveParticle(null, new ParticleStack(Particles.electron, 1, 10)));
        assertFalse(storage.receiveParticle(null, new ParticleStack(Particles.proton, 3, 10)));
        assertEquals(
            Integer.MAX_VALUE - 2,
            storage.getParticleStack()
                .getAmount());
    }

    @Test
    void energyAveragingRemainsExactBeyondLongMultiplicationRange() {
        ParticleStorage storage = new ParticleStorage(
            new ParticleStack(Particles.proton, 1_000_000_000, Long.MAX_VALUE - 2),
            Long.MAX_VALUE);
        assertTrue(storage.receiveParticle(null, new ParticleStack(Particles.proton, 1_000_000_000, Long.MAX_VALUE)));
        assertEquals(
            Long.MAX_VALUE - 1,
            storage.getParticleStack()
                .getMeanEnergy());
        assertEquals(
            2_000_000_000,
            storage.getParticleStack()
                .getAmount());
    }

    @Test
    void energyAveragingPreservesLegacyIntegerTruncationAndExistingFocus() {
        ParticleStorage storage = new ParticleStorage(new ParticleStack(Particles.proton, 2, 10, 3), 100);
        assertTrue(storage.receiveParticle(null, new ParticleStack(Particles.proton, 1, 20, 9)));
        assertEquals(
            13,
            storage.getParticleStack()
                .getMeanEnergy());
        assertEquals(
            3,
            storage.getParticleStack()
                .getFocus());
    }

    @Test
    void partialExtractionReturnsTheBeamAndLeavesExactlyTheRemainder() {
        ParticleStorage storage = new ParticleStorage(new ParticleStack(Particles.proton, 10, 42, 7), 100);
        ParticleStack extracted = storage.extractParticle(Direction.UP, 4);
        assertNotNull(extracted);
        assertEquals(4, extracted.getAmount());
        assertEquals(42, extracted.getMeanEnergy());
        assertEquals(7, extracted.getFocus());
        assertEquals(
            6,
            storage.getParticleStack()
                .getAmount());
        extracted.setAmount(1);
        assertEquals(
            6,
            storage.getParticleStack()
                .getAmount());
        ParticleStack rest = storage.extractParticle(null, 100);
        assertNotNull(rest);
        assertEquals(6, rest.getAmount());
        assertNull(storage.getParticleStack());
        assertFalse(storage.canExtractParticle(null));
    }

    @Test
    void extractionRejectsWrongSpeciesOrNonpositiveCountsWithoutMutation() {
        ParticleStorage storage = new ParticleStorage(new ParticleStack(Particles.proton, 10, 42), 100);
        assertNull(storage.extractParticle(null, Particles.electron));
        assertNull(storage.extractParticle(null, Particles.electron, 2));
        assertNull(storage.extractParticle(null, 0));
        assertNull(storage.extractParticle(null, -5));
        assertEquals(
            10,
            storage.getParticleStack()
                .getAmount());
        assertEquals(
            10,
            storage.extractParticle(null, Particles.proton, 10)
                .getAmount());
        assertNull(storage.getParticleStack());
    }

    @Test
    void nestedStorageNbtRoundTripsLongLimitsAndClearsEmptyState() {
        ParticleStorage storage = new ParticleStorage(
            new ParticleStack(Particles.muon, 17, 1L << 40, 4),
            1L << 50,
            20,
            1L << 35);
        CompoundTag tag = storage.writeToNBT(new CompoundTag(), 3);
        assertTrue(tag.contains("ParticleStorage3"));
        ParticleStorage restored = new ParticleStorage();
        restored.readFromNBT(tag, 3);
        assertEquals(storage.getMaxEnergy(), restored.getMaxEnergy());
        assertEquals(storage.getMinEnergy(), restored.getMinEnergy());
        assertEquals(20, restored.getCapacity());
        assertSame(
            Particles.muon,
            restored.getParticleStack()
                .getParticle());
        assertEquals(
            17,
            restored.getParticleStack()
                .getAmount());
        assertEquals(
            1L << 40,
            restored.getParticleStack()
                .getMeanEnergy());
        storage.extractParticle(null);
        restored.readFromNBT(storage.writeToNBT(new CompoundTag()));
        assertNull(restored.getParticleStack());
    }

    @Test
    void missingStorageLimitKeysPreserveConstructedDefaults() {
        ParticleStorage storage = new ParticleStorage(null, 100, 10, 20);
        storage.readFromNBT(new CompoundTag());
        assertEquals(100, storage.getMaxEnergy());
        assertEquals(10, storage.getCapacity());
        assertEquals(20, storage.getMinEnergy());
    }

    @Test
    void invalidLimitChangesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new ParticleStorage(null, 10, -1));
        assertThrows(IllegalArgumentException.class, () -> new ParticleStorage(null, 10, 1, 11));
        ParticleStorage storage = new ParticleStorage(null, 100, 10, 20);
        assertThrows(IllegalArgumentException.class, () -> storage.setCapacity(-1));
        assertThrows(IllegalArgumentException.class, () -> storage.setMinEnergy(101));
        assertThrows(IllegalArgumentException.class, () -> storage.setMaxEnergy(19));
    }
}
