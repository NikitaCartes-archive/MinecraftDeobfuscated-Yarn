package net.minecraft.client.network.packet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Style;
import net.minecraft.text.TextComponent;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;
import net.minecraft.util.PacketByteBuf;

public class QueryResponseS2CPacket implements Packet<ClientQueryPacketListener> {
	private static final Gson GSON = new GsonBuilder()
		.registerTypeAdapter(ServerMetadata.Version.class, new ServerMetadata.Version.Serializer())
		.registerTypeAdapter(ServerMetadata.Players.class, new ServerMetadata.Players.Deserializer())
		.registerTypeAdapter(ServerMetadata.class, new ServerMetadata.Deserializer())
		.registerTypeHierarchyAdapter(TextComponent.class, new TextComponent.Serializer())
		.registerTypeHierarchyAdapter(Style.class, new Style.Serializer())
		.registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory())
		.create();
	private ServerMetadata field_13281;

	public QueryResponseS2CPacket() {
	}

	public QueryResponseS2CPacket(ServerMetadata serverMetadata) {
		this.field_13281 = serverMetadata;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_13281 = JsonHelper.deserialize(GSON, packetByteBuf.readString(32767), ServerMetadata.class);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeString(GSON.toJson(this.field_13281));
	}

	public void method_12671(ClientQueryPacketListener clientQueryPacketListener) {
		clientQueryPacketListener.method_12667(this);
	}

	@Environment(EnvType.CLIENT)
	public ServerMetadata method_12672() {
		return this.field_13281;
	}
}
