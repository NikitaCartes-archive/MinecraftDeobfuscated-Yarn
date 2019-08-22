/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network.packet;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.PacketByteBuf;

public class TeamS2CPacket
implements Packet<ClientPlayPacketListener> {
    private String teamName = "";
    private Text displayName = new LiteralText("");
    private Text prefix = new LiteralText("");
    private Text suffix = new LiteralText("");
    private String nameTagVisibilityRule;
    private String collisionRule;
    private Formatting color;
    private final Collection<String> playerList;
    private int mode;
    private int flags;

    public TeamS2CPacket() {
        this.nameTagVisibilityRule = AbstractTeam.VisibilityRule.ALWAYS.name;
        this.collisionRule = AbstractTeam.CollisionRule.ALWAYS.name;
        this.color = Formatting.RESET;
        this.playerList = Lists.newArrayList();
    }

    public TeamS2CPacket(Team team, int i) {
        this.nameTagVisibilityRule = AbstractTeam.VisibilityRule.ALWAYS.name;
        this.collisionRule = AbstractTeam.CollisionRule.ALWAYS.name;
        this.color = Formatting.RESET;
        this.playerList = Lists.newArrayList();
        this.teamName = team.getName();
        this.mode = i;
        if (i == 0 || i == 2) {
            this.displayName = team.getDisplayName();
            this.flags = team.getFriendlyFlagsBitwise();
            this.nameTagVisibilityRule = team.getNameTagVisibilityRule().name;
            this.collisionRule = team.getCollisionRule().name;
            this.color = team.getColor();
            this.prefix = team.getPrefix();
            this.suffix = team.getSuffix();
        }
        if (i == 0) {
            this.playerList.addAll(team.getPlayerList());
        }
    }

    public TeamS2CPacket(Team team, Collection<String> collection, int i) {
        this.nameTagVisibilityRule = AbstractTeam.VisibilityRule.ALWAYS.name;
        this.collisionRule = AbstractTeam.CollisionRule.ALWAYS.name;
        this.color = Formatting.RESET;
        this.playerList = Lists.newArrayList();
        if (i != 3 && i != 4) {
            throw new IllegalArgumentException("Method must be join or leave for player constructor");
        }
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException("Players cannot be null/empty");
        }
        this.mode = i;
        this.teamName = team.getName();
        this.playerList.addAll(collection);
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        this.teamName = packetByteBuf.readString(16);
        this.mode = packetByteBuf.readByte();
        if (this.mode == 0 || this.mode == 2) {
            this.displayName = packetByteBuf.readText();
            this.flags = packetByteBuf.readByte();
            this.nameTagVisibilityRule = packetByteBuf.readString(40);
            this.collisionRule = packetByteBuf.readString(40);
            this.color = packetByteBuf.readEnumConstant(Formatting.class);
            this.prefix = packetByteBuf.readText();
            this.suffix = packetByteBuf.readText();
        }
        if (this.mode == 0 || this.mode == 3 || this.mode == 4) {
            int i = packetByteBuf.readVarInt();
            for (int j = 0; j < i; ++j) {
                this.playerList.add(packetByteBuf.readString(40));
            }
        }
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeString(this.teamName);
        packetByteBuf.writeByte(this.mode);
        if (this.mode == 0 || this.mode == 2) {
            packetByteBuf.writeText(this.displayName);
            packetByteBuf.writeByte(this.flags);
            packetByteBuf.writeString(this.nameTagVisibilityRule);
            packetByteBuf.writeString(this.collisionRule);
            packetByteBuf.writeEnumConstant(this.color);
            packetByteBuf.writeText(this.prefix);
            packetByteBuf.writeText(this.suffix);
        }
        if (this.mode == 0 || this.mode == 3 || this.mode == 4) {
            packetByteBuf.writeVarInt(this.playerList.size());
            for (String string : this.playerList) {
                packetByteBuf.writeString(string);
            }
        }
    }

    public void method_11860(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onTeam(this);
    }

    @Environment(value=EnvType.CLIENT)
    public String getTeamName() {
        return this.teamName;
    }

    @Environment(value=EnvType.CLIENT)
    public Text getDisplayName() {
        return this.displayName;
    }

    @Environment(value=EnvType.CLIENT)
    public Collection<String> getPlayerList() {
        return this.playerList;
    }

    @Environment(value=EnvType.CLIENT)
    public int getMode() {
        return this.mode;
    }

    @Environment(value=EnvType.CLIENT)
    public int getFlags() {
        return this.flags;
    }

    @Environment(value=EnvType.CLIENT)
    public Formatting getPlayerPrefix() {
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
}

