package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;

public class BoatPaddleStateC2SPacket implements Packet<ServerPlayPacketListener> {
	private final boolean leftPaddling;
	private final boolean rightPaddling;

	public BoatPaddleStateC2SPacket(boolean leftPaddling, boolean rightPaddling) {
		this.leftPaddling = leftPaddling;
		this.rightPaddling = rightPaddling;
	}

	public BoatPaddleStateC2SPacket(PacketByteBuf buf) {
		this.leftPaddling = buf.readBoolean();
		this.rightPaddling = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBoolean(this.leftPaddling);
		buf.writeBoolean(this.rightPaddling);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onBoatPaddleState(this);
	}

	public boolean isLeftPaddling() {
		return this.leftPaddling;
	}

	public boolean isRightPaddling() {
		return this.rightPaddling;
	}
}
