package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Hand;

public class PlayerInteractItemC2SPacket implements Packet<ServerPlayPacketListener> {
	private final Hand hand;

	public PlayerInteractItemC2SPacket(Hand hand) {
		this.hand = hand;
	}

	public PlayerInteractItemC2SPacket(PacketByteBuf buf) {
		this.hand = buf.readEnumConstant(Hand.class);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeEnumConstant(this.hand);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onPlayerInteractItem(this);
	}

	public Hand getHand() {
		return this.hand;
	}
}
