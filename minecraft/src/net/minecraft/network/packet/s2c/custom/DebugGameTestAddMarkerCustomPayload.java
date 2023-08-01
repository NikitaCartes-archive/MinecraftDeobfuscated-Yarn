package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record DebugGameTestAddMarkerCustomPayload(BlockPos pos, int color, String text, int durationMs) implements CustomPayload {
	public static final Identifier ID = new Identifier("debug/game_test_add_marker");

	public DebugGameTestAddMarkerCustomPayload(PacketByteBuf buf) {
		this(buf.readBlockPos(), buf.readInt(), buf.readString(), buf.readInt());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBlockPos(this.pos);
		buf.writeInt(this.color);
		buf.writeString(this.text);
		buf.writeInt(this.durationMs);
	}

	@Override
	public Identifier id() {
		return ID;
	}
}
