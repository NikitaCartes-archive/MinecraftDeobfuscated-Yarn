package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.world.Difficulty;

public class DifficultyS2CPacket implements Packet<ClientPlayPacketListener> {
	private final Difficulty difficulty;
	private final boolean difficultyLocked;

	public DifficultyS2CPacket(Difficulty difficulty, boolean difficultyLocked) {
		this.difficulty = difficulty;
		this.difficultyLocked = difficultyLocked;
	}

	public DifficultyS2CPacket(PacketByteBuf buf) {
		this.difficulty = Difficulty.byId(buf.readUnsignedByte());
		this.difficultyLocked = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeByte(this.difficulty.getId());
		buf.writeBoolean(this.difficultyLocked);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onDifficulty(this);
	}

	public boolean isDifficultyLocked() {
		return this.difficultyLocked;
	}

	public Difficulty getDifficulty() {
		return this.difficulty;
	}
}
