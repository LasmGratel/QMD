package lach_01298.qmd.compat.jei.category;

import com.mojang.serialization.Codec;
import lach_01298.qmd.compat.jei.QMDJeiRecipeTypes;
import lach_01298.qmd.client.particle.ParticleStackRenderer;
import lach_01298.qmd.compat.jei.ingredient.ParticleType;
import lach_01298.qmd.compat.jei.recipe.ParticleInfoRecipe;
import lach_01298.qmd.particle.Particle;
import lach_01298.qmd.particle.ParticleStack;
import lach_01298.qmd.particle.Particles;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.ICodecHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;

import java.math.BigDecimal;
import java.util.List;

/** Complete particle properties and a scrollable description, preserving constituents as JEI inputs. */
public final class ParticleInfoCategory implements IRecipeCategory<ParticleInfoRecipe> {

    private final IDrawable icon;

    public ParticleInfoCategory(IGuiHelper guiHelper) {
        Particles.initialize();
        icon = guiHelper.createDrawableIngredient(new ParticleStackRenderer(), new ParticleStack(Particles.alpha));
    }

    @Override
    public RecipeType<ParticleInfoRecipe> getRecipeType() {
        return QMDJeiRecipeTypes.PARTICLE_INFO;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("qmd.gui.jei.category.particle_info");
    }

    @Override
    public int getWidth() {
        return 174;
    }

    @Override
    public int getHeight() {
        return 180;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(ParticleInfoRecipe recipe, mezz.jei.api.gui.ingredient.IRecipeSlotsView slots,
        net.minecraft.client.gui.GuiGraphics graphics, double mouseX, double mouseY) {
        lach_01298.qmd.compat.jei.JeiPanel.of("jei/blank", 0, 0, getWidth(), getHeight())
            .draw(graphics, 0, 0);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ParticleInfoRecipe recipe, IFocusGroup focuses) {
        builder.addOutputSlot(1, 25)
            .setSlotName("particle")
            .setStandardSlotBackground()
            .addIngredient(ParticleType.PARTICLE, recipe.particleStack());
        List<ParticleStack> components = recipe.components();
        for (int index = 0; index < components.size(); index++) {
            builder.addInputSlot(53 + index % 6 * 20, 25 + index / 6 * 20)
                .setSlotName("component_" + index)
                .setStandardSlotBackground()
                .addIngredient(ParticleType.PARTICLE, components.get(index));
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, ParticleInfoRecipe recipe, IFocusGroup focuses) {
        Particle particle = recipe.particle();
        builder.addText(Component.translatable(particle.getUnlocalizedName()), getWidth(), 11)
            .setPosition(0, 0);
        if (particle.hasComponentParticles()) {
            builder.addText(Component.translatable("gui.qmd.jei.particle.components"), 120, 10)
                .setPosition(50, 13);
        }
        int rows = Math.max(
            1,
            (recipe.components()
                .size() + 5) / 6);
        int textTop = 48 + (rows - 1) * 20;
        List<FormattedText> information = List.of(
            Component.translatable("gui.qmd.jei.particle.mass", decimal(particle.getMass()) + " MeV/c²"),
            Component.translatable("gui.qmd.jei.particle.charge", decimal(particle.getCharge())),
            Component.translatable("gui.qmd.jei.particle.spin", decimal(particle.getSpin())),
            Component.translatable(
                "gui.qmd.jei.particle.colour",
                Component.translatable(particle.interactsWithStrong() ? "gui.yes" : "gui.no")),
            Component.translatable(
                "gui.qmd.jei.particle.weak",
                Component.translatable(particle.interactsWithWeak() ? "gui.yes" : "gui.no")),
            Component.translatable(
                "gui.qmd.jei.particle.antiparticle",
                Component.translatable(
                    particle.getAntiParticle()
                        .getUnlocalizedName())),
            Component.empty(),
            Component.translatable("qmd.particle." + particle.getName() + ".desc"));
        builder.addScrollBoxWidget(getWidth(), getHeight() - textTop, 0, textTop)
            .setContents(information);
    }

    @Override
    public ResourceLocation getRegistryName(ParticleInfoRecipe recipe) {
        return ResourceLocation.fromNamespaceAndPath(
            "qmd",
            "particle_info/" + recipe.particle()
                .getName());
    }

    @Override
    public List<ResourceLocation> getSourceRecipeIds(ParticleInfoRecipe recipe) {
        // Catalogue pages are informational entries, not datapack recipes that can be removed by ID.
        return List.of();
    }

    @Override
    public Codec<ParticleInfoRecipe> getCodec(ICodecHelper codecHelper, IRecipeManager recipeManager) {
        return ParticleInfoRecipe.CODEC;
    }

    private static String decimal(double value) {
        return BigDecimal.valueOf(value)
            .stripTrailingZeros()
            .toPlainString();
    }
}
