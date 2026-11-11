package lach_01298.qmd.compat.jei;

import lach_01298.qmd.compat.jei.ingredient.ParticleType;
import lach_01298.qmd.client.screen.CreativeParticleSourceScreen;
import mezz.jei.api.gui.builder.IClickableIngredientFactory;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.runtime.IClickableIngredient;

import java.util.Optional;

/** Uses the server-acknowledged preview rather than unsubmitted source editor fields. */
public final class ParticleSourceGuiHandler implements IGuiContainerHandler<CreativeParticleSourceScreen> {

    @Override
    public Optional<? extends IClickableIngredient<?>> getClickableIngredientUnderMouse(
        IClickableIngredientFactory factory, CreativeParticleSourceScreen screen, double mouseX, double mouseY) {
        int x = screen.getGuiLeft() + 134;
        int y = screen.getGuiTop() + 43;
        if (mouseX < x || mouseX >= x + 16 || mouseY < y || mouseY >= y + 16) return Optional.empty();
        var particle = screen.getMenu()
            .getBeam()
            .toStack();
        if (particle == null || particle.isEmpty()) return Optional.empty();
        return factory.createBuilder(ParticleType.PARTICLE, particle)
            .buildWithArea(x, y, 16, 16);
    }
}
