package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.world.border.WorldBorder;

public class class_5899 implements Packet<ClientPlayPacketListener> {
	private final int field_29150;

	public class_5899(WorldBorder worldBorder) {
		this.field_29150 = worldBorder.getWarningBlocks();
	}

	public class_5899(PacketByteBuf packetByteBuf) {
		this.field_29150 = packetByteBuf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.field_29150);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_34081(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_34168() {
		return this.field_29150;
	}
}
