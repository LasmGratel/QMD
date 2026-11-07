package nclegacy.tile;

import nc.recipe.ingredient.IItemIngredient;
import nc.tile.inventory.ITileInventory;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface IItemProcessorLegacy extends IProcessorLegacy, ITileInventory {
	
	public int getItemInputSize();
	
	public int getItemOutputSize();
	
	public List<ItemStack> getItemInputs();
	
	public List<IItemIngredient> getItemIngredients();
	
	public List<IItemIngredient> getItemProducts();
}
