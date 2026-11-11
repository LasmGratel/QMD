package lach_01298.qmd.client.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import lach_01298.qmd.particle.ParticleStack;

import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/** Focusable, narrated instruments. Mouse and keyboard activation use the same menu action. */
public abstract class MachineWidget extends AbstractButton {

    private final Supplier<List<Component>> information;
    private final Runnable action;

    protected MachineWidget(int x, int y, int width, int height, Supplier<List<Component>> information,
        Runnable action) {
        super(x, y, width, height, Component.empty());
        this.information = information;
        this.action = action;
    }

    @Override
    public void onPress() {
        action.run();
    }

    @Override
    protected final void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        var lines = information.get();
        var text = Component.empty();
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) text.append("\n");
            text.append(lines.get(i));
        }
        if (!text.equals(getMessage())) {
            setMessage(text);
            setTooltip(Tooltip.create(text));
        }
        drawContents(graphics);
        if (isFocused()) graphics.renderOutline(getX() - 1, getY() - 1, width + 2, height + 2, 0xFFFFFFFF);
    }

    protected abstract void drawContents(GuiGraphics graphics);

    public static final class Readout extends MachineWidget {

        private final Supplier<List<Component>> lines;
        private final IntSupplier color;

        public Readout(int x, int y, int width, int height, Supplier<List<Component>> lines, IntSupplier color) {
            super(x, y, width, height, lines, () -> {});
            this.lines = lines;
            this.color = color;
        }

        @Override
        protected void drawContents(GuiGraphics graphics) {
            var font = Minecraft.getInstance().font;
            var text = lines.get()
                .getFirst();
            float scale = Math.min(1, width / (float) Math.max(1, font.width(text)));
            graphics.pose()
                .pushPose();
            graphics.pose()
                .translate(getX(), getY(), 0);
            graphics.pose()
                .scale(scale, scale, 1);
            graphics.drawString(font, text, 0, 0, color.getAsInt(), false);
            graphics.pose()
                .popPose();
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }

    public static final class Fluid extends MachineWidget {

        private final Supplier<FluidStack> fluid;
        private final DoubleSupplier capacity;

        public Fluid(int x, int y, int width, int height, Supplier<FluidStack> fluid, DoubleSupplier capacity,
            Runnable action) {
            super(
                x,
                y,
                width,
                height,
                () -> List.of(
                    fluid.get()
                        .isEmpty() ? Component.translatable("gui.qmd.chamber.empty")
                            : fluid.get()
                                .getHoverName(),
                    Component.literal(
                        fluid.get()
                            .getAmount() + " / "
                            + (long) capacity.getAsDouble()
                            + " mB"),
                    Component.translatable("gui.qmd.clear_tank")),
                action);
            this.fluid = fluid;
            this.capacity = capacity;
        }

        public FluidStack fluid() {
            return fluid.get();
        }

        @Override
        protected void drawContents(GuiGraphics graphics) {
            var stack = fluid.get();
            if (stack.isEmpty()) return;
            var extension = IClientFluidTypeExtensions.of(stack.getFluid());
            var sprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(extension.getStillTexture(stack));
            int tint = extension.getTintColor(stack);
            int filled = (int) Math.ceil(height * Math.clamp(stack.getAmount() / capacity.getAsDouble(), 0, 1));
            graphics.enableScissor(getX(), getY() + height - filled, getX() + width, getY() + height);
            graphics.setColor((tint >> 16 & 255) / 255F, (tint >> 8 & 255) / 255F, (tint & 255) / 255F, 1);
            for (int dx = 0; dx < width; dx += 16) for (int dy = 0; dy < filled; dy += 16)
                graphics.blit(getX() + dx, getY() + height - dy - 16, 0, 16, 16, sprite);
            graphics.setColor(1, 1, 1, 1);
            graphics.disableScissor();
        }
    }

    public static final class Particle extends MachineWidget {

        private final Supplier<ParticleStack> particle;

        public Particle(int x, int y, Supplier<ParticleStack> particle) {
            super(x, y, 16, 16, () -> {
                var stack = particle.get();
                return stack == null ? List.of(Component.translatable("gui.qmd.particlestack.empty"))
                    : List.of(
                        Component.translatable(
                            stack.getParticle()
                                .getUnlocalizedName()),
                        Component.translatable("gui.qmd.particlestack.amount", stack.getAmount() + " pu"),
                        Component.translatable("gui.qmd.particlestack.mean_energy", stack.getMeanEnergy() + " keV"),
                        Component.translatable("gui.qmd.particlestack.focus", stack.getFocus()));
            }, () -> {});
            this.particle = particle;
        }

        public ParticleStack stack() {
            return particle.get();
        }

        @Override
        protected void drawContents(GuiGraphics graphics) {
            var stack = particle.get();
            if (stack != null) graphics.blit(
                stack.getParticle()
                    .getTexture(),
                getX(),
                getY(),
                0,
                0,
                16,
                16,
                16,
                16);
        }
    }

    public static final class Gauge extends MachineWidget {

        private final ResourceLocation texture;
        private final int u, v;
        private final DoubleSupplier fraction;
        private final boolean vertical;
        private boolean fromTop;
        private boolean fromRight;

        public Gauge(int x, int y, int width, int height, ResourceLocation texture, int u, int v, boolean vertical,
            DoubleSupplier fraction, Supplier<List<Component>> information) {
            super(x, y, width, height, information, () -> {});
            this.texture = texture;
            this.u = u;
            this.v = v;
            this.vertical = vertical;
            this.fraction = fraction;
        }

        public Gauge fromTop() {
            fromTop = true;
            return this;
        }

        public Gauge fromRight() {
            fromRight = true;
            return this;
        }

        @Override
        protected void drawContents(GuiGraphics graphics) {
            double value = Math.clamp(fraction.getAsDouble(), 0, 1);
            int filled = (int) Math.round((vertical ? height : width) * value);
            int offset = vertical && !fromTop ? height - filled : 0;
            int offsetX = !vertical && fromRight ? width - filled : 0;
            if (filled > 0) graphics.blit(
                texture,
                getX() + offsetX,
                getY() + offset,
                u + offsetX,
                v + offset,
                vertical ? width : filled,
                vertical ? filled : height);
        }
    }
}
