package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
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

	public WorldBorderS2CPacket(WorldBorder worldBorder, WorldBorderS2CPacket.Type type) {
		this.type = type;
		this.centerX = worldBorder.getCenterX();
		this.centerZ = worldBorder.getCenterZ();
		this.oldSize = worldBorder.getSize();
		this.size = worldBorder.getTargetSize();
		this.interpolationDuration = worldBorder.getTargetRemainingTime();
		this.portalTeleportPosLimit = worldBorder.getMaxWorldBorderRadius();
		this.warningBlocks = worldBorder.getWarningBlocks();
		this.warningTime = worldBorder.getWarningTime();
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.type = packetByteBuf.readEnumConstant(WorldBorderS2CPacket.Type.class);
		switch (this.type) {
			case field_12456:
				this.size = packetByteBuf.readDouble();
				break;
			case field_12452:
				this.oldSize = packetByteBuf.readDouble();
				this.size = packetByteBuf.readDouble();
				this.interpolationDuration = packetByteBuf.readVarLong();
				break;
			case field_12450:
				this.centerX = packetByteBuf.readDouble();
				this.centerZ = packetByteBuf.readDouble();
				break;
			case field_12451:
				this.warningBlocks = packetByteBuf.readVarInt();
				break;
			case field_12455:
				this.warningTime = packetByteBuf.readVarInt();
				break;
			case field_12454:
				this.centerX = packetByteBuf.readDouble();
				this.centerZ = packetByteBuf.readDouble();
				this.oldSize = packetByteBuf.readDouble();
				this.size = packetByteBuf.readDouble();
				this.interpolationDuration = packetByteBuf.readVarLong();
				this.portalTeleportPosLimit = packetByteBuf.readVarInt();
				this.warningBlocks = packetByteBuf.readVarInt();
				this.warningTime = packetByteBuf.readVarInt();
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeEnumConstant(this.type);
		switch (this.type) {
			case field_12456:
				packetByteBuf.writeDouble(this.size);
				break;
			case field_12452:
				packetByteBuf.writeDouble(this.oldSize);
				packetByteBuf.writeDouble(this.size);
				packetByteBuf.writeVarLong(this.interpolationDuration);
				break;
			case field_12450:
				packetByteBuf.writeDouble(this.centerX);
				packetByteBuf.writeDouble(this.centerZ);
				break;
			case field_12451:
				packetByteBuf.writeVarInt(this.warningBlocks);
				break;
			case field_12455:
				packetByteBuf.writeVarInt(this.warningTime);
				break;
			case field_12454:
				packetByteBuf.writeDouble(this.centerX);
				packetByteBuf.writeDouble(this.centerZ);
				packetByteBuf.writeDouble(this.oldSize);
				packetByteBuf.writeDouble(this.size);
				packetByteBuf.writeVarLong(this.interpolationDuration);
				packetByteBuf.writeVarInt(this.portalTeleportPosLimit);
				packetByteBuf.writeVarInt(this.warningBlocks);
				packetByteBuf.writeVarInt(this.warningTime);
		}
	}

	public void method_11796(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onWorldBorder(this);
	}

	@Environment(EnvType.CLIENT)
	public void apply(WorldBorder worldBorder) {
		switch (this.type) {
			case field_12456:
				worldBorder.setSize(this.size);
				break;
			case field_12452:
				worldBorder.interpolateSize(this.oldSize, this.size, this.interpolationDuration);
				break;
			case field_12450:
				worldBorder.setCenter(this.centerX, this.centerZ);
				break;
			case field_12451:
				worldBorder.setWarningBlocks(this.warningBlocks);
				break;
			case field_12455:
				worldBorder.setWarningTime(this.warningTime);
				break;
			case field_12454:
				worldBorder.setCenter(this.centerX, this.centerZ);
				if (this.interpolationDuration > 0L) {
					worldBorder.interpolateSize(this.oldSize, this.size, this.interpolationDuration);
				} else {
					worldBorder.setSize(this.size);
				}

				worldBorder.setMaxWorldBorderRadius(this.portalTeleportPosLimit);
				worldBorder.setWarningBlocks(this.warningBlocks);
				worldBorder.setWarningTime(this.warningTime);
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
