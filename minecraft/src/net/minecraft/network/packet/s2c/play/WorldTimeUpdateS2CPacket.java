package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record WorldTimeUpdateS2CPacket(long time, long timeOfDay, boolean tickDayTime) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, WorldTimeUpdateS2CPacket> CODEC = PacketCodec.tuple(
		PacketCodecs.LONG,
		WorldTimeUpdateS2CPacket::time,
		PacketCodecs.LONG,
		WorldTimeUpdateS2CPacket::timeOfDay,
		PacketCodecs.BOOL,
		WorldTimeUpdateS2CPacket::tickDayTime,
		WorldTimeUpdateS2CPacket::new
	);

	@Override
	public PacketType<WorldTimeUpdateS2CPacket> getPacketId() {
		return PlayPackets.SET_TIME;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onWorldTimeUpdate(this);
	}
}
