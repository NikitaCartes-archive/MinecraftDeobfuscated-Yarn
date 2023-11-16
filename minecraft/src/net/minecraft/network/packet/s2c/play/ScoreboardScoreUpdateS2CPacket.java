package net.minecraft.network.packet.s2c.play;

import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.NumberFormatTypes;
import net.minecraft.text.Text;

public record ScoreboardScoreUpdateS2CPacket(
	String scoreHolderName, String objectiveName, int score, @Nullable Text display, @Nullable NumberFormat numberFormat
) implements Packet<ClientPlayPacketListener> {
	public ScoreboardScoreUpdateS2CPacket(PacketByteBuf buf) {
		this(buf.readString(), buf.readString(), buf.readVarInt(), buf.readNullable(PacketByteBuf::readUnlimitedText), buf.readNullable(NumberFormatTypes::fromBuf));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeString(this.scoreHolderName);
		buf.writeString(this.objectiveName);
		buf.writeVarInt(this.score);
		buf.writeNullable(this.display, PacketByteBuf::writeText);
		buf.writeNullable(this.numberFormat, NumberFormatTypes::toBuf);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onScoreboardScoreUpdate(this);
	}
}
