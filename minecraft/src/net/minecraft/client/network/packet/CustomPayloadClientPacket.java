package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class CustomPayloadClientPacket implements Packet<ClientPlayPacketListener> {
	public static final Identifier TRADER_LIST = new Identifier("trader_list");
	public static final Identifier BRAND = new Identifier("brand");
	public static final Identifier DEBUG_PATH = new Identifier("debug/path");
	public static final Identifier DEBUG_NEIGHBORS_UPDATE = new Identifier("debug/neighbors_update");
	public static final Identifier DEBUG_CAVES = new Identifier("debug/caves");
	public static final Identifier DEBUG_STRUCTURES = new Identifier("debug/structures");
	public static final Identifier DEBUG_WORLDGEN_ATTEMPT = new Identifier("debug/worldgen_attempt");
	private Identifier channel;
	private PacketByteBuf data;

	public CustomPayloadClientPacket() {
	}

	public CustomPayloadClientPacket(Identifier identifier, PacketByteBuf packetByteBuf) {
		this.channel = identifier;
		this.data = packetByteBuf;
		if (packetByteBuf.writerIndex() > 1048576) {
			throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
		}
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.channel = packetByteBuf.readIdentifier();
		int i = packetByteBuf.readableBytes();
		if (i >= 0 && i <= 1048576) {
			this.data = new PacketByteBuf(packetByteBuf.readBytes(i));
		} else {
			throw new IOException("Payload may not be larger than 1048576 bytes");
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeIdentifier(this.channel);
		packetByteBuf.writeBytes(this.data.copy());
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onCustomPayload(this);
	}

	@Environment(EnvType.CLIENT)
	public Identifier getChannel() {
		return this.channel;
	}

	@Environment(EnvType.CLIENT)
	public PacketByteBuf getData() {
		return new PacketByteBuf(this.data.copy());
	}
}
