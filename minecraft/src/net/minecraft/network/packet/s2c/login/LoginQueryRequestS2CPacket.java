package net.minecraft.network.packet.s2c.login;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.packet.LoginPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.util.Identifier;

public record LoginQueryRequestS2CPacket(int queryId, LoginQueryRequestPayload payload) implements Packet<ClientLoginPacketListener> {
	public static final PacketCodec<PacketByteBuf, LoginQueryRequestS2CPacket> CODEC = Packet.createCodec(
		LoginQueryRequestS2CPacket::write, LoginQueryRequestS2CPacket::new
	);
	private static final int MAX_PAYLOAD_SIZE = 1048576;

	private LoginQueryRequestS2CPacket(PacketByteBuf buf) {
		this(buf.readVarInt(), readPayload(buf.readIdentifier(), buf));
	}

	private static LoginQueryRequestPayload readPayload(Identifier id, PacketByteBuf buf) {
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

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.queryId);
		buf.writeIdentifier(this.payload.id());
		this.payload.write(buf);
	}

	@Override
	public PacketType<LoginQueryRequestS2CPacket> getPacketId() {
		return LoginPackets.CUSTOM_QUERY;
	}

	public void apply(ClientLoginPacketListener clientLoginPacketListener) {
		clientLoginPacketListener.onQueryRequest(this);
	}
}
