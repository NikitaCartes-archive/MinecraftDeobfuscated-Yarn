package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class TeleportConfirmC2SPacket implements Packet<ServerPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, TeleportConfirmC2SPacket> CODEC = Packet.createCodec(
		TeleportConfirmC2SPacket::write, TeleportConfirmC2SPacket::new
	);
	private final int teleportId;

	public TeleportConfirmC2SPacket(int teleportId) {
		this.teleportId = teleportId;
	}

	private TeleportConfirmC2SPacket(PacketByteBuf buf) {
		this.teleportId = buf.readVarInt();
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.teleportId);
	}

	@Override
	public PacketType<TeleportConfirmC2SPacket> getPacketId() {
		return PlayPackets.ACCEPT_TELEPORTATION;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onTeleportConfirm(this);
	}

	public int getTeleportId() {
		return this.teleportId;
	}
}
