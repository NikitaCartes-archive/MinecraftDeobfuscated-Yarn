package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Identifier;

public class CustomPayloadS2CPacket implements Packet<ClientPlayPacketListener> {
	private static final int MAX_PAYLOAD_SIZE = 1048576;
	public static final Identifier BRAND = new Identifier("brand");
	public static final Identifier DEBUG_PATH = new Identifier("debug/path");
	public static final Identifier DEBUG_NEIGHBORS_UPDATE = new Identifier("debug/neighbors_update");
	public static final Identifier DEBUG_STRUCTURES = new Identifier("debug/structures");
	public static final Identifier DEBUG_WORLDGEN_ATTEMPT = new Identifier("debug/worldgen_attempt");
	public static final Identifier DEBUG_POI_TICKET_COUNT = new Identifier("debug/poi_ticket_count");
	public static final Identifier DEBUG_POI_ADDED = new Identifier("debug/poi_added");
	public static final Identifier DEBUG_POI_REMOVED = new Identifier("debug/poi_removed");
	public static final Identifier DEBUG_VILLAGE_SECTIONS = new Identifier("debug/village_sections");
	public static final Identifier DEBUG_GOAL_SELECTOR = new Identifier("debug/goal_selector");
	public static final Identifier DEBUG_BRAIN = new Identifier("debug/brain");
	public static final Identifier DEBUG_BEE = new Identifier("debug/bee");
	public static final Identifier DEBUG_HIVE = new Identifier("debug/hive");
	public static final Identifier DEBUG_GAME_TEST_ADD_MARKER = new Identifier("debug/game_test_add_marker");
	public static final Identifier DEBUG_GAME_TEST_CLEAR = new Identifier("debug/game_test_clear");
	public static final Identifier DEBUG_RAIDS = new Identifier("debug/raids");
	public static final Identifier DEBUG_GAME_EVENT = new Identifier("debug/game_event");
	public static final Identifier DEBUG_GAME_EVENT_LISTENERS = new Identifier("debug/game_event_listeners");
	private final Identifier channel;
	private final PacketByteBuf data;

	public CustomPayloadS2CPacket(Identifier channel, PacketByteBuf data) {
		this.channel = channel;
		this.data = data;
		if (data.writerIndex() > 1048576) {
			throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
		}
	}

	public CustomPayloadS2CPacket(PacketByteBuf buf) {
		this.channel = buf.readIdentifier();
		int i = buf.readableBytes();
		if (i >= 0 && i <= 1048576) {
			this.data = new PacketByteBuf(buf.readBytes(i));
		} else {
			throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
		}
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeIdentifier(this.channel);
		buf.writeBytes(this.data.copy());
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onCustomPayload(this);
	}

	public Identifier getChannel() {
		return this.channel;
	}

	public PacketByteBuf getData() {
		return new PacketByteBuf(this.data.copy());
	}
}
