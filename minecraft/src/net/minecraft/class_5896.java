package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.world.border.WorldBorder;

public class class_5896 implements Packet<ClientPlayPacketListener> {
	private final double field_29145;
	private final double field_29146;
	private final long field_29147;

	public class_5896(WorldBorder worldBorder) {
		this.field_29145 = worldBorder.getSize();
		this.field_29146 = worldBorder.getSizeLerpTarget();
		this.field_29147 = worldBorder.getSizeLerpTime();
	}

	public class_5896(PacketByteBuf packetByteBuf) {
		this.field_29145 = packetByteBuf.readDouble();
		this.field_29146 = packetByteBuf.readDouble();
		this.field_29147 = packetByteBuf.readVarLong();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeDouble(this.field_29145);
		buf.writeDouble(this.field_29146);
		buf.writeVarLong(this.field_29147);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_34078(this);
	}

	@Environment(EnvType.CLIENT)
	public double method_34160() {
		return this.field_29145;
	}

	@Environment(EnvType.CLIENT)
	public double method_34161() {
		return this.field_29146;
	}

	@Environment(EnvType.CLIENT)
	public long method_34162() {
		return this.field_29147;
	}
}
