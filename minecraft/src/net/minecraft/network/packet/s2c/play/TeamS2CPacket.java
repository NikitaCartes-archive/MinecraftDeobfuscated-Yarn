package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Formatting;

public class TeamS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, TeamS2CPacket> CODEC = Packet.createCodec(TeamS2CPacket::write, TeamS2CPacket::new);
	/**
	 * The {@link #packetType} that creates a new team with a few players. Has value
	 * {@value}.
	 */
	private static final int ADD = 0;
	/**
	 * The {@link #packetType} that removes a team. Has value {@value}.
	 */
	private static final int REMOVE = 1;
	/**
	 * The {@link #packetType} that updates a team's information. Has value {@value}.
	 */
	private static final int UPDATE = 2;
	/**
	 * The {@link #packetType} that adds a few players to a team. Has value {@value}.
	 */
	private static final int ADD_PLAYERS = 3;
	/**
	 * The {@link #packetType} that removes a few players from a team. Has value {@value}.
	 */
	private static final int REMOVE_PLAYERS = 4;
	/**
	 * One of the name tag visibility rule or collision rule strings' max length.
	 * Has value {@value}.
	 */
	private static final int FIRST_MAX_VISIBILITY_OR_COLLISION_RULE_LENGTH = 40;
	/**
	 * One of the name tag visibility rule or collision rule strings' max length.
	 * Has value {@value}.
	 */
	private static final int SECOND_MAX_VISIBILITY_OR_COLLISION_RULE_LENGTH = 40;
	/**
	 * Indicates the type of this packet. Is one of {@link #ADD}, {@link #REMOVE},
	 * {@link #UPDATE}, {@link #ADD_PLAYERS}, or {@link #REMOVE_PLAYERS}.
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
			updatePlayers ? ADD : UPDATE,
			Optional.of(new TeamS2CPacket.SerializableTeam(team)),
			(Collection<String>)(updatePlayers ? team.getPlayerList() : ImmutableList.<String>of())
		);
	}

	public static TeamS2CPacket updateRemovedTeam(Team team) {
		return new TeamS2CPacket(team.getName(), REMOVE, Optional.empty(), ImmutableList.<String>of());
	}

	public static TeamS2CPacket changePlayerTeam(Team team, String playerName, TeamS2CPacket.Operation operation) {
		return new TeamS2CPacket(
			team.getName(), operation == TeamS2CPacket.Operation.ADD ? ADD_PLAYERS : REMOVE_PLAYERS, Optional.empty(), ImmutableList.<String>of(playerName)
		);
	}

	private TeamS2CPacket(RegistryByteBuf buf) {
		this.teamName = buf.readString();
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

	private void write(RegistryByteBuf buf) {
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
		return packetType == 0 || packetType == ADD_PLAYERS || packetType == REMOVE_PLAYERS;
	}

	private static boolean containsTeamInfo(int packetType) {
		return packetType == 0 || packetType == UPDATE;
	}

	@Nullable
	public TeamS2CPacket.Operation getPlayerListOperation() {
		return switch (this.packetType) {
			case 0, 3 -> TeamS2CPacket.Operation.ADD;
			default -> null;
			case 4 -> TeamS2CPacket.Operation.REMOVE;
		};
	}

	@Nullable
	public TeamS2CPacket.Operation getTeamOperation() {
		return switch (this.packetType) {
			case 0 -> TeamS2CPacket.Operation.ADD;
			case 1 -> TeamS2CPacket.Operation.REMOVE;
			default -> null;
		};
	}

	@Override
	public PacketType<TeamS2CPacket> getPacketId() {
		return PlayPackets.SET_PLAYER_TEAM;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onTeam(this);
	}

	public String getTeamName() {
		return this.teamName;
	}

	public Collection<String> getPlayerNames() {
		return this.playerNames;
	}

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

		public SerializableTeam(RegistryByteBuf buf) {
			this.displayName = TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.decode(buf);
			this.friendlyFlags = buf.readByte();
			this.nameTagVisibilityRule = buf.readString(40);
			this.collisionRule = buf.readString(40);
			this.color = buf.readEnumConstant(Formatting.class);
			this.prefix = TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.decode(buf);
			this.suffix = TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.decode(buf);
		}

		public Text getDisplayName() {
			return this.displayName;
		}

		public int getFriendlyFlagsBitwise() {
			return this.friendlyFlags;
		}

		public Formatting getColor() {
			return this.color;
		}

		public String getNameTagVisibilityRule() {
			return this.nameTagVisibilityRule;
		}

		public String getCollisionRule() {
			return this.collisionRule;
		}

		public Text getPrefix() {
			return this.prefix;
		}

		public Text getSuffix() {
			return this.suffix;
		}

		public void write(RegistryByteBuf buf) {
			TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.encode(buf, this.displayName);
			buf.writeByte(this.friendlyFlags);
			buf.writeString(this.nameTagVisibilityRule);
			buf.writeString(this.collisionRule);
			buf.writeEnumConstant(this.color);
			TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.encode(buf, this.prefix);
			TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC.encode(buf, this.suffix);
		}
	}
}
