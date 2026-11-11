package lach_01298.qmd.item.equipment;

import lach_01298.qmd.registry.QMDRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/** Legacy dynamic HEV dye recipe; all non-color stack components are retained. */
public final class HevArmorDyeRecipe extends CustomRecipe {

    public HevArmorDyeRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        ItemStack armor = ItemStack.EMPTY;
        boolean hasDye = false;
        for (int index = 0; index < input.size(); index++) {
            ItemStack stack = input.getItem(index);
            if (stack.isEmpty()) continue;
            if (stack.getItem() instanceof HevArmorItem) {
                if (!armor.isEmpty()) return false;
                armor = stack;
            } else if (stack.getItem() instanceof DyeItem) {
                hasDye = true;
            } else {
                return false;
            }
        }
        return !armor.isEmpty() && hasDye;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack armor = ItemStack.EMPTY;
        List<DyeItem> dyes = new ArrayList<>();
        for (int index = 0; index < input.size(); index++) {
            ItemStack stack = input.getItem(index);
            if (stack.isEmpty()) continue;
            if (stack.getItem() instanceof HevArmorItem) {
                if (!armor.isEmpty()) return ItemStack.EMPTY;
                armor = stack.copyWithCount(1);
            } else if (stack.getItem() instanceof DyeItem dye) {
                dyes.add(dye);
            } else {
                return ItemStack.EMPTY;
            }
        }
        if (armor.isEmpty() || dyes.isEmpty()) return ItemStack.EMPTY;
        armor.set(DataComponents.DYED_COLOR, mixColor(armor, dyes));
        return armor;
    }

    private static DyedItemColor mixColor(ItemStack armor, List<DyeItem> dyes) {
        int red = 0;
        int green = 0;
        int blue = 0;
        int brightness = 0;
        int samples = 0;
        DyedItemColor existing = armor.get(DataComponents.DYED_COLOR);
        if (existing != null) {
            int color = existing.rgb();
            int r = FastColor.ARGB32.red(color);
            int g = FastColor.ARGB32.green(color);
            int b = FastColor.ARGB32.blue(color);
            red += r;
            green += g;
            blue += b;
            brightness += Math.max(r, Math.max(g, b));
            samples++;
        }
        for (DyeItem dye : dyes) {
            int color = dye.getDyeColor()
                .getTextureDiffuseColor();
            int r = FastColor.ARGB32.red(color);
            int g = FastColor.ARGB32.green(color);
            int b = FastColor.ARGB32.blue(color);
            red += r;
            green += g;
            blue += b;
            brightness += Math.max(r, Math.max(g, b));
            samples++;
        }
        int averageRed = red / samples;
        int averageGreen = green / samples;
        int averageBlue = blue / samples;
        float averageBrightness = (float) brightness / samples;
        float maximum = Math.max(averageRed, Math.max(averageGreen, averageBlue));
        averageRed = (int) (averageRed * averageBrightness / maximum);
        averageGreen = (int) (averageGreen * averageBrightness / maximum);
        averageBlue = (int) (averageBlue * averageBrightness / maximum);
        int rgb = FastColor.ARGB32.color(0, averageRed, averageGreen, averageBlue);
        return new DyedItemColor(rgb, existing == null || existing.showInTooltip());
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return QMDRecipes.ARMOUR_DYE_SERIALIZER.get();
    }
}
