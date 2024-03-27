package net.minecraft.network.packet.s2c.config;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientConfigurationPacketListener;
import net.minecraft.network.packet.ConfigPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

public class ResetChatS2CPacket implements Packet<ClientConfigurationPacketListener> {
	public static final ResetChatS2CPacket INSTANCE = new ResetChatS2CPacket();
	public static final PacketCodec<ByteBuf, ResetChatS2CPacket> CODEC = PacketCodec.unit(INSTANCE);

	private ResetChatS2CPacket() {
	}

	@Override
	public PacketType<ResetChatS2CPacket> getPacketId() {
		return ConfigPackets.RESET_CHAT;
	}

	public void apply(ClientConfigurationPacketListener clientConfigurationPacketListener) {
		clientConfigurationPacketListener.onResetChat(this);
	}
}
