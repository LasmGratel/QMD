package nclegacy.jei;

import mezz.jei.api.IGuiHelper;
import nc.recipe.BasicRecipeHandler;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface IJEIHandlerLegacy<WRAPPER extends JEIBasicRecipeWrapperLegacy<WRAPPER>> {
	
	public JEIBasicCategoryLegacy<?> getCategory(IGuiHelper guiHelper);
	
	public BasicRecipeHandler getRecipeHandler();
	
	public Class<WRAPPER> getRecipeWrapperClass();
	
	public List<WRAPPER> getJEIRecipes(IGuiHelper guiHelper);
	
	public String getUid();
	
	public boolean getEnabled();
	
	public List<ItemStack> getCrafters();
	
	public String getTextureName();
}
