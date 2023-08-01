package net.minecraft.network.packet.s2c.login;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Identifier;

public record LoginQueryRequestS2CPacket(int queryId, LoginQueryRequestPayload payload) implements Packet<ClientLoginPacketListener> {
	private static final int MAX_PAYLOAD_SIZE = 1048576;

	public LoginQueryRequestS2CPacket(PacketByteBuf buf) {
		this(buf.readVarInt(), readPayload(buf.readIdentifier(), buf));
	}

	private static UnknownLoginQueryRequestPayload readPayload(Identifier id, PacketByteBuf buf) {
		return readUnknownPayload(id, buf);
	}

	private static UnknownLoginQueryRequestPayload readUnknownPayload(Identifier id, PacketByteBuf buf) {
		int i = buf.readableBytes();
		if (i >= 0 && i <= 1048576) {
			buf.skipBytes(i);
			return new UnknownLoginQueryRequestPayload(id);
		} else {
			throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
		}
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.queryId);
		buf.writeIdentifier(this.payload.id());
		this.payload.write(buf);
	}

	public void apply(ClientLoginPacketListener clientLoginPacketListener) {
		clientLoginPacketListener.onQueryRequest(this);
	}
}
