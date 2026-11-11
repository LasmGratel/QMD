package lach_01298.qmd.compat.jei;

import lach_01298.qmd.client.screen.widget.MachineWidget;
import lach_01298.qmd.compat.jei.ingredient.ParticleType;
import mezz.jei.api.gui.builder.IClickableIngredientFactory;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.runtime.IClickableIngredient;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/** Uses the actual widget bounds, so JEI cannot drift away from a machine's visible instruments. */
public class MachineGuiHandler<S extends AbstractContainerScreen<?>> implements IGuiContainerHandler<S> {

    private final Function<S, List<MachineWidget>> instruments;

    public MachineGuiHandler(Function<S, List<MachineWidget>> instruments) {
        this.instruments = instruments;
    }

    @Override
    public Optional<? extends IClickableIngredient<?>> getClickableIngredientUnderMouse(
        IClickableIngredientFactory factory, S screen, double mouseX, double mouseY) {
        for (var widget : instruments.apply(screen)) {
            if (!widget.isMouseOver(mouseX, mouseY)) continue;
            if (widget instanceof MachineWidget.Fluid fluid && !fluid.fluid()
                .isEmpty()) return factory
                    .createBuilder(
                        NeoForgeTypes.FLUID_STACK,
                        fluid.fluid()
                            .copy())
                    .buildWithArea(widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight());
            if (widget instanceof MachineWidget.Particle particle && particle.stack() != null)
                return factory.createBuilder(ParticleType.PARTICLE, particle.stack())
                    .buildWithArea(widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight());
        }
        return Optional.empty();
    }
}
