package net.minecraft.network.packet.s2c.play;

import java.util.Optional;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;

public class ServerMetadataS2CPacket implements Packet<ClientPlayPacketListener> {
	private final Text description;
	private final Optional<byte[]> favicon;
	private final boolean secureChatEnforced;

	public ServerMetadataS2CPacket(Text description, Optional<byte[]> favicon, boolean previewsChat) {
		this.description = description;
		this.favicon = favicon;
		this.secureChatEnforced = previewsChat;
	}

	public ServerMetadataS2CPacket(PacketByteBuf buf) {
		this.description = buf.readText();
		this.favicon = buf.readOptional(PacketByteBuf::readByteArray);
		this.secureChatEnforced = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeText(this.description);
		buf.writeOptional(this.favicon, PacketByteBuf::writeByteArray);
		buf.writeBoolean(this.secureChatEnforced);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onServerMetadata(this);
	}

	public Text getDescription() {
		return this.description;
	}

	public Optional<byte[]> getFavicon() {
		return this.favicon;
	}

	public boolean isSecureChatEnforced() {
		return this.secureChatEnforced;
	}
}
