package net.minecraft;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class class_5891 implements Packet<ClientPlayPacketListener> {
	public class_5891() {
	}

	public class_5891(PacketByteBuf packetByteBuf) {
	}

	@Override
	public void write(PacketByteBuf buf) {
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_34074(this);
	}
}
