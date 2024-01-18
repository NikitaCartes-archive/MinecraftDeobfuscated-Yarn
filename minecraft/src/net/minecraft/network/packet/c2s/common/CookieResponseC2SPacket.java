package net.minecraft.network.packet.c2s.common;

import javax.annotation.Nullable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ServerCookieResponsePacketListener;
import net.minecraft.network.packet.CookiePackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.util.Identifier;

public record CookieResponseC2SPacket(Identifier key, @Nullable byte[] payload) implements Packet<ServerCookieResponsePacketListener> {
	public static final PacketCodec<PacketByteBuf, CookieResponseC2SPacket> CODEC = Packet.createCodec(
		CookieResponseC2SPacket::write, CookieResponseC2SPacket::new
	);

	private CookieResponseC2SPacket(PacketByteBuf buf) {
		this(buf.readIdentifier(), buf.readNullable(bufx -> bufx.readByteArray(5120)));
	}

	private void write(PacketByteBuf buf) {
		buf.writeIdentifier(this.key);
		buf.writeNullable(this.payload, PacketByteBuf::writeByteArray);
	}

	@Override
	public PacketType<CookieResponseC2SPacket> getPacketId() {
		return CookiePackets.COOKIE_RESPONSE;
	}

	public void apply(ServerCookieResponsePacketListener serverCookieResponsePacketListener) {
		serverCookieResponsePacketListener.onCookieResponse(this);
	}
}
