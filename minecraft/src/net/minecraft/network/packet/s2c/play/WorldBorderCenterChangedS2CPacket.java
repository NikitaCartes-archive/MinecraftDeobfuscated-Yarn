package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.world.border.WorldBorder;

public class WorldBorderCenterChangedS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, WorldBorderCenterChangedS2CPacket> CODEC = Packet.createCodec(
		WorldBorderCenterChangedS2CPacket::write, WorldBorderCenterChangedS2CPacket::new
	);
	private final double centerX;
	private final double centerZ;

	public WorldBorderCenterChangedS2CPacket(WorldBorder worldBorder) {
		this.centerX = worldBorder.getCenterX();
		this.centerZ = worldBorder.getCenterZ();
	}

	private WorldBorderCenterChangedS2CPacket(PacketByteBuf buf) {
		this.centerX = buf.readDouble();
		this.centerZ = buf.readDouble();
	}

	private void write(PacketByteBuf buf) {
		buf.writeDouble(this.centerX);
		buf.writeDouble(this.centerZ);
	}

	@Override
	public PacketType<WorldBorderCenterChangedS2CPacket> getPacketId() {
		return PlayPackets.SET_BORDER_CENTER;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onWorldBorderCenterChanged(this);
	}

	public double getCenterZ() {
		return this.centerZ;
	}

	public double getCenterX() {
		return this.centerX;
	}
}
