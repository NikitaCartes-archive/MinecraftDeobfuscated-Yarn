package net.minecraft.network.packet.s2c.play;

import java.util.Optional;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.NumberFormatTypes;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public class ScoreboardObjectiveUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, ScoreboardObjectiveUpdateS2CPacket> CODEC = Packet.createCodec(
		ScoreboardObjectiveUpdateS2CPacket::write, ScoreboardObjectiveUpdateS2CPacket::new
	);
	public static final int ADD_MODE = 0;
	public static final int REMOVE_MODE = 1;
	public static final int UPDATE_MODE = 2;
	private final String name;
	private final Text displayName;
	private final ScoreboardCriterion.RenderType type;
	private final Optional<NumberFormat> numberFormat;
	private final int mode;

	public ScoreboardObjectiveUpdateS2CPacket(ScoreboardObjective objective, int mode) {
		this.name = objective.getName();
		this.displayName = objective.getDisplayName();
		this.type = objective.getRenderType();
		this.numberFormat = Optional.ofNullable(objective.getNumberFormat());
		this.mode = mode;
	}

	private ScoreboardObjectiveUpdateS2CPacket(RegistryByteBuf buf) {
		this.name = buf.readString();
		this.mode = buf.readByte();
		if (this.mode != 0 && this.mode != 2) {
			this.displayName = ScreenTexts.EMPTY;
			this.type = ScoreboardCriterion.RenderType.INTEGER;
			this.numberFormat = Optional.empty();
		} else {
			this.displayName = TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.decode(buf);
			this.type = buf.readEnumConstant(ScoreboardCriterion.RenderType.class);
			this.numberFormat = NumberFormatTypes.OPTIONAL_PACKET_CODEC.decode(buf);
		}
	}

	private void write(RegistryByteBuf buf) {
		buf.writeString(this.name);
		buf.writeByte(this.mode);
		if (this.mode == 0 || this.mode == 2) {
			TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.encode(buf, this.displayName);
			buf.writeEnumConstant(this.type);
			NumberFormatTypes.OPTIONAL_PACKET_CODEC.encode(buf, this.numberFormat);
		}
	}

	@Override
	public PacketType<ScoreboardObjectiveUpdateS2CPacket> getPacketId() {
		return PlayPackets.SET_OBJECTIVE;
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

	public Optional<NumberFormat> getNumberFormat() {
		return this.numberFormat;
	}
}
