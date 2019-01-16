package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.util.PacketByteBuf;

public class PlayerInteractBlockServerPacket implements Packet<ServerPlayPacketListener> {
	private BlockHitResult field_17602;
	private Hand hand;

	public PlayerInteractBlockServerPacket() {
	}

	@Environment(EnvType.CLIENT)
	public PlayerInteractBlockServerPacket(Hand hand, BlockHitResult blockHitResult) {
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

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onPlayerInteractBlock(this);
	}

	public Hand getHand() {
		return this.hand;
	}

	public BlockHitResult getHitY() {
		return this.field_17602;
	}
}
