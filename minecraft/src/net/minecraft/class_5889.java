package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.world.border.WorldBorder;

public class class_5889 implements Packet<ClientPlayPacketListener> {
	private final double field_29123;
	private final double field_29124;
	private final double field_29125;
	private final double field_29126;
	private final long field_29127;
	private final int field_29128;
	private final int field_29129;
	private final int field_29130;

	public class_5889(PacketByteBuf packetByteBuf) {
		this.field_29123 = packetByteBuf.readDouble();
		this.field_29124 = packetByteBuf.readDouble();
		this.field_29125 = packetByteBuf.readDouble();
		this.field_29126 = packetByteBuf.readDouble();
		this.field_29127 = packetByteBuf.readVarLong();
		this.field_29128 = packetByteBuf.readVarInt();
		this.field_29129 = packetByteBuf.readVarInt();
		this.field_29130 = packetByteBuf.readVarInt();
	}

	public class_5889(WorldBorder worldBorder) {
		this.field_29123 = worldBorder.getCenterX();
		this.field_29124 = worldBorder.getCenterZ();
		this.field_29125 = worldBorder.getSize();
		this.field_29126 = worldBorder.getSizeLerpTarget();
		this.field_29127 = worldBorder.getSizeLerpTime();
		this.field_29128 = worldBorder.getMaxRadius();
		this.field_29129 = worldBorder.getWarningBlocks();
		this.field_29130 = worldBorder.getWarningTime();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeDouble(this.field_29123);
		buf.writeDouble(this.field_29124);
		buf.writeDouble(this.field_29125);
		buf.writeDouble(this.field_29126);
		buf.writeVarLong(this.field_29127);
		buf.writeVarInt(this.field_29128);
		buf.writeVarInt(this.field_29129);
		buf.writeVarInt(this.field_29130);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_34072(this);
	}

	@Environment(EnvType.CLIENT)
	public double method_34124() {
		return this.field_29123;
	}

	@Environment(EnvType.CLIENT)
	public double method_34125() {
		return this.field_29124;
	}

	@Environment(EnvType.CLIENT)
	public double method_34126() {
		return this.field_29126;
	}

	@Environment(EnvType.CLIENT)
	public double method_34127() {
		return this.field_29125;
	}

	@Environment(EnvType.CLIENT)
	public long method_34128() {
		return this.field_29127;
	}

	@Environment(EnvType.CLIENT)
	public int method_34129() {
		return this.field_29128;
	}

	@Environment(EnvType.CLIENT)
	public int method_34130() {
		return this.field_29130;
	}

	@Environment(EnvType.CLIENT)
	public int method_34131() {
		return this.field_29129;
	}
}
