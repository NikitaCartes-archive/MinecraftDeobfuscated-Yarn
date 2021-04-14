package net.minecraft.network.packet.s2c.play;

import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;

public class ResourcePackSendS2CPacket implements Packet<ClientPlayPacketListener> {
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
		if (buf.readBoolean()) {
			this.prompt = buf.readText();
		} else {
			this.prompt = null;
		}
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeString(this.url);
		buf.writeString(this.hash);
		buf.writeBoolean(this.required);
		if (this.prompt != null) {
			buf.writeBoolean(true);
			buf.writeText(this.prompt);
		} else {
			buf.writeBoolean(false);
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onResourcePackSend(this);
	}

	public String getURL() {
		return this.url;
	}

	public String getSHA1() {
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
