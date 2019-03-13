package net.minecraft.client.network.packet;

import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.util.PacketByteBuf;

public class ScoreboardPlayerUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private String playerName = "";
	@Nullable
	private String objectiveName;
	private int score;
	private ServerScoreboard.UpdateMode field_12612;

	public ScoreboardPlayerUpdateS2CPacket() {
	}

	public ScoreboardPlayerUpdateS2CPacket(ServerScoreboard.UpdateMode updateMode, @Nullable String string, String string2, int i) {
		if (updateMode != ServerScoreboard.UpdateMode.field_13430 && string == null) {
			throw new IllegalArgumentException("Need an objective name");
		} else {
			this.playerName = string2;
			this.objectiveName = string;
			this.score = i;
			this.field_12612 = updateMode;
		}
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.playerName = packetByteBuf.readString(40);
		this.field_12612 = packetByteBuf.readEnumConstant(ServerScoreboard.UpdateMode.class);
		String string = packetByteBuf.readString(16);
		this.objectiveName = Objects.equals(string, "") ? null : string;
		if (this.field_12612 != ServerScoreboard.UpdateMode.field_13430) {
			this.score = packetByteBuf.readVarInt();
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeString(this.playerName);
		packetByteBuf.writeEnumConstant(this.field_12612);
		packetByteBuf.writeString(this.objectiveName == null ? "" : this.objectiveName);
		if (this.field_12612 != ServerScoreboard.UpdateMode.field_13430) {
			packetByteBuf.writeVarInt(this.score);
		}
	}

	public void method_11866(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11118(this);
	}

	@Environment(EnvType.CLIENT)
	public String getPlayerName() {
		return this.playerName;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public String getObjectiveName() {
		return this.objectiveName;
	}

	@Environment(EnvType.CLIENT)
	public int getScore() {
		return this.score;
	}

	@Environment(EnvType.CLIENT)
	public ServerScoreboard.UpdateMode method_11863() {
		return this.field_12612;
	}
}
