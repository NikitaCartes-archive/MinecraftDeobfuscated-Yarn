package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TeamS2CPacket implements Packet<ClientPlayPacketListener> {
	/**
	 * <ul><li>0 - Create/Update team with players</li>
	 * <li>1 - Remove player/team</li>
	 * <li>2 - Update team</li>
	 * <li>3 - Change player team</li></ul>
	 */
	private final int packetType;
	private final String teamName;
	private final Collection<String> playerNames;
	private final Optional<TeamS2CPacket.SerializableTeam> team;

	private TeamS2CPacket(String teamName, int packetType, Optional<TeamS2CPacket.SerializableTeam> team, Collection<String> playerNames) {
		this.teamName = teamName;
		this.packetType = packetType;
		this.team = team;
		this.playerNames = ImmutableList.<String>copyOf(playerNames);
	}

	public static TeamS2CPacket updateTeam(Team team, boolean updatePlayers) {
		return new TeamS2CPacket(
			team.getName(),
			updatePlayers ? 0 : 2,
			Optional.of(new TeamS2CPacket.SerializableTeam(team)),
			(Collection<String>)(updatePlayers ? team.getPlayerList() : ImmutableList.<String>of())
		);
	}

	public static TeamS2CPacket updateRemovedTeam(Team team) {
		return new TeamS2CPacket(team.getName(), 1, Optional.empty(), ImmutableList.<String>of());
	}

	public static TeamS2CPacket changePlayerTeam(Team team, String playerName, TeamS2CPacket.Operation operation) {
		return new TeamS2CPacket(team.getName(), operation == TeamS2CPacket.Operation.ADD ? 3 : 4, Optional.empty(), ImmutableList.<String>of(playerName));
	}

	public TeamS2CPacket(PacketByteBuf buf) {
		this.teamName = buf.readString(16);
		this.packetType = buf.readByte();
		if (containsTeamInfo(this.packetType)) {
			this.team = Optional.of(new TeamS2CPacket.SerializableTeam(buf));
		} else {
			this.team = Optional.empty();
		}

		if (containsPlayers(this.packetType)) {
			this.playerNames = buf.<String>readList(PacketByteBuf::readString);
		} else {
			this.playerNames = ImmutableList.<String>of();
		}
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeString(this.teamName);
		buf.writeByte(this.packetType);
		if (containsTeamInfo(this.packetType)) {
			((TeamS2CPacket.SerializableTeam)this.team.orElseThrow(() -> new IllegalStateException("Parameters not present, but method is" + this.packetType)))
				.write(buf);
		}

		if (containsPlayers(this.packetType)) {
			buf.writeCollection(this.playerNames, PacketByteBuf::writeString);
		}
	}

	private static boolean containsPlayers(int packetType) {
		return packetType == 0 || packetType == 3 || packetType == 4;
	}

	private static boolean containsTeamInfo(int packetType) {
		return packetType == 0 || packetType == 2;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public TeamS2CPacket.Operation getPlayerListOperation() {
		switch (this.packetType) {
			case 0:
			case 3:
				return TeamS2CPacket.Operation.ADD;
			case 1:
			case 2:
			default:
				return null;
			case 4:
				return TeamS2CPacket.Operation.REMOVE;
		}
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public TeamS2CPacket.Operation getTeamOperation() {
		switch (this.packetType) {
			case 0:
				return TeamS2CPacket.Operation.ADD;
			case 1:
				return TeamS2CPacket.Operation.REMOVE;
			default:
				return null;
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onTeam(this);
	}

	@Environment(EnvType.CLIENT)
	public String getTeamName() {
		return this.teamName;
	}

	@Environment(EnvType.CLIENT)
	public Collection<String> getPlayerNames() {
		return this.playerNames;
	}

	@Environment(EnvType.CLIENT)
	public Optional<TeamS2CPacket.SerializableTeam> getTeam() {
		return this.team;
	}

	public static enum Operation {
		ADD,
		REMOVE;
	}

	public static class SerializableTeam {
		private final Text displayName;
		private final Text prefix;
		private final Text suffix;
		private final String nameTagVisibilityRule;
		private final String collisionRule;
		private final Formatting color;
		private final int friendlyFlags;

		public SerializableTeam(Team team) {
			this.displayName = team.getDisplayName();
			this.friendlyFlags = team.getFriendlyFlagsBitwise();
			this.nameTagVisibilityRule = team.getNameTagVisibilityRule().name;
			this.collisionRule = team.getCollisionRule().name;
			this.color = team.getColor();
			this.prefix = team.getPrefix();
			this.suffix = team.getSuffix();
		}

		public SerializableTeam(PacketByteBuf buf) {
			this.displayName = buf.readText();
			this.friendlyFlags = buf.readByte();
			this.nameTagVisibilityRule = buf.readString(40);
			this.collisionRule = buf.readString(40);
			this.color = buf.readEnumConstant(Formatting.class);
			this.prefix = buf.readText();
			this.suffix = buf.readText();
		}

		@Environment(EnvType.CLIENT)
		public Text getDisplayName() {
			return this.displayName;
		}

		@Environment(EnvType.CLIENT)
		public int getFriendlyFlagsBitwise() {
			return this.friendlyFlags;
		}

		@Environment(EnvType.CLIENT)
		public Formatting getColor() {
			return this.color;
		}

		@Environment(EnvType.CLIENT)
		public String getNameTagVisibilityRule() {
			return this.nameTagVisibilityRule;
		}

		@Environment(EnvType.CLIENT)
		public String getCollisionRule() {
			return this.collisionRule;
		}

		@Environment(EnvType.CLIENT)
		public Text getPrefix() {
			return this.prefix;
		}

		@Environment(EnvType.CLIENT)
		public Text getSuffix() {
			return this.suffix;
		}

		public void write(PacketByteBuf buf) {
			buf.writeText(this.displayName);
			buf.writeByte(this.friendlyFlags);
			buf.writeString(this.nameTagVisibilityRule);
			buf.writeString(this.collisionRule);
			buf.writeEnumConstant(this.color);
			buf.writeText(this.prefix);
			buf.writeText(this.suffix);
		}
	}
}
