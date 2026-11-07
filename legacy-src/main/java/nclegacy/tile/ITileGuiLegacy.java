package nclegacy.tile;

import java.util.Set;

import nc.network.tile.TileUpdatePacket;
import nc.tile.ITilePacket;
import net.minecraft.entity.player.*;

public interface ITileGuiLegacy<PACKET extends TileUpdatePacket> extends ITilePacket<PACKET> {
	
	public int getGuiID();
	
	public Set<EntityPlayer> getTileUpdatePacketListeners();
	
	public default void addTileUpdatePacketListener(EntityPlayer player) {
		getTileUpdatePacketListeners().add(player);
		sendTileUpdatePacketToPlayer(player);
	}
	
	public default void removeTileUpdatePacketListener(EntityPlayer player) {
		getTileUpdatePacketListeners().remove(player);
	}
	
	public default void sendTileUpdatePacketToListeners() {
		for (EntityPlayer player : getTileUpdatePacketListeners()) {
			getTileUpdatePacket().sendTo((EntityPlayerMP) player);
		}
	}
}
