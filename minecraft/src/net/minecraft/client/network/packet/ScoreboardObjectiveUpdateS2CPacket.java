package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import net.minecraft.util.PacketByteBuf;

public class ScoreboardObjectiveUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private String name;
	private Text displayName;
	private ScoreboardCriterion.RenderType type;
	private int mode;

	public ScoreboardObjectiveUpdateS2CPacket() {
	}

	public ScoreboardObjectiveUpdateS2CPacket(ScoreboardObjective scoreboardObjective, int i) {
		this.name = scoreboardObjective.getName();
		this.displayName = scoreboardObjective.method_1114();
		this.type = scoreboardObjective.getRenderType();
		this.mode = i;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.name = packetByteBuf.readString(16);
		this.mode = packetByteBuf.readByte();
		if (this.mode == 0 || this.mode == 2) {
			this.displayName = packetByteBuf.method_10808();
			this.type = packetByteBuf.readEnumConstant(ScoreboardCriterion.RenderType.class);
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeString(this.name);
		packetByteBuf.writeByte(this.mode);
		if (this.mode == 0 || this.mode == 2) {
			packetByteBuf.method_10805(this.displayName);
			packetByteBuf.writeEnumConstant(this.type);
		}
	}

	public void method_11838(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onScoreboardObjectiveUpdate(this);
	}

	@Environment(EnvType.CLIENT)
	public String getName() {
		return this.name;
	}

	@Environment(EnvType.CLIENT)
	public Text getDisplayName() {
		return this.displayName;
	}

	@Environment(EnvType.CLIENT)
	public int getMode() {
		return this.mode;
	}

	@Environment(EnvType.CLIENT)
	public ScoreboardCriterion.RenderType getType() {
		return this.type;
	}
}
