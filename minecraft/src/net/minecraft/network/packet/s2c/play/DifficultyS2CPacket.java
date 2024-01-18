package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.world.Difficulty;

public class DifficultyS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, DifficultyS2CPacket> CODEC = Packet.createCodec(DifficultyS2CPacket::write, DifficultyS2CPacket::new);
	private final Difficulty difficulty;
	private final boolean difficultyLocked;

	public DifficultyS2CPacket(Difficulty difficulty, boolean difficultyLocked) {
		this.difficulty = difficulty;
		this.difficultyLocked = difficultyLocked;
	}

	private DifficultyS2CPacket(PacketByteBuf buf) {
		this.difficulty = Difficulty.byId(buf.readUnsignedByte());
		this.difficultyLocked = buf.readBoolean();
	}

	private void write(PacketByteBuf buf) {
		buf.writeByte(this.difficulty.getId());
		buf.writeBoolean(this.difficultyLocked);
	}

	@Override
	public PacketType<DifficultyS2CPacket> getPacketId() {
		return PlayPackets.CHANGE_DIFFICULTY_S2C;
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
