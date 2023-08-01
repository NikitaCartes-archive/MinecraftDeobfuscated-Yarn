package net.minecraft.network.packet.c2s.login;

import net.minecraft.network.PacketByteBuf;

public interface LoginQueryResponsePayload {
	void write(PacketByteBuf buf);
}
