package lach_01298.qmd.compat.jei;

import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

/** A crop of authored legacy GUI art; animation is owned by JEI's widget lifecycle. */
public record JeiPanel(ResourceLocation texture, int u, int v, int width, int height) {

    public static JeiPanel of(String name, int u, int v, int width, int height) {
        return new JeiPanel(ResourceLocation.parse("qmd:textures/gui/" + name + ".png"), u, v, width, height);
    }

    public void draw(GuiGraphics graphics, int x, int y) {
        graphics.blit(texture, x, y, u, v, width, height);
    }

    public void progress(IGuiHelper helper, IRecipeExtrasBuilder builder, int u, int v, int width, int height, int x,
        int y, int ticks) {
        var drawable = helper.createDrawable(texture, u, v, width, height);
        builder
            .addDrawableWidget(
                helper.createAnimatedDrawable(drawable, ticks, IDrawableAnimated.StartDirection.LEFT, false))
            .setPosition(x, y);
    }
}
