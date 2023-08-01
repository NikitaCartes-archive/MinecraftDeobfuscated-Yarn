package net.minecraft.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record UnknownCustomPayload(Identifier id) implements CustomPayload {
	@Override
	public void write(PacketByteBuf buf) {
	}
}
