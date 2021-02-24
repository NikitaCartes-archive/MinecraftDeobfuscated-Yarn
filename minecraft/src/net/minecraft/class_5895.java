package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.world.border.WorldBorder;

public class class_5895 implements Packet<ClientPlayPacketListener> {
	private final double field_29143;
	private final double field_29144;

	public class_5895(WorldBorder worldBorder) {
		this.field_29143 = worldBorder.getCenterX();
		this.field_29144 = worldBorder.getCenterZ();
	}

	public class_5895(PacketByteBuf packetByteBuf) {
		this.field_29143 = packetByteBuf.readDouble();
		this.field_29144 = packetByteBuf.readDouble();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeDouble(this.field_29143);
		buf.writeDouble(this.field_29144);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_34077(this);
	}

	@Environment(EnvType.CLIENT)
	public double method_34157() {
		return this.field_29144;
	}

	@Environment(EnvType.CLIENT)
	public double method_34158() {
		return this.field_29143;
	}
}
