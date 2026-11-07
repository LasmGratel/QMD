package nclegacy.tile;

import nc.tile.dummy.IInterfaceable;
import nc.util.NCMath;
import net.minecraft.util.ITickable;

public interface IProcessorLegacy extends ITickable, IInterfaceable {
	
	public void refreshRecipe();
	
	public void refreshActivity();
	
	public void refreshActivityOnProduction();
}
