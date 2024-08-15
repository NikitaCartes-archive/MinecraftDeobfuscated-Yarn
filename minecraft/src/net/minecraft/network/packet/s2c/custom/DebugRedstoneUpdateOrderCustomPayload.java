package net.minecraft.network.packet.s2c.custom;

import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.block.WireOrientation;

public record DebugRedstoneUpdateOrderCustomPayload(long time, List<DebugRedstoneUpdateOrderCustomPayload.Wire> wires) implements CustomPayload {
	public static final CustomPayload.Id<DebugRedstoneUpdateOrderCustomPayload> ID = CustomPayload.id("debug/redstone_update_order");
	public static final PacketCodec<PacketByteBuf, DebugRedstoneUpdateOrderCustomPayload> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.VAR_LONG,
		DebugRedstoneUpdateOrderCustomPayload::time,
		DebugRedstoneUpdateOrderCustomPayload.Wire.PACKET_CODEC.collect(PacketCodecs.toList()),
		DebugRedstoneUpdateOrderCustomPayload::wires,
		DebugRedstoneUpdateOrderCustomPayload::new
	);

	@Override
	public CustomPayload.Id<DebugRedstoneUpdateOrderCustomPayload> getId() {
		return ID;
	}

	public static record Wire(BlockPos pos, WireOrientation orientation) {
		public static final PacketCodec<ByteBuf, DebugRedstoneUpdateOrderCustomPayload.Wire> PACKET_CODEC = PacketCodec.tuple(
			BlockPos.PACKET_CODEC,
			DebugRedstoneUpdateOrderCustomPayload.Wire::pos,
			WireOrientation.PACKET_CODEC,
			DebugRedstoneUpdateOrderCustomPayload.Wire::orientation,
			DebugRedstoneUpdateOrderCustomPayload.Wire::new
		);
	}
}
