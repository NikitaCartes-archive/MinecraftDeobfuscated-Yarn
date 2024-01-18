package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class BoatPaddleStateC2SPacket implements Packet<ServerPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, BoatPaddleStateC2SPacket> CODEC = Packet.createCodec(
		BoatPaddleStateC2SPacket::write, BoatPaddleStateC2SPacket::new
	);
	private final boolean leftPaddling;
	private final boolean rightPaddling;

	public BoatPaddleStateC2SPacket(boolean leftPaddling, boolean rightPaddling) {
		this.leftPaddling = leftPaddling;
		this.rightPaddling = rightPaddling;
	}

	private BoatPaddleStateC2SPacket(PacketByteBuf buf) {
		this.leftPaddling = buf.readBoolean();
		this.rightPaddling = buf.readBoolean();
	}

	private void write(PacketByteBuf buf) {
		buf.writeBoolean(this.leftPaddling);
		buf.writeBoolean(this.rightPaddling);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onBoatPaddleState(this);
	}

	@Override
	public PacketType<BoatPaddleStateC2SPacket> getPacketId() {
		return PlayPackets.PADDLE_BOAT;
	}

	public boolean isLeftPaddling() {
		return this.leftPaddling;
	}

	public boolean isRightPaddling() {
		return this.rightPaddling;
	}
}
