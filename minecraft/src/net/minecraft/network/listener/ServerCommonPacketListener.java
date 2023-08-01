package net.minecraft.network.listener;

import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.common.PlayPongC2SPacket;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;

public interface ServerCommonPacketListener extends ServerCrashSafePacketListener {
	void onKeepAlive(KeepAliveC2SPacket packet);

	void onPlayPong(PlayPongC2SPacket packet);

	void onCustomPayload(CustomPayloadC2SPacket packet);

	void onResourcePackStatus(ResourcePackStatusC2SPacket packet);
}
