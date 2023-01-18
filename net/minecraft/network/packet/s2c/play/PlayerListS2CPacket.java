/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.base.MoreObjects;
import com.mojang.authlib.GameProfile;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

public class PlayerListS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final EnumSet<Action> actions;
    private final List<Entry> entries;

    public PlayerListS2CPacket(EnumSet<Action> actions, Collection<ServerPlayerEntity> players) {
        this.actions = actions;
        this.entries = players.stream().map(Entry::new).toList();
    }

    public PlayerListS2CPacket(Action action, ServerPlayerEntity player) {
        this.actions = EnumSet.of(action);
        this.entries = List.of(new Entry(player));
    }

    public static PlayerListS2CPacket entryFromPlayer(Collection<ServerPlayerEntity> players) {
        EnumSet<Action[]> enumSet = EnumSet.of(Action.ADD_PLAYER, new Action[]{Action.INITIALIZE_CHAT, Action.UPDATE_GAME_MODE, Action.UPDATE_LISTED, Action.UPDATE_LATENCY, Action.UPDATE_DISPLAY_NAME});
        return new PlayerListS2CPacket(enumSet, players);
    }

    public PlayerListS2CPacket(PacketByteBuf buf) {
        this.actions = buf.readEnumSet(Action.class);
        this.entries = buf.readList(buf2 -> {
            Serialized serialized = new Serialized(buf2.readUuid());
            for (Action action : this.actions) {
                action.reader.read(serialized, (PacketByteBuf)buf2);
            }
            return serialized.toEntry();
        });
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeEnumSet(this.actions, Action.class);
        buf.writeCollection(this.entries, (buf2, entry) -> {
            buf2.writeUuid(entry.profileId());
            for (Action action : this.actions) {
                action.writer.write((PacketByteBuf)buf2, (Entry)entry);
            }
        });
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onPlayerList(this);
    }

    public EnumSet<Action> getActions() {
        return this.actions;
    }

    public List<Entry> getEntries() {
        return this.entries;
    }

    public List<Entry> getPlayerAdditionEntries() {
        return this.actions.contains((Object)Action.ADD_PLAYER) ? this.entries : List.of();
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("actions", this.actions).add("entries", this.entries).toString();
    }

    public record Entry(UUID profileId, GameProfile profile, boolean listed, int latency, GameMode gameMode, @Nullable Text displayName, @Nullable PublicPlayerSession.Serialized chatSession) {
        Entry(ServerPlayerEntity player) {
            this(player.getUuid(), player.getGameProfile(), true, player.pingMilliseconds, player.interactionManager.getGameMode(), player.getPlayerListName(), Util.map(player.getSession(), PublicPlayerSession::toSerialized));
        }

        @Nullable
        public Text displayName() {
            return this.displayName;
        }

        @Nullable
        public PublicPlayerSession.Serialized chatSession() {
            return this.chatSession;
        }
    }

    public static enum Action {
        ADD_PLAYER((serialized, buf) -> {
            GameProfile gameProfile = new GameProfile(serialized.profileId, buf.readString(16));
            gameProfile.getProperties().putAll(buf.readPropertyMap());
            serialized.gameProfile = gameProfile;
        }, (buf, entry) -> {
            buf.writeString(entry.profile().getName(), 16);
            buf.writePropertyMap(entry.profile().getProperties());
        }),
        INITIALIZE_CHAT((serialized, buf) -> {
            serialized.session = (PublicPlayerSession.Serialized)buf.readNullable(PublicPlayerSession.Serialized::fromBuf);
        }, (buf, entry) -> buf.writeNullable(entry.chatSession, PublicPlayerSession.Serialized::write)),
        UPDATE_GAME_MODE((serialized, buf) -> {
            serialized.gameMode = GameMode.byId(buf.readVarInt());
        }, (buf, entry) -> buf.writeVarInt(entry.gameMode().getId())),
        UPDATE_LISTED((serialized, buf) -> {
            serialized.listed = buf.readBoolean();
        }, (buf, entry) -> buf.writeBoolean(entry.listed())),
        UPDATE_LATENCY((serialized, buf) -> {
            serialized.latency = buf.readVarInt();
        }, (buf, entry) -> buf.writeVarInt(entry.latency())),
        UPDATE_DISPLAY_NAME((serialized, buf) -> {
            serialized.displayName = (Text)buf.readNullable(PacketByteBuf::readText);
        }, (buf, entry) -> buf.writeNullable(entry.displayName(), PacketByteBuf::writeText));

        final Reader reader;
        final Writer writer;

        private Action(Reader reader, Writer writer) {
            this.reader = reader;
            this.writer = writer;
        }

        public static interface Reader {
            public void read(Serialized var1, PacketByteBuf var2);
        }

        public static interface Writer {
            public void write(PacketByteBuf var1, Entry var2);
        }
    }

    static class Serialized {
        final UUID profileId;
        GameProfile gameProfile;
        boolean listed;
        int latency;
        GameMode gameMode = GameMode.DEFAULT;
        @Nullable
        Text displayName;
        @Nullable
        PublicPlayerSession.Serialized session;

        Serialized(UUID profileId) {
            this.profileId = profileId;
            this.gameProfile = new GameProfile(profileId, null);
        }

        Entry toEntry() {
            return new Entry(this.profileId, this.gameProfile, this.listed, this.latency, this.gameMode, this.displayName, this.session);
        }
    }
}

