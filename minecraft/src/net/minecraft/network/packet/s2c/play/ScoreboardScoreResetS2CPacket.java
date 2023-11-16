package net.minecraft.network.packet.s2c.play;

import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;

public record ScoreboardScoreResetS2CPacket(String scoreHolderName, @Nullable String objectiveName) implements Packet<ClientPlayPacketListener> {
	public ScoreboardScoreResetS2CPacket(PacketByteBuf buf) {
		this(buf.readString(), buf.readNullable(PacketByteBuf::readString));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeString(this.scoreHolderName);
		buf.writeNullable(this.objectiveName, PacketByteBuf::writeString);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onScoreboardScoreReset(this);
	}
}
