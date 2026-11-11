package lach_01298.qmd.client.screen.chamber;

import lach_01298.qmd.block.menu.chamber.AbstractChamberMenu;
import lach_01298.qmd.client.screen.widget.MachineWidget;
import lach_01298.qmd.particle.ParticleBeam;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

/** Native chamber instruments and controls shared by the six authored controller panels. */
public abstract class AbstractChamberScreen<M extends AbstractChamberMenu> extends AbstractContainerScreen<M> {

    private static final ResourceLocation INVENTORY = ResourceLocation.parse("qmd:textures/gui/accelerator_source.png");
    private final ResourceLocation texture;
    private final List<MachineWidget> instruments = new ArrayList<>();

    protected AbstractChamberScreen(M menu, Inventory inventory, Component title, String texture) {
        super(menu, inventory, title);
        this.texture = ResourceLocation.parse("qmd:textures/gui/" + texture + ".png");
        imageWidth = 176;
        imageHeight = menu.inventoryY() + 104;
        inventoryLabelX = 8;
        inventoryLabelY = menu.inventoryY() - 12;
    }

    public final List<MachineWidget> instruments() {
        return List.copyOf(instruments);
    }

    protected final <W extends MachineWidget> W instrument(W widget) {
        instruments.add(widget);
        return addRenderableWidget(widget);
    }

    protected static Component text(String value) {
        return Component.literal(value);
    }

    private List<Component> progressInfo(int channel) {
        var c = menu.controller();
        long work = c.vacuum() == null ? c.work()
            : c.vacuum()
                .work(channel);
        long required = c.vacuum() == null ? c.requiredWork()
            : c.vacuum()
                .required(channel);
        return List
            .of(text(work + " / " + required + " pu"), text(Math.round(c.efficiency * 100) + "%"), c.validationError());
    }

    public abstract ScreenRectangle recipeArea();

    protected abstract void addMachineWidgets();

    protected abstract void renderConnections(GuiGraphics graphics);

    protected ScreenRectangle statusBounds() {
        return new ScreenRectangle(8, 94, 140, 10);
    }

    protected int clearX() {
        return 150;
    }

    protected int clearY() {
        return 85;
    }

    protected int backgroundX() {
        return 0;
    }

    protected int backgroundWidth() {
        return 176;
    }

    protected boolean embeddedInventory() {
        return false;
    }

    @Override
    protected final void init() {
        super.init();
        instruments.clear();
        addMachineWidgets();
        int controls = topPos + imageHeight - 21;
        var status = statusBounds();
        instrument(
            new MachineWidget.Readout(
                leftPos + status.left(),
                topPos + status.top(),
                status.width(),
                status.height(),
                () -> {
                    var c = menu.controller();
                    Component state = Component.translatable(
                        !c.assembled() ? "gui.qmd.chamber.disassembled"
                            : !c.enabled() ? "gui.qmd.chamber.disabled"
                                : c.active() ? "gui.qmd.chamber.active" : "gui.qmd.chamber.idle");
                    return List.of(
                        state,
                        title,
                        c.validationError(),
                        text(c.work() + " / " + c.requiredWork() + " pu"),
                        text(Math.round(c.efficiency * 100) + "%"));
                },
                () -> menu.controller()
                    .active() ? 0x207020 : 0xA03020));
        addRenderableWidget(
            Button.builder(Component.translatable("gui.qmd.chamber.toggle"), b -> action(0))
                .bounds(leftPos + 2, controls, 84, 20)
                .tooltip(Tooltip.create(Component.translatable("gui.qmd.chamber.toggle")))
                .build());
        if (menu.kind().particleOutputs > 0) addRenderableWidget(
            Button.builder(Component.translatable("gui.qmd.chamber.rotate"), b -> action(1))
                .bounds(leftPos + 88, controls, 86, 20)
                .build());
        addRenderableWidget(
            Button.builder(Component.literal("×"), button -> { if (hasShiftDown()) action(10); })
                .bounds(leftPos + clearX(), topPos + clearY(), 18, 18)
                .tooltip(Tooltip.create(Component.translatable("gui.qmd.clear_material")))
                .build());
    }

