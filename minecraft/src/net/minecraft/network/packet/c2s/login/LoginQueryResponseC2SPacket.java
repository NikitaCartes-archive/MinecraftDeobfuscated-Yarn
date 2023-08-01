package net.minecraft.network.packet.c2s.login;

import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.network.packet.Packet;

public record LoginQueryResponseC2SPacket(int queryId, @Nullable LoginQueryResponsePayload response) implements Packet<ServerLoginPacketListener> {
	private static final int MAX_PAYLOAD_SIZE = 1048576;

	public static LoginQueryResponseC2SPacket read(PacketByteBuf buf) {
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

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.queryId);
		buf.writeNullable(this.response, (bufx, response) -> response.write(bufx));
	}

	public void apply(ServerLoginPacketListener serverLoginPacketListener) {
		serverLoginPacketListener.onQueryResponse(this);
	}
}
