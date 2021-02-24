package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.world.border.WorldBorder;

public class class_5898 implements Packet<ClientPlayPacketListener> {
	private final int field_29149;

	public class_5898(WorldBorder worldBorder) {
		this.field_29149 = worldBorder.getWarningTime();
	}

	public class_5898(PacketByteBuf packetByteBuf) {
		this.field_29149 = packetByteBuf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.field_29149);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_34080(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_34166() {
		return this.field_29149;
	}
}
