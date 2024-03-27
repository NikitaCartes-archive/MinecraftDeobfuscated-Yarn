package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.EntityEffectPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.server.network.ServerPlayerEntity;

public class EffectsChangedCriterion extends AbstractCriterion<EffectsChangedCriterion.Conditions> {
	@Override
	public Codec<EffectsChangedCriterion.Conditions> getConditionsCodec() {
		return EffectsChangedCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, @Nullable Entity source) {
		LootContext lootContext = source != null ? EntityPredicate.createAdvancementEntityLootContext(player, source) : null;
		this.trigger(player, conditions -> conditions.matches(player, lootContext));
	}

	public static record Conditions(Optional<LootContextPredicate> player, Optional<EntityEffectPredicate> effects, Optional<LootContextPredicate> source)
		implements AbstractCriterion.Conditions {
		public static final Codec<EffectsChangedCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(EffectsChangedCriterion.Conditions::player),
						EntityEffectPredicate.CODEC.optionalFieldOf("effects").forGetter(EffectsChangedCriterion.Conditions::effects),
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("source").forGetter(EffectsChangedCriterion.Conditions::source)
					)
					.apply(instance, EffectsChangedCriterion.Conditions::new)
		);

		public static AdvancementCriterion<EffectsChangedCriterion.Conditions> create(EntityEffectPredicate.Builder effects) {
			return Criteria.EFFECTS_CHANGED.create(new EffectsChangedCriterion.Conditions(Optional.empty(), effects.build(), Optional.empty()));
		}

		public static AdvancementCriterion<EffectsChangedCriterion.Conditions> create(EntityPredicate.Builder source) {
			return Criteria.EFFECTS_CHANGED
				.create(new EffectsChangedCriterion.Conditions(Optional.empty(), Optional.empty(), Optional.of(EntityPredicate.asLootContextPredicate(source.build()))));
		}

		public boolean matches(ServerPlayerEntity player, @Nullable LootContext context) {
			return this.effects.isPresent() && !((EntityEffectPredicate)this.effects.get()).test((LivingEntity)player)
				? false
				: !this.source.isPresent() || context != null && ((LootContextPredicate)this.source.get()).test(context);
		}

		@Override
		public void validate(LootContextPredicateValidator validator) {
			AbstractCriterion.Conditions.super.validate(validator);
			validator.validateEntityPredicate(this.source, ".source");
		}
	}
}
