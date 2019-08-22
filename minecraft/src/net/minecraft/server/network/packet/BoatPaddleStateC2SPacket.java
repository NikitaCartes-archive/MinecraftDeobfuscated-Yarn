package net.minecraft.server.network.packet;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class BoatPaddleStateC2SPacket implements Packet<ServerPlayPacketListener> {
	private boolean leftPaddling;
	private boolean rightPaddling;

	public BoatPaddleStateC2SPacket() {
	}

	public BoatPaddleStateC2SPacket(boolean bl, boolean bl2) {
		this.leftPaddling = bl;
		this.rightPaddling = bl2;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.leftPaddling = packetByteBuf.readBoolean();
		this.rightPaddling = packetByteBuf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeBoolean(this.leftPaddling);
		packetByteBuf.writeBoolean(this.rightPaddling);
	}

	public void method_12283(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onBoatPaddleState(this);
	}

	public boolean isLeftPaddling() {
		return this.leftPaddling;
	}

	public boolean isRightPaddling() {
		return this.rightPaddling;
	}
}
