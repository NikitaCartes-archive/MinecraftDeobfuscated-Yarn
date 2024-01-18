package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.world.border.WorldBorder;

public class WorldBorderSizeChangedS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, WorldBorderSizeChangedS2CPacket> CODEC = Packet.createCodec(
		WorldBorderSizeChangedS2CPacket::write, WorldBorderSizeChangedS2CPacket::new
	);
	private final double sizeLerpTarget;

	public WorldBorderSizeChangedS2CPacket(WorldBorder worldBorder) {
		this.sizeLerpTarget = worldBorder.getSizeLerpTarget();
	}

	private WorldBorderSizeChangedS2CPacket(PacketByteBuf buf) {
		this.sizeLerpTarget = buf.readDouble();
	}

	private void write(PacketByteBuf buf) {
		buf.writeDouble(this.sizeLerpTarget);
	}

	@Override
	public PacketType<WorldBorderSizeChangedS2CPacket> getPacketId() {
		return PlayPackets.SET_BORDER_SIZE;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onWorldBorderSizeChanged(this);
	}

	public double getSizeLerpTarget() {
		return this.sizeLerpTarget;
	}
}
