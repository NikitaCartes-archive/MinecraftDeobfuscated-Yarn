package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Hand;

public class HandSwingC2SPacket implements Packet<ServerPlayPacketListener> {
	private Hand hand;

	public HandSwingC2SPacket() {
	}

	public HandSwingC2SPacket(Hand hand) {
		this.hand = hand;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.hand = buf.readEnumConstant(Hand.class);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeEnumConstant(this.hand);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onHandSwing(this);
	}

	public Hand getHand() {
		return this.hand;
	}
}
