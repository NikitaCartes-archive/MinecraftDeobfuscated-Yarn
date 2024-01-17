package net.minecraft.network.listener;

import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;

public interface ServerCommonPacketListener extends ServerCookieResponsePacketListener, ServerCrashSafePacketListener {
	void onKeepAlive(KeepAliveC2SPacket packet);

	void onPong(CommonPongC2SPacket packet);

	void onCustomPayload(CustomPayloadC2SPacket packet);

	void onResourcePackStatus(ResourcePackStatusC2SPacket packet);

	void onClientOptions(ClientOptionsC2SPacket packet);
}
