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
import net.minecraft.network.encryption.PlayerPublicKey;
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
            this.entries.add(PlayerListS2CPacket.entryFromPlayer(serverPlayerEntity));
        }
    }

    public PlayerListS2CPacket(Action action, Collection<ServerPlayerEntity> players) {
        this.action = action;
        this.entries = Lists.newArrayListWithCapacity(players.size());
        for (ServerPlayerEntity serverPlayerEntity : players) {
            this.entries.add(PlayerListS2CPacket.entryFromPlayer(serverPlayerEntity));
        }
    }

    public PlayerListS2CPacket(PacketByteBuf buf) {
        this.action = buf.readEnumConstant(Action.class);
        this.entries = buf.readList(this.action::read);
    }

    private static Entry entryFromPlayer(ServerPlayerEntity player) {
        PlayerPublicKey playerPublicKey = player.getPublicKey();
        PlayerPublicKey.PublicKeyData publicKeyData = playerPublicKey != null ? playerPublicKey.data() : null;
        return new Entry(player.getGameProfile(), player.pingMilliseconds, player.interactionManager.getGameMode(), player.getPlayerListName(), publicKeyData);
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
                Text text = (Text)buf.readNullable(PacketByteBuf::readText);
                PlayerPublicKey.PublicKeyData publicKeyData = (PlayerPublicKey.PublicKeyData)buf.readNullable(buf2 -> buf2.decode(PlayerPublicKey.PublicKeyData.CODEC));
                return new Entry(gameProfile, i, gameMode, text, publicKeyData);
            }

            @Override
            protected void write(PacketByteBuf buf, Entry entry) {
                buf.writeGameProfile(entry.getProfile());
                buf.writeVarInt(entry.getGameMode().getId());
                buf.writeVarInt(entry.getLatency());
                buf.writeNullable(entry.getDisplayName(), PacketByteBuf::writeText);
                buf.writeNullable(entry.getPublicKeyData(), (buf2, publicKeyData) -> buf2.encode(PlayerPublicKey.PublicKeyData.CODEC, publicKeyData));
            }
        }
        ,
        UPDATE_GAME_MODE{

            @Override
            protected Entry read(PacketByteBuf buf) {
                GameProfile gameProfile = new GameProfile(buf.readUuid(), null);
                GameMode gameMode = GameMode.byId(buf.readVarInt());
                return new Entry(gameProfile, 0, gameMode, null, null);
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
                return new Entry(gameProfile, i, null, null, null);
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
                Text text = (Text)buf.readNullable(PacketByteBuf::readText);
                return new Entry(gameProfile, 0, null, text, null);
            }

            @Override
            protected void write(PacketByteBuf buf, Entry entry) {
                buf.writeUuid(entry.getProfile().getId());
                buf.writeNullable(entry.getDisplayName(), PacketByteBuf::writeText);
            }
        }
        ,
        REMOVE_PLAYER{

            @Override
            protected Entry read(PacketByteBuf buf) {
                GameProfile gameProfile = new GameProfile(buf.readUuid(), null);
                return new Entry(gameProfile, 0, null, null, null);
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
        @Nullable
        private final PlayerPublicKey.PublicKeyData publicKeyData;

        public Entry(GameProfile profile, int latency, @Nullable GameMode gameMode, @Nullable Text displayName, @Nullable PlayerPublicKey.PublicKeyData publicKeyData) {
            this.profile = profile;
            this.latency = latency;
            this.gameMode = gameMode;
            this.displayName = displayName;
            this.publicKeyData = publicKeyData;
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

        @Nullable
        public PlayerPublicKey.PublicKeyData getPublicKeyData() {
            return this.publicKeyData;
        }

        public String toString() {
            return MoreObjects.toStringHelper(this).add("latency", this.latency).add("gameMode", (Object)this.gameMode).add("profile", this.profile).add("displayName", this.displayName == null ? null : Text.Serializer.toJson(this.displayName)).add("profilePublicKey", this.publicKeyData).toString();
        }
    }
}

