package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.TextComponent;
import net.minecraft.util.PacketByteBuf;

public class ScoreboardObjectiveUpdateClientPacket implements Packet<ClientPlayPacketListener> {
	private String name;
	private TextComponent field_12591;
	private ScoreboardCriterion.Type type;
	private int mode;

	public ScoreboardObjectiveUpdateClientPacket() {
	}

	public ScoreboardObjectiveUpdateClientPacket(ScoreboardObjective scoreboardObjective, int i) {
		this.name = scoreboardObjective.getName();
		this.field_12591 = scoreboardObjective.getDisplayName();
		this.type = scoreboardObjective.getCriterionType();
		this.mode = i;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.name = packetByteBuf.readString(16);
		this.mode = packetByteBuf.readByte();
		if (this.mode == 0 || this.mode == 2) {
			this.field_12591 = packetByteBuf.readTextComponent();
			this.type = packetByteBuf.readEnumConstant(ScoreboardCriterion.Type.class);
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeString(this.name);
		packetByteBuf.writeByte(this.mode);
		if (this.mode == 0 || this.mode == 2) {
			packetByteBuf.writeTextComponent(this.field_12591);
			packetByteBuf.writeEnumConstant(this.type);
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onScoreboardObjectiveUpdate(this);
	}

	@Environment(EnvType.CLIENT)
	public String getName() {
		return this.name;
	}

	@Environment(EnvType.CLIENT)
	public TextComponent getValue() {
		return this.field_12591;
	}

	@Environment(EnvType.CLIENT)
	public int getMode() {
		return this.mode;
	}

	@Environment(EnvType.CLIENT)
	public ScoreboardCriterion.Type getType() {
		return this.type;
	}
}
