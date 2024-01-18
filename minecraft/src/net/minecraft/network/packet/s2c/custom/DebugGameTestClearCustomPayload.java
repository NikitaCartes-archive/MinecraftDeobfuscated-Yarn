package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record DebugGameTestClearCustomPayload() implements CustomPayload {
	public static final PacketCodec<PacketByteBuf, DebugGameTestClearCustomPayload> CODEC = CustomPayload.codecOf(
		DebugGameTestClearCustomPayload::write, DebugGameTestClearCustomPayload::new
	);
	public static final CustomPayload.Id<DebugGameTestClearCustomPayload> ID = CustomPayload.id("debug/game_test_clear");

	private DebugGameTestClearCustomPayload(PacketByteBuf buf) {
		this();
	}

	private void write(PacketByteBuf buf) {
	}

	@Override
	public CustomPayload.Id<DebugGameTestClearCustomPayload> getId() {
		return ID;
	}
}
