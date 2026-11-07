package nclegacy.tile;

import nc.network.tile.TileUpdatePacket;
import nc.tile.ITileGui;

public interface ITileSideConfigGuiLegacy<PACKET extends TileUpdatePacket> extends ITileGuiLegacy<PACKET> {
	
	public int getSideConfigXOffset();
	
	public int getSideConfigYOffset();
}
