package net.minecraft.network.packet.c2s.login;

import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.network.packet.LoginPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

public record LoginQueryResponseC2SPacket(int queryId, @Nullable LoginQueryResponsePayload response) implements Packet<ServerLoginPacketListener> {
	public static final PacketCodec<PacketByteBuf, LoginQueryResponseC2SPacket> CODEC = Packet.createCodec(
		LoginQueryResponseC2SPacket::write, LoginQueryResponseC2SPacket::read
	);
	private static final int MAX_PAYLOAD_SIZE = 1048576;

	private static LoginQueryResponseC2SPacket read(PacketByteBuf buf) {
		int i = buf.readVarInt();
		return new LoginQueryResponseC2SPacket(i, readPayload(i, buf));
	}

	/**
	 * {@return the response payload read from {@code buf}}
	 * 
	 * @implNote This delegates the logic to {@link #getVanillaPayload},
	 * which simply validates the size of the buffer and returns {@link
	 * UnknownLoginQueryResponsePayload#INSTANCE}.
	 */
	private static LoginQueryResponsePayload readPayload(int queryId, PacketByteBuf buf) {
		return getVanillaPayload(buf);
	}

	private static LoginQueryResponsePayload getVanillaPayload(PacketByteBuf buf) {
		int i = buf.readableBytes();
		if (i >= 0 && i <= 1048576) {
			buf.skipBytes(i);
			return UnknownLoginQueryResponsePayload.INSTANCE;
		} else {
			throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
		}
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.queryId);
		buf.writeNullable(this.response, (bufx, response) -> response.write(bufx));
	}

	@Override
	public PacketType<LoginQueryResponseC2SPacket> getPacketId() {
		return LoginPackets.CUSTOM_QUERY_ANSWER;
	}

	public void apply(ServerLoginPacketListener serverLoginPacketListener) {
		serverLoginPacketListener.onQueryResponse(this);
	}
}
