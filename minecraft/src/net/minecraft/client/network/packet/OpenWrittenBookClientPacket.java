package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Hand;
import net.minecraft.util.PacketByteBuf;

public class OpenWrittenBookClientPacket implements Packet<ClientPlayPacketListener> {
	private Hand hand;

	public OpenWrittenBookClientPacket() {
	}

	public OpenWrittenBookClientPacket(Hand hand) {
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

	public void method_17187(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onOpenWrittenBook(this);
	}

	@Environment(EnvType.CLIENT)
	public Hand getHand() {
		return this.hand;
	}
}
