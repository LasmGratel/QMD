package lach_01298.qmd.recipe;

import lach_01298.qmd.particle.ParticleStack;
import lach_01298.qmd.particle.Particles;
import lach_01298.qmd.recipe.ingredient.ParticleIngredient;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParticleIngredientTest {

    @BeforeAll
    static void initialize() {
        Particles.initialize();
    }

    @Test
    void rateDoesNotPreventAccumulatingReactionWorkButBoundariesStillApply() {
        var input = ParticleIngredient.of(new ParticleStack(Particles.proton, 1000000, 400, 2), 900);
        assertTrue(input.matches(new ParticleStack(Particles.proton, 1, 400, 2), false));
        assertTrue(input.matches(new ParticleStack(Particles.proton, 1000000, 900, 2), true));
        assertFalse(input.matches(new ParticleStack(Particles.proton, 999999, 900, 2), true));
        assertFalse(input.matches(new ParticleStack(Particles.proton, 1000000, 901, 2), false));
        assertFalse(input.matches(new ParticleStack(Particles.proton, 1000000, 399, 2), false));
        assertFalse(input.matches(new ParticleStack(Particles.proton, 1000000, 400, 1), false));
    }

    @Test
    void displayMutationCannotChangeRecipeRequirements() {
        var input = ParticleIngredient.of(new ParticleStack(Particles.proton, 5, 8, 2), 12);
        var stack = input.alternatives()
            .getFirst();
        stack.setAmount(1000);
        stack.setMeanEnergy(1000);
        assertEquals(
            5,
            input.alternatives()
                .getFirst()
                .getAmount());
        assertEquals(
            8,
            input.alternatives()
                .getFirst()
                .getMeanEnergy());
        assertThrows(
            UnsupportedOperationException.class,
            () -> input.particles()
                .clear());
    }

    @Test
    void malformedOrAmbiguousAlternativesAreRejected() {
        var proton = ResourceLocation.parse("qmd:proton");
        assertThrows(IllegalArgumentException.class, () -> new ParticleIngredient(List.of(proton, proton), 1, 0, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> new ParticleIngredient(List.of(proton), 1, 2, 1, 0));
        assertThrows(
            IllegalArgumentException.class,
            () -> new ParticleIngredient(List.of(proton), 1, 0, 1, Double.NaN));
        assertThrows(
            IllegalArgumentException.class,
            () -> new ParticleIngredient(List.of(ResourceLocation.parse("qmd:unknown")), 1, 0, 1, 0));
        assertTrue(ParticleIngredient.EMPTY.matches(null, false));
        assertFalse(ParticleIngredient.EMPTY.matches(new ParticleStack(Particles.proton), false));
    }
}
