package lach_01298.qmd.compat.kubejs;

import lach_01298.qmd.particle.Particle;
import lach_01298.qmd.particle.ParticleStack;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QMDParticlesTest {

    @Test
    void namespacedAndLegacyNamesResolveTheSameDefinition() {
        Particle proton = QMDParticles.get("proton");
        assertSame(proton, QMDParticles.get("qmd:proton"));
        assertEquals("proton", proton.getName());
        assertSame(
            proton,
            QMDParticles.stack("qmd:proton")
                .getParticle());
    }

    @Test
    void omittedStackPropertiesUseDocumentedDefaults() {
        ParticleStack single = QMDParticles.stack("proton");
        assertEquals(1, single.getAmount());
        assertEquals(0, single.getMeanEnergy());
        assertEquals(0D, single.getFocus());

        ParticleStack amountOnly = QMDParticles.stack("proton", 120);
        assertEquals(120, amountOnly.getAmount());
        assertEquals(0, amountOnly.getMeanEnergy());
        assertEquals(0D, amountOnly.getFocus());

        ParticleStack withEnergy = QMDParticles.stack("proton", 120, 1000000);
        assertEquals(120, withEnergy.getAmount());
        assertEquals(1000000, withEnergy.getMeanEnergy());
        assertEquals(0D, withEnergy.getFocus());

        ParticleStack allProperties = QMDParticles.stack("proton", 120, 1000000, 0.75);
        assertEquals(120, allProperties.getAmount());
        assertEquals(1000000, allProperties.getMeanEnergy());
        assertEquals(0.75, allProperties.getFocus());
    }

    @Test
    void stacksCreatedBySeparateCallsDoNotShareMutableValues() {
        ParticleStack first = QMDParticles.stack("proton", 120, 1000000, 0.75);
        ParticleStack second = QMDParticles.stack("proton", 120, 1000000, 0.75);
        assertNotSame(first, second);
        first.setAmount(1);
        first.setMeanEnergy(2);
        first.setFocus(3);
        assertEquals(120, second.getAmount());
        assertEquals(1000000, second.getMeanEnergy());
        assertEquals(0.75, second.getFocus());
    }

    @Test
    void amountAcceptsZeroAndMaximumWithoutTruncation() {
        ParticleStack empty = QMDParticles.stack("proton", 0);
        assertEquals(0, empty.getAmount());
        assertTrue(empty.isEmpty());
        assertEquals(
            Integer.MAX_VALUE,
            QMDParticles.stack("proton", Integer.MAX_VALUE)
                .getAmount());
    }

    @Test
    void invalidAmountsFailBeforeTheCoreCanClampThem() {
        double[] invalidAmounts = { -1, 0.5, Integer.MAX_VALUE + 1D, Double.NaN, Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY };
        for (double amount : invalidAmounts) {
            IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> QMDParticles.stack("proton", amount),
                "amount " + amount);
            assertTrue(
                error.getMessage()
                    .contains("amount"));
        }
        assertThrows(IllegalArgumentException.class, () -> QMDParticles.stack("proton", 0.5, "1", 0));
    }

    @Test
    void numericEnergyHonorsTheJavaScriptSafeIntegerBoundary() {
        long maximumSafe = 9_007_199_254_740_991L;
        assertEquals(
            0,
            QMDParticles.stack("photon", 1, 0)
                .getMeanEnergy());
        assertEquals(
            maximumSafe,
            QMDParticles.stack("photon", 1, (double) maximumSafe)
                .getMeanEnergy());

        double[] invalidEnergies = { -1, 0.5, maximumSafe + 1D, Double.NaN, Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY, (double) Long.MAX_VALUE };
        for (double energy : invalidEnergies) {
            IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> QMDParticles.stack("photon", 1, energy, 1),
                "energy " + energy);
            assertTrue(
                error.getMessage()
                    .contains("mean energy"));
            assertTrue(
                error.getMessage()
                    .contains("decimal string"));
        }
    }

    @Test
    void decimalStringEnergyPreservesAllLongBits() {
        assertEquals(
            0,
            QMDParticles.stack("photon", 1, "0", 0)
                .getMeanEnergy());
        assertEquals(
            9_007_199_254_740_993L,
            QMDParticles.stack("photon", 1, "9007199254740993", 0.5)
                .getMeanEnergy());
        ParticleStack maximum = QMDParticles.stack("photon", 2, "9223372036854775807", 1.25);
        assertEquals(Long.MAX_VALUE, maximum.getMeanEnergy());
        assertEquals(2, maximum.getAmount());
        assertEquals(1.25, maximum.getFocus());
    }

    @Test
    void malformedNegativeAndOverflowingEnergyStringsFail() {
        String[] invalidEnergies = { null, "", "-1", "1.5", "NaN", "Infinity", "0x10", "9223372036854775808",
            "18446744073709551615" };
        for (String energy : invalidEnergies) {
            IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> QMDParticles.stack("photon", 1, energy, 0),
                "energy " + energy);
            assertTrue(
                error.getMessage()
                    .contains("mean energy"));
            assertTrue(
                error.getMessage()
                    .contains("9223372036854775807"));
        }
    }

    @Test
    void nonFiniteOrNegativeFocusFailsForBothEnergyOverloads() {
        for (double focus : new double[] { -Double.MIN_VALUE, -1, Double.NaN, Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY }) {
            IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> QMDParticles.stack("proton", 1, 1, focus),
                "focus " + focus);
            assertTrue(
                error.getMessage()
                    .contains("focus"));
            assertThrows(
                IllegalArgumentException.class,
                () -> QMDParticles.stack("proton", 1, "1", focus),
                "string energy, focus " + focus);
        }
    }

    @Test
    void invalidNamesProduceActionableLookupErrors() {
        for (String name : new String[] { null, "", "  ", "missing", "other:proton" }) {
            IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> QMDParticles.get(name),
                "name " + name);
            assertTrue(
                error.getMessage()
                    .contains("QMDParticles.names()"));
            assertThrows(IllegalArgumentException.class, () -> QMDParticles.stack(name));
        }
        assertTrue(
            assertThrows(IllegalArgumentException.class, () -> QMDParticles.get("qmd:missing")).getMessage()
                .contains("qmd:missing"));
    }

    @Test
    void catalogAndNameListsAreConsistentImmutableSnapshots() {
        List<Particle> catalog = QMDParticles.catalog();
        List<String> names = QMDParticles.names();
        assertFalse(catalog.isEmpty());
        assertEquals(
            catalog.stream()
                .map(Particle::getName)
                .toList(),
            names);
        for (Particle particle : catalog) {
            assertSame(particle, QMDParticles.get(particle.getName()));
        }
        assertThrows(UnsupportedOperationException.class, catalog::clear);
        assertThrows(UnsupportedOperationException.class, () -> names.add("missing"));

        List<Particle> callerCopy = new ArrayList<>(catalog);
        callerCopy.clear();
        assertEquals(names, QMDParticles.names());
        assertEquals(catalog, QMDParticles.catalog());
    }
}
