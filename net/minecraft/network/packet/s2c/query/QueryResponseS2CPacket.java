/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.query;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.reflect.Type;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;

public class QueryResponseS2CPacket
implements Packet<ClientQueryPacketListener> {
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter((Type)((Object)ServerMetadata.Version.class), new ServerMetadata.Version.Serializer()).registerTypeAdapter((Type)((Object)ServerMetadata.Players.class), new ServerMetadata.Players.Deserializer()).registerTypeAdapter((Type)((Object)ServerMetadata.class), new ServerMetadata.Deserializer()).registerTypeHierarchyAdapter(Text.class, new Text.Serializer()).registerTypeHierarchyAdapter(Style.class, new Style.Serializer()).registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory()).create();
    private final ServerMetadata metadata;

    public QueryResponseS2CPacket(ServerMetadata metadata) {
        this.metadata = metadata;
    }

    public QueryResponseS2CPacket(PacketByteBuf buf) {
        this.metadata = JsonHelper.deserialize(GSON, buf.readString(PacketByteBuf.DEFAULT_MAX_STRING_LENGTH), ServerMetadata.class);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeString(GSON.toJson(this.metadata));
    }

    @Override
    public void apply(ClientQueryPacketListener clientQueryPacketListener) {
        clientQueryPacketListener.onResponse(this);
    }

    public ServerMetadata getServerMetadata() {
        return this.metadata;
    }
}

