/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.util.Collection;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

public class PlayerListS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final Action action;
    private final List<Entry> entries;

    public PlayerListS2CPacket(Action action, ServerPlayerEntity ... players) {
        this.action = action;
        this.entries = Lists.newArrayListWithCapacity(players.length);
        for (ServerPlayerEntity serverPlayerEntity : players) {
            this.entries.add(new Entry(serverPlayerEntity.getGameProfile(), serverPlayerEntity.pingMilliseconds, serverPlayerEntity.interactionManager.getGameMode(), serverPlayerEntity.getPlayerListName()));
        }
    }

    public PlayerListS2CPacket(Action action, Collection<ServerPlayerEntity> players) {
        this.action = action;
        this.entries = Lists.newArrayListWithCapacity(players.size());
        for (ServerPlayerEntity serverPlayerEntity : players) {
            this.entries.add(new Entry(serverPlayerEntity.getGameProfile(), serverPlayerEntity.pingMilliseconds, serverPlayerEntity.interactionManager.getGameMode(), serverPlayerEntity.getPlayerListName()));
        }
    }

    public PlayerListS2CPacket(PacketByteBuf buf) {
        this.action = buf.readEnumConstant(Action.class);
        this.entries = buf.readList(this.action::read);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeEnumConstant(this.action);
        buf.writeCollection(this.entries, this.action::write);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onPlayerList(this);
    }

    public List<Entry> getEntries() {
        return this.entries;
    }

    public Action getAction() {
        return this.action;
    }

    @Nullable
    static Text readOptionalText(PacketByteBuf buf) {
        return buf.readBoolean() ? buf.readText() : null;
    }

    static void writeOptionalText(PacketByteBuf buf, @Nullable Text text) {
        if (text == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            buf.writeText(text);
        }
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("action", (Object)this.action).add("entries", this.entries).toString();
    }

    /*
     * Uses 'sealed' constructs - enablewith --sealed true
     */
    public static enum Action {
        ADD_PLAYER{

            @Override
            protected Entry read(PacketByteBuf buf) {
                GameProfile gameProfile = buf.readGameProfile();
                GameMode gameMode = GameMode.byId(buf.readVarInt());
                int i = buf.readVarInt();
                Text text = PlayerListS2CPacket.readOptionalText(buf);
                return new Entry(gameProfile, i, gameMode, text);
            }

            @Override
            protected void write(PacketByteBuf buf, Entry entry) {
                buf.writeGameProfile(entry.getProfile());
                buf.writeVarInt(entry.getGameMode().getId());
                buf.writeVarInt(entry.getLatency());
                PlayerListS2CPacket.writeOptionalText(buf, entry.getDisplayName());
            }
        }
        ,
        UPDATE_GAME_MODE{

            @Override
            protected Entry read(PacketByteBuf buf) {
                GameProfile gameProfile = new GameProfile(buf.readUuid(), null);
                GameMode gameMode = GameMode.byId(buf.readVarInt());
                return new Entry(gameProfile, 0, gameMode, null);
            }

            @Override
            protected void write(PacketByteBuf buf, Entry entry) {
                buf.writeUuid(entry.getProfile().getId());
                buf.writeVarInt(entry.getGameMode().getId());
            }
        }
        ,
        UPDATE_LATENCY{

            @Override
            protected Entry read(PacketByteBuf buf) {
                GameProfile gameProfile = new GameProfile(buf.readUuid(), null);
                int i = buf.readVarInt();
                return new Entry(gameProfile, i, null, null);
            }

            @Override
            protected void write(PacketByteBuf buf, Entry entry) {
                buf.writeUuid(entry.getProfile().getId());
                buf.writeVarInt(entry.getLatency());
            }
        }
        ,
        UPDATE_DISPLAY_NAME{

            @Override
            protected Entry read(PacketByteBuf buf) {
                GameProfile gameProfile = new GameProfile(buf.readUuid(), null);
                Text text = PlayerListS2CPacket.readOptionalText(buf);
                return new Entry(gameProfile, 0, null, text);
            }

            @Override
            protected void write(PacketByteBuf buf, Entry entry) {
                buf.writeUuid(entry.getProfile().getId());
                PlayerListS2CPacket.writeOptionalText(buf, entry.getDisplayName());
            }
        }
        ,
        REMOVE_PLAYER{

            @Override
            protected Entry read(PacketByteBuf buf) {
                GameProfile gameProfile = new GameProfile(buf.readUuid(), null);
                return new Entry(gameProfile, 0, null, null);
            }

            @Override
            protected void write(PacketByteBuf buf, Entry entry) {
                buf.writeUuid(entry.getProfile().getId());
            }
        };


        protected abstract Entry read(PacketByteBuf var1);

        protected abstract void write(PacketByteBuf var1, Entry var2);
    }

    public static class Entry {
        private final int latency;
        private final GameMode gameMode;
        private final GameProfile profile;
        @Nullable
        private final Text displayName;

        public Entry(GameProfile profile, int latency, @Nullable GameMode gameMode, @Nullable Text displayName) {
            this.profile = profile;
            this.latency = latency;
            this.gameMode = gameMode;
            this.displayName = displayName;
        }

        public GameProfile getProfile() {
            return this.profile;
        }

        public int getLatency() {
            return this.latency;
        }

        public GameMode getGameMode() {
            return this.gameMode;
        }

        @Nullable
        public Text getDisplayName() {
            return this.displayName;
        }

        public String toString() {
            return MoreObjects.toStringHelper(this).add("latency", this.latency).add("gameMode", (Object)this.gameMode).add("profile", this.profile).add("displayName", this.displayName == null ? null : Text.Serializer.toJson(this.displayName)).toString();
        }
    }
}

