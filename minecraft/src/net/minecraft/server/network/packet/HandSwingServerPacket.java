package net.minecraft.server.network.packet;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Hand;
import net.minecraft.util.PacketByteBuf;

public class HandSwingServerPacket implements Packet<ServerPlayPacketListener> {
	private Hand hand;

	public HandSwingServerPacket() {
	}

	public HandSwingServerPacket(Hand hand) {
		this.hand = hand;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.hand = packetByteBuf.readEnumConstant(Hand.class);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeEnumConstant(this.hand);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onHandSwing(this);
	}

	public Hand getHand() {
		return this.hand;
	}
}
