package net.minecraft.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public interface CustomPayload {
	void write(PacketByteBuf buf);

	Identifier id();
}
