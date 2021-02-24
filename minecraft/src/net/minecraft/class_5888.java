package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class class_5888 implements Packet<ClientPlayPacketListener> {
	private final boolean field_29122;

	public class_5888(boolean bl) {
		this.field_29122 = bl;
	}

	public class_5888(PacketByteBuf packetByteBuf) {
		this.field_29122 = packetByteBuf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBoolean(this.field_29122);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_34071(this);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_34116() {
		return this.field_29122;
	}
}
