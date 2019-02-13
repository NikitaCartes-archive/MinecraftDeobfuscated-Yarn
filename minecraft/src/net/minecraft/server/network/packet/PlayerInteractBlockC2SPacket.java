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
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.hand = packetByteBuf.readEnumConstant(Hand.class);
		this.field_17602 = packetByteBuf.method_17814();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeEnumConstant(this.hand);
		packetByteBuf.method_17813(this.field_17602);
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
