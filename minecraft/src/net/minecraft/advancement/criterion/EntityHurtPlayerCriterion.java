package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class EntityHurtPlayerCriterion extends AbstractCriterion<EntityHurtPlayerCriterion.Conditions> {
	public EntityHurtPlayerCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		Optional<DamagePredicate> optional2 = DamagePredicate.fromJson(jsonObject.get("damage"));
		return new EntityHurtPlayerCriterion.Conditions(optional, optional2);
	}

	public void trigger(ServerPlayerEntity player, DamageSource source, float dealt, float taken, boolean blocked) {
		this.trigger(player, conditions -> conditions.matches(player, source, dealt, taken, blocked));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Optional<DamagePredicate> damage;

		public Conditions(Optional<LootContextPredicate> playerPredicate, Optional<DamagePredicate> damage) {
			super(playerPredicate);
			this.damage = damage;
		}

		public static AdvancementCriterion<EntityHurtPlayerCriterion.Conditions> create() {
			return Criteria.ENTITY_HURT_PLAYER.create(new EntityHurtPlayerCriterion.Conditions(Optional.empty(), Optional.empty()));
		}

		public static AdvancementCriterion<EntityHurtPlayerCriterion.Conditions> create(DamagePredicate predicate) {
			return Criteria.ENTITY_HURT_PLAYER.create(new EntityHurtPlayerCriterion.Conditions(Optional.empty(), Optional.of(predicate)));
		}

		public static AdvancementCriterion<EntityHurtPlayerCriterion.Conditions> create(DamagePredicate.Builder damageBuilder) {
			return Criteria.ENTITY_HURT_PLAYER.create(new EntityHurtPlayerCriterion.Conditions(Optional.empty(), Optional.of(damageBuilder.build())));
		}

		public boolean matches(ServerPlayerEntity player, DamageSource damageSource, float dealt, float taken, boolean blocked) {
			return !this.damage.isPresent() || ((DamagePredicate)this.damage.get()).test(player, damageSource, dealt, taken, blocked);
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
			this.damage.ifPresent(damagePredicate -> jsonObject.add("damage", damagePredicate.toJson()));
			return jsonObject;
		}
	}
}
