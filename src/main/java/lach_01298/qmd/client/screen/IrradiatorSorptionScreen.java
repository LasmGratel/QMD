package lach_01298.qmd.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

/** A complete six-face, per-slot side-configuration screen; the open server menu owns every change. */
public final class IrradiatorSorptionScreen extends Screen {

    private final IrradiatorScreen parent;
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("qmd:textures/gui/irradiator.png");
    private final Button[][] sorptions = new Button[6][3];
    private Button output;

    public IrradiatorSorptionScreen(IrradiatorScreen parent) {
        super(Component.translatable("gui.qmd.irradiator.side_config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int left = (width - 176) / 2, top = (height - 226) / 2;
        for (Direction face : Direction.values()) for (int slot = 0; slot < 3; slot++) {
            int action = 10 + face.ordinal() * 3 + slot;
            sorptions[face.ordinal()][slot] = addRenderableWidget(
                Button.builder(Component.empty(), button -> parent.action(action))
                    .bounds(left + 31 + slot * 47, top + 60 + face.ordinal() * 21, 45, 20)
                    .tooltip(
                        Tooltip.create(
                            Component.translatable("gui.qmd.irradiator.face." + face.getName())
                                .append(" · ")
                                .append(Component.translatable("gui.qmd.irradiator.slot." + slot))))
                    .build());
        }
        output = addRenderableWidget(
            Button.builder(Component.empty(), button -> parent.action(1))
                .bounds(left + 2, top + 188, 172, 20)
                .build());
        addRenderableWidget(
            Button.builder(Component.translatable("gui.done"), button -> onClose())
                .bounds(left + 38, top + 210, 100, 20)
                .build());
        refreshLabels();
    }

    private void refreshLabels() {
        for (Direction face : Direction.values())
            for (int slot = 0; slot < 3; slot++) sorptions[face.ordinal()][slot].setMessage(
                Component.translatable(
                    "gui.qmd.irradiator.sorption." + parent.getMenu()
                        .sorption(face, slot)
                        .name()
                        .toLowerCase(java.util.Locale.ROOT)));
        output.setMessage(
            Component.translatable(
                "gui.qmd.irradiator.output_mode",
                Component.translatable(
                    "gui.qmd.irradiator.output." + parent.getMenu()
                        .outputMode()
                        .name()
                        .toLowerCase(java.util.Locale.ROOT))));
    }

    @Override
    public void tick() {
        if (minecraft.player == null || minecraft.player.containerMenu != parent.getMenu()) {
            minecraft.setScreen(null);
            return;
        }
        refreshLabels();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        int left = (width - 176) / 2, top = (height - 226) / 2;
        graphics.blit(TEXTURE, left, top, 0, 0, 176, 166);
        graphics.drawCenteredString(font, title, width / 2, top + 5, 0x404040);
        for (int slot = 0; slot < 3; slot++) graphics.drawCenteredString(
            font,
            Component.translatable("gui.qmd.irradiator.slot." + slot),
            left + 53 + slot * 47,
            top + 47,
            0x404040);
        for (int slot = 0; slot < 3; slot++) graphics.renderItem(
            parent.getMenu()
                .getSlot(slot)
                .getItem(),
            left + 45 + slot * 47,
            top + 24);
        for (Direction face : Direction.values()) graphics.drawString(
            font,
            font.plainSubstrByWidth(
                Component.translatable("gui.qmd.irradiator.face." + face.getName())
                    .getString(),
                26),
            left + 3,
            top + 66 + face.ordinal() * 21,
            0x404040);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
