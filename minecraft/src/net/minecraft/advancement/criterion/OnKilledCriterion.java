package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class OnKilledCriterion extends AbstractCriterion<OnKilledCriterion.Conditions> {
	final Identifier id;

	public OnKilledCriterion(Identifier id) {
		this.id = id;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	public OnKilledCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, LootContextPredicate lootContextPredicate, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		return new OnKilledCriterion.Conditions(
			this.id,
			lootContextPredicate,
			EntityPredicate.contextPredicateFromJson(jsonObject, "entity", advancementEntityPredicateDeserializer),
			DamageSourcePredicate.fromJson(jsonObject.get("killing_blow"))
		);
	}

	public void trigger(ServerPlayerEntity player, Entity entity, DamageSource killingDamage) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, entity);
		this.trigger(player, conditions -> conditions.test(player, lootContext, killingDamage));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final LootContextPredicate entity;
		private final DamageSourcePredicate killingBlow;

		public Conditions(Identifier id, LootContextPredicate player, LootContextPredicate entity, DamageSourcePredicate killingBlow) {
			super(id, player);
			this.entity = entity;
			this.killingBlow = killingBlow;
		}

		public static OnKilledCriterion.Conditions createPlayerKilledEntity(EntityPredicate killedEntityPredicate) {
			return new OnKilledCriterion.Conditions(
				Criteria.PLAYER_KILLED_ENTITY.id, LootContextPredicate.EMPTY, EntityPredicate.asLootContextPredicate(killedEntityPredicate), DamageSourcePredicate.EMPTY
			);
		}

		public static OnKilledCriterion.Conditions createPlayerKilledEntity(EntityPredicate.Builder killedEntityPredicateBuilder) {
			return new OnKilledCriterion.Conditions(
				Criteria.PLAYER_KILLED_ENTITY.id,
				LootContextPredicate.EMPTY,
				EntityPredicate.asLootContextPredicate(killedEntityPredicateBuilder.build()),
				DamageSourcePredicate.EMPTY
			);
		}

		public static OnKilledCriterion.Conditions createPlayerKilledEntity() {
			return new OnKilledCriterion.Conditions(
				Criteria.PLAYER_KILLED_ENTITY.id, LootContextPredicate.EMPTY, LootContextPredicate.EMPTY, DamageSourcePredicate.EMPTY
			);
		}

		public static OnKilledCriterion.Conditions createPlayerKilledEntity(EntityPredicate killedEntityPredicate, DamageSourcePredicate damageSourcePredicate) {
			return new OnKilledCriterion.Conditions(
				Criteria.PLAYER_KILLED_ENTITY.id, LootContextPredicate.EMPTY, EntityPredicate.asLootContextPredicate(killedEntityPredicate), damageSourcePredicate
			);
		}

		public static OnKilledCriterion.Conditions createPlayerKilledEntity(
			EntityPredicate.Builder killedEntityPredicateBuilder, DamageSourcePredicate damageSourcePredicate
		) {
			return new OnKilledCriterion.Conditions(
				Criteria.PLAYER_KILLED_ENTITY.id,
				LootContextPredicate.EMPTY,
				EntityPredicate.asLootContextPredicate(killedEntityPredicateBuilder.build()),
				damageSourcePredicate
			);
		}

		public static OnKilledCriterion.Conditions createPlayerKilledEntity(
			EntityPredicate killedEntityPredicate, DamageSourcePredicate.Builder damageSourcePredicateBuilder
		) {
			return new OnKilledCriterion.Conditions(
				Criteria.PLAYER_KILLED_ENTITY.id,
				LootContextPredicate.EMPTY,
				EntityPredicate.asLootContextPredicate(killedEntityPredicate),
				damageSourcePredicateBuilder.build()
			);
		}

		public static OnKilledCriterion.Conditions createPlayerKilledEntity(
			EntityPredicate.Builder killedEntityPredicateBuilder, DamageSourcePredicate.Builder killingBlowBuilder
		) {
			return new OnKilledCriterion.Conditions(
				Criteria.PLAYER_KILLED_ENTITY.id,
				LootContextPredicate.EMPTY,
				EntityPredicate.asLootContextPredicate(killedEntityPredicateBuilder.build()),
				killingBlowBuilder.build()
			);
		}

		public static OnKilledCriterion.Conditions createKillMobNearSculkCatalyst() {
			return new OnKilledCriterion.Conditions(
				Criteria.KILL_MOB_NEAR_SCULK_CATALYST.id, LootContextPredicate.EMPTY, LootContextPredicate.EMPTY, DamageSourcePredicate.EMPTY
			);
		}

		public static OnKilledCriterion.Conditions createEntityKilledPlayer(EntityPredicate killerEntityPredicate) {
			return new OnKilledCriterion.Conditions(
				Criteria.ENTITY_KILLED_PLAYER.id, LootContextPredicate.EMPTY, EntityPredicate.asLootContextPredicate(killerEntityPredicate), DamageSourcePredicate.EMPTY
			);
		}

		public static OnKilledCriterion.Conditions createEntityKilledPlayer(EntityPredicate.Builder killerEntityPredicateBuilder) {
			return new OnKilledCriterion.Conditions(
				Criteria.ENTITY_KILLED_PLAYER.id,
				LootContextPredicate.EMPTY,
				EntityPredicate.asLootContextPredicate(killerEntityPredicateBuilder.build()),
				DamageSourcePredicate.EMPTY
			);
		}

		public static OnKilledCriterion.Conditions createEntityKilledPlayer() {
			return new OnKilledCriterion.Conditions(
				Criteria.ENTITY_KILLED_PLAYER.id, LootContextPredicate.EMPTY, LootContextPredicate.EMPTY, DamageSourcePredicate.EMPTY
			);
		}

		public static OnKilledCriterion.Conditions createEntityKilledPlayer(EntityPredicate killerEntityPredicate, DamageSourcePredicate damageSourcePredicate) {
			return new OnKilledCriterion.Conditions(
				Criteria.ENTITY_KILLED_PLAYER.id, LootContextPredicate.EMPTY, EntityPredicate.asLootContextPredicate(killerEntityPredicate), damageSourcePredicate
			);
		}

		public static OnKilledCriterion.Conditions createEntityKilledPlayer(
			EntityPredicate.Builder killerEntityPredicateBuilder, DamageSourcePredicate damageSourcePredicate
		) {
			return new OnKilledCriterion.Conditions(
				Criteria.ENTITY_KILLED_PLAYER.id,
				LootContextPredicate.EMPTY,
				EntityPredicate.asLootContextPredicate(killerEntityPredicateBuilder.build()),
				damageSourcePredicate
			);
		}

		public static OnKilledCriterion.Conditions createEntityKilledPlayer(
			EntityPredicate killerEntityPredicate, DamageSourcePredicate.Builder damageSourcePredicateBuilder
		) {
			return new OnKilledCriterion.Conditions(
				Criteria.ENTITY_KILLED_PLAYER.id,
				LootContextPredicate.EMPTY,
				EntityPredicate.asLootContextPredicate(killerEntityPredicate),
				damageSourcePredicateBuilder.build()
			);
		}

		public static OnKilledCriterion.Conditions createEntityKilledPlayer(
			EntityPredicate.Builder killerEntityPredicateBuilder, DamageSourcePredicate.Builder damageSourcePredicateBuilder
		) {
			return new OnKilledCriterion.Conditions(
				Criteria.ENTITY_KILLED_PLAYER.id,
				LootContextPredicate.EMPTY,
				EntityPredicate.asLootContextPredicate(killerEntityPredicateBuilder.build()),
				damageSourcePredicateBuilder.build()
			);
		}

		public boolean test(ServerPlayerEntity player, LootContext killedEntityContext, DamageSource killingBlow) {
			return !this.killingBlow.test(player, killingBlow) ? false : this.entity.test(killedEntityContext);
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.add("entity", this.entity.toJson(predicateSerializer));
			jsonObject.add("killing_blow", this.killingBlow.toJson());
			return jsonObject;
		}
	}
}
