package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record DebugGameTestAddMarkerCustomPayload(BlockPos pos, int color, String text, int durationMs) implements CustomPayload {
	public static final PacketCodec<PacketByteBuf, DebugGameTestAddMarkerCustomPayload> CODEC = CustomPayload.codecOf(
		DebugGameTestAddMarkerCustomPayload::write, DebugGameTestAddMarkerCustomPayload::new
	);
	public static final CustomPayload.Id<DebugGameTestAddMarkerCustomPayload> ID = CustomPayload.id("debug/game_test_add_marker");

	private DebugGameTestAddMarkerCustomPayload(PacketByteBuf buf) {
		this(buf.readBlockPos(), buf.readInt(), buf.readString(), buf.readInt());
	}

	private void write(PacketByteBuf buf) {
		buf.writeBlockPos(this.pos);
		buf.writeInt(this.color);
		buf.writeString(this.text);
		buf.writeInt(this.durationMs);
	}

	@Override
	public CustomPayload.Id<DebugGameTestAddMarkerCustomPayload> getId() {
		return ID;
	}
}
