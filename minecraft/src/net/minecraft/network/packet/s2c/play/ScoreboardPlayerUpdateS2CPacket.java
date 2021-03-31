package net.minecraft.network.packet.s2c.play;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.scoreboard.ServerScoreboard;

public class ScoreboardPlayerUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private final String playerName;
	@Nullable
	private final String objectiveName;
	private final int score;
	private final ServerScoreboard.UpdateMode mode;

	public ScoreboardPlayerUpdateS2CPacket(ServerScoreboard.UpdateMode mode, @Nullable String objectiveName, String playerName, int score) {
		if (mode != ServerScoreboard.UpdateMode.REMOVE && objectiveName == null) {
			throw new IllegalArgumentException("Need an objective name");
		} else {
			this.playerName = playerName;
			this.objectiveName = objectiveName;
			this.score = score;
			this.mode = mode;
		}
	}

	public ScoreboardPlayerUpdateS2CPacket(PacketByteBuf buf) {
		this.playerName = buf.readString(40);
		this.mode = buf.readEnumConstant(ServerScoreboard.UpdateMode.class);
		String string = buf.readString(16);
		this.objectiveName = Objects.equals(string, "") ? null : string;
		if (this.mode != ServerScoreboard.UpdateMode.REMOVE) {
			this.score = buf.readVarInt();
		} else {
			this.score = 0;
		}
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeString(this.playerName);
		buf.writeEnumConstant(this.mode);
		buf.writeString(this.objectiveName == null ? "" : this.objectiveName);
		if (this.mode != ServerScoreboard.UpdateMode.REMOVE) {
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
		return this.mode;
	}
}
