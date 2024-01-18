package net.minecraft.network.packet.s2c.play;

import java.util.Optional;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.text.Text;

public class ServerMetadataS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, ServerMetadataS2CPacket> CODEC = Packet.createCodec(
		ServerMetadataS2CPacket::write, ServerMetadataS2CPacket::new
	);
	private final Text description;
	private final Optional<byte[]> favicon;

	public ServerMetadataS2CPacket(Text description, Optional<byte[]> favicon) {
		this.description = description;
		this.favicon = favicon;
	}

	private ServerMetadataS2CPacket(PacketByteBuf buf) {
		this.description = buf.readUnlimitedText();
		this.favicon = buf.readOptional(PacketByteBuf::readByteArray);
	}

	private void write(PacketByteBuf buf) {
		buf.writeText(this.description);
		buf.writeOptional(this.favicon, PacketByteBuf::writeByteArray);
	}

	@Override
	public PacketType<ServerMetadataS2CPacket> getPacketId() {
		return PlayPackets.SERVER_DATA;
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
}
