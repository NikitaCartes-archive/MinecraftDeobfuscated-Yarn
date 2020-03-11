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
			case SET_SIZE:
				this.size = buf.readDouble();
				break;
			case LERP_SIZE:
				this.oldSize = buf.readDouble();
				this.size = buf.readDouble();
				this.interpolationDuration = buf.readVarLong();
				break;
			case SET_CENTER:
				this.centerX = buf.readDouble();
				this.centerZ = buf.readDouble();
				break;
			case SET_WARNING_BLOCKS:
				this.warningBlocks = buf.readVarInt();
				break;
			case SET_WARNING_TIME:
				this.warningTime = buf.readVarInt();
				break;
			case INITIALIZE:
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
			case SET_SIZE:
				buf.writeDouble(this.size);
				break;
			case LERP_SIZE:
				buf.writeDouble(this.oldSize);
				buf.writeDouble(this.size);
				buf.writeVarLong(this.interpolationDuration);
				break;
			case SET_CENTER:
				buf.writeDouble(this.centerX);
				buf.writeDouble(this.centerZ);
				break;
			case SET_WARNING_BLOCKS:
				buf.writeVarInt(this.warningBlocks);
				break;
			case SET_WARNING_TIME:
				buf.writeVarInt(this.warningTime);
				break;
			case INITIALIZE:
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

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
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
