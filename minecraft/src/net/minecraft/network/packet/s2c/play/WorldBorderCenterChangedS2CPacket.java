package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.world.border.WorldBorder;

public class WorldBorderCenterChangedS2CPacket implements Packet<ClientPlayPacketListener> {
	private final double centerX;
	private final double centerZ;

	public WorldBorderCenterChangedS2CPacket(WorldBorder worldBorder) {
		this.centerX = worldBorder.getCenterX();
		this.centerZ = worldBorder.getCenterZ();
	}

	public WorldBorderCenterChangedS2CPacket(PacketByteBuf buf) {
		this.centerX = buf.readDouble();
		this.centerZ = buf.readDouble();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeDouble(this.centerX);
		buf.writeDouble(this.centerZ);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onWorldBorderCenterChanged(this);
	}

	@Environment(EnvType.CLIENT)
	public double getCenterX() {
		return this.centerZ;
	}

	@Environment(EnvType.CLIENT)
	public double getCenterZ() {
		return this.centerX;
	}
}
