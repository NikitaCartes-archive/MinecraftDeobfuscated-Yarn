package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class OnKilledCriterion extends AbstractCriterion<OnKilledCriterion.Conditions> {
	public OnKilledCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		return new OnKilledCriterion.Conditions(
			optional,
			EntityPredicate.contextPredicateFromJson(jsonObject, "entity", advancementEntityPredicateDeserializer),
			DamageSourcePredicate.fromJson(jsonObject.get("killing_blow"))
		);
	}

	public void trigger(ServerPlayerEntity player, Entity entity, DamageSource killingDamage) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, entity);
		this.trigger(player, conditions -> conditions.test(player, lootContext, killingDamage));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Optional<LootContextPredicate> entity;
		private final Optional<DamageSourcePredicate> killingBlow;

		public Conditions(Optional<LootContextPredicate> optional, Optional<LootContextPredicate> playerPredicate, Optional<DamageSourcePredicate> entity) {
			super(optional);
			this.entity = playerPredicate;
			this.killingBlow = entity;
		}

		public static AdvancementCriterion<OnKilledCriterion.Conditions> createPlayerKilledEntity(Optional<EntityPredicate> entity) {
			return Criteria.PLAYER_KILLED_ENTITY
				.create(new OnKilledCriterion.Conditions(Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(entity), Optional.empty()));
		}

		public static AdvancementCriterion<OnKilledCriterion.Conditions> createPlayerKilledEntity(EntityPredicate.Builder killedEntityPredicateBuilder) {
			return Criteria.PLAYER_KILLED_ENTITY
				.create(
					new OnKilledCriterion.Conditions(
						Optional.empty(), Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(killedEntityPredicateBuilder)), Optional.empty()
					)
				);
		}

		public static AdvancementCriterion<OnKilledCriterion.Conditions> createPlayerKilledEntity() {
			return Criteria.PLAYER_KILLED_ENTITY.create(new OnKilledCriterion.Conditions(Optional.empty(), Optional.empty(), Optional.empty()));
		}

		public static AdvancementCriterion<OnKilledCriterion.Conditions> createPlayerKilledEntity(
			Optional<EntityPredicate> entity, Optional<DamageSourcePredicate> killingBlow
		) {
			return Criteria.PLAYER_KILLED_ENTITY
				.create(new OnKilledCriterion.Conditions(Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(entity), killingBlow));
		}

		public static AdvancementCriterion<OnKilledCriterion.Conditions> createPlayerKilledEntity(
			EntityPredicate.Builder killedEntityPredicateBuilder, Optional<DamageSourcePredicate> killingBlow
		) {
			return Criteria.PLAYER_KILLED_ENTITY
				.create(
					new OnKilledCriterion.Conditions(
						Optional.empty(), Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(killedEntityPredicateBuilder)), killingBlow
					)
				);
		}

		public static AdvancementCriterion<OnKilledCriterion.Conditions> createPlayerKilledEntity(
			Optional<EntityPredicate> entity, DamageSourcePredicate.Builder damageSourcePredicateBuilder
		) {
			return Criteria.PLAYER_KILLED_ENTITY
				.create(
					new OnKilledCriterion.Conditions(
						Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(entity), Optional.of(damageSourcePredicateBuilder.build())
					)
				);
		}

		public static AdvancementCriterion<OnKilledCriterion.Conditions> createPlayerKilledEntity(
			EntityPredicate.Builder killedEntityPredicateBuilder, DamageSourcePredicate.Builder killingBlowBuilder
		) {
			return Criteria.PLAYER_KILLED_ENTITY
				.create(
					new OnKilledCriterion.Conditions(
						Optional.empty(), Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(killedEntityPredicateBuilder)), Optional.of(killingBlowBuilder.build())
					)
				);
		}

		public static AdvancementCriterion<OnKilledCriterion.Conditions> createKillMobNearSculkCatalyst() {
			return Criteria.KILL_MOB_NEAR_SCULK_CATALYST.create(new OnKilledCriterion.Conditions(Optional.empty(), Optional.empty(), Optional.empty()));
		}

		public static AdvancementCriterion<OnKilledCriterion.Conditions> createEntityKilledPlayer(Optional<EntityPredicate> entity) {
			return Criteria.ENTITY_KILLED_PLAYER
				.create(new OnKilledCriterion.Conditions(Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(entity), Optional.empty()));
		}

		public static AdvancementCriterion<OnKilledCriterion.Conditions> createEntityKilledPlayer(EntityPredicate.Builder killerEntityPredicateBuilder) {
			return Criteria.ENTITY_KILLED_PLAYER
				.create(
					new OnKilledCriterion.Conditions(
						Optional.empty(), Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(killerEntityPredicateBuilder)), Optional.empty()
					)
				);
		}

		public static AdvancementCriterion<OnKilledCriterion.Conditions> createEntityKilledPlayer() {
			return Criteria.ENTITY_KILLED_PLAYER.create(new OnKilledCriterion.Conditions(Optional.empty(), Optional.empty(), Optional.empty()));
		}

		public static AdvancementCriterion<OnKilledCriterion.Conditions> createEntityKilledPlayer(
			Optional<EntityPredicate> entity, Optional<DamageSourcePredicate> killingBlow
		) {
			return Criteria.ENTITY_KILLED_PLAYER
				.create(new OnKilledCriterion.Conditions(Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(entity), killingBlow));
		}

		public static AdvancementCriterion<OnKilledCriterion.Conditions> createEntityKilledPlayer(
			EntityPredicate.Builder killerEntityPredicateBuilder, Optional<DamageSourcePredicate> killingBlow
		) {
			return Criteria.ENTITY_KILLED_PLAYER
				.create(
					new OnKilledCriterion.Conditions(
						Optional.empty(), Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(killerEntityPredicateBuilder)), killingBlow
					)
				);
		}

		public static AdvancementCriterion<OnKilledCriterion.Conditions> createEntityKilledPlayer(
			Optional<EntityPredicate> entity, DamageSourcePredicate.Builder damageSourcePredicateBuilder
		) {
			return Criteria.ENTITY_KILLED_PLAYER
				.create(
					new OnKilledCriterion.Conditions(
						Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(entity), Optional.of(damageSourcePredicateBuilder.build())
					)
				);
		}

		public static AdvancementCriterion<OnKilledCriterion.Conditions> createEntityKilledPlayer(
			EntityPredicate.Builder killerEntityPredicateBuilder, DamageSourcePredicate.Builder damageSourcePredicateBuilder
		) {
			return Criteria.ENTITY_KILLED_PLAYER
				.create(
					new OnKilledCriterion.Conditions(
						Optional.empty(),
						Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(killerEntityPredicateBuilder)),
						Optional.of(damageSourcePredicateBuilder.build())
					)
				);
		}

		public boolean test(ServerPlayerEntity player, LootContext entity, DamageSource killingBlow) {
			return this.killingBlow.isPresent() && !((DamageSourcePredicate)this.killingBlow.get()).test(player, killingBlow)
				? false
				: this.entity.isEmpty() || ((LootContextPredicate)this.entity.get()).test(entity);
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
			this.entity.ifPresent(lootContextPredicate -> jsonObject.add("entity", lootContextPredicate.toJson()));
			this.killingBlow.ifPresent(damageSourcePredicate -> jsonObject.add("killing_blow", damageSourcePredicate.toJson()));
			return jsonObject;
		}
	}
}
