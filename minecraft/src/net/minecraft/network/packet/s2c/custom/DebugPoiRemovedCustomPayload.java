package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record DebugPoiRemovedCustomPayload(BlockPos pos) implements CustomPayload {
	public static final PacketCodec<PacketByteBuf, DebugPoiRemovedCustomPayload> CODEC = CustomPayload.codecOf(
		DebugPoiRemovedCustomPayload::write, DebugPoiRemovedCustomPayload::new
	);
	public static final CustomPayload.Id<DebugPoiRemovedCustomPayload> ID = CustomPayload.id("debug/poi_removed");

	private DebugPoiRemovedCustomPayload(PacketByteBuf buf) {
		this(buf.readBlockPos());
	}

	private void write(PacketByteBuf buf) {
		buf.writeBlockPos(this.pos);
	}

	@Override
	public CustomPayload.Id<DebugPoiRemovedCustomPayload> getId() {
		return ID;
	}
}
