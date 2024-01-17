package net.minecraft.network.packet.s2c.play;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.RegistryByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketIdentifier;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.NumberFormatTypes;
import net.minecraft.text.Text;

public record ScoreboardScoreUpdateS2CPacket(
	String scoreHolderName, String objectiveName, int score, @Nullable Text display, Optional<NumberFormat> numberFormat
) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, ScoreboardScoreUpdateS2CPacket> CODEC = Packet.createCodec(
		ScoreboardScoreUpdateS2CPacket::write, ScoreboardScoreUpdateS2CPacket::new
	);

	private ScoreboardScoreUpdateS2CPacket(RegistryByteBuf buf) {
		this(
			buf.readString(),
			buf.readString(),
			buf.readVarInt(),
			buf.readNullable(PacketByteBuf::readUnlimitedText),
			NumberFormatTypes.OPTIONAL_PACKET_CODEC.decode(buf)
		);
	}

	private void write(RegistryByteBuf buf) {
		buf.writeString(this.scoreHolderName);
		buf.writeString(this.objectiveName);
		buf.writeVarInt(this.score);
		buf.writeNullable(this.display, PacketByteBuf::writeText);
		NumberFormatTypes.OPTIONAL_PACKET_CODEC.encode(buf, this.numberFormat);
	}

	@Override
	public PacketIdentifier<ScoreboardScoreUpdateS2CPacket> getPacketId() {
		return PlayPackets.SET_SCORE;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onScoreboardScoreUpdate(this);
	}
}
