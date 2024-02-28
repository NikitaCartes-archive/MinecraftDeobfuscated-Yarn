package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record CustomModelDataComponent(int value) {
	public static final CustomModelDataComponent DEFAULT = new CustomModelDataComponent(0);
	public static final Codec<CustomModelDataComponent> CODEC = Codec.INT.xmap(CustomModelDataComponent::new, CustomModelDataComponent::value);
	public static final PacketCodec<ByteBuf, CustomModelDataComponent> PACKET_CODEC = PacketCodecs.VAR_INT
		.xmap(CustomModelDataComponent::new, CustomModelDataComponent::value);
}
