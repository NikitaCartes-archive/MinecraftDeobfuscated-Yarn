package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.Difficulty;

public class DifficultyS2CPacket implements Packet<ClientPlayPacketListener> {
	private Difficulty difficulty;
	private boolean difficultyLocked;

	public DifficultyS2CPacket() {
	}

	public DifficultyS2CPacket(Difficulty difficulty, boolean bl) {
		this.difficulty = difficulty;
		this.difficultyLocked = bl;
	}

	public void method_11341(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onDifficulty(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.difficulty = Difficulty.getDifficulty(packetByteBuf.readUnsignedByte());
		this.difficultyLocked = packetByteBuf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.difficulty.getId());
		packetByteBuf.writeBoolean(this.difficultyLocked);
	}

	@Environment(EnvType.CLIENT)
	public boolean isDifficultyLocked() {
		return this.difficultyLocked;
	}

	@Environment(EnvType.CLIENT)
	public Difficulty getDifficulty() {
		return this.difficulty;
	}
}
