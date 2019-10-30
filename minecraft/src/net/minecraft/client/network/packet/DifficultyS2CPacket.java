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

	public DifficultyS2CPacket(Difficulty difficulty, boolean difficultyLocked) {
		this.difficulty = difficulty;
		this.difficultyLocked = difficultyLocked;
	}

	public void method_11341(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onDifficulty(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.difficulty = Difficulty.byOrdinal(buf.readUnsignedByte());
		this.difficultyLocked = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.difficulty.getId());
		buf.writeBoolean(this.difficultyLocked);
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
