package net.minecraft.server.network.packet;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ChatMessageServerPacket implements Packet<ServerPlayPacketListener> {
	private String field_12764;

	public ChatMessageServerPacket() {
	}

	public ChatMessageServerPacket(String string) {
		if (string.length() > 256) {
			string = string.substring(0, 256);
		}

		this.field_12764 = string;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12764 = packetByteBuf.readString(256);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeString(this.field_12764);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onChatMessage(this);
	}

	public String method_12114() {
		return this.field_12764;
	}
}
