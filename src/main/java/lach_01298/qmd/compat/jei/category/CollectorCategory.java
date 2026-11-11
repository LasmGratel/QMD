package lach_01298.qmd.compat.jei.category;

import lach_01298.qmd.compat.jei.JeiPanel;
import lach_01298.qmd.compat.jei.QMDJeiRecipeTypes;
import lach_01298.qmd.compat.jei.recipe.CollectorWorldInfo;
import lach_01298.qmd.config.MachineViewerStats;
import lach_01298.qmd.recipe.CollectorRecipe;
import lach_01298.qmd.registry.QMDBlocks;
import mezz.jei.api.constants.VanillaTypes;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

/** World collection has no consumed inventory inputs; all predicates remain visible in a scroll box. */
public final class CollectorCategory implements IRecipeCategory<RecipeHolder<CollectorRecipe>> {

    private static final int WIDTH = 176;
    private static final int HEIGHT = 150;
    private final CollectorRecipe.Kind kind;
    private final IDrawable icon;
    private final JeiPanel panel;

    public CollectorCategory(IGuiHelper guiHelper, CollectorRecipe.Kind kind) {
        this.kind = kind;
        panel = JeiPanel.of(
            "jei/" + (kind == CollectorRecipe.Kind.LIQUID ? "liquid_collector" : "atmosphere_collector"),
            0,
            0,
            100,
            kind == CollectorRecipe.Kind.LIQUID ? 60 : 40);
        ItemStack machine = new ItemStack(
            kind == CollectorRecipe.Kind.LIQUID ? QMDBlocks.LIQUID_COLLECTOR.get()
                : QMDBlocks.ATMOSPHERE_COLLECTOR.get());
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, machine);
    }

    @Override
    public RecipeType<RecipeHolder<CollectorRecipe>> getRecipeType() {
        return kind == CollectorRecipe.Kind.LIQUID ? QMDJeiRecipeTypes.LIQUID_COLLECTOR
            : QMDJeiRecipeTypes.ATMOSPHERE_COLLECTOR;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(
            "qmd.gui.jei.category."
                + (kind == CollectorRecipe.Kind.LIQUID ? "liquid_collector" : "atmosphere_collector"));
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return panel.height() + 112;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public boolean isHandled(RecipeHolder<CollectorRecipe> holder) {
        return holder.value()
            .kind() == kind;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<CollectorRecipe> holder, IFocusGroup focuses) {
        CollectorRecipe recipe = holder.value();
        builder.addOutputSlot(112, 8)
            .setFluidRenderer(
                recipe.output()
                    .getAmount(),
                false,
                16,
                16)
            .addIngredient(NeoForgeTypes.FLUID_STACK, recipe.output())
            .addRichTooltipCallback((slot, tooltip) -> {
                tooltip.add(
                    Component.translatable(
                        "gui.qmd.jei.collector.rate",
                        recipe.output()
                            .getAmount()));
                tooltip.add(Component.translatable("gui.qmd.jei.collector.efficiency"));
            });

        // Conditions describe the world and are not consumed. Make item-backed blocks discoverable as catalysts.
        List<ItemStack> worldBlocks = recipe.world()
            .blocks()
            .stream()
            .map(match -> new ItemStack(match.block()))
            .filter(stack -> !stack.isEmpty())
            .toList();
        if (!worldBlocks.isEmpty()) {
            builder.addInvisibleIngredients(RecipeIngredientRole.CATALYST)
                .addItemStacks(worldBlocks);
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<CollectorRecipe> holder,
        IFocusGroup focuses) {
        CollectorRecipe recipe = holder.value();
        builder.addText(
            Component.translatable(
                "gui.qmd.jei.collector.rate",
                recipe.output()
                    .getAmount()),
            142,
            12)
            .setPosition(0, panel.height() + 2);
        builder.addText(Component.translatable("gui.qmd.jei.collector.conditions"), WIDTH, 12)
            .setPosition(0, panel.height() + 33);

        List<FormattedText> lines = new ArrayList<>();
        lines.add(Component.translatable("gui.qmd.jei.collector.efficiency"));
        lines.add(
            Component.translatable(
                "gui.qmd.jei.collector."
                    + (kind == CollectorRecipe.Kind.LIQUID ? "liquid_volume" : "atmosphere_volume")));
        lines.addAll(CollectorWorldInfo.describe(holder));
        builder.addScrollBoxWidget(WIDTH, 63, 0, panel.height() + 49)
            .setContents(lines);
    }

    @Override
    public void draw(RecipeHolder<CollectorRecipe> holder, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX,
        double mouseY) {
        // COMMON config can reload while this layout remains open, independently of recipe reloads.
        panel.draw(graphics, 38, 0);
        MachineViewerStats settings = MachineViewerStats.client();
        int power = kind == CollectorRecipe.Kind.LIQUID ? settings.liquidCollectorPower()
            : settings.atmosphereCollectorPower();
        Component label = Component.translatable("gui.qmd.jei.collector.power", power);
        var font = Minecraft.getInstance().font;
        float scale = Math.min(1F, 142F / Math.max(1, font.width(label)));
        graphics.pose()
            .pushPose();
        graphics.pose()
            .translate(0, panel.height() + 15, 0);
        graphics.pose()
            .scale(scale, scale, 1);
        graphics.drawString(font, label, 0, 0, 0xFF404040, false);
        graphics.pose()
            .popPose();
    }
}
