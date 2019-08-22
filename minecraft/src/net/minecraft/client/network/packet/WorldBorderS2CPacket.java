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
			case SET_SIZE:
				this.size = packetByteBuf.readDouble();
				break;
			case LERP_SIZE:
				this.oldSize = packetByteBuf.readDouble();
				this.size = packetByteBuf.readDouble();
				this.interpolationDuration = packetByteBuf.readVarLong();
				break;
			case SET_CENTER:
				this.centerX = packetByteBuf.readDouble();
				this.centerZ = packetByteBuf.readDouble();
				break;
			case SET_WARNING_BLOCKS:
				this.warningBlocks = packetByteBuf.readVarInt();
				break;
			case SET_WARNING_TIME:
				this.warningTime = packetByteBuf.readVarInt();
				break;
			case INITIALIZE:
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
			case SET_SIZE:
				packetByteBuf.writeDouble(this.size);
				break;
			case LERP_SIZE:
				packetByteBuf.writeDouble(this.oldSize);
				packetByteBuf.writeDouble(this.size);
				packetByteBuf.writeVarLong(this.interpolationDuration);
				break;
			case SET_CENTER:
				packetByteBuf.writeDouble(this.centerX);
				packetByteBuf.writeDouble(this.centerZ);
				break;
			case SET_WARNING_BLOCKS:
				packetByteBuf.writeVarInt(this.warningBlocks);
				break;
			case SET_WARNING_TIME:
				packetByteBuf.writeVarInt(this.warningTime);
				break;
			case INITIALIZE:
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
			case SET_SIZE:
				worldBorder.setSize(this.size);
				break;
			case LERP_SIZE:
				worldBorder.interpolateSize(this.oldSize, this.size, this.interpolationDuration);
				break;
			case SET_CENTER:
				worldBorder.setCenter(this.centerX, this.centerZ);
				break;
			case SET_WARNING_BLOCKS:
				worldBorder.setWarningBlocks(this.warningBlocks);
				break;
			case SET_WARNING_TIME:
				worldBorder.setWarningTime(this.warningTime);
				break;
			case INITIALIZE:
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
		SET_SIZE,
		LERP_SIZE,
		SET_CENTER,
		INITIALIZE,
		SET_WARNING_TIME,
		SET_WARNING_BLOCKS;
	}
}
