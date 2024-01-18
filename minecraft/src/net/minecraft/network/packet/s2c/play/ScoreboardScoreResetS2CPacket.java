package net.minecraft.network.packet.s2c.play;

import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record ScoreboardScoreResetS2CPacket(String scoreHolderName, @Nullable String objectiveName) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, ScoreboardScoreResetS2CPacket> CODEC = Packet.createCodec(
		ScoreboardScoreResetS2CPacket::write, ScoreboardScoreResetS2CPacket::new
	);

	private ScoreboardScoreResetS2CPacket(PacketByteBuf buf) {
		this(buf.readString(), buf.readNullable(PacketByteBuf::readString));
	}

	private void write(PacketByteBuf buf) {
		buf.writeString(this.scoreHolderName);
		buf.writeNullable(this.objectiveName, PacketByteBuf::writeString);
	}

	@Override
	public PacketType<ScoreboardScoreResetS2CPacket> getPacketId() {
		return PlayPackets.RESET_SCORE;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onScoreboardScoreReset(this);
	}
}
