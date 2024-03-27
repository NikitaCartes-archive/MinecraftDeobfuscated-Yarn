package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class TargetHitCriterion extends AbstractCriterion<TargetHitCriterion.Conditions> {
	@Override
	public Codec<TargetHitCriterion.Conditions> getConditionsCodec() {
		return TargetHitCriterion.Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, Entity projectile, Vec3d hitPos, int signalStrength) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, projectile);
		this.trigger(player, conditions -> conditions.test(lootContext, hitPos, signalStrength));
	}

	public static record Conditions(Optional<LootContextPredicate> player, NumberRange.IntRange signalStrength, Optional<LootContextPredicate> projectile)
		implements AbstractCriterion.Conditions {
		public static final Codec<TargetHitCriterion.Conditions> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(TargetHitCriterion.Conditions::player),
						NumberRange.IntRange.CODEC.optionalFieldOf("signal_strength", NumberRange.IntRange.ANY).forGetter(TargetHitCriterion.Conditions::signalStrength),
						EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("projectile").forGetter(TargetHitCriterion.Conditions::projectile)
					)
					.apply(instance, TargetHitCriterion.Conditions::new)
		);

		public static AdvancementCriterion<TargetHitCriterion.Conditions> create(NumberRange.IntRange signalStrength, Optional<LootContextPredicate> projectile) {
			return Criteria.TARGET_HIT.create(new TargetHitCriterion.Conditions(Optional.empty(), signalStrength, projectile));
		}

		public boolean test(LootContext projectile, Vec3d hitPos, int signalStrength) {
			return !this.signalStrength.test(signalStrength) ? false : !this.projectile.isPresent() || ((LootContextPredicate)this.projectile.get()).test(projectile);
		}

		@Override
		public void validate(LootContextPredicateValidator validator) {
			AbstractCriterion.Conditions.super.validate(validator);
			validator.validateEntityPredicate(this.projectile, ".projectile");
		}
	}
}
