package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class ScoreboardObjectiveUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private final String name;
	private final Text displayName;
	private final ScoreboardCriterion.RenderType type;
	private final int mode;

	public ScoreboardObjectiveUpdateS2CPacket(ScoreboardObjective objective, int mode) {
		this.name = objective.getName();
		this.displayName = objective.getDisplayName();
		this.type = objective.getRenderType();
		this.mode = mode;
	}

	public ScoreboardObjectiveUpdateS2CPacket(PacketByteBuf packetByteBuf) {
		this.name = packetByteBuf.readString(16);
		this.mode = packetByteBuf.readByte();
		if (this.mode != 0 && this.mode != 2) {
			this.displayName = LiteralText.EMPTY;
			this.type = ScoreboardCriterion.RenderType.INTEGER;
		} else {
			this.displayName = packetByteBuf.readText();
			this.type = packetByteBuf.readEnumConstant(ScoreboardCriterion.RenderType.class);
		}
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeString(this.name);
		buf.writeByte(this.mode);
		if (this.mode == 0 || this.mode == 2) {
			buf.writeText(this.displayName);
			buf.writeEnumConstant(this.type);
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
