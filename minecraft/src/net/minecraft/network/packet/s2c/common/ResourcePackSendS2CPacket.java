package net.minecraft.network.packet.s2c.common;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.CommonPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketIdentifier;
import net.minecraft.text.Text;

public record ResourcePackSendS2CPacket(UUID id, String url, String hash, boolean required, @Nullable Text prompt) implements Packet<ClientCommonPacketListener> {
	public static final PacketCodec<PacketByteBuf, ResourcePackSendS2CPacket> CODEC = Packet.createCodec(
		ResourcePackSendS2CPacket::write, ResourcePackSendS2CPacket::new
	);
	public static final int MAX_HASH_LENGTH = 40;

	public ResourcePackSendS2CPacket(UUID uUID, String string, String string2, boolean bl, @Nullable Text text) {
		if (string2.length() > 40) {
			throw new IllegalArgumentException("Hash is too long (max 40, was " + string2.length() + ")");
		} else {
			this.id = uUID;
			this.url = string;
			this.hash = string2;
			this.required = bl;
			this.prompt = text;
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
	public PacketIdentifier<ResourcePackSendS2CPacket> getPacketId() {
		return CommonPackets.RESOURCE_PACK_PUSH;
	}

	public void apply(ClientCommonPacketListener clientCommonPacketListener) {
		clientCommonPacketListener.onResourcePackSend(this);
	}
}
