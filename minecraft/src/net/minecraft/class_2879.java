package net.minecraft;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Hand;
import net.minecraft.util.PacketByteBuf;

public class class_2879 implements Packet<ServerPlayPacketListener> {
	private Hand field_13102;

	public class_2879() {
	}

	public class_2879(Hand hand) {
		this.field_13102 = hand;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_13102 = packetByteBuf.readEnumConstant(Hand.class);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeEnumConstant(this.field_13102);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12052(this);
	}

	public Hand method_12512() {
		return this.field_13102;
	}
}
