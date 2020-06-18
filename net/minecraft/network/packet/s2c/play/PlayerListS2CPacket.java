/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.io.IOException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

public class PlayerListS2CPacket
implements Packet<ClientPlayPacketListener> {
    private Action action;
    private final List<Entry> entries = Lists.newArrayList();

    public PlayerListS2CPacket() {
    }

    public PlayerListS2CPacket(Action action, ServerPlayerEntity ... players) {
        this.action = action;
        for (ServerPlayerEntity serverPlayerEntity : players) {
            this.entries.add(new Entry(serverPlayerEntity.getGameProfile(), serverPlayerEntity.pingMilliseconds, serverPlayerEntity.interactionManager.getGameMode(), serverPlayerEntity.getPlayerListName()));
        }
    }

    public PlayerListS2CPacket(Action action, Iterable<ServerPlayerEntity> iterable) {
        this.action = action;
        for (ServerPlayerEntity serverPlayerEntity : iterable) {
            this.entries.add(new Entry(serverPlayerEntity.getGameProfile(), serverPlayerEntity.pingMilliseconds, serverPlayerEntity.interactionManager.getGameMode(), serverPlayerEntity.getPlayerListName()));
        }
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.action = buf.readEnumConstant(Action.class);
        int i = buf.readVarInt();
        for (int j = 0; j < i; ++j) {
            GameProfile gameProfile = null;
            int k = 0;
            GameMode gameMode = null;
            Text text = null;
            switch (this.action) {
                case ADD_PLAYER: {
                    gameProfile = new GameProfile(buf.readUuid(), buf.readString(16));
                    int l = buf.readVarInt();
                    for (int m = 0; m < l; ++m) {
                        String string = buf.readString(Short.MAX_VALUE);
                        String string2 = buf.readString(Short.MAX_VALUE);
                        if (buf.readBoolean()) {
                            gameProfile.getProperties().put(string, new Property(string, string2, buf.readString(Short.MAX_VALUE)));
                            continue;
                        }
                        gameProfile.getProperties().put(string, new Property(string, string2));
                    }
                    gameMode = GameMode.byId(buf.readVarInt());
                    k = buf.readVarInt();
                    if (!buf.readBoolean()) break;
                    text = buf.readText();
                    break;
                }
                case UPDATE_GAME_MODE: {
                    gameProfile = new GameProfile(buf.readUuid(), null);
                    gameMode = GameMode.byId(buf.readVarInt());
                    break;
                }
                case UPDATE_LATENCY: {
                    gameProfile = new GameProfile(buf.readUuid(), null);
                    k = buf.readVarInt();
                    break;
                }
                case UPDATE_DISPLAY_NAME: {
                    gameProfile = new GameProfile(buf.readUuid(), null);
                    if (!buf.readBoolean()) break;
                    text = buf.readText();
                    break;
                }
                case REMOVE_PLAYER: {
                    gameProfile = new GameProfile(buf.readUuid(), null);
                }
            }
            this.entries.add(new Entry(gameProfile, k, gameMode, text));
        }
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeEnumConstant(this.action);
        buf.writeVarInt(this.entries.size());
        for (Entry entry : this.entries) {
            switch (this.action) {
                case ADD_PLAYER: {
                    buf.writeUuid(entry.getProfile().getId());
                    buf.writeString(entry.getProfile().getName());
                    buf.writeVarInt(entry.getProfile().getProperties().size());
                    for (Property property : entry.getProfile().getProperties().values()) {
                        buf.writeString(property.getName());
                        buf.writeString(property.getValue());
                        if (property.hasSignature()) {
                            buf.writeBoolean(true);
                            buf.writeString(property.getSignature());
                            continue;
                        }
                        buf.writeBoolean(false);
                    }
                    buf.writeVarInt(entry.getGameMode().getId());
                    buf.writeVarInt(entry.getLatency());
                    if (entry.getDisplayName() == null) {
                        buf.writeBoolean(false);
                        break;
                    }
                    buf.writeBoolean(true);
                    buf.writeText(entry.getDisplayName());
                    break;
                }
                case UPDATE_GAME_MODE: {
                    buf.writeUuid(entry.getProfile().getId());
                    buf.writeVarInt(entry.getGameMode().getId());
                    break;
                }
                case UPDATE_LATENCY: {
                    buf.writeUuid(entry.getProfile().getId());
                    buf.writeVarInt(entry.getLatency());
                    break;
                }
                case UPDATE_DISPLAY_NAME: {
                    buf.writeUuid(entry.getProfile().getId());
                    if (entry.getDisplayName() == null) {
                        buf.writeBoolean(false);
                        break;
                    }
                    buf.writeBoolean(true);
                    buf.writeText(entry.getDisplayName());
                    break;
                }
                case REMOVE_PLAYER: {
                    buf.writeUuid(entry.getProfile().getId());
                }
            }
        }
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onPlayerList(this);
    }

    @Environment(value=EnvType.CLIENT)
    public List<Entry> getEntries() {
        return this.entries;
    }

    @Environment(value=EnvType.CLIENT)
    public Action getAction() {
        return this.action;
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("action", (Object)this.action).add("entries", this.entries).toString();
    }

    public class Entry {
        private final int latency;
        private final GameMode gameMode;
        private final GameProfile profile;
        private final Text displayName;

        public Entry(GameProfile profile, @Nullable int latency, @Nullable GameMode gameMode, Text displayName) {
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

    public static enum Action {
        ADD_PLAYER,
        UPDATE_GAME_MODE,
        UPDATE_LATENCY,
        UPDATE_DISPLAY_NAME,
        REMOVE_PLAYER;

    }
}

