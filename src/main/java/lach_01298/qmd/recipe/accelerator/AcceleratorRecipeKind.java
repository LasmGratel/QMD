package lach_01298.qmd.recipe.accelerator;

/** Stable data-pack identities; accelerator machine topology is represented by AcceleratorKind. */
public enum AcceleratorRecipeKind {

    SOURCE("accelerator_source"),
    COOLING("accelerator_cooling"),
    SPECTROMETER("mass_spectrometer");

    private final String id;

    AcceleratorRecipeKind(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }
}
