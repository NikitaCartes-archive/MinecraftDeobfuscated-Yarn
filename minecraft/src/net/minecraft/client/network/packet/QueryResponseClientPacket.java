package net.minecraft.client.network.packet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3530;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Style;
import net.minecraft.text.TextComponent;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.PacketByteBuf;

public class QueryResponseClientPacket implements Packet<ClientQueryPacketListener> {
	private static final Gson GSON = new GsonBuilder()
		.registerTypeAdapter(ServerMetadata.Version.class, new ServerMetadata.Version.class_2931())
		.registerTypeAdapter(ServerMetadata.Players.class, new ServerMetadata.Players.class_2928())
		.registerTypeAdapter(ServerMetadata.class, new ServerMetadata.class_2929())
		.registerTypeHierarchyAdapter(TextComponent.class, new TextComponent.Serializer())
		.registerTypeHierarchyAdapter(Style.class, new Style.Serializer())
		.registerTypeAdapterFactory(new class_3530())
		.create();
	private ServerMetadata metadata;

	public QueryResponseClientPacket() {
	}

	public QueryResponseClientPacket(ServerMetadata serverMetadata) {
		this.metadata = serverMetadata;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.metadata = JsonHelper.deserialize(GSON, packetByteBuf.readString(32767), ServerMetadata.class);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeString(GSON.toJson(this.metadata));
	}

	public void apply(ClientQueryPacketListener clientQueryPacketListener) {
		clientQueryPacketListener.onResponse(this);
	}

	@Environment(EnvType.CLIENT)
	public ServerMetadata getServerMetadata() {
		return this.metadata;
	}
}
