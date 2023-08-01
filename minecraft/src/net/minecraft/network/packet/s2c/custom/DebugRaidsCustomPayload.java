package net.minecraft.network.packet.s2c.custom;

import java.util.List;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record DebugRaidsCustomPayload(List<BlockPos> raidCenters) implements CustomPayload {
	public static final Identifier ID = new Identifier("debug/raids");

	public DebugRaidsCustomPayload(PacketByteBuf buf) {
		this(buf.readList(PacketByteBuf::readBlockPos));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeCollection(this.raidCenters, PacketByteBuf::writeBlockPos);
	}

	@Override
	public Identifier id() {
		return ID;
	}
}
