package net.minecraft.network.listener;

import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.common.PlayPingS2CPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.common.SynchronizeTagsS2CPacket;

public interface ClientCommonPacketListener extends ClientPacketListener {
	void onKeepAlive(KeepAliveS2CPacket packet);

	void onPlayPing(PlayPingS2CPacket packet);

	void onCustomPayload(CustomPayloadS2CPacket packet);

	void onDisconnect(DisconnectS2CPacket packet);

	void onResourcePackSend(ResourcePackSendS2CPacket packet);

	void onSynchronizeTags(SynchronizeTagsS2CPacket packet);
}
