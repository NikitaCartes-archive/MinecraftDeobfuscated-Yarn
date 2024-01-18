package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.world.border.WorldBorder;

public class WorldBorderInterpolateSizeS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, WorldBorderInterpolateSizeS2CPacket> CODEC = Packet.createCodec(
		WorldBorderInterpolateSizeS2CPacket::write, WorldBorderInterpolateSizeS2CPacket::new
	);
	private final double size;
	private final double sizeLerpTarget;
	private final long sizeLerpTime;

	public WorldBorderInterpolateSizeS2CPacket(WorldBorder worldBorder) {
		this.size = worldBorder.getSize();
		this.sizeLerpTarget = worldBorder.getSizeLerpTarget();
		this.sizeLerpTime = worldBorder.getSizeLerpTime();
	}

	private WorldBorderInterpolateSizeS2CPacket(PacketByteBuf buf) {
		this.size = buf.readDouble();
		this.sizeLerpTarget = buf.readDouble();
		this.sizeLerpTime = buf.readVarLong();
	}

	private void write(PacketByteBuf buf) {
		buf.writeDouble(this.size);
		buf.writeDouble(this.sizeLerpTarget);
		buf.writeVarLong(this.sizeLerpTime);
	}

	@Override
	public PacketType<WorldBorderInterpolateSizeS2CPacket> getPacketId() {
		return PlayPackets.SET_BORDER_LERP_SIZE;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onWorldBorderInterpolateSize(this);
	}

	public double getSize() {
		return this.size;
	}

	public double getSizeLerpTarget() {
		return this.sizeLerpTarget;
	}

	public long getSizeLerpTime() {
		return this.sizeLerpTime;
	}
}
