package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class PlayerHurtEntityCriterion extends AbstractCriterion<PlayerHurtEntityCriterion.Conditions> {
	static final Identifier ID = new Identifier("player_hurt_entity");

	@Override
	public Identifier getId() {
		return ID;
	}

	public PlayerHurtEntityCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		Optional<DamagePredicate> optional2 = DamagePredicate.fromJson(jsonObject.get("damage"));
		Optional<LootContextPredicate> optional3 = EntityPredicate.contextPredicateFromJson(jsonObject, "entity", advancementEntityPredicateDeserializer);
		return new PlayerHurtEntityCriterion.Conditions(optional, optional2, optional3);
	}

	public void trigger(ServerPlayerEntity player, Entity entity, DamageSource damage, float dealt, float taken, boolean blocked) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, entity);
		this.trigger(player, conditions -> conditions.matches(player, lootContext, damage, dealt, taken, blocked));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Optional<DamagePredicate> damage;
		private final Optional<LootContextPredicate> entity;

		public Conditions(Optional<LootContextPredicate> playerPredicate, Optional<DamagePredicate> damage, Optional<LootContextPredicate> entity) {
			super(PlayerHurtEntityCriterion.ID, playerPredicate);
			this.damage = damage;
			this.entity = entity;
		}

		public static PlayerHurtEntityCriterion.Conditions create() {
			return new PlayerHurtEntityCriterion.Conditions(Optional.empty(), Optional.empty(), Optional.empty());
		}

		public static PlayerHurtEntityCriterion.Conditions method_35296(Optional<DamagePredicate> optional) {
			return new PlayerHurtEntityCriterion.Conditions(Optional.empty(), optional, Optional.empty());
		}

		public static PlayerHurtEntityCriterion.Conditions create(DamagePredicate.Builder damage) {
			return new PlayerHurtEntityCriterion.Conditions(Optional.empty(), damage.build(), Optional.empty());
		}

		public static PlayerHurtEntityCriterion.Conditions create(Optional<EntityPredicate> entity) {
			return new PlayerHurtEntityCriterion.Conditions(Optional.empty(), Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(entity));
		}

		public static PlayerHurtEntityCriterion.Conditions create(Optional<DamagePredicate> damage, Optional<EntityPredicate> entity) {
			return new PlayerHurtEntityCriterion.Conditions(Optional.empty(), damage, EntityPredicate.contextPredicateFromEntityPredicate(entity));
		}

		public static PlayerHurtEntityCriterion.Conditions create(DamagePredicate.Builder damage, Optional<EntityPredicate> entity) {
			return new PlayerHurtEntityCriterion.Conditions(Optional.empty(), damage.build(), EntityPredicate.contextPredicateFromEntityPredicate(entity));
		}

		public boolean matches(ServerPlayerEntity player, LootContext entity, DamageSource damageSource, float dealt, float taken, boolean blocked) {
			return this.damage.isPresent() && !((DamagePredicate)this.damage.get()).test(player, damageSource, dealt, taken, blocked)
				? false
				: !this.entity.isPresent() || ((LootContextPredicate)this.entity.get()).test(entity);
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
			this.damage.ifPresent(damagePredicate -> jsonObject.add("damage", damagePredicate.toJson()));
			this.entity.ifPresent(lootContextPredicate -> jsonObject.add("entity", lootContextPredicate.toJson()));
			return jsonObject;
		}
	}
}
