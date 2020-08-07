package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.world.border.WorldBorder;

public class WorldBorderS2CPacket implements Packet<ClientPlayPacketListener> {
	private WorldBorderS2CPacket.Type type;
	private int portalTeleportPosLimit;
	private double centerX;
	private double centerZ;
	private double size;
	private double oldSize;
	private long interpolationDuration;
	private int warningTime;
	private int warningBlocks;

	public WorldBorderS2CPacket() {
	}

	public WorldBorderS2CPacket(WorldBorder border, WorldBorderS2CPacket.Type type) {
		this.type = type;
		this.centerX = border.getCenterX();
		this.centerZ = border.getCenterZ();
		this.oldSize = border.getSize();
		this.size = border.getTargetSize();
		this.interpolationDuration = border.getTargetRemainingTime();
		this.portalTeleportPosLimit = border.getMaxWorldBorderRadius();
		this.warningBlocks = border.getWarningBlocks();
		this.warningTime = border.getWarningTime();
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.type = buf.readEnumConstant(WorldBorderS2CPacket.Type.class);
		switch (this.type) {
			case field_12456:
				this.size = buf.readDouble();
				break;
			case field_12452:
				this.oldSize = buf.readDouble();
				this.size = buf.readDouble();
				this.interpolationDuration = buf.readVarLong();
				break;
			case field_12450:
				this.centerX = buf.readDouble();
				this.centerZ = buf.readDouble();
				break;
			case field_12451:
				this.warningBlocks = buf.readVarInt();
				break;
			case field_12455:
				this.warningTime = buf.readVarInt();
				break;
			case field_12454:
				this.centerX = buf.readDouble();
				this.centerZ = buf.readDouble();
				this.oldSize = buf.readDouble();
				this.size = buf.readDouble();
				this.interpolationDuration = buf.readVarLong();
				this.portalTeleportPosLimit = buf.readVarInt();
				this.warningBlocks = buf.readVarInt();
				this.warningTime = buf.readVarInt();
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeEnumConstant(this.type);
		switch (this.type) {
			case field_12456:
				buf.writeDouble(this.size);
				break;
			case field_12452:
				buf.writeDouble(this.oldSize);
				buf.writeDouble(this.size);
				buf.writeVarLong(this.interpolationDuration);
				break;
			case field_12450:
				buf.writeDouble(this.centerX);
				buf.writeDouble(this.centerZ);
				break;
			case field_12451:
				buf.writeVarInt(this.warningBlocks);
				break;
			case field_12455:
				buf.writeVarInt(this.warningTime);
				break;
			case field_12454:
				buf.writeDouble(this.centerX);
				buf.writeDouble(this.centerZ);
				buf.writeDouble(this.oldSize);
				buf.writeDouble(this.size);
				buf.writeVarLong(this.interpolationDuration);
				buf.writeVarInt(this.portalTeleportPosLimit);
				buf.writeVarInt(this.warningBlocks);
				buf.writeVarInt(this.warningTime);
		}
	}

	public void method_11796(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onWorldBorder(this);
	}

	@Environment(EnvType.CLIENT)
	public void apply(WorldBorder border) {
		switch (this.type) {
			case field_12456:
				border.setSize(this.size);
				break;
			case field_12452:
				border.interpolateSize(this.oldSize, this.size, this.interpolationDuration);
				break;
			case field_12450:
				border.setCenter(this.centerX, this.centerZ);
				break;
			case field_12451:
				border.setWarningBlocks(this.warningBlocks);
				break;
			case field_12455:
				border.setWarningTime(this.warningTime);
				break;
			case field_12454:
				border.setCenter(this.centerX, this.centerZ);
				if (this.interpolationDuration > 0L) {
					border.interpolateSize(this.oldSize, this.size, this.interpolationDuration);
				} else {
					border.setSize(this.size);
				}

				border.setMaxWorldBorderRadius(this.portalTeleportPosLimit);
				border.setWarningBlocks(this.warningBlocks);
				border.setWarningTime(this.warningTime);
		}
	}

	public static enum Type {
		field_12456,
		field_12452,
		field_12450,
		field_12454,
		field_12455,
		field_12451;
	}
}
