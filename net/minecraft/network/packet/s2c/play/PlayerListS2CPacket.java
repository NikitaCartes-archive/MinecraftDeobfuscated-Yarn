/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import java.util.Collection;
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
    private final class_5893 action;
    private final List<Entry> entries;

    public PlayerListS2CPacket(class_5893 action, ServerPlayerEntity ... players) {
        this.action = action;
        this.entries = Lists.newArrayListWithCapacity(players.length);
        for (ServerPlayerEntity serverPlayerEntity : players) {
            this.entries.add(new Entry(serverPlayerEntity.getGameProfile(), serverPlayerEntity.pingMilliseconds, serverPlayerEntity.interactionManager.getGameMode(), serverPlayerEntity.getPlayerListName()));
        }
    }

    public PlayerListS2CPacket(class_5893 arg, Collection<ServerPlayerEntity> collection) {
        this.action = arg;
        this.entries = Lists.newArrayListWithCapacity(collection.size());
        for (ServerPlayerEntity serverPlayerEntity : collection) {
            this.entries.add(new Entry(serverPlayerEntity.getGameProfile(), serverPlayerEntity.pingMilliseconds, serverPlayerEntity.interactionManager.getGameMode(), serverPlayerEntity.getPlayerListName()));
        }
    }

    public PlayerListS2CPacket(PacketByteBuf packetByteBuf) {
        this.action = packetByteBuf.readEnumConstant(class_5893.class);
        this.entries = packetByteBuf.method_34066(this.action::method_34150);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeEnumConstant(this.action);
        buf.method_34062(this.entries, this.action::method_34151);
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
    public class_5893 getAction() {
        return this.action;
    }

    @Nullable
    private static Text method_34149(PacketByteBuf packetByteBuf) {
        return packetByteBuf.readBoolean() ? packetByteBuf.readText() : null;
    }

    private static void method_34148(PacketByteBuf packetByteBuf, @Nullable Text text) {
        if (text == null) {
            packetByteBuf.writeBoolean(false);
        } else {
            packetByteBuf.writeBoolean(true);
            packetByteBuf.writeText(text);
        }
    }

    public String toString() {
        return MoreObjects.toStringHelper(this).add("action", (Object)this.action).add("entries", this.entries).toString();
    }

    public static class Entry {
        private final int latency;
        private final GameMode gameMode;
        private final GameProfile profile;
        @Nullable
        private final Text displayName;

        public Entry(GameProfile gameProfile, int i, @Nullable GameMode gameMode, @Nullable Text text) {
            this.profile = gameProfile;
            this.latency = i;
            this.gameMode = gameMode;
            this.displayName = text;
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

    public static enum class_5893 {
        field_29136{

            @Override
            protected Entry method_34150(PacketByteBuf packetByteBuf2) {
                GameProfile gameProfile = new GameProfile(packetByteBuf2.readUuid(), packetByteBuf2.readString(16));
                PropertyMap propertyMap = gameProfile.getProperties();
                packetByteBuf2.method_34065(packetByteBuf -> {
                    String string = packetByteBuf.readString();
                    String string2 = packetByteBuf.readString();
                    if (packetByteBuf.readBoolean()) {
                        String string3 = packetByteBuf.readString();
                        propertyMap.put(string, new Property(string, string2, string3));
                    } else {
                        propertyMap.put(string, new Property(string, string2));
                    }
                });
                GameMode gameMode = GameMode.byId(packetByteBuf2.readVarInt());
                int i = packetByteBuf2.readVarInt();
                Text text = PlayerListS2CPacket.method_34149(packetByteBuf2);
                return new Entry(gameProfile, i, gameMode, text);
            }

            @Override
            protected void method_34151(PacketByteBuf packetByteBuf2, Entry entry) {
                packetByteBuf2.writeUuid(entry.getProfile().getId());
                packetByteBuf2.writeString(entry.getProfile().getName());
                packetByteBuf2.method_34062(entry.getProfile().getProperties().values(), (packetByteBuf, property) -> {
                    packetByteBuf.writeString(property.getName());
                    packetByteBuf.writeString(property.getValue());
                    if (property.hasSignature()) {
                        packetByteBuf.writeBoolean(true);
                        packetByteBuf.writeString(property.getSignature());
                    } else {
                        packetByteBuf.writeBoolean(false);
                    }
                });
                packetByteBuf2.writeVarInt(entry.getGameMode().getId());
                packetByteBuf2.writeVarInt(entry.getLatency());
                PlayerListS2CPacket.method_34148(packetByteBuf2, entry.getDisplayName());
            }
        }
        ,
        field_29137{

            @Override
            protected Entry method_34150(PacketByteBuf packetByteBuf) {
                GameProfile gameProfile = new GameProfile(packetByteBuf.readUuid(), null);
                GameMode gameMode = GameMode.byId(packetByteBuf.readVarInt());
                return new Entry(gameProfile, 0, gameMode, null);
            }

            @Override
            protected void method_34151(PacketByteBuf packetByteBuf, Entry entry) {
                packetByteBuf.writeUuid(entry.getProfile().getId());
                packetByteBuf.writeVarInt(entry.getGameMode().getId());
            }
        }
        ,
        field_29138{

            @Override
            protected Entry method_34150(PacketByteBuf packetByteBuf) {
                GameProfile gameProfile = new GameProfile(packetByteBuf.readUuid(), null);
                int i = packetByteBuf.readVarInt();
                return new Entry(gameProfile, i, null, null);
            }

            @Override
            protected void method_34151(PacketByteBuf packetByteBuf, Entry entry) {
                packetByteBuf.writeUuid(entry.getProfile().getId());
                packetByteBuf.writeVarInt(entry.getLatency());
            }
        }
        ,
        field_29139{

            @Override
            protected Entry method_34150(PacketByteBuf packetByteBuf) {
                GameProfile gameProfile = new GameProfile(packetByteBuf.readUuid(), null);
                Text text = PlayerListS2CPacket.method_34149(packetByteBuf);
                return new Entry(gameProfile, 0, null, text);
            }

            @Override
            protected void method_34151(PacketByteBuf packetByteBuf, Entry entry) {
                packetByteBuf.writeUuid(entry.getProfile().getId());
                PlayerListS2CPacket.method_34148(packetByteBuf, entry.getDisplayName());
            }
        }
        ,
        field_29140{

            @Override
            protected Entry method_34150(PacketByteBuf packetByteBuf) {
                GameProfile gameProfile = new GameProfile(packetByteBuf.readUuid(), null);
                return new Entry(gameProfile, 0, null, null);
            }

            @Override
            protected void method_34151(PacketByteBuf packetByteBuf, Entry entry) {
                packetByteBuf.writeUuid(entry.getProfile().getId());
            }
        };


        protected abstract Entry method_34150(PacketByteBuf var1);

        protected abstract void method_34151(PacketByteBuf var1, Entry var2);
    }
}

