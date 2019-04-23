/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network.packet;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.io.IOException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.chat.Component;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

public class PlayerListS2CPacket
implements Packet<ClientPlayPacketListener> {
    private Action action;
    private final List<Entry> entries = Lists.newArrayList();

    public PlayerListS2CPacket() {
    }

    public PlayerListS2CPacket(Action action, ServerPlayerEntity ... serverPlayerEntitys) {
        this.action = action;
        for (ServerPlayerEntity serverPlayerEntity : serverPlayerEntitys) {
            this.entries.add(new Entry(serverPlayerEntity.getGameProfile(), serverPlayerEntity.field_13967, serverPlayerEntity.interactionManager.getGameMode(), serverPlayerEntity.method_14206()));
        }
    }

    public PlayerListS2CPacket(Action action, Iterable<ServerPlayerEntity> iterable) {
        this.action = action;
        for (ServerPlayerEntity serverPlayerEntity : iterable) {
            this.entries.add(new Entry(serverPlayerEntity.getGameProfile(), serverPlayerEntity.field_13967, serverPlayerEntity.interactionManager.getGameMode(), serverPlayerEntity.method_14206()));
        }
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        this.action = packetByteBuf.readEnumConstant(Action.class);
        int i = packetByteBuf.readVarInt();
        for (int j = 0; j < i; ++j) {
            GameProfile gameProfile = null;
            int k = 0;
            GameMode gameMode = null;
            Component component = null;
            switch (this.action) {
                case ADD_PLAYER: {
                    gameProfile = new GameProfile(packetByteBuf.readUuid(), packetByteBuf.readString(16));
                    int l = packetByteBuf.readVarInt();
                    for (int m = 0; m < l; ++m) {
                        String string = packetByteBuf.readString(Short.MAX_VALUE);
                        String string2 = packetByteBuf.readString(Short.MAX_VALUE);
                        if (packetByteBuf.readBoolean()) {
                            gameProfile.getProperties().put(string, new Property(string, string2, packetByteBuf.readString(Short.MAX_VALUE)));
                            continue;
                        }
                        gameProfile.getProperties().put(string, new Property(string, string2));
                    }
                    gameMode = GameMode.byId(packetByteBuf.readVarInt());
                    k = packetByteBuf.readVarInt();
                    if (!packetByteBuf.readBoolean()) break;
                    component = packetByteBuf.readTextComponent();
                    break;
                }
                case UPDATE_GAME_MODE: {
                    gameProfile = new GameProfile(packetByteBuf.readUuid(), null);
                    gameMode = GameMode.byId(packetByteBuf.readVarInt());
                    break;
                }
                case UPDATE_LATENCY: {
                    gameProfile = new GameProfile(packetByteBuf.readUuid(), null);
                    k = packetByteBuf.readVarInt();
                    break;
                }
                case UPDATE_DISPLAY_NAME: {
                    gameProfile = new GameProfile(packetByteBuf.readUuid(), null);
                    if (!packetByteBuf.readBoolean()) break;
                    component = packetByteBuf.readTextComponent();
                    break;
                }
                case REMOVE_PLAYER: {
                    gameProfile = new GameProfile(packetByteBuf.readUuid(), null);
                }
            }
            this.entries.add(new Entry(gameProfile, k, gameMode, component));
        }
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeEnumConstant(this.action);
        packetByteBuf.writeVarInt(this.entries.size());
        for (Entry entry : this.entries) {
            switch (this.action) {
                case ADD_PLAYER: {
                    packetByteBuf.writeUuid(entry.getProfile().getId());
                    packetByteBuf.writeString(entry.getProfile().getName());
                    packetByteBuf.writeVarInt(entry.getProfile().getProperties().size());
                    for (Property property : entry.getProfile().getProperties().values()) {
                        packetByteBuf.writeString(property.getName());
                        packetByteBuf.writeString(property.getValue());
                        if (property.hasSignature()) {
                            packetByteBuf.writeBoolean(true);
                            packetByteBuf.writeString(property.getSignature());
                            continue;
                        }
                        packetByteBuf.writeBoolean(false);
                    }
                    packetByteBuf.writeVarInt(entry.getGameMode().getId());
                    packetByteBuf.writeVarInt(entry.getLatency());
                    if (entry.getDisplayName() == null) {
                        packetByteBuf.writeBoolean(false);
                        break;
                    }
                    packetByteBuf.writeBoolean(true);
                    packetByteBuf.writeTextComponent(entry.getDisplayName());
                    break;
                }
                case UPDATE_GAME_MODE: {
                    packetByteBuf.writeUuid(entry.getProfile().getId());
                    packetByteBuf.writeVarInt(entry.getGameMode().getId());
                    break;
                }
                case UPDATE_LATENCY: {
                    packetByteBuf.writeUuid(entry.getProfile().getId());
                    packetByteBuf.writeVarInt(entry.getLatency());
                    break;
                }
                case UPDATE_DISPLAY_NAME: {
                    packetByteBuf.writeUuid(entry.getProfile().getId());
                    if (entry.getDisplayName() == null) {
                        packetByteBuf.writeBoolean(false);
                        break;
                    }
                    packetByteBuf.writeBoolean(true);
                    packetByteBuf.writeTextComponent(entry.getDisplayName());
                    break;
                }
                case REMOVE_PLAYER: {
                    packetByteBuf.writeUuid(entry.getProfile().getId());
                }
            }
        }
    }

    public void method_11721(ClientPlayPacketListener clientPlayPacketListener) {
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
        private final Component displayName;

        public Entry(GameProfile gameProfile, @Nullable int i, @Nullable GameMode gameMode, Component component) {
            this.profile = gameProfile;
            this.latency = i;
            this.gameMode = gameMode;
            this.displayName = component;
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
        public Component getDisplayName() {
            return this.displayName;
        }

        public String toString() {
            return MoreObjects.toStringHelper(this).add("latency", this.latency).add("gameMode", (Object)this.gameMode).add("profile", this.profile).add("displayName", this.displayName == null ? null : Component.Serializer.toJsonString(this.displayName)).toString();
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