    protected final void particle(boolean input, int index, int x, int y) {
        instrument(new MachineWidget.Particle(leftPos + x, topPos + y, () -> {
            List<ParticleBeam> beams = input ? menu.controller()
                .inputBeams()
                : menu.controller()
                    .outputBeams();
            return index < beams.size() ? beams.get(index)
                .toStack() : null;
        }));
    }

    protected final void tank(int index, int x, int y, int width, int height) {
        instrument(
            new MachineWidget.Fluid(
                leftPos + x,
                topPos + y,
                width,
                height,
                () -> menu.controller()
                    .tank(index)
                    .getFluid(),
                () -> menu.controller()
                    .tank(index)
                    .getCapacity(),
                () -> action(hasShiftDown() ? index + 11 : index + 2)));
    }

    protected final void power(int x, int y, int width, int height, int u) {
        instrument(
            new MachineWidget.Gauge(
                leftPos + x,
                topPos + y,
                width,
                height,
                texture,
                u,
                0,
                true,
                () -> menu.controller()
                    .energy()
                    / (double) Math.max(
                        1,
                        menu.controller()
                            .energyCapacity()),
                () -> List.of(
                    text(
                        menu.controller()
                            .energy() + " / "
                            + menu.controller()
                                .energyCapacity()
                            + " FE"),
                    text(menu.controller().requiredPower + " FE/t"))));
    }

    protected final void heat(int x, int y, int width, int height, int u, boolean casing) {
        instrument(
            new MachineWidget.Gauge(
                leftPos + x,
                topPos + y,
                width,
                height,
                texture,
                u,
                0,
                true,
                () -> casing ? (menu.controller()
                    .vacuum()
                    .casingTemperature() - 293) / 2400
                    : menu.controller()
                        .vacuum()
                        .temperature() / 400,
                () -> List.of(
                    text(
                        Math.round(
                            casing ? menu.controller()
                                .vacuum()
                                .casingTemperature()
                                : menu.controller()
                                    .vacuum()
                                    .temperature())
                            + " K"),
                    text(
                        menu.controller()
                            .vacuum()
                            .heating() + " H/t; −"
                            + menu.controller()
                                .vacuum()
                                .casingCooling()
                            + " H/t"))));
    }

    protected final void progress(int x, int y, int width, int height, int u, int v, int channel) {
        var gauge = new MachineWidget.Gauge(
            leftPos + x,
            topPos + y,
            width,
            height,
            texture,
            u,
            v,
            false,
            () -> menu.controller()
                .vacuum() == null
                    ? menu.controller()
                        .work()
                        / (double) Math.max(
                            1,
                            menu.controller()
                                .requiredWork())
                    : menu.controller()
                        .vacuum()
                        .work(channel)
                        / (double) Math.max(
                            1,
                            menu.controller()
                                .vacuum()
                                .required(channel)),
            () -> progressInfo(channel));
        instrument(channel == 1 ? gauge.fromRight() : gauge);
    }

    protected final void action(int id) {
        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id);
    }

    @Override
    protected final void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(texture, leftPos + backgroundX(), topPos, 0, 0, backgroundWidth(), menu.panelHeight());
        if (!embeddedInventory()) graphics.blit(INVENTORY, leftPos, topPos + menu.inventoryY() - 6, 0, 78, 176, 88);
        renderConnections(graphics);
    }

    protected final void connection(GuiGraphics graphics, boolean input, int channel, int x, int y, int u, int v,
        int width, int height) {
        var beams = input ? menu.controller()
            .inputBeams()
            : menu.controller()
                .outputBeams();
        if (channel < beams.size() && beams.get(channel)
            .toStack() != null) graphics.blit(texture, leftPos + x, topPos + y, u, v, width, height);
    }

    protected void renderTitle(GuiGraphics graphics) {
        drawTitle(graphics, 8, 152);
    }

    protected final void drawTitle(GuiGraphics graphics, int x, int width) {
        float scale = Math.min(1, width / (float) Math.max(1, font.width(title)));
        graphics.pose()
            .pushPose();
        graphics.pose()
            .translate(x, 3, 0);
        graphics.pose()
            .scale(scale, scale, 1);
        graphics.drawString(font, title, 0, 0, 0x404040, false);
        graphics.pose()
            .popPose();
    }

    @Override
    protected final void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        renderTitle(graphics);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
