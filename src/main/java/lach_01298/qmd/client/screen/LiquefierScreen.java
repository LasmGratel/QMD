package lach_01298.qmd.client.screen;

import lach_01298.qmd.block.menu.LiquefierMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import java.util.Locale;

/** Original six-line controller and modifier view, using Minecraft's readout and button widgets. */
public final class LiquefierScreen extends AbstractContainerScreen<LiquefierMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.parse("qmd:textures/gui/liquefier_controller.png");
    private final StringWidget[] readouts = new StringWidget[6];
    private StringWidget heading;
    private Button clear;

    public LiquefierScreen(LiquefierMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 100;
    }

    @Override
    protected void init() {
        super.init();
        heading = addRenderableWidget(new StringWidget(leftPos + 8, topPos + 6, 140, 10, title, font).alignCenter());
        for (int i = 0; i < readouts.length; i++) readouts[i] = addRenderableWidget(
            new StringWidget(leftPos + 6, topPos + 25 + 12 * i, 164, 10, Component.empty(), font).alignCenter());
        clear = addRenderableWidget(
            Button.builder(
                Component.literal("×"),
                button -> { if (hasShiftDown()) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0); })
                .bounds(leftPos + 153, topPos + 5, 18, 18)
                .tooltip(Tooltip.create(Component.translatable("gui.qmd.liquefier.clear")))
                .build());
        refresh();
    }

    private static String number(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }

    private Component label(String key, Object... args) {
        return Component.translatable("gui.qmd.liquefier." + key, args);
    }

    private void refresh() {
        var s = menu.state();
        int color = s.active() ? 0x404040 : 0xEE5500;
        heading.setMessage(
            Component.literal(s.x() + "×" + s.y() + "×" + s.z() + " ")
                .append(title));
        heading.setColor(color);
        heading.setTooltip(Tooltip.create(heading.getMessage()));
        clear.active = hasShiftDown();
        Component[] lines = hasShiftDown()
            ? new Component[] { label("gas_in", number(s.gasIn())), label("coolant_out", number(s.coolantOut())),
                label("energy_efficiency", number(100 * s.energyEfficiency())),
                label("temperature", number(s.tempDiff())), label("heat_efficiency", number(100 * s.heatEfficiency())),
                label("pressure_efficiency", number(100 * s.pressureEfficiency())) }
            : new Component[] { label("liquid_out", number(s.liquidOut())),
                label(
                    "coolant_in",
                    number(s.coolantIn()),
                    number(
                        s.heatEfficiency() * s.pressureEfficiency() > 0
                            ? 100 / (s.heatEfficiency() * s.pressureEfficiency())
                            : 0)),
                label(
                    "power",
                    number(s.power()),
                    number(
                        s.energyEfficiency() * s.pressureEfficiency() > 0
                            ? 100 / (s.energyEfficiency() * s.pressureEfficiency())
                            : 0)),
                label("heat", number(s.heat())), label("parts", s.compressors(), s.nozzles()),
                label("pressure", number(s.pressure())) };
        for (int i = 0; i < lines.length; i++) {
            if (!readouts[i].getMessage()
                .equals(lines[i])) {
                readouts[i].setMessage(lines[i]);
                readouts[i].setTooltip(Tooltip.create(lines[i]));
            }
            readouts[i].setColor(color);
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        refresh();
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        graphics.hLine(
            leftPos + 20,
            leftPos + 148,
            topPos + 18,
            0xFF000000 | (menu.state()
                .active() ? 0x404040 : 0xEE5500));
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {}

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Modifier views respond immediately, including while the simulation is paused.
        refresh();
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
