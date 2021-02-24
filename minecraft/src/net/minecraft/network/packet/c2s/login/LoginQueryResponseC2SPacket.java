package net.minecraft.network.packet.c2s.login;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerLoginPacketListener;

public class LoginQueryResponseC2SPacket implements Packet<ServerLoginPacketListener> {
	private final int queryId;
	private final PacketByteBuf response;

	@Environment(EnvType.CLIENT)
	public LoginQueryResponseC2SPacket(int queryId, @Nullable PacketByteBuf response) {
		this.queryId = queryId;
		this.response = response;
	}

	public LoginQueryResponseC2SPacket(PacketByteBuf packetByteBuf) {
		this.queryId = packetByteBuf.readVarInt();
		if (packetByteBuf.readBoolean()) {
			int i = packetByteBuf.readableBytes();
			if (i < 0 || i > 1048576) {
				throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
			}

			this.response = new PacketByteBuf(packetByteBuf.readBytes(i));
		} else {
			this.response = null;
		}
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.queryId);
		if (this.response != null) {
			buf.writeBoolean(true);
			buf.writeBytes(this.response.copy());
		} else {
			buf.writeBoolean(false);
		}
	}

	public void apply(ServerLoginPacketListener serverLoginPacketListener) {
		serverLoginPacketListener.onQueryResponse(this);
	}
}
