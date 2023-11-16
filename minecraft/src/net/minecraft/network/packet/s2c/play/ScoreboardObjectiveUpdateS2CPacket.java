package net.minecraft.network.packet.s2c.play;

import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.NumberFormatTypes;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class ScoreboardObjectiveUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final int ADD_MODE = 0;
	public static final int REMOVE_MODE = 1;
	public static final int UPDATE_MODE = 2;
	private final String name;
	private final Text displayName;
	private final ScoreboardCriterion.RenderType type;
	@Nullable
	private final NumberFormat numberFormat;
	private final int mode;

	public ScoreboardObjectiveUpdateS2CPacket(ScoreboardObjective objective, int mode) {
		this.name = objective.getName();
		this.displayName = objective.getDisplayName();
		this.type = objective.getRenderType();
		this.numberFormat = objective.getNumberFormat();
		this.mode = mode;
	}

	public ScoreboardObjectiveUpdateS2CPacket(PacketByteBuf buf) {
		this.name = buf.readString();
		this.mode = buf.readByte();
		if (this.mode != 0 && this.mode != 2) {
			this.displayName = ScreenTexts.EMPTY;
			this.type = ScoreboardCriterion.RenderType.INTEGER;
			this.numberFormat = null;
		} else {
			this.displayName = buf.readUnlimitedText();
			this.type = buf.readEnumConstant(ScoreboardCriterion.RenderType.class);
			this.numberFormat = buf.readNullable(NumberFormatTypes::fromBuf);
		}
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeString(this.name);
		buf.writeByte(this.mode);
		if (this.mode == 0 || this.mode == 2) {
			buf.writeText(this.displayName);
			buf.writeEnumConstant(this.type);
			buf.writeNullable(this.numberFormat, NumberFormatTypes::toBuf);
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onScoreboardObjectiveUpdate(this);
	}

	public String getName() {
		return this.name;
	}

	public Text getDisplayName() {
		return this.displayName;
	}

	public int getMode() {
		return this.mode;
	}

	public ScoreboardCriterion.RenderType getType() {
		return this.type;
	}

	@Nullable
	public NumberFormat getNumberFormat() {
		return this.numberFormat;
	}
}
