package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record MapIdComponent(int id) {
	public static final Codec<MapIdComponent> CODEC = Codec.INT.xmap(MapIdComponent::new, MapIdComponent::id);
	public static final PacketCodec<ByteBuf, MapIdComponent> PACKET_CODEC = PacketCodecs.VAR_INT.xmap(MapIdComponent::new, MapIdComponent::id);

	public String asString() {
		return "map_" + this.id;
	}
}
