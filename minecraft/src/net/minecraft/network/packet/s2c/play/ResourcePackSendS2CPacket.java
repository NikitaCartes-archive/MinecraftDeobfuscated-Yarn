package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class ResourcePackSendS2CPacket implements Packet<ClientPlayPacketListener> {
	private String url;
	private String hash;

	public ResourcePackSendS2CPacket() {
	}

	public ResourcePackSendS2CPacket(String url, String hash) {
		this.url = url;
		this.hash = hash;
		if (hash.length() > 40) {
			throw new IllegalArgumentException("Hash is too long (max 40, was " + hash.length() + ")");
		}
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.url = buf.readString(32767);
		this.hash = buf.readString(40);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeString(this.url);
		buf.writeString(this.hash);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onResourcePackSend(this);
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
