package net.minecraft.predicate.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.predicate.NumberRange;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.dynamic.Codecs;

public record EntityEffectPredicate(Map<RegistryEntry<StatusEffect>, EntityEffectPredicate.EffectData> effects) {
	public static final Codec<EntityEffectPredicate> CODEC = Codec.unboundedMap(
			Registries.STATUS_EFFECT.createEntryCodec(), EntityEffectPredicate.EffectData.CODEC
		)
		.xmap(EntityEffectPredicate::new, EntityEffectPredicate::effects);

	public boolean test(Entity entity) {
		if (entity instanceof LivingEntity livingEntity && this.test(livingEntity.getActiveStatusEffects())) {
			return true;
		}

		return false;
	}

	public boolean test(LivingEntity livingEntity) {
		return this.test(livingEntity.getActiveStatusEffects());
	}

	public boolean test(Map<StatusEffect, StatusEffectInstance> effects) {
		for (Entry<RegistryEntry<StatusEffect>, EntityEffectPredicate.EffectData> entry : this.effects.entrySet()) {
			StatusEffectInstance statusEffectInstance = (StatusEffectInstance)effects.get(((RegistryEntry)entry.getKey()).value());
			if (!((EntityEffectPredicate.EffectData)entry.getValue()).test(statusEffectInstance)) {
				return false;
			}
		}

		return true;
	}

	public static class Builder {
		private final ImmutableMap.Builder<RegistryEntry<StatusEffect>, EntityEffectPredicate.EffectData> EFFECTS = ImmutableMap.builder();

		public static EntityEffectPredicate.Builder create() {
			return new EntityEffectPredicate.Builder();
		}

		public EntityEffectPredicate.Builder addEffect(StatusEffect effect) {
			this.EFFECTS.put(effect.getRegistryEntry(), new EntityEffectPredicate.EffectData());
			return this;
		}

		public EntityEffectPredicate.Builder addEffect(StatusEffect effect, EntityEffectPredicate.EffectData effectData) {
			this.EFFECTS.put(effect.getRegistryEntry(), effectData);
			return this;
		}

		public Optional<EntityEffectPredicate> build() {
			return Optional.of(new EntityEffectPredicate(this.EFFECTS.build()));
		}
	}

	public static record EffectData(NumberRange.IntRange amplifier, NumberRange.IntRange duration, Optional<Boolean> ambient, Optional<Boolean> visible) {
		public static final Codec<EntityEffectPredicate.EffectData> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codecs.createStrictOptionalFieldCodec(NumberRange.IntRange.CODEC, "amplifier", NumberRange.IntRange.ANY)
							.forGetter(EntityEffectPredicate.EffectData::amplifier),
						Codecs.createStrictOptionalFieldCodec(NumberRange.IntRange.CODEC, "duration", NumberRange.IntRange.ANY)
							.forGetter(EntityEffectPredicate.EffectData::duration),
						Codecs.createStrictOptionalFieldCodec(Codec.BOOL, "ambient").forGetter(EntityEffectPredicate.EffectData::ambient),
						Codecs.createStrictOptionalFieldCodec(Codec.BOOL, "visible").forGetter(EntityEffectPredicate.EffectData::visible)
					)
					.apply(instance, EntityEffectPredicate.EffectData::new)
		);

		public EffectData() {
			this(NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, Optional.empty(), Optional.empty());
		}

		public boolean test(@Nullable StatusEffectInstance statusEffectInstance) {
			if (statusEffectInstance == null) {
				return false;
			} else if (!this.amplifier.test(statusEffectInstance.getAmplifier())) {
				return false;
			} else if (!this.duration.test(statusEffectInstance.getDuration())) {
				return false;
			} else {
				return this.ambient.isPresent() && this.ambient.get() != statusEffectInstance.isAmbient()
					? false
					: !this.visible.isPresent() || (Boolean)this.visible.get() == statusEffectInstance.shouldShowParticles();
			}
		}
	}
}
