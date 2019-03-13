package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class CustomPayloadS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final Identifier field_12158 = new Identifier("brand");
	public static final Identifier field_12161 = new Identifier("debug/path");
	public static final Identifier field_12157 = new Identifier("debug/neighbors_update");
	public static final Identifier field_12156 = new Identifier("debug/caves");
	public static final Identifier field_12163 = new Identifier("debug/structures");
	public static final Identifier field_12164 = new Identifier("debug/worldgen_attempt");
	public static final Identifier field_18798 = new Identifier("debug/poi");
	public static final Identifier field_18799 = new Identifier("debug/goal_selector");
	public static final Identifier field_18800 = new Identifier("debug/brain");
	private Identifier field_12165;
	private PacketByteBuf data;

	public CustomPayloadS2CPacket() {
	}

	public CustomPayloadS2CPacket(Identifier identifier, PacketByteBuf packetByteBuf) {
		this.field_12165 = identifier;
		this.data = packetByteBuf;
		if (packetByteBuf.writerIndex() > 1048576) {
			throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
		}
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12165 = packetByteBuf.method_10810();
		int i = packetByteBuf.readableBytes();
		if (i >= 0 && i <= 1048576) {
			this.data = new PacketByteBuf(packetByteBuf.readBytes(i));
		} else {
			throw new IOException("Payload may not be larger than 1048576 bytes");
		}
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.method_10812(this.field_12165);
		packetByteBuf.writeBytes(this.data.copy());
	}

	public void method_11457(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11152(this);
	}

	@Environment(EnvType.CLIENT)
	public Identifier method_11456() {
		return this.field_12165;
	}

	@Environment(EnvType.CLIENT)
	public PacketByteBuf getData() {
		return new PacketByteBuf(this.data.copy());
	}
}
