package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Hand;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.hit.BlockHitResult;

public class PlayerInteractBlockC2SPacket implements Packet<ServerPlayPacketListener> {
	private BlockHitResult field_17602;
	private Hand hand;

	public PlayerInteractBlockC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public PlayerInteractBlockC2SPacket(Hand hand, BlockHitResult blockHitResult) {
		this.hand = hand;
		this.field_17602 = blockHitResult;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.hand = buf.readEnumConstant(Hand.class);
		this.field_17602 = buf.readBlockHitResult();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeEnumConstant(this.hand);
		buf.writeBlockHitResult(this.field_17602);
	}

	public void method_12547(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onPlayerInteractBlock(this);
	}

	public Hand getHand() {
		return this.hand;
	}

	public BlockHitResult getHitY() {
		return this.field_17602;
	}
}
