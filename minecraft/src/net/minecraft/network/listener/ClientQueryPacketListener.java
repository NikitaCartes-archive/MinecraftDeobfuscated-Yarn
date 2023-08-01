package net.minecraft.network.listener;

import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;

public interface ClientQueryPacketListener extends ClientPacketListener {
	@Override
	default NetworkState getState() {
		return NetworkState.STATUS;
	}

	void onResponse(QueryResponseS2CPacket packet);

	void onPong(QueryPongS2CPacket packet);
}
