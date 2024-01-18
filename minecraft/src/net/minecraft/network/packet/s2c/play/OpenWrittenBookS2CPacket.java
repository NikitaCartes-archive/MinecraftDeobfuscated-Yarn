package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.Hand;

public class OpenWrittenBookS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, OpenWrittenBookS2CPacket> CODEC = Packet.createCodec(
		OpenWrittenBookS2CPacket::write, OpenWrittenBookS2CPacket::new
	);
	private final Hand hand;

	public OpenWrittenBookS2CPacket(Hand hand) {
		this.hand = hand;
	}

	private OpenWrittenBookS2CPacket(PacketByteBuf buf) {
		this.hand = buf.readEnumConstant(Hand.class);
	}

	private void write(PacketByteBuf buf) {
		buf.writeEnumConstant(this.hand);
	}

	@Override
	public PacketType<OpenWrittenBookS2CPacket> getPacketId() {
		return PlayPackets.OPEN_BOOK;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onOpenWrittenBook(this);
	}

	public Hand getHand() {
		return this.hand;
	}
}
