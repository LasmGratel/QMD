package nclegacy.tile;

import nc.recipe.ingredient.IItemIngredient;
import nc.tile.inventory.ITileInventory;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface IItemGeneratorLegacy extends IGeneratorLegacy, ITileInventory {
	
	int getItemInputSize();
	
	int getItemOutputSize();
	
	List<ItemStack> getItemInputs(boolean consumed);
	
	List<IItemIngredient> getItemIngredients();
	
	List<IItemIngredient> getItemProducts();
}
