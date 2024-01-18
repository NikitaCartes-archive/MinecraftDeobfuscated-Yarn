package net.minecraft.network.packet.c2s.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.world.Difficulty;

public class UpdateDifficultyC2SPacket implements Packet<ServerPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, UpdateDifficultyC2SPacket> CODEC = Packet.createCodec(
		UpdateDifficultyC2SPacket::write, UpdateDifficultyC2SPacket::new
	);
	private final Difficulty difficulty;

	public UpdateDifficultyC2SPacket(Difficulty difficulty) {
		this.difficulty = difficulty;
	}

	private UpdateDifficultyC2SPacket(PacketByteBuf buf) {
		this.difficulty = Difficulty.byId(buf.readUnsignedByte());
	}

	private void write(PacketByteBuf buf) {
		buf.writeByte(this.difficulty.getId());
	}

	@Override
	public PacketType<UpdateDifficultyC2SPacket> getPacketId() {
		return PlayPackets.CHANGE_DIFFICULTY_C2S;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onUpdateDifficulty(this);
	}

	public Difficulty getDifficulty() {
		return this.difficulty;
	}
}
