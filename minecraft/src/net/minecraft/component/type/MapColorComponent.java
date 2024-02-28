package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record MapColorComponent(int rgb) {
	public static final Codec<MapColorComponent> CODEC = Codec.INT.xmap(MapColorComponent::new, MapColorComponent::rgb);
	public static final PacketCodec<ByteBuf, MapColorComponent> PACKET_CODEC = PacketCodecs.INTEGER.xmap(MapColorComponent::new, MapColorComponent::rgb);
	public static final MapColorComponent DEFAULT = new MapColorComponent(4603950);
}
