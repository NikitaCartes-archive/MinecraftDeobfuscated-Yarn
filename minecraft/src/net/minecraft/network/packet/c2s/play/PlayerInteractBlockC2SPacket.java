package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

public class PlayerInteractBlockC2SPacket implements Packet<ServerPlayPacketListener> {
	private final BlockHitResult blockHitResult;
	private final Hand hand;
	private final int sequence;

	public PlayerInteractBlockC2SPacket(Hand hand, BlockHitResult blockHitResult, int sequence) {
		this.hand = hand;
		this.blockHitResult = blockHitResult;
		this.sequence = sequence;
	}

	public PlayerInteractBlockC2SPacket(PacketByteBuf buf) {
		this.hand = buf.readEnumConstant(Hand.class);
		this.blockHitResult = buf.readBlockHitResult();
		this.sequence = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeEnumConstant(this.hand);
		buf.writeBlockHitResult(this.blockHitResult);
		buf.writeVarInt(this.sequence);
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
