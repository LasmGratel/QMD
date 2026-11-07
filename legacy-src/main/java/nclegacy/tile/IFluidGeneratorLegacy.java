package nclegacy.tile;

import nc.recipe.ingredient.IFluidIngredient;
import nc.tile.fluid.ITileFluid;
import nc.tile.internal.fluid.Tank;

import java.util.List;

public interface IFluidGeneratorLegacy extends IGeneratorLegacy, ITileFluid {
	
	int getFluidInputSize();
	
	int getFluidOutputSize();
	
	List<Tank> getFluidInputs(boolean consumed);
	
	List<IFluidIngredient> getFluidIngredients();
	
	List<IFluidIngredient> getFluidProducts();
}
