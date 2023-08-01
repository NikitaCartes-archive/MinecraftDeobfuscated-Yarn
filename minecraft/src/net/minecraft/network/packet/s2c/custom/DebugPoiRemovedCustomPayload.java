package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record DebugPoiRemovedCustomPayload(BlockPos pos) implements CustomPayload {
	public static final Identifier ID = new Identifier("debug/poi_removed");

	public DebugPoiRemovedCustomPayload(PacketByteBuf buf) {
		this(buf.readBlockPos());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBlockPos(this.pos);
	}

	@Override
	public Identifier id() {
		return ID;
	}
}
