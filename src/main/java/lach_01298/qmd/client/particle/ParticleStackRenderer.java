package lach_01298.qmd.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import lach_01298.qmd.particle.ParticleStack;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class ParticleStackRenderer implements IIngredientRenderer<ParticleStack> {

    @Override
    public void render(GuiGraphics graphics, ParticleStack ingredient) {
        if (ingredient == null || ingredient.getParticle() == null) return;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        graphics.blit(
            ingredient.getParticle()
                .getTexture(),
            0,
            0,
            0,
            0,
            16,
            16,
            16,
            16);
        RenderSystem.disableBlend();
    }

    @Override
    @SuppressWarnings("removal")
    public List<Component> getTooltip(ParticleStack ingredient, TooltipFlag tooltipFlag) {
        return tooltip(ingredient, tooltipFlag);
    }

    @Override
    public List<Component> getTooltip(ParticleStack ingredient, Item.TooltipContext context, @Nullable Player player,
        TooltipFlag tooltipFlag) {
        return tooltip(ingredient, tooltipFlag);
    }

    private static List<Component> tooltip(ParticleStack ingredient, TooltipFlag flag) {
        if (ingredient == null || ingredient.getParticle() == null) {
            return List.of(Component.translatable("gui.qmd.particlestack.empty"));
        }
        List<Component> lines = new ArrayList<>();
        lines.add(
            Component.translatable(
                ingredient.getParticle()
                    .getUnlocalizedName()));
        lines.add(
            Component.translatable("gui.qmd.particlestack.amount", ingredient.getAmount() + " pu")
                .withStyle(ChatFormatting.GRAY));
        // Decimal long text keeps exact energy visible even above JavaScript/double integer precision.
        lines.add(
            Component.translatable("gui.qmd.particlestack.mean_energy", ingredient.getMeanEnergy() + " keV")
                .withStyle(ChatFormatting.GRAY));
        lines.add(
            Component.translatable("gui.qmd.particlestack.focus", Double.toString(ingredient.getFocus()))
                .withStyle(ChatFormatting.GRAY));
        if (flag.isAdvanced()) lines.add(
            Component.literal(
                "qmd:" + ingredient.getParticle()
                    .getName())
                .withStyle(ChatFormatting.DARK_GRAY));
        return lines;
    }
}
