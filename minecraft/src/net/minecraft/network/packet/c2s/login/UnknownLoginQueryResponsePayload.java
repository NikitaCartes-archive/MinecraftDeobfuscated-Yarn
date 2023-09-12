package net.minecraft.network.packet.c2s.login;

import net.minecraft.network.PacketByteBuf;

public record UnknownLoginQueryResponsePayload() implements LoginQueryResponsePayload {
	public static final UnknownLoginQueryResponsePayload INSTANCE = new UnknownLoginQueryResponsePayload();

	@Override
	public void write(PacketByteBuf buf) {
	}
}
