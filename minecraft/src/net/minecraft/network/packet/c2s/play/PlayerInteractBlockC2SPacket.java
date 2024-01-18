package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

public class PlayerInteractBlockC2SPacket implements Packet<ServerPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, PlayerInteractBlockC2SPacket> CODEC = Packet.createCodec(
		PlayerInteractBlockC2SPacket::write, PlayerInteractBlockC2SPacket::new
	);
	private final BlockHitResult blockHitResult;
	private final Hand hand;
	private final int sequence;

	public PlayerInteractBlockC2SPacket(Hand hand, BlockHitResult blockHitResult, int sequence) {
		this.hand = hand;
		this.blockHitResult = blockHitResult;
		this.sequence = sequence;
	}

	private PlayerInteractBlockC2SPacket(PacketByteBuf buf) {
		this.hand = buf.readEnumConstant(Hand.class);
		this.blockHitResult = buf.readBlockHitResult();
		this.sequence = buf.readVarInt();
	}

	private void write(PacketByteBuf buf) {
		buf.writeEnumConstant(this.hand);
		buf.writeBlockHitResult(this.blockHitResult);
		buf.writeVarInt(this.sequence);
	}

	@Override
	public PacketType<PlayerInteractBlockC2SPacket> getPacketId() {
		return PlayPackets.USE_ITEM_ON;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onPlayerInteractBlock(this);
	}

	public Hand getHand() {
		return this.hand;
	}

	public BlockHitResult getBlockHitResult() {
		return this.blockHitResult;
	}

	public int getSequence() {
		return this.sequence;
	}
}
