package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record DebugGameTestClearCustomPayload() implements CustomPayload {
	public static final Identifier ID = new Identifier("debug/game_test_clear");

	public DebugGameTestClearCustomPayload(PacketByteBuf buf) {
		this();
	}

	@Override
	public void write(PacketByteBuf buf) {
	}

	@Override
	public Identifier id() {
		return ID;
	}
}
