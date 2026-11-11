package lach_01298.qmd.compat.jei.category.chamber;

import lach_01298.qmd.compat.jei.JeiPanel;
import lach_01298.qmd.compat.jei.OreLeacherDisplay;
import lach_01298.qmd.compat.jei.ingredient.ParticleType;
import lach_01298.qmd.multiblock.ChamberPart;
import lach_01298.qmd.recipe.chamber.AbstractChamberRecipe;
import lach_01298.qmd.recipe.chamber.ChamberKind;
import lach_01298.qmd.registry.QMDBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

/** Shared rendering of typed physical channels; concrete categories own their authored diagrams. */
public abstract class AbstractChamberCategory<R extends AbstractChamberRecipe>
    implements IRecipeCategory<RecipeHolder<R>> {

    protected record Point(int x, int y) {}

    private final ChamberKind kind;
    private final RecipeType<RecipeHolder<R>> recipeType;
    private final IDrawable icon;
    protected final IGuiHelper gui;
    protected final JeiPanel panel;
    private final int detailsY;

    protected AbstractChamberCategory(IGuiHelper gui, ChamberKind kind, RecipeType<RecipeHolder<R>> recipeType,
        ChamberPart controller, JeiPanel panel, int detailsY) {
        this.kind = kind;
        this.recipeType = recipeType;
        this.gui = gui;
        this.panel = panel;
        this.detailsY = detailsY;
        icon = gui.createDrawableItemLike(
            QMDBlocks.CHAMBER_PARTS.get(controller)
                .get());
    }

    @Override
    public final RecipeType<RecipeHolder<R>> getRecipeType() {
        return recipeType;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("qmd.gui.jei.category." + kind.id());
    }

    @Override
    public int getWidth() {
        return 174;
    }

    @Override
    public int getHeight() {
        return detailsY + 56;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    protected final int offset() {
        return (getWidth() - panel.width()) / 2;
    }

    protected List<Point> particleInputs() {
        return List.of();
    }

    protected List<Point> particleOutputs() {
        return List.of();
    }

    protected List<Point> itemInputs() {
        return List.of();
    }

    protected List<Point> itemOutputs() {
        return List.of();
    }

    protected List<Point> fluidInputs() {
        return List.of();
    }

    protected List<Point> fluidOutputs() {
        return List.of();
    }

    protected int fluidSize(boolean input, int index) {
        return 16;
    }

    protected int workScale() {
        return 100;
    }

    protected void addRecipeDetails(List<FormattedText> lines, R recipe) {
        lines.add(Component.translatable("jei.qmd.chamber.cross_section", recipe.crossSection()));
        lines.add(Component.literal("Q: " + recipe.energyReleased() + " keV"));
    }

    protected void addAnimations(IRecipeExtrasBuilder builder, R recipe) {}

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<R> holder, IFocusGroup focuses) {
        var recipe = holder.value();
        for (int i = 0; i < recipe.particleInputs()
            .size(); i++) {
            var ingredient = recipe.particleInputs()
                .get(i);
            if (ingredient.isEmpty()) continue;
            Point p = particleInputs().get(i);
            int scale = workScale();
            long required = java.math.BigInteger.valueOf(ingredient.amount())
                .multiply(java.math.BigInteger.valueOf(scale))
                .divide(java.math.BigInteger.valueOf(100))
                .min(java.math.BigInteger.valueOf(Long.MAX_VALUE))
                .longValue();
            var alternatives = ingredient.alternatives()
                .stream()
                .map(stack -> {
                    stack.setAmount((int) Math.min(Integer.MAX_VALUE, Math.max(1, required)));
                    return stack;
                })
                .toList();
            builder.addSlot(RecipeIngredientRole.INPUT, offset() + p.x, p.y)
                .setSlotName("particle_input_" + i)
                .addIngredients(ParticleType.PARTICLE, alternatives)
                .addRichTooltipCallback((slot, tip) -> {
                    tip.add(
                        Component.translatable(
                            "jei.qmd.chamber.energy_range",
                            ingredient.minEnergy(),
                            ingredient.maxEnergy()));
                    tip.add(Component.translatable("jei.qmd.chamber.work", Math.max(1, required)));
                });
        }
        for (int i = 0; i < recipe.particleOutputs()
            .size(); i++) {
            var stack = recipe.particleOutputs()
                .get(i)
                .toStack();
            if (stack == null) continue;
            Point p = particleOutputs().get(i);
            builder.addOutputSlot(offset() + p.x, p.y)
                .setSlotName("particle_output_" + i)
                .addIngredient(ParticleType.PARTICLE, stack)
                .addRichTooltipCallback(
                    (slot, tip) -> tip.add(Component.translatable("jei.qmd.chamber.product_ratio")));
        }
        for (int i = 0; i < recipe.getItemIngredients()
            .size(); i++) {
            var ingredient = recipe.getItemIngredients()
                .get(i);
            if (ingredient.isEmpty()) continue;
            Point p = itemInputs().get(i);
            builder.addInputSlot(offset() + p.x, p.y)
                .setSlotName("item_input_" + i)
                .addItemStacks(OreLeacherDisplay.itemAlternatives(ingredient));
        }
        for (int i = 0; i < recipe.getItemProducts()
            .size(); i++) {
            var product = recipe.getItemProducts()
                .get(i);
            if (product.isEmpty()) continue;
            Point p = itemOutputs().get(i);
            builder.addOutputSlot(offset() + p.x, p.y)
                .setSlotName("item_output_" + i)
                .addItemStacks(OreLeacherDisplay.itemAlternatives(product))
                .addRichTooltipCallback((slot, tip) -> {
                    if (product.chancePercent() < 100) {
                        tip.add(
                            Component.translatable(
                                "jei.qmd.chance_output",
                                product.minStackSize(),
                                product.count(),
                                OreLeacherDisplay.meanOutput(product)));
                        tip.add(Component.translatable("jei.qmd.chance_per_item", product.chancePercent()));
                    }
                });
        }
        for (boolean input : new boolean[] { true, false }) {
            var fluids = input ? recipe.getFluidIngredients() : recipe.getFluidProducts();
            for (int i = 0; i < fluids.size(); i++) {
                var fluid = fluids.get(i);
                if (fluid.isEmpty()) continue;
                Point p = (input ? fluidInputs() : fluidOutputs()).get(i);
                int size = fluidSize(input, i);
                builder.addSlot(input ? RecipeIngredientRole.INPUT : RecipeIngredientRole.OUTPUT, offset() + p.x, p.y)
                    .setSlotName("fluid_" + (input ? "input_" : "output_") + i)
                    .setFluidRenderer(fluid.amount(), false, size, size)
                    .addIngredients(NeoForgeTypes.FLUID_STACK, OreLeacherDisplay.fluidAlternatives(fluid));
            }
        }
    }

    @Override
    public void draw(RecipeHolder<R> holder, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX,
        double mouseY) {
        panel.draw(graphics, offset(), 0);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<R> holder, IFocusGroup focuses) {
        var recipe = holder.value();
        List<FormattedText> lines = new ArrayList<>();
        addRecipeDetails(lines, recipe);
        if (recipe.heat() > 0) lines.add(Component.translatable("jei.qmd.chamber.heat", recipe.heat()));
        if (recipe.radiation() > 0) lines.add(Component.translatable("jei.qmd.base_radiation", recipe.radiation()));
        lines.add(Component.translatable("jei.qmd.chamber.efficiency"));
        builder.addScrollBoxWidget(getWidth(), getHeight() - detailsY, 0, detailsY)
            .setContents(lines);
        addAnimations(builder, recipe);
    }
}
