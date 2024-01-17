package net.minecraft.network.packet.s2c.play;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketIdentifier;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;

public class ScoreboardDisplayS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, ScoreboardDisplayS2CPacket> CODEC = Packet.createCodec(
		ScoreboardDisplayS2CPacket::write, ScoreboardDisplayS2CPacket::new
	);
	private final ScoreboardDisplaySlot slot;
	private final String name;

	public ScoreboardDisplayS2CPacket(ScoreboardDisplaySlot slot, @Nullable ScoreboardObjective objective) {
		this.slot = slot;
		if (objective == null) {
			this.name = "";
		} else {
			this.name = objective.getName();
		}
	}

	private ScoreboardDisplayS2CPacket(PacketByteBuf buf) {
		this.slot = buf.decode(ScoreboardDisplaySlot.FROM_ID);
		this.name = buf.readString();
	}

	private void write(PacketByteBuf buf) {
		buf.encode(ScoreboardDisplaySlot::getId, this.slot);
		buf.writeString(this.name);
	}

	@Override
	public PacketIdentifier<ScoreboardDisplayS2CPacket> getPacketId() {
		return PlayPackets.SET_DISPLAY_OBJECTIVE;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onScoreboardDisplay(this);
	}

	public ScoreboardDisplaySlot getSlot() {
		return this.slot;
	}

	@Nullable
	public String getName() {
		return Objects.equals(this.name, "") ? null : this.name;
	}
}
