package lach_01298.qmd.client.screen;

import lach_01298.qmd.block.menu.CreativeParticleSourceMenu;
import lach_01298.qmd.network.ConfigureParticleSourcePayload;

import lach_01298.qmd.particle.Particle;
import lach_01298.qmd.particle.ParticleBeam;
import lach_01298.qmd.particle.Particles;
import lach_01298.qmd.client.screen.widget.MachineWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

/** The legacy four-field source editor, with server-authoritative configuration. */
public final class CreativeParticleSourceScreen extends AbstractContainerScreen<CreativeParticleSourceMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation
        .parse("qmd:textures/gui/creative_particle_source.png");
    private static final String LABEL = "gui.qmd.container.creative_particle_source.";
    private final EditBox[] fields = new EditBox[4];
    private Button setButton;
    private Component validationError;

    public CreativeParticleSourceScreen(CreativeParticleSourceMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 115;
    }

    @Override
    protected void init() {
        String[] values;
        if (fields[0] == null) {
            ParticleBeam beam = menu.getBeam();
            values = new String[] { beam.particleName(), Integer.toString(beam.amount()),
                Long.toString(beam.meanEnergy()), Double.toString(beam.focus()) };
        } else {
            // Window resizing must retain edits that have not been submitted.
            values = new String[] { fields[0].getValue(), fields[1].getValue(), fields[2].getValue(),
                fields[3].getValue() };
        }
        super.init();
        addRenderableWidget(
            new MachineWidget.Particle(
                leftPos + 134,
                topPos + 43,
                () -> menu.getBeam()
                    .toStack()));
        String[] labels = { "particle_name", "particle_amount", "particle_energy", "particle_focus" };
        int[] limits = { 64, 10, 19, 24 };
        for (int index = 0; index < fields.length; ++index) {
            EditBox field = new EditBox(
                font,
                leftPos + 10,
                topPos + 25 + 25 * index,
                100,
                10,
                Component.translatable(LABEL + labels[index]));
            field.setMaxLength(limits[index]);
            field.setValue(values[index]);
            field.setResponder(value -> validateInput());
            fields[index] = addRenderableWidget(field);
        }
        setButton = addRenderableWidget(
            Button.builder(Component.translatable(LABEL + "set"), button -> submit())
                .bounds(leftPos + 118, topPos + 16, 50, 20)
                .build());
        addRenderableWidget(Button.builder(Component.translatable(LABEL + "clear"), button -> {
            send(ParticleBeam.EMPTY);
            fields[0].setValue("");
            fields[1].setValue("0");
            fields[2].setValue("0");
            fields[3].setValue("0");
        })
            .bounds(leftPos + 118, topPos + 68, 50, 20)
            .build());
        setInitialFocus(fields[0]);
        validateInput();
    }

    private ParticleBeam inputBeam() {
        String name = fields[0].getValue()
            .trim();
        Particle particle = Particles.getParticleFromName(name);
        if (particle == null) throw new InvalidInput("invalid_particle");
        int amount;
        long energy;
        double focus;
        try {
            amount = Integer.parseInt(
                fields[1].getValue()
                    .trim());
            if (amount < 0) throw new NumberFormatException();
        } catch (NumberFormatException exception) {
            throw new InvalidInput("invalid_amount");
        }
        try {
            energy = Long.parseLong(
                fields[2].getValue()
                    .trim());
            if (energy < 0) throw new NumberFormatException();
        } catch (NumberFormatException exception) {
            throw new InvalidInput("invalid_energy");
        }
        try {
            focus = Double.parseDouble(
                fields[3].getValue()
                    .trim());
            if (!Double.isFinite(focus) || focus < 0) throw new NumberFormatException();
        } catch (NumberFormatException exception) {
            throw new InvalidInput("invalid_focus");
        }
        return new ParticleBeam(particle.getName(), amount, energy, focus);
    }

    private void validateInput() {
        if (setButton == null || fields[3] == null) return;
        try {
            inputBeam();
            validationError = null;
            setButton.active = true;
            setButton.setTooltip(null);
        } catch (InvalidInput exception) {
            validationError = Component.translatable(LABEL + exception.key);
            setButton.active = false;
            setButton.setTooltip(Tooltip.create(validationError));
        }
    }

    private void submit() {
        validateInput();
        if (setButton.active) send(inputBeam());
    }

    private void send(ParticleBeam beam) {
        PacketDistributor.sendToServer(new ConfigureParticleSourcePayload(menu.containerId, menu.getBlockPos(), beam));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            submit();
            return true;
        }
        if (getFocused() instanceof EditBox field && keyCode != GLFW.GLFW_KEY_ESCAPE && keyCode != GLFW.GLFW_KEY_TAB) {
            // Letter/number bindings must remain text input, rather than closing the inventory.
            field.keyPressed(keyCode, scanCode, modifiers);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.pose()
            .pushPose();
        float titleScale = Math.min(1F, 164F / Math.max(1, font.width(title)));
        graphics.pose()
            .translate(imageWidth / 2F, 4, 0);
        graphics.pose()
            .scale(titleScale, titleScale, 1);
        graphics.drawString(font, title, -font.width(title) / 2, 0, 4210752, false);
        graphics.pose()
            .popPose();
        String[] labels = { "particle_name", "particle_amount", "particle_energy", "particle_focus" };
        for (int index = 0; index < labels.length; ++index) {
            // Long translations fit the original 100-pixel label column.
            Component text = Component.translatable(LABEL + labels[index]);
            float scale = Math.min(1F, 100F / Math.max(1, font.width(text)));
            graphics.pose()
                .pushPose();
            graphics.pose()
                .translate(10, 15 + 25 * index, 0);
            graphics.pose()
                .scale(scale, scale, 1);
            graphics.drawString(font, text, 0, 0, 4210752, false);
            graphics.pose()
                .popPose();
        }
        if (validationError != null) {
            graphics.drawWordWrap(font, Component.translatable(LABEL + "invalid_input"), 118, 92, 50, 0xA00000);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        if (validationError != null && isHovering(118, 92, 50, 20, mouseX, mouseY)) {
            graphics.renderTooltip(font, validationError, mouseX, mouseY);
        }
    }

    private static final class InvalidInput extends IllegalArgumentException {

        private final String key;

        private InvalidInput(String key) {
            this.key = key;
        }
    }
}
