package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public record DamageResistantComponent(TagKey<DamageType> types) {
	public static final Codec<DamageResistantComponent> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(TagKey.codec(RegistryKeys.DAMAGE_TYPE).fieldOf("types").forGetter(DamageResistantComponent::types))
				.apply(instance, DamageResistantComponent::new)
	);
	public static final PacketCodec<RegistryByteBuf, DamageResistantComponent> PACKET_CODEC = PacketCodec.tuple(
		TagKey.packetCodec(RegistryKeys.DAMAGE_TYPE), DamageResistantComponent::types, DamageResistantComponent::new
	);

	public boolean resists(DamageSource damageSource) {
		return damageSource.isIn(this.types);
	}
}
