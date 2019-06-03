package net.minecraft.server.network.packet;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.Difficulty;

public class UpdateDifficultyC2SPacket implements Packet<ServerPlayPacketListener> {
	private Difficulty difficulty;

	public UpdateDifficultyC2SPacket() {
	}

	public UpdateDifficultyC2SPacket(Difficulty difficulty) {
		this.difficulty = difficulty;
	}

	public void method_19477(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onUpdateDifficulty(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.difficulty = Difficulty.byOrdinal(packetByteBuf.readUnsignedByte());
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.difficulty.getId());
	}

	public Difficulty getDifficulty() {
		return this.difficulty;
	}
}
