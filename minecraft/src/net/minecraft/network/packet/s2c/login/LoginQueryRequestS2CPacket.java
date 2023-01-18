package net.minecraft.network.packet.s2c.login;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Identifier;

public class LoginQueryRequestS2CPacket implements Packet<ClientLoginPacketListener> {
	private static final int MAX_PAYLOAD_SIZE = 1048576;
	private final int queryId;
	private final Identifier channel;
	private final PacketByteBuf payload;

	public LoginQueryRequestS2CPacket(int queryId, Identifier channel, PacketByteBuf payload) {
		this.queryId = queryId;
		this.channel = channel;
		this.payload = payload;
	}

	public LoginQueryRequestS2CPacket(PacketByteBuf buf) {
		this.queryId = buf.readVarInt();
		this.channel = buf.readIdentifier();
		int i = buf.readableBytes();
		if (i >= 0 && i <= 1048576) {
			this.payload = new PacketByteBuf(buf.readBytes(i));
		} else {
			throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
		}
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.queryId);
		buf.writeIdentifier(this.channel);
		buf.writeBytes(this.payload.copy());
	}

	public void apply(ClientLoginPacketListener clientLoginPacketListener) {
		clientLoginPacketListener.onQueryRequest(this);
	}

	public int getQueryId() {
		return this.queryId;
	}

	public Identifier getChannel() {
		return this.channel;
	}

	public PacketByteBuf getPayload() {
		return this.payload;
	}
}
