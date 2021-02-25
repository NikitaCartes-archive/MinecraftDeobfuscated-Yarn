/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

public class TeamS2CPacket
implements Packet<ClientPlayPacketListener> {
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
        return new TeamS2CPacket(team.getName(), updatePlayers ? 0 : 2, Optional.of(new SerializableTeam(team)), updatePlayers ? team.getPlayerList() : ImmutableList.of());
    }

    public static TeamS2CPacket updateRemovedTeam(Team team) {
        return new TeamS2CPacket(team.getName(), 1, Optional.empty(), ImmutableList.of());
    }

    public static TeamS2CPacket changePlayerTeam(Team team, String playerName, Operation operation) {
        return new TeamS2CPacket(team.getName(), operation == Operation.ADD ? 3 : 1, Optional.empty(), ImmutableList.of(playerName));
    }

    public TeamS2CPacket(PacketByteBuf buf) {
        this.teamName = buf.readString(16);
        this.packetType = buf.readByte();
        this.team = TeamS2CPacket.method_34175(this.packetType) ? Optional.of(new SerializableTeam(buf)) : Optional.empty();
        this.playerNames = TeamS2CPacket.method_34169(this.packetType) ? buf.readList(PacketByteBuf::readString) : ImmutableList.of();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeString(this.teamName);
        buf.writeByte(this.packetType);
        if (TeamS2CPacket.method_34175(this.packetType)) {
            this.team.orElseThrow(() -> new IllegalStateException("Parameters not present, but method is" + this.packetType)).write(buf);
        }
        if (TeamS2CPacket.method_34169(this.packetType)) {
            buf.writeCollection(this.playerNames, PacketByteBuf::writeString);
        }
    }

    private static boolean method_34169(int i) {
        return i == 0 || i == 3 || i == 4;
    }

    private static boolean method_34175(int i) {
        return i == 0 || i == 2;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public Operation method_34174() {
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
    @Environment(value=EnvType.CLIENT)
    public Operation method_34176() {
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

    @Environment(value=EnvType.CLIENT)
    public String getTeamName() {
        return this.teamName;
    }

    @Environment(value=EnvType.CLIENT)
    public Collection<String> getPlayerNames() {
        return this.playerNames;
    }

    @Environment(value=EnvType.CLIENT)
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

        @Environment(value=EnvType.CLIENT)
        public Text getDisplayName() {
            return this.displayName;
        }

        @Environment(value=EnvType.CLIENT)
        public int getFriendlyFlagsBitwise() {
            return this.friendlyFlags;
        }

        @Environment(value=EnvType.CLIENT)
        public Formatting getColor() {
            return this.color;
        }

        @Environment(value=EnvType.CLIENT)
        public String getNameTagVisibilityRule() {
            return this.nameTagVisibilityRule;
        }

        @Environment(value=EnvType.CLIENT)
        public String getCollisionRule() {
            return this.collisionRule;
        }

        @Environment(value=EnvType.CLIENT)
        public Text getPrefix() {
            return this.prefix;
        }

        @Environment(value=EnvType.CLIENT)
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

