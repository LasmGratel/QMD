package lach_01298.qmd.compat.jei;

import lach_01298.qmd.compat.jei.ingredient.ParticleStackHelper;
import lach_01298.qmd.compat.jei.ingredient.ParticleStackListFactory;
import lach_01298.qmd.compat.jei.ingredient.ParticleType;
import lach_01298.qmd.compat.jei.recipe.ParticleInfoRecipe;
import lach_01298.qmd.particle.Particle;
import lach_01298.qmd.particle.ParticleStack;
import lach_01298.qmd.particle.Particles;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class ParticleIngredientTest {

    private final ParticleStackHelper helper = new ParticleStackHelper();

    @BeforeAll
    static void initialize() {
        Particles.initialize();
    }

    @Test
    void everyCatalogueSpeciesHasOneSearchableIngredientAndInformationRecipe() {
        var ingredients = ParticleStackListFactory.create();
        var recipes = ParticleInfoRecipe.createAll();
        assertEquals(64, ingredients.size());
        assertEquals(64, recipes.size());
        assertEquals(
            64,
            new HashSet<>(
                ingredients.stream()
                    .map(stack -> helper.getUid(stack, UidContext.Ingredient))
                    .toList()).size());
        assertTrue(
            ingredients.stream()
                .allMatch(helper::isValidIngredient));
        assertEquals(
            ingredients.stream()
                .map(ParticleStack::getParticle)
                .toList(),
            recipes.stream()
                .map(ParticleInfoRecipe::particle)
                .toList());
        assertEquals(
            ingredients.stream()
                .map(
                    stack -> stack.getParticle()
                        .getName())
                .sorted()
                .toList(),
            ingredients.stream()
                .map(
                    stack -> stack.getParticle()
                        .getName())
                .toList());
    }

    @Test
    void lookupIdentityMatchesSpeciesAndDoesNotDependOnBeamConditions() {
        var canonical = new ParticleStack(Particles.proton);
        var beam = new ParticleStack(Particles.proton, 700, Long.MAX_VALUE, 123.5);
        assertEquals(helper.getUid(canonical, UidContext.Ingredient), helper.getUid(beam, UidContext.Recipe));
        assertEquals(helper.getGroupingUid(canonical), helper.getGroupingUid(beam));
        assertNotEquals(
            helper.getUid(beam, UidContext.Recipe),
            helper.getUid(new ParticleStack(Particles.antiproton), UidContext.Recipe));
        assertEquals(ResourceLocation.parse("qmd:proton"), helper.getResourceLocation(beam));
        assertEquals(700, helper.getAmount(beam));
    }

    @Test
    void helperCopiesAndNormalizationNeverMutateTheOriginalBeam() {
        var original = new ParticleStack(Particles.proton, 700, Long.MAX_VALUE, 123.5);
        var copy = helper.copyIngredient(original);
        copy.setAmount(2);
        copy.setMeanEnergy(3);
        copy.setFocus(4);
        var normalized = helper.normalizeIngredient(original);
        assertSame(Particles.proton, normalized.getParticle());
        assertEquals(1, normalized.getAmount());
        assertEquals(0, normalized.getMeanEnergy());
        assertEquals(0, normalized.getFocus());
        var limited = helper.copyWithAmount(original, Long.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, limited.getAmount());
        assertEquals(Long.MAX_VALUE, limited.getMeanEnergy());
        assertEquals(123.5, limited.getFocus());
        assertEquals(700, original.getAmount());
        assertEquals(Long.MAX_VALUE, original.getMeanEnergy());
        assertEquals(123.5, original.getFocus());
        assertEquals(
            0,
            helper.copyWithAmount(original, -2)
                .getAmount());
    }

    @Test
    void invalidIngredientsAreRejectedWithoutCrashingDiagnosticOutput() {
        assertFalse(helper.isValidIngredient(null));
        assertFalse(helper.isValidIngredient(new ParticleStack()));
        assertFalse(helper.isValidIngredient(new ParticleStack(Particles.proton, 0)));
        Particle fake = new Particle("proton", ResourceLocation.parse("qmd:fake"), 1, 1, 1);
        assertFalse(helper.isValidIngredient(new ParticleStack(fake)));
        assertEquals("null", helper.getErrorInfo(null));
        assertTrue(
            helper.getErrorInfo(new ParticleStack())
                .contains("particle=null"));
    }

    @Test
    void ingredientCodecPreservesExactBeamAndRejectsEmptyUnknownAndMalformedStates() {
        var original = new ParticleStack(Particles.proton, 700, Long.MAX_VALUE, 123.5);
        var encoded = ParticleType.CODEC.encodeStart(NbtOps.INSTANCE, original)
            .getOrThrow();
        var decoded = ParticleType.CODEC.parse(NbtOps.INSTANCE, encoded)
            .getOrThrow();
        assertSame(Particles.proton, decoded.getParticle());
        assertEquals(700, decoded.getAmount());
        assertEquals(Long.MAX_VALUE, decoded.getMeanEnergy());
        assertEquals(123.5, decoded.getFocus());
        assertTrue(
            ParticleType.CODEC.encodeStart(NbtOps.INSTANCE, new ParticleStack())
                .error()
                .isPresent());
        CompoundTag malformed = ((CompoundTag) encoded).copy();
        malformed.putString("particle", "missing_particle");
        assertTrue(
            ParticleType.CODEC.parse(NbtOps.INSTANCE, malformed)
                .error()
                .isPresent());
        malformed = ((CompoundTag) encoded).copy();
        malformed.putInt("amount", 0);
        assertTrue(
            ParticleType.CODEC.parse(NbtOps.INSTANCE, malformed)
                .error()
                .isPresent());
    }

    @Test
    void particleInformationConstituentsHaveExactCountsAndIndependentMutableStacks() {
        var recipe = new ParticleInfoRecipe(Particles.proton);
        var first = recipe.components();
        assertFalse(first.isEmpty());
        for (var component : first) {
            assertEquals(
                Particles.proton.getComponentParticles()
                    .get(component.getParticle())
                    .intValue(),
                component.getAmount());
            component.setAmount(99);
        }
        var next = recipe.components();
        for (var component : next) {
            assertEquals(
                Particles.proton.getComponentParticles()
                    .get(component.getParticle())
                    .intValue(),
                component.getAmount());
        }
        var output = recipe.particleStack();
        output.setAmount(99);
        assertEquals(
            1,
            recipe.particleStack()
                .getAmount());
    }
}
