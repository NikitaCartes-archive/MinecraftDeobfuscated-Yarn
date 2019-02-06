package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ResourcePackSendS2CPacket implements Packet<ClientPlayPacketListener> {
	private String url;
	private String hash;

	public ResourcePackSendS2CPacket() {
	}

	public ResourcePackSendS2CPacket(String string, String string2) {
		this.url = string;
		this.hash = string2;
		if (string2.length() > 40) {
			throw new IllegalArgumentException("Hash is too long (max 40, was " + string2.length() + ")");
		}
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.url = packetByteBuf.readString(32767);
		this.hash = packetByteBuf.readString(40);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeString(this.url);
		packetByteBuf.writeString(this.hash);
	}

	public void method_11774(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11141(this);
	}

	@Environment(EnvType.CLIENT)
	public String getURL() {
		return this.url;
	}

	@Environment(EnvType.CLIENT)
	public String getSHA1() {
		return this.hash;
	}
}
