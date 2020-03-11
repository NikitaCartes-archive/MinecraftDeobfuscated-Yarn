package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;

public class BoatPaddleStateC2SPacket implements Packet<ServerPlayPacketListener> {
	private boolean leftPaddling;
	private boolean rightPaddling;

	public BoatPaddleStateC2SPacket() {
	}

	public BoatPaddleStateC2SPacket(boolean leftPaddling, boolean rightPaddling) {
		this.leftPaddling = leftPaddling;
		this.rightPaddling = rightPaddling;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.leftPaddling = buf.readBoolean();
		this.rightPaddling = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
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
