package net.minecraft.network.packet.s2c.common;

import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;

public class ResourcePackSendS2CPacket implements Packet<ClientCommonPacketListener> {
	public static final int MAX_HASH_LENGTH = 40;
	private final String url;
	private final String hash;
	private final boolean required;
	@Nullable
	private final Text prompt;

	public ResourcePackSendS2CPacket(String url, String hash, boolean required, @Nullable Text prompt) {
		if (hash.length() > 40) {
			throw new IllegalArgumentException("Hash is too long (max 40, was " + hash.length() + ")");
		} else {
			this.url = url;
			this.hash = hash;
			this.required = required;
			this.prompt = prompt;
		}
	}

	public ResourcePackSendS2CPacket(PacketByteBuf buf) {
		this.url = buf.readString();
		this.hash = buf.readString(40);
		this.required = buf.readBoolean();
		this.prompt = buf.readNullable(PacketByteBuf::readText);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeString(this.url);
		buf.writeString(this.hash);
		buf.writeBoolean(this.required);
		buf.writeNullable(this.prompt, PacketByteBuf::writeText);
	}

	public void apply(ClientCommonPacketListener clientCommonPacketListener) {
		clientCommonPacketListener.onResourcePackSend(this);
	}

	public String getUrl() {
		return this.url;
	}

	public String getHash() {
		return this.hash;
	}

	public boolean isRequired() {
		return this.required;
	}

	@Nullable
	public Text getPrompt() {
		return this.prompt;
	}
}
