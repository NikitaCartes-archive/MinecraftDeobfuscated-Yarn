package net.minecraft.network.packet.s2c.common;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.CommonPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.text.Text;

public record ResourcePackSendS2CPacket(UUID id, String url, String hash, boolean required, @Nullable Text prompt) implements Packet<ClientCommonPacketListener> {
	public static final PacketCodec<PacketByteBuf, ResourcePackSendS2CPacket> CODEC = Packet.createCodec(
		ResourcePackSendS2CPacket::write, ResourcePackSendS2CPacket::new
	);
	public static final int MAX_HASH_LENGTH = 40;

	public ResourcePackSendS2CPacket(UUID id, String url, String hash, boolean required, @Nullable Text prompt) {
		if (hash.length() > 40) {
			throw new IllegalArgumentException("Hash is too long (max 40, was " + hash.length() + ")");
		} else {
			this.id = id;
			this.url = url;
			this.hash = hash;
			this.required = required;
			this.prompt = prompt;
		}
	}

	private ResourcePackSendS2CPacket(PacketByteBuf buf) {
		this(buf.readUuid(), buf.readString(), buf.readString(40), buf.readBoolean(), buf.readNullable(PacketByteBuf::readUnlimitedText));
	}

	private void write(PacketByteBuf buf) {
		buf.writeUuid(this.id);
		buf.writeString(this.url);
		buf.writeString(this.hash);
		buf.writeBoolean(this.required);
		buf.writeNullable(this.prompt, PacketByteBuf::writeText);
	}

	@Override
	public PacketType<ResourcePackSendS2CPacket> getPacketId() {
		return CommonPackets.RESOURCE_PACK_PUSH;
	}

	public void apply(ClientCommonPacketListener clientCommonPacketListener) {
		clientCommonPacketListener.onResourcePackSend(this);
	}
}
