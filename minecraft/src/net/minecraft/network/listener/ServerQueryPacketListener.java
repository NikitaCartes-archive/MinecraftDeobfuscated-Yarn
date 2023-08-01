package net.minecraft.network.listener;

import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;

public interface ServerQueryPacketListener extends ServerCrashSafePacketListener {
	@Override
	default NetworkState getState() {
		return NetworkState.STATUS;
	}

	void onPing(QueryPingC2SPacket packet);

	void onRequest(QueryRequestC2SPacket packet);
}
