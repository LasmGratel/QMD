package nclegacy.container;

import nc.tile.ITileGui;
import nclegacy.tile.ITileGuiLegacy;

public class ContainerSorptionsLegacy<T extends ITileGuiLegacy<?>> extends ContainerTileLegacy<T> {
	
	public ContainerSorptionsLegacy(T tile) {
		super(tile);
	}
}
