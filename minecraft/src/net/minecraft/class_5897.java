package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.world.border.WorldBorder;

public class class_5897 implements Packet<ClientPlayPacketListener> {
	private final double field_29148;

	public class_5897(WorldBorder worldBorder) {
		this.field_29148 = worldBorder.getSizeLerpTarget();
	}

	public class_5897(PacketByteBuf packetByteBuf) {
		this.field_29148 = packetByteBuf.readDouble();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeDouble(this.field_29148);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_34079(this);
	}

	@Environment(EnvType.CLIENT)
	public double method_34164() {
		return this.field_29148;
	}
}
