package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class ResourcePackSendS2CPacket implements Packet<ClientPlayPacketListener> {
	private final String url;
	private final String hash;
	private final boolean required;

	public ResourcePackSendS2CPacket(String url, String hash, boolean required) {
		if (hash.length() > 40) {
			throw new IllegalArgumentException("Hash is too long (max 40, was " + hash.length() + ")");
		} else {
			this.url = url;
			this.hash = hash;
			this.required = required;
		}
	}

	public ResourcePackSendS2CPacket(PacketByteBuf buf) {
		this.url = buf.readString();
		this.hash = buf.readString(40);
		this.required = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeString(this.url);
		buf.writeString(this.hash);
		buf.writeBoolean(this.required);
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

	@Environment(EnvType.CLIENT)
	public boolean isRequired() {
		return this.required;
	}
}
