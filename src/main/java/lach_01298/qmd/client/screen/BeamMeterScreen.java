package lach_01298.qmd.client.screen;

import lach_01298.qmd.client.screen.widget.MachineWidget;
import lach_01298.qmd.menu.BeamMeterMenu;
import lach_01298.qmd.particle.ParticleBeam;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** Server-authored measurements displayed as native readouts on QMD's authored instrument panel. */
public final class BeamMeterScreen extends AbstractContainerScreen<BeamMeterMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.parse("qmd:textures/gui/liquefier_controller.png");
    private final StringWidget[] lines = new StringWidget[7];

    public BeamMeterScreen(BeamMeterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 100;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new StringWidget(leftPos + 8, topPos + 6, 140, 10, title, font).alignLeft());
        addRenderableWidget(new MachineWidget.Particle(leftPos + 153, topPos + 5, () -> {
            var r = menu.reading();
            return r.hasBeam() ? new ParticleBeam(r.particleName(), r.amount(), r.meanEnergy(), r.focus()).toStack()
                : null;
        }));
        for (int i = 0; i < lines.length; i++) lines[i] = addRenderableWidget(
            new StringWidget(leftPos + 8, topPos + 20 + 11 * i, 160, 10, Component.empty(), font).alignLeft());
        refresh();
    }

    private void line(int index, Component value) {
        lines[index].setMessage(value);
        lines[index].setColor(0x404040);
        lines[index].setTooltip(Tooltip.create(value));
    }

    private void refresh() {
        for (int i = 0; i < lines.length; i++) line(i, Component.empty());
        var r = menu.reading();
        switch (r.kind()) {
            case EMPTY -> line(0, Component.translatable("gui.qmd.particlestack.empty"));
            case ACCELERATOR_PORT -> {
                line(0, Component.translatable("gui.qmd.beam_meter.accelerator_port"));
                line(
                    1,
                    Component.translatable(
                        "gui.qmd.beam_meter.port_mode",
                        Component.translatable(
                            "gui.qmd.beam_meter.mode." + r.portMode()
                                .name()
                                .toLowerCase(Locale.ROOT))));
            }
            case CHAMBER_PORT -> {
                line(0, Component.translatable("gui.qmd.beam_meter.chamber_port"));
                line(1, Component.translatable("gui.qmd.beam_meter.output_channel", r.outputChannel()));
            }
            case BEAM -> {
                line(0, Component.translatable("qmd.particle." + r.particleName() + ".name"));
                line(1, Component.translatable("gui.qmd.particlestack.amount", formatSi(r.amount(), "pu")));
                line(
                    2,
                    Component
                        .translatable("gui.qmd.particlestack.mean_energy", formatSi(r.meanEnergy() * 1000D, "eV")));
                line(3, Component.translatable("gui.qmd.particlestack.focus", decimal(r.focus(), 4)));
                line(4, Component.translatable("gui.qmd.particlestack.focus_loss", decimal(r.focusLossPerBlock(), 4)));
                line(5, Component.translatable("gui.qmd.particlestack.travel_distance", distance(r.travelDistance())));
            }
        }
        if (r.beamlineLength() > 0)
            line(6, Component.translatable("gui.qmd.beam_meter.beamline_length", r.beamlineLength()));
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        refresh();
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {}

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    private static String distance(double value) {
        if (Double.isInfinite(value)) return "Infinity";
        if (Double.isNaN(value)) return "Unavailable";
        return decimal(value, 1);
    }

    private static String decimal(double value, int places) {
        DecimalFormat format = new DecimalFormat(
            places == 1 ? "#.#" : "#.####",
            DecimalFormatSymbols.getInstance(Locale.ROOT));
        return format.format(value);
    }

    private static String formatSi(double value, String unit) {
        if (!Double.isFinite(value)) return value > 0 ? "Infinity " + unit : "Unavailable";
        if (value == 0) return "0 " + unit;
        String[] prefixes = { "", "k", "M", "G", "T", "P", "E", "Z", "Y", "R", "Q" };
        int exponent = Math.clamp((int) Math.floor(Math.log10(Math.abs(value)) / 3D), 0, prefixes.length - 1);
        double scaled = value / Math.pow(1_000D, exponent);
        int places = Math.abs(scaled) >= 100 ? 0 : Math.abs(scaled) >= 10 ? 1 : 2;
        String pattern = places == 0 ? "#" : places == 1 ? "#.0" : "#.00";
        DecimalFormat format = new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(Locale.ROOT));
        return format.format(scaled) + " " + prefixes[exponent] + unit;
    }
}
