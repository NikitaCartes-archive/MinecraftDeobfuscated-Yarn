package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class TargetHitCriterion extends AbstractCriterion<TargetHitCriterion.Conditions> {
	public TargetHitCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("signal_strength"));
		Optional<LootContextPredicate> optional2 = EntityPredicate.contextPredicateFromJson(jsonObject, "projectile", advancementEntityPredicateDeserializer);
		return new TargetHitCriterion.Conditions(optional, intRange, optional2);
	}

	public void trigger(ServerPlayerEntity player, Entity projectile, Vec3d hitPos, int signalStrength) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, projectile);
		this.trigger(player, conditions -> conditions.test(lootContext, hitPos, signalStrength));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final NumberRange.IntRange signalStrength;
		private final Optional<LootContextPredicate> projectile;

		public Conditions(Optional<LootContextPredicate> playerPredicate, NumberRange.IntRange signalStrength, Optional<LootContextPredicate> projectile) {
			super(playerPredicate);
			this.signalStrength = signalStrength;
			this.projectile = projectile;
		}

		public static AdvancementCriterion<TargetHitCriterion.Conditions> create(NumberRange.IntRange signalStrength, Optional<LootContextPredicate> projectile) {
			return Criteria.TARGET_HIT.create(new TargetHitCriterion.Conditions(Optional.empty(), signalStrength, projectile));
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
			jsonObject.add("signal_strength", this.signalStrength.toJson());
			this.projectile.ifPresent(projectile -> jsonObject.add("projectile", projectile.toJson()));
			return jsonObject;
		}

		public boolean test(LootContext projectile, Vec3d hitPos, int signalStrength) {
			return !this.signalStrength.test(signalStrength) ? false : !this.projectile.isPresent() || ((LootContextPredicate)this.projectile.get()).test(projectile);
		}
	}
}
