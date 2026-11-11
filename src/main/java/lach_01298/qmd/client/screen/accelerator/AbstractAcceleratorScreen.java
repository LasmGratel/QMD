package lach_01298.qmd.client.screen.accelerator;

import lach_01298.qmd.block.menu.accelerator.AbstractAcceleratorMenu;
import lach_01298.qmd.client.screen.widget.MachineWidget;
import lach_01298.qmd.particle.ParticleStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.FluidStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Legacy source, spectrometer and controller artwork with native, narrated instruments. */
public abstract class AbstractAcceleratorScreen<M extends AbstractAcceleratorMenu> extends AbstractContainerScreen<M> {

    protected static final ResourceLocation SOURCE = texture("accelerator_source");
    protected final ResourceLocation texture;
    private final int panelHeight;
    private final List<MachineWidget> instruments = new ArrayList<>();
    protected CompoundTag state = new CompoundTag();

    protected AbstractAcceleratorScreen(M menu, Inventory inventory, Component title, String textureName, int width,
        int height, int panelHeight) {
        super(menu, inventory, title);
        texture = texture(textureName);
        imageWidth = width;
        imageHeight = height;
        this.panelHeight = panelHeight;
        inventoryLabelX = menu.inventoryX();
        inventoryLabelY = menu.inventoryY() - 12;
    }

    public abstract lach_01298.qmd.recipe.accelerator.AcceleratorRecipeKind recipeKind();

    public abstract net.minecraft.client.gui.navigation.ScreenRectangle recipeArea();

    protected static ResourceLocation texture(String name) {
        return ResourceLocation.parse("qmd:textures/gui/" + name + ".png");
    }

    public List<MachineWidget> instruments() {
        return List.copyOf(instruments);
    }

    protected final <W extends MachineWidget> W instrument(W widget) {
        instruments.add(widget);
        return addRenderableWidget(widget);
    }

    protected final double ratio(String value, String capacity) {
        return state.getLong(value) / (double) Math.max(1, state.getLong(capacity));
    }

    protected static Component value(String text) {
        return Component.literal(text);
    }

    @Override
    protected final void init() {
        super.init();
        instruments.clear();
        state = menu.snapshot();
        addMachineWidgets();
    }

    protected abstract void addMachineWidgets();

    protected void renderInventory(GuiGraphics graphics) {}

    @Override
    protected final void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        state = menu.snapshot();
        graphics.blit(texture, leftPos, topPos, 0, 0, imageWidth, panelHeight);
        renderInventory(graphics);
    }

    protected final void controllerGauges(int statusY, int statusWidth, int y, int gaugeWidth, int gaugeHeight,
        int energyTextureX, int heatX, int heatTextureX, int coolantX) {
        instrument(
            new MachineWidget.Readout(
                leftPos + 40,
                topPos + statusY,
                statusWidth,
                10,
                () -> List.of(
                    Component.translatable("gui.qmd.container.accelerator.length", state.getInt("beamLength"))
                        .append(" · " + (state.getInt("percentage") < 0 ? "RS" : state.getInt("percentage") + "%")),
                    Component.translatable("gui.qmd.container.accelerator.radius", state.getDouble("radius")),
                    value(state.getString("validation")),
                    Component.translatable("gui.qmd.accelerator.error." + state.getInt("error"))),
                () -> state.getBoolean("operating") ? 0x207020 : 0xA03020));
        instrument(
            new MachineWidget.Gauge(
                leftPos + 8,
                topPos + y,
                gaugeWidth,
                gaugeHeight,
                texture,
                energyTextureX,
                0,
                true,
                () -> ratio("energy", "energyCapacity"),
                () -> List.of(
                    value(state.getLong("energy") + " / " + state.getLong("energyCapacity") + " FE"),
                    value(state.getLong("requiredEnergy") + " FE/t"))));
        instrument(
            new MachineWidget.Gauge(
                leftPos + heatX,
                topPos + y,
                gaugeWidth,
                gaugeHeight,
                texture,
                heatTextureX,
                0,
                true,
                () -> ratio("heat", "heatCapacity"),
                () -> List.of(
                    value(state.getLong("heat") + " / " + state.getLong("heatCapacity") + " H"),
                    value("+" + state.getLong("rawHeating") + " H/t; −" + state.getLong("cooling") + " H/t"),
                    value(
                        String.format(
                            Locale.ROOT,
                            "%.1f / %.1f K",
                            400 * ratio("heat", "heatCapacity"),
                            state.getDouble("maxTemperature"))))));
        tank("coolantInput", coolantX, y, gaugeWidth, gaugeHeight);
    }

    protected final void controls() {
        int controls = topPos + imageHeight - 21;
        addRenderableWidget(
            Button.builder(Component.translatable("gui.qmd.accelerator.redstone"), b -> action(0))
                .bounds(leftPos + 3, controls, 48, 20)
                .tooltip(Tooltip.create(Component.translatable("gui.qmd.accelerator.redstone")))
                .build());
        addRenderableWidget(
            Button.builder(value("−"), b -> action(1))
                .bounds(leftPos + 53, controls, 20, 20)
                .build());
        addRenderableWidget(
            Button.builder(value("+"), b -> action(2))
                .bounds(leftPos + 75, controls, 20, 20)
                .build());
        addRenderableWidget(
            Button.builder(Component.translatable("gui.qmd.accelerator.reverse"), b -> action(3))
                .bounds(leftPos + 97, controls, imageWidth - 100, 20)
                .build());
    }

    protected final void clearMaterial(int x, int y) {
        addRenderableWidget(
            Button.builder(Component.literal("×"), button -> { if (hasShiftDown()) action(4); })
                .bounds(leftPos + x, topPos + y, 18, 18)
                .tooltip(Tooltip.create(Component.translatable("gui.qmd.clear_material")))
                .build());
    }

    protected final void tank(String key, int x, int y, int width, int height) {
        int action = key.equals("sourceTank") ? 20
            : key.equals("coolantInput") ? 10
                : key.equals("coolantOutput") ? 11 : 12 + Integer.parseInt(key.substring("processFluid".length()));
        instrument(
            new MachineWidget.Fluid(
                leftPos + x,
                topPos + y,
                width,
                height,
                () -> fluid(key),
                () -> Math.max(1, state.getInt(key + "Capacity")),
                () -> { if (hasShiftDown()) action(action); }));
    }

    protected final FluidStack fluid(String key) {
        return FluidStack.parseOptional(
            menu.part()
                .getLevel()
                .registryAccess(),
            state.getCompound(key)
                .getCompound("Fluid"));
    }

    protected final ParticleStack beam(int index) {
        return ParticleStack.loadParticleStackFromNBT(
            state.getCompound("ParticleStorage" + index)
                .getCompound("particle_stack"));
    }

    protected final void action(int id) {
        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id);
    }

    protected final void line(GuiGraphics graphics, Component text, int x, int y, int width, int color) {
        float scale = Math.min(1, width / (float) Math.max(1, font.width(text)));
        graphics.pose()
            .pushPose();
        graphics.pose()
            .translate(x, y, 0);
        graphics.pose()
            .scale(scale, scale, 1);
        graphics.drawString(font, text, 0, 0, color, false);
        graphics.pose()
            .popPose();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
