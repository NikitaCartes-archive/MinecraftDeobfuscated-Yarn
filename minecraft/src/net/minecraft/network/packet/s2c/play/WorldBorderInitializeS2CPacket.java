package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.world.border.WorldBorder;

public class WorldBorderInitializeS2CPacket implements Packet<ClientPlayPacketListener> {
	private final double centerX;
	private final double centerZ;
	private final double size;
	private final double sizeLerpTarget;
	private final long sizeLerpTime;
	private final int maxRadius;
	private final int warningBlocks;
	private final int warningTime;

	public WorldBorderInitializeS2CPacket(PacketByteBuf buf) {
		this.centerX = buf.readDouble();
		this.centerZ = buf.readDouble();
		this.size = buf.readDouble();
		this.sizeLerpTarget = buf.readDouble();
		this.sizeLerpTime = buf.readVarLong();
		this.maxRadius = buf.readVarInt();
		this.warningBlocks = buf.readVarInt();
		this.warningTime = buf.readVarInt();
	}

	public WorldBorderInitializeS2CPacket(WorldBorder worldBorder) {
		this.centerX = worldBorder.getCenterX();
		this.centerZ = worldBorder.getCenterZ();
		this.size = worldBorder.getSize();
		this.sizeLerpTarget = worldBorder.getSizeLerpTarget();
		this.sizeLerpTime = worldBorder.getSizeLerpTime();
		this.maxRadius = worldBorder.getMaxRadius();
		this.warningBlocks = worldBorder.getWarningBlocks();
		this.warningTime = worldBorder.getWarningTime();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeDouble(this.centerX);
		buf.writeDouble(this.centerZ);
		buf.writeDouble(this.size);
		buf.writeDouble(this.sizeLerpTarget);
		buf.writeVarLong(this.sizeLerpTime);
		buf.writeVarInt(this.maxRadius);
		buf.writeVarInt(this.warningBlocks);
		buf.writeVarInt(this.warningTime);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onWorldBorderInitialize(this);
	}

	@Environment(EnvType.CLIENT)
	public double getCenterX() {
		return this.centerX;
	}

	@Environment(EnvType.CLIENT)
	public double getCenterZ() {
		return this.centerZ;
	}

	@Environment(EnvType.CLIENT)
	public double getSizeLerpTarget() {
		return this.sizeLerpTarget;
	}

	@Environment(EnvType.CLIENT)
	public double getSize() {
		return this.size;
	}

	@Environment(EnvType.CLIENT)
	public long getSizeLerpTime() {
		return this.sizeLerpTime;
	}

	@Environment(EnvType.CLIENT)
	public int getMaxRadius() {
		return this.maxRadius;
	}

	@Environment(EnvType.CLIENT)
	public int getWarningTime() {
		return this.warningTime;
	}

	@Environment(EnvType.CLIENT)
	public int getWarningBlocks() {
		return this.warningBlocks;
	}
}
