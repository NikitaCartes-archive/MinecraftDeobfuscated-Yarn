package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record DebugPoiAddedCustomPayload(BlockPos pos, String type, int freeTicketCount) implements CustomPayload {
	public static final Identifier ID = new Identifier("debug/poi_added");

	public DebugPoiAddedCustomPayload(PacketByteBuf buf) {
		this(buf.readBlockPos(), buf.readString(), buf.readInt());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBlockPos(this.pos);
		buf.writeString(this.type);
		buf.writeInt(this.freeTicketCount);
	}

	@Override
	public Identifier id() {
		return ID;
	}
}
