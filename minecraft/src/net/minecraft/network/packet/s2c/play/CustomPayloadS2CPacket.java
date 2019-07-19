package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class CustomPayloadS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final Identifier BRAND = new Identifier("brand");
	public static final Identifier DEBUG_PATH = new Identifier("debug/path");
	public static final Identifier DEBUG_NEIGHBORS_UPDATE = new Identifier("debug/neighbors_update");
	public static final Identifier DEBUG_CAVES = new Identifier("debug/caves");
	public static final Identifier DEBUG_STRUCTURES = new Identifier("debug/structures");
	public static final Identifier DEBUG_WORLDGEN_ATTEMPT = new Identifier("debug/worldgen_attempt");
	public static final Identifier DEBUG_POI_TICKET_COUNT = new Identifier("debug/poi_ticket_count");
	public static final Identifier DEBUG_POI_ADDED = new Identifier("debug/poi_added");
	public static final Identifier DEBUG_POI_REMOVED = new Identifier("debug/poi_removed");
	public static final Identifier DEBUG_VILLAGE_SECTIONS = new Identifier("debug/village_sections");
	public static final Identifier DEBUG_GOAL_SELECTOR = new Identifier("debug/goal_selector");
	public static final Identifier DEBUG_BRAIN = new Identifier("debug/brain");
	public static final Identifier DEBUG_RAIDS = new Identifier("debug/raids");
	private Identifier channel;
	private PacketByteBuf data;

	public CustomPayloadS2CPacket() {
	}

	public CustomPayloadS2CPacket(Identifier channel, PacketByteBuf data) {
		this.channel = channel;
		this.data = data;
		if (data.writerIndex() > 1048576) {
			throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.channel = buf.readIdentifier();
		int i = buf.readableBytes();
		if (i >= 0 && i <= 1048576) {
			this.data = new PacketByteBuf(buf.readBytes(i));
		} else {
			throw new IOException("Payload may not be larger than 1048576 bytes");
		}
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeIdentifier(this.channel);
		buf.writeBytes(this.data.copy());
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
