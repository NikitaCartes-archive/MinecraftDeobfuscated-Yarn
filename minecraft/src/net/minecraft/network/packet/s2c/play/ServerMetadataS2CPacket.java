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

	public ServerMetadataS2CPacket(@Nullable Text description, @Nullable String favicon, boolean previewsChat) {
		this.description = Optional.ofNullable(description);
		this.favicon = Optional.ofNullable(favicon);
		this.previewsChat = previewsChat;
	}

	public ServerMetadataS2CPacket(PacketByteBuf buf) {
		this.description = buf.readOptional(PacketByteBuf::readText);
		this.favicon = buf.readOptional(PacketByteBuf::readString);
		this.previewsChat = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeOptional(this.description, PacketByteBuf::writeText);
		buf.writeOptional(this.favicon, PacketByteBuf::writeString);
		buf.writeBoolean(this.previewsChat);
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
}
