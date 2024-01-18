package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record DebugPoiAddedCustomPayload(BlockPos pos, String poiType, int freeTicketCount) implements CustomPayload {
	public static final PacketCodec<PacketByteBuf, DebugPoiAddedCustomPayload> CODEC = CustomPayload.codecOf(
		DebugPoiAddedCustomPayload::write, DebugPoiAddedCustomPayload::new
	);
	public static final CustomPayload.Id<DebugPoiAddedCustomPayload> ID = CustomPayload.id("debug/poi_added");

	private DebugPoiAddedCustomPayload(PacketByteBuf buf) {
		this(buf.readBlockPos(), buf.readString(), buf.readInt());
	}

	private void write(PacketByteBuf buf) {
		buf.writeBlockPos(this.pos);
		buf.writeString(this.poiType);
		buf.writeInt(this.freeTicketCount);
	}

	@Override
	public CustomPayload.Id<DebugPoiAddedCustomPayload> getId() {
		return ID;
	}
}
