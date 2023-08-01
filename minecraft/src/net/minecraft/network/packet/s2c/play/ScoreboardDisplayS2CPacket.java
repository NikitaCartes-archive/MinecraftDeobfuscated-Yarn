package net.minecraft.network.packet.s2c.play;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;

public class ScoreboardDisplayS2CPacket implements Packet<ClientPlayPacketListener> {
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

	public ScoreboardDisplayS2CPacket(PacketByteBuf buf) {
		this.slot = buf.decode(ScoreboardDisplaySlot.FROM_ID);
		this.name = buf.readString();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.encode(ScoreboardDisplaySlot::getId, this.slot);
		buf.writeString(this.name);
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
