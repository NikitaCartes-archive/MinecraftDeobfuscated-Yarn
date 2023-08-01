package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record DebugNeighborsUpdateCustomPayload(long time, BlockPos pos) implements CustomPayload {
	public static final Identifier ID = new Identifier("debug/neighbors_update");

	public DebugNeighborsUpdateCustomPayload(PacketByteBuf buf) {
		this(buf.readVarLong(), buf.readBlockPos());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarLong(this.time);
		buf.writeBlockPos(this.pos);
	}

	@Override
	public Identifier id() {
		return ID;
	}
}
