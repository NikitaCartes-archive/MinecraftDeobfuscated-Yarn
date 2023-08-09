package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
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
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		return new OnKilledCriterion.Conditions(
			this.id,
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

		public Conditions(
			Identifier id, Optional<LootContextPredicate> playerPredicate, Optional<LootContextPredicate> entity, Optional<DamageSourcePredicate> killingBlow
		) {
			super(id, playerPredicate);
			this.entity = entity;
			this.killingBlow = killingBlow;
		}

		public static OnKilledCriterion.Conditions createPlayerKilledEntity(Optional<EntityPredicate> entity) {
			return new OnKilledCriterion.Conditions(
				Criteria.PLAYER_KILLED_ENTITY.id, Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(entity), Optional.empty()
			);
		}

		public static OnKilledCriterion.Conditions createPlayerKilledEntity(EntityPredicate.Builder killedEntityPredicateBuilder) {
			return new OnKilledCriterion.Conditions(
				Criteria.PLAYER_KILLED_ENTITY.id, Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(killedEntityPredicateBuilder), Optional.empty()
			);
		}

		public static OnKilledCriterion.Conditions createPlayerKilledEntity() {
			return new OnKilledCriterion.Conditions(Criteria.PLAYER_KILLED_ENTITY.id, Optional.empty(), Optional.empty(), Optional.empty());
		}

		public static OnKilledCriterion.Conditions createPlayerKilledEntity(Optional<EntityPredicate> entity, Optional<DamageSourcePredicate> killingBlow) {
			return new OnKilledCriterion.Conditions(
				Criteria.PLAYER_KILLED_ENTITY.id, Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(entity), killingBlow
			);
		}

		public static OnKilledCriterion.Conditions createPlayerKilledEntity(
			EntityPredicate.Builder killedEntityPredicateBuilder, Optional<DamageSourcePredicate> killingBlow
		) {
			return new OnKilledCriterion.Conditions(
				Criteria.PLAYER_KILLED_ENTITY.id, Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(killedEntityPredicateBuilder), killingBlow
			);
		}

		public static OnKilledCriterion.Conditions createPlayerKilledEntity(
			Optional<EntityPredicate> entity, DamageSourcePredicate.Builder damageSourcePredicateBuilder
		) {
			return new OnKilledCriterion.Conditions(
				Criteria.PLAYER_KILLED_ENTITY.id, Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(entity), damageSourcePredicateBuilder.build()
			);
		}

		public static OnKilledCriterion.Conditions createPlayerKilledEntity(
			EntityPredicate.Builder killedEntityPredicateBuilder, DamageSourcePredicate.Builder killingBlowBuilder
		) {
			return new OnKilledCriterion.Conditions(
				Criteria.PLAYER_KILLED_ENTITY.id,
				Optional.empty(),
				EntityPredicate.contextPredicateFromEntityPredicate(killedEntityPredicateBuilder),
				killingBlowBuilder.build()
			);
		}

		public static OnKilledCriterion.Conditions createKillMobNearSculkCatalyst() {
			return new OnKilledCriterion.Conditions(Criteria.KILL_MOB_NEAR_SCULK_CATALYST.id, Optional.empty(), Optional.empty(), Optional.empty());
		}

		public static OnKilledCriterion.Conditions createEntityKilledPlayer(Optional<EntityPredicate> entity) {
			return new OnKilledCriterion.Conditions(
				Criteria.ENTITY_KILLED_PLAYER.id, Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(entity), Optional.empty()
			);
		}

		public static OnKilledCriterion.Conditions createEntityKilledPlayer(EntityPredicate.Builder killerEntityPredicateBuilder) {
			return new OnKilledCriterion.Conditions(
				Criteria.ENTITY_KILLED_PLAYER.id, Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(killerEntityPredicateBuilder), Optional.empty()
			);
		}

		public static OnKilledCriterion.Conditions createEntityKilledPlayer() {
			return new OnKilledCriterion.Conditions(Criteria.ENTITY_KILLED_PLAYER.id, Optional.empty(), Optional.empty(), Optional.empty());
		}

		public static OnKilledCriterion.Conditions createEntityKilledPlayer(Optional<EntityPredicate> entity, Optional<DamageSourcePredicate> killingBlow) {
			return new OnKilledCriterion.Conditions(
				Criteria.ENTITY_KILLED_PLAYER.id, Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(entity), killingBlow
			);
		}

		public static OnKilledCriterion.Conditions createEntityKilledPlayer(
			EntityPredicate.Builder killerEntityPredicateBuilder, Optional<DamageSourcePredicate> killingBlow
		) {
			return new OnKilledCriterion.Conditions(
				Criteria.ENTITY_KILLED_PLAYER.id, Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(killerEntityPredicateBuilder), killingBlow
			);
		}

		public static OnKilledCriterion.Conditions createEntityKilledPlayer(
			Optional<EntityPredicate> entity, DamageSourcePredicate.Builder damageSourcePredicateBuilder
		) {
			return new OnKilledCriterion.Conditions(
				Criteria.ENTITY_KILLED_PLAYER.id, Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(entity), damageSourcePredicateBuilder.build()
			);
		}

		public static OnKilledCriterion.Conditions createEntityKilledPlayer(
			EntityPredicate.Builder killerEntityPredicateBuilder, DamageSourcePredicate.Builder damageSourcePredicateBuilder
		) {
			return new OnKilledCriterion.Conditions(
				Criteria.ENTITY_KILLED_PLAYER.id,
				Optional.empty(),
				EntityPredicate.contextPredicateFromEntityPredicate(killerEntityPredicateBuilder),
				damageSourcePredicateBuilder.build()
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
