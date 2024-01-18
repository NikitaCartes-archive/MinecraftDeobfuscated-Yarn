package net.minecraft.network.packet.s2c.common;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.CommonPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.util.Identifier;

public record StoreCookieS2CPacket(Identifier key, byte[] payload) implements Packet<ClientCommonPacketListener> {
	public static final PacketCodec<PacketByteBuf, StoreCookieS2CPacket> CODEC = Packet.createCodec(StoreCookieS2CPacket::write, StoreCookieS2CPacket::new);
	public static final int MAX_COOKIE_LENGTH = 5120;

	private StoreCookieS2CPacket(PacketByteBuf buf) {
		this(buf.readIdentifier(), buf.readByteArray(5120));
	}

	private void write(PacketByteBuf buf) {
		buf.writeIdentifier(this.key);
		buf.writeByteArray(this.payload);
	}

	@Override
	public PacketType<StoreCookieS2CPacket> getPacketId() {
		return CommonPackets.STORE_COOKIE;
	}

	public void apply(ClientCommonPacketListener clientCommonPacketListener) {
		clientCommonPacketListener.onStoreCookie(this);
	}
}
