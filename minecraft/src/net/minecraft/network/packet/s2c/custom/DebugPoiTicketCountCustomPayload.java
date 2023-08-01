package net.minecraft.network.packet.s2c.custom;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record DebugPoiTicketCountCustomPayload(BlockPos pos, int freeTicketCount) implements CustomPayload {
	public static final Identifier ID = new Identifier("debug/poi_ticket_count");

	public DebugPoiTicketCountCustomPayload(PacketByteBuf buf) {
		this(buf.readBlockPos(), buf.readInt());
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBlockPos(this.pos);
		buf.writeInt(this.freeTicketCount);
	}

	@Override
	public Identifier id() {
		return ID;
	}
}
