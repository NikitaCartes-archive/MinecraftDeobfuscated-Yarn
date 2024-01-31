package net.minecraft.item.map;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record MapId(int id) {
	public static final PacketCodec<ByteBuf, MapId> CODEC = PacketCodecs.VAR_INT.xmap(MapId::new, MapId::id);

	public String asString() {
		return "map_" + this.id;
	}
}
