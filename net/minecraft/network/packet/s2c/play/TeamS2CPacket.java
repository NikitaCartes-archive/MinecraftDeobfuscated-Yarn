/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

public class TeamS2CPacket
implements Packet<ClientPlayPacketListener> {
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
    private final Optional<SerializableTeam> team;

    private TeamS2CPacket(String teamName, int packetType, Optional<SerializableTeam> team, Collection<String> playerNames) {
        this.teamName = teamName;
        this.packetType = packetType;
        this.team = team;
        this.playerNames = ImmutableList.copyOf(playerNames);
    }

    public static TeamS2CPacket updateTeam(Team team, boolean updatePlayers) {
        return new TeamS2CPacket(team.getName(), updatePlayers ? ADD : UPDATE, Optional.of(new SerializableTeam(team)), updatePlayers ? team.getPlayerList() : ImmutableList.of());
    }

    public static TeamS2CPacket updateRemovedTeam(Team team) {
        return new TeamS2CPacket(team.getName(), REMOVE, Optional.empty(), ImmutableList.of());
    }

    public static TeamS2CPacket changePlayerTeam(Team team, String playerName, Operation operation) {
        return new TeamS2CPacket(team.getName(), operation == Operation.ADD ? ADD_PLAYERS : REMOVE_PLAYERS, Optional.empty(), ImmutableList.of(playerName));
    }

    public TeamS2CPacket(PacketByteBuf buf) {
        this.teamName = buf.readString(16);
        this.packetType = buf.readByte();
        this.team = TeamS2CPacket.containsTeamInfo(this.packetType) ? Optional.of(new SerializableTeam(buf)) : Optional.empty();
        this.playerNames = TeamS2CPacket.containsPlayers(this.packetType) ? buf.readList(PacketByteBuf::readString) : ImmutableList.of();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeString(this.teamName);
        buf.writeByte(this.packetType);
        if (TeamS2CPacket.containsTeamInfo(this.packetType)) {
            this.team.orElseThrow(() -> new IllegalStateException("Parameters not present, but method is" + this.packetType)).write(buf);
        }
        if (TeamS2CPacket.containsPlayers(this.packetType)) {
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
    public Operation getPlayerListOperation() {
        switch (this.packetType) {
            case 0: 
            case 3: {
                return Operation.ADD;
            }
            case 4: {
                return Operation.REMOVE;
            }
        }
        return null;
    }

    @Nullable
    public Operation getTeamOperation() {
        switch (this.packetType) {
            case 0: {
                return Operation.ADD;
            }
            case 1: {
                return Operation.REMOVE;
            }
        }
        return null;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onTeam(this);
    }

    public String getTeamName() {
        return this.teamName;
    }

    public Collection<String> getPlayerNames() {
        return this.playerNames;
    }

    public Optional<SerializableTeam> getTeam() {
        return this.team;
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

    public static enum Operation {
        ADD,
        REMOVE;

    }
}

