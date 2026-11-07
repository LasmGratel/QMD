package lach_01298.qmd.compat.kubejs;

import lach_01298.qmd.particle.ParticleStack;

import java.util.ArrayList;
import java.util.List;

/** Test-only state shared by startup scripts, server script generations and GameTests. */
public final class KubeJSMigrationSmokeBridge {
    private static int startupPasses;
    private static int serverGeneration;
    private static int recipeGeneration;
    private static int managerGeneration;
    private static final List<String> FAILURES = new ArrayList<>();

    private KubeJSMigrationSmokeBridge() {
    }

    public static synchronized void startupPassed(ParticleStack exactEnergyBeam) {
        if (exactEnergyBeam.getMeanEnergy() != Long.MAX_VALUE
                || !exactEnergyBeam.getParticle().getName().equals("photon")) {
            throw new IllegalStateException("Decimal-string particle energy did not retain Long.MAX_VALUE");
        }
        ++startupPasses;
    }

    public static synchronized int serverLoaded() {
        return ++serverGeneration;
    }

    public static synchronized void recipesPassed(int generation) {
        if (generation != serverGeneration) {
            throw new IllegalStateException("Stale server script recipe event: " + generation);
        }
        recipeGeneration = generation;
    }

    public static synchronized void managerPassed(int generation) {
        if (generation != serverGeneration || generation != recipeGeneration) {
            throw new IllegalStateException("Recipe manager verification preceded this generation's recipe event");
        }
        managerGeneration = generation;
    }

    public static synchronized void failure(String message) {
        FAILURES.add(message);
    }

    public static synchronized int startupPasses() {
        return startupPasses;
    }

    public static synchronized int serverGeneration() {
        return serverGeneration;
    }

    public static synchronized int recipeGeneration() {
        return recipeGeneration;
    }

    public static synchronized int managerGeneration() {
        return managerGeneration;
    }

    public static synchronized List<String> failures() {
        return List.copyOf(FAILURES);
    }
}
