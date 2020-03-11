package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Hand;

public class OpenWrittenBookS2CPacket implements Packet<ClientPlayPacketListener> {
	private Hand hand;

	public OpenWrittenBookS2CPacket() {
	}

	public OpenWrittenBookS2CPacket(Hand hand) {
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

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onOpenWrittenBook(this);
	}

	@Environment(EnvType.CLIENT)
	public Hand getHand() {
		return this.hand;
	}
}
