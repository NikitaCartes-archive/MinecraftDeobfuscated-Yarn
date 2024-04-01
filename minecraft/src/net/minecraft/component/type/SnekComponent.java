package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record SnekComponent(boolean revealed) {
	public static SnekComponent DEFAULT = new SnekComponent(false);
	public static final Codec<SnekComponent> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(Codec.BOOL.fieldOf("revealed").forGetter(SnekComponent::revealed)).apply(instance, SnekComponent::new)
	);
	public static final PacketCodec<? super RegistryByteBuf, SnekComponent> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.BOOL, SnekComponent::revealed, SnekComponent::new
	);
}
