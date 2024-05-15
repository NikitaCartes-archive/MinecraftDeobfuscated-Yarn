package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Util;

public record SuspiciousStewEffectsComponent(List<SuspiciousStewEffectsComponent.StewEffect> effects) {
	public static final SuspiciousStewEffectsComponent DEFAULT = new SuspiciousStewEffectsComponent(List.of());
	public static final Codec<SuspiciousStewEffectsComponent> CODEC = SuspiciousStewEffectsComponent.StewEffect.CODEC
		.listOf()
		.xmap(SuspiciousStewEffectsComponent::new, SuspiciousStewEffectsComponent::effects);
	public static final PacketCodec<RegistryByteBuf, SuspiciousStewEffectsComponent> PACKET_CODEC = SuspiciousStewEffectsComponent.StewEffect.PACKET_CODEC
		.collect(PacketCodecs.toList())
		.xmap(SuspiciousStewEffectsComponent::new, SuspiciousStewEffectsComponent::effects);

	public SuspiciousStewEffectsComponent with(SuspiciousStewEffectsComponent.StewEffect stewEffect) {
		return new SuspiciousStewEffectsComponent(Util.withAppended(this.effects, stewEffect));
	}

	public static record StewEffect(RegistryEntry<StatusEffect> effect, int duration) {
		public static final Codec<SuspiciousStewEffectsComponent.StewEffect> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						StatusEffect.ENTRY_CODEC.fieldOf("id").forGetter(SuspiciousStewEffectsComponent.StewEffect::effect),
						Codec.INT.lenientOptionalFieldOf("duration", Integer.valueOf(160)).forGetter(SuspiciousStewEffectsComponent.StewEffect::duration)
					)
					.apply(instance, SuspiciousStewEffectsComponent.StewEffect::new)
		);
		public static final PacketCodec<RegistryByteBuf, SuspiciousStewEffectsComponent.StewEffect> PACKET_CODEC = PacketCodec.tuple(
			StatusEffect.ENTRY_PACKET_CODEC,
			SuspiciousStewEffectsComponent.StewEffect::effect,
			PacketCodecs.VAR_INT,
			SuspiciousStewEffectsComponent.StewEffect::duration,
			SuspiciousStewEffectsComponent.StewEffect::new
		);

		public StatusEffectInstance createStatusEffectInstance() {
			return new StatusEffectInstance(this.effect, this.duration);
		}
	}
}
