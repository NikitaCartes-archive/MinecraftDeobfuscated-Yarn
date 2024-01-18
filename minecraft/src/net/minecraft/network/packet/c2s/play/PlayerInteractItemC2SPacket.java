package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.Hand;

public class PlayerInteractItemC2SPacket implements Packet<ServerPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, PlayerInteractItemC2SPacket> CODEC = Packet.createCodec(
		PlayerInteractItemC2SPacket::write, PlayerInteractItemC2SPacket::new
	);
	private final Hand hand;
	private final int sequence;

	public PlayerInteractItemC2SPacket(Hand hand, int sequence) {
		this.hand = hand;
		this.sequence = sequence;
	}

	private PlayerInteractItemC2SPacket(PacketByteBuf buf) {
		this.hand = buf.readEnumConstant(Hand.class);
		this.sequence = buf.readVarInt();
	}

	private void write(PacketByteBuf buf) {
		buf.writeEnumConstant(this.hand);
		buf.writeVarInt(this.sequence);
	}

	@Override
	public PacketType<PlayerInteractItemC2SPacket> getPacketId() {
		return PlayPackets.USE_ITEM;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onPlayerInteractItem(this);
	}

	public Hand getHand() {
		return this.hand;
	}

	public int getSequence() {
		return this.sequence;
	}
}
