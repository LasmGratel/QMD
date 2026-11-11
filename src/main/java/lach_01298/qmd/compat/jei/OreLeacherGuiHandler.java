package lach_01298.qmd.compat.jei;

import lach_01298.qmd.block.entity.OreLeacherBlockEntity;
import lach_01298.qmd.client.screen.OreLeacherScreen;
import mezz.jei.api.gui.builder.IClickableIngredientFactory;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.runtime.IClickableIngredient;

import java.util.Optional;

/** Makes the actual synchronized acid tanks available to JEI's recipe/use keys. */
public final class OreLeacherGuiHandler implements IGuiContainerHandler<OreLeacherScreen> {

    @Override
    public Optional<? extends IClickableIngredient<?>> getClickableIngredientUnderMouse(
        IClickableIngredientFactory factory, OreLeacherScreen screen, double mouseX, double mouseY) {
        if (!(screen.getMenu()
            .getSlot(0).container instanceof OreLeacherBlockEntity machine)) {
            return Optional.empty();
        }
        int y = screen.getGuiTop() + 42;
        for (int tank = 0; tank < 3; tank++) {
            int x = screen.getGuiLeft() + 36 + 20 * tank;
            if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                var fluid = machine.getTanks()
                    .get(tank)
                    .getFluid();
                if (fluid.isEmpty()) return Optional.empty();
                return factory.createBuilder(NeoForgeTypes.FLUID_STACK, fluid.copy())
                    .buildWithArea(x, y, 16, 16);
            }
        }
        return Optional.empty();
    }
}
