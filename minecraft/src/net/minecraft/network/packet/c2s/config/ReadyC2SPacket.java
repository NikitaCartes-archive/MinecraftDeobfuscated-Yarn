package net.minecraft.network.packet.c2s.config;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerConfigurationPacketListener;
import net.minecraft.network.packet.ConfigPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

public class ReadyC2SPacket implements Packet<ServerConfigurationPacketListener> {
	public static final ReadyC2SPacket INSTANCE = new ReadyC2SPacket();
	public static final PacketCodec<ByteBuf, ReadyC2SPacket> CODEC = PacketCodec.unit(INSTANCE);

	private ReadyC2SPacket() {
	}

	@Override
	public PacketType<ReadyC2SPacket> getPacketId() {
		return ConfigPackets.FINISH_CONFIGURATION_C2S;
	}

	public void apply(ServerConfigurationPacketListener serverConfigurationPacketListener) {
		serverConfigurationPacketListener.onReady(this);
	}

	@Override
	public boolean transitionsNetworkState() {
		return true;
	}
}
