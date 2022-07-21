package net.minecraft.network.packet.s2c.play;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;

public class ServerMetadataS2CPacket implements Packet<ClientPlayPacketListener> {
	private final Optional<Text> description;
	private final Optional<String> favicon;
	private final boolean previewsChat;
	private final boolean field_39920;

	public ServerMetadataS2CPacket(@Nullable Text description, @Nullable String favicon, boolean previewsChat, boolean bl) {
		this.description = Optional.ofNullable(description);
		this.favicon = Optional.ofNullable(favicon);
		this.previewsChat = previewsChat;
		this.field_39920 = bl;
	}

	public ServerMetadataS2CPacket(PacketByteBuf buf) {
		this.description = buf.readOptional(PacketByteBuf::readText);
		this.favicon = buf.readOptional(PacketByteBuf::readString);
		this.previewsChat = buf.readBoolean();
		this.field_39920 = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeOptional(this.description, PacketByteBuf::writeText);
		buf.writeOptional(this.favicon, PacketByteBuf::writeString);
		buf.writeBoolean(this.previewsChat);
		buf.writeBoolean(this.field_39920);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onServerMetadata(this);
	}

	public Optional<Text> getDescription() {
		return this.description;
	}

	public Optional<String> getFavicon() {
		return this.favicon;
	}

	public boolean shouldPreviewChat() {
		return this.previewsChat;
	}

	public boolean method_45058() {
		return this.field_39920;
	}
}
