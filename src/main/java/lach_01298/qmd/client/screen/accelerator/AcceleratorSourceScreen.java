package lach_01298.qmd.client.screen.accelerator;

import lach_01298.qmd.block.menu.accelerator.AcceleratorSourceMenu;
import lach_01298.qmd.recipe.accelerator.AcceleratorRecipeKind;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class AcceleratorSourceScreen extends AbstractAcceleratorScreen<AcceleratorSourceMenu> {

    public AcceleratorSourceScreen(AcceleratorSourceMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, "accelerator_source", 176, 166, 166);
    }

    @Override
    public AcceleratorRecipeKind recipeKind() {
        return AcceleratorRecipeKind.SOURCE;
    }

    @Override
    public ScreenRectangle recipeArea() {
        return new ScreenRectangle(70, 24, 85, 40);
    }

    @Override
    protected void addMachineWidgets() {
        tank("sourceTank", 80, 43, 16, 16);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        line(graphics, title, 8, 6, 160, 0x404040);
    }
}
