package net.minecraft.network.packet.s2c.play;

import java.lang.runtime.ObjectMethods;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;

public final class StartChunkSendS2CPacket extends Record implements Packet<ClientPlayPacketListener> {
	public StartChunkSendS2CPacket(PacketByteBuf buf) {
		this();
	}

	public StartChunkSendS2CPacket() {
	}

	@Override
	public void write(PacketByteBuf buf) {
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onStartChunkSend(this);
	}

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",StartChunkSendS2CPacket,"">(this);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",StartChunkSendS2CPacket,"">(this);
	}

	public final boolean equals(Object object) {
		return ObjectMethods.bootstrap<"equals",StartChunkSendS2CPacket,"">(this, object);
	}
}
