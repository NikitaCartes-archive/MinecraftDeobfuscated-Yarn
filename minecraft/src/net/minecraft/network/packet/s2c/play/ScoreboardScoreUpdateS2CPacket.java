package net.minecraft.network.packet.s2c.play;

import java.util.Optional;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.NumberFormatTypes;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public record ScoreboardScoreUpdateS2CPacket(
	String scoreHolderName, String objectiveName, int score, Optional<Text> display, Optional<NumberFormat> numberFormat
) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, ScoreboardScoreUpdateS2CPacket> CODEC = PacketCodec.tuple(
		PacketCodecs.STRING,
		ScoreboardScoreUpdateS2CPacket::scoreHolderName,
		PacketCodecs.STRING,
		ScoreboardScoreUpdateS2CPacket::objectiveName,
		PacketCodecs.VAR_INT,
		ScoreboardScoreUpdateS2CPacket::score,
		TextCodecs.OPTIONAL_UNLIMITED_REGISTRY_PACKET_CODEC,
		ScoreboardScoreUpdateS2CPacket::display,
		NumberFormatTypes.OPTIONAL_PACKET_CODEC,
		ScoreboardScoreUpdateS2CPacket::numberFormat,
		ScoreboardScoreUpdateS2CPacket::new
	);

	@Override
	public PacketType<ScoreboardScoreUpdateS2CPacket> getPacketId() {
		return PlayPackets.SET_SCORE;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onScoreboardScoreUpdate(this);
	}
}
