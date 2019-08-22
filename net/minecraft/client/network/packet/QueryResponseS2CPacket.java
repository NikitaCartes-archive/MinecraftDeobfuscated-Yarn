/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network.packet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.lang.reflect.Type;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;
import net.minecraft.util.PacketByteBuf;

public class QueryResponseS2CPacket
implements Packet<ClientQueryPacketListener> {
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter((Type)((Object)ServerMetadata.Version.class), new ServerMetadata.Version.Serializer()).registerTypeAdapter((Type)((Object)ServerMetadata.Players.class), new ServerMetadata.Players.Deserializer()).registerTypeAdapter((Type)((Object)ServerMetadata.class), new ServerMetadata.Deserializer()).registerTypeHierarchyAdapter(Text.class, new Text.Serializer()).registerTypeHierarchyAdapter(Style.class, new Style.Serializer()).registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory()).create();
    private ServerMetadata metadata;

    public QueryResponseS2CPacket() {
    }

    public QueryResponseS2CPacket(ServerMetadata serverMetadata) {
        this.metadata = serverMetadata;
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        this.metadata = JsonHelper.deserialize(GSON, packetByteBuf.readString(Short.MAX_VALUE), ServerMetadata.class);
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeString(GSON.toJson(this.metadata));
    }

    public void method_12671(ClientQueryPacketListener clientQueryPacketListener) {
        clientQueryPacketListener.onResponse(this);
    }

    @Environment(value=EnvType.CLIENT)
    public ServerMetadata getServerMetadata() {
        return this.metadata;
    }
}

