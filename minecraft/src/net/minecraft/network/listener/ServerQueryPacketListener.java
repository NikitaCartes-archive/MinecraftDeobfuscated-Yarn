package net.minecraft.network.listener;

import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;

public interface ServerQueryPacketListener extends ServerCrashSafePacketListener, ServerQueryPingPacketListener {
	@Override
	default NetworkState getState() {
		return NetworkState.STATUS;
	}

	void onRequest(QueryRequestC2SPacket packet);
}
