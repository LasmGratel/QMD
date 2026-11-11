package lach_01298.qmd.client.screen;

import lach_01298.qmd.block.menu.IrradiatorMenu;
import lach_01298.qmd.item.ParticleReservoirItem;
import lach_01298.qmd.client.screen.widget.MachineWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class IrradiatorScreen extends AbstractContainerScreen<IrradiatorMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.parse("qmd:textures/gui/irradiator.png");

    public IrradiatorScreen(IrradiatorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 166;
        inventoryLabelY = 72;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(
            new MachineWidget.Gauge(
                leftPos + 62,
                topPos + 57,
                52,
                10,
                TEXTURE,
                176,
                0,
                false,
                () -> menu.progressScaled(10000) / 10000D,
                () -> java.util.List
                    .of(Component.translatable("gui.qmd.irradiator.progress", menu.progressPercent(), menu.speed()))));
        addRenderableWidget(
            new MachineWidget.Gauge(
                leftPos + 68,
                topPos + 38,
                40,
                19,
                TEXTURE,
                176,
                10,
                true,
                () -> menu.progressScaled(10000) / 10000D,
                () -> java.util.List
                    .of(Component.translatable("gui.qmd.irradiator.progress", menu.progressPercent(), menu.speed())))
                        .fromTop());
        addRenderableWidget(
            Button.builder(Component.literal("⚙"), button -> minecraft.setScreen(new IrradiatorSorptionScreen(this)))
                .bounds(leftPos + 6, topPos + 40, 18, 18)
                .tooltip(Tooltip.create(Component.translatable("gui.qmd.irradiator.side_config")))
                .build());
        addRenderableWidget(
            Button.builder(Component.literal("R"), button -> action(0))
                .bounds(leftPos + 6, topPos + 60, 18, 18)
                .build());
    }

    public void action(int action) {
        if (minecraft != null && minecraft.gameMode != null)
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, action);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        graphics.fill(
            leftPos + 7,
            topPos + 78,
            leftPos + 23,
            topPos + 80,
            menu.redstoneControl() ? 0xFFFF5555 : 0xFF555555);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        if (isHovering(6, 40, 18, 18, mouseX, mouseY))
            graphics.renderTooltip(font, Component.translatable("gui.qmd.irradiator.side_config"), mouseX, mouseY);
        else if (isHovering(6, 60, 18, 18, mouseX, mouseY)) graphics.renderTooltip(
            font,
            Component.translatable(
                "gui.qmd.irradiator.redstone",
                Component.translatable(menu.redstoneControl() ? "options.on" : "options.off")),
            mouseX,
            mouseY);
        else if (isHovering(62, 38, 52, 29, mouseX, mouseY)) graphics.renderTooltip(
            font,
            Component.translatable("gui.qmd.irradiator.progress", menu.progressPercent(), menu.speed()),
            mouseX,
            mouseY);
        else if (isHovering(79, 20, 18, 18, mouseX, mouseY)) graphics.renderTooltip(
            font,
            Component.translatable(
                "tooltip.qmd.particle_reservoir",
                ParticleReservoirItem.stored(
                    menu.getSlot(1)
                        .getItem()),
                menu.getSlot(1)
                    .getItem()
                    .getItem() instanceof ParticleReservoirItem item ? item.capacity() : 0),
            mouseX,
            mouseY);
    }
}
