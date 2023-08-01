package net.minecraft.network.packet.s2c.play;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.scoreboard.ServerScoreboard;

public class ScoreboardPlayerUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private final String playerName;
	@Nullable
	private final String objectiveName;
	private final int score;
	private final ServerScoreboard.UpdateMode updateMode;

	public ScoreboardPlayerUpdateS2CPacket(ServerScoreboard.UpdateMode updateMode, @Nullable String objectiveName, String playerName, int score) {
		if (updateMode != ServerScoreboard.UpdateMode.REMOVE && objectiveName == null) {
			throw new IllegalArgumentException("Need an objective name");
		} else {
			this.playerName = playerName;
			this.objectiveName = objectiveName;
			this.score = score;
			this.updateMode = updateMode;
		}
	}

	public ScoreboardPlayerUpdateS2CPacket(PacketByteBuf buf) {
		this.playerName = buf.readString();
		this.updateMode = buf.readEnumConstant(ServerScoreboard.UpdateMode.class);
		String string = buf.readString();
		this.objectiveName = Objects.equals(string, "") ? null : string;
		if (this.updateMode != ServerScoreboard.UpdateMode.REMOVE) {
			this.score = buf.readVarInt();
		} else {
			this.score = 0;
		}
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeString(this.playerName);
		buf.writeEnumConstant(this.updateMode);
		buf.writeString(this.objectiveName == null ? "" : this.objectiveName);
		if (this.updateMode != ServerScoreboard.UpdateMode.REMOVE) {
			buf.writeVarInt(this.score);
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onScoreboardPlayerUpdate(this);
	}

	public String getPlayerName() {
		return this.playerName;
	}

	@Nullable
	public String getObjectiveName() {
		return this.objectiveName;
	}

	public int getScore() {
		return this.score;
	}

	public ServerScoreboard.UpdateMode getUpdateMode() {
		return this.updateMode;
	}
}
