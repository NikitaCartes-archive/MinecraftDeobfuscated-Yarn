package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.world.border.WorldBorder;

public class WorldBorderInitializeS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, WorldBorderInitializeS2CPacket> CODEC = Packet.createCodec(
		WorldBorderInitializeS2CPacket::write, WorldBorderInitializeS2CPacket::new
	);
	private final double centerX;
	private final double centerZ;
	private final double size;
	private final double sizeLerpTarget;
	private final long sizeLerpTime;
	private final int maxRadius;
	private final int warningBlocks;
	private final int warningTime;

	private WorldBorderInitializeS2CPacket(PacketByteBuf buf) {
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

	private void write(PacketByteBuf buf) {
		buf.writeDouble(this.centerX);
		buf.writeDouble(this.centerZ);
		buf.writeDouble(this.size);
		buf.writeDouble(this.sizeLerpTarget);
		buf.writeVarLong(this.sizeLerpTime);
		buf.writeVarInt(this.maxRadius);
		buf.writeVarInt(this.warningBlocks);
		buf.writeVarInt(this.warningTime);
	}

	@Override
	public PacketType<WorldBorderInitializeS2CPacket> getPacketId() {
		return PlayPackets.INITIALIZE_BORDER;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onWorldBorderInitialize(this);
	}

	public double getCenterX() {
		return this.centerX;
	}

	public double getCenterZ() {
		return this.centerZ;
	}

	public double getSizeLerpTarget() {
		return this.sizeLerpTarget;
	}

	public double getSize() {
		return this.size;
	}

	public long getSizeLerpTime() {
		return this.sizeLerpTime;
	}

	public int getMaxRadius() {
		return this.maxRadius;
	}

	public int getWarningTime() {
		return this.warningTime;
	}

	public int getWarningBlocks() {
		return this.warningBlocks;
	}
}
