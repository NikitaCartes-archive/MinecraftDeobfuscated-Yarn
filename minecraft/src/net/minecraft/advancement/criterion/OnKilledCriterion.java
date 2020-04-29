package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class OnKilledCriterion extends AbstractCriterion<OnKilledCriterion.Conditions> {
	private final Identifier id;

	public OnKilledCriterion(Identifier id) {
		this.id = id;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	public OnKilledCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		return new OnKilledCriterion.Conditions(
			this.id,
			extended,
			EntityPredicate.Extended.getInJson(jsonObject, "entity", advancementEntityPredicateDeserializer),
			DamageSourcePredicate.fromJson(jsonObject.get("killing_blow"))
		);
	}

	public void trigger(ServerPlayerEntity player, Entity entity, DamageSource killingDamage) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, entity);
		this.test(player, conditions -> conditions.test(player, lootContext, killingDamage));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate.Extended entity;
		private final DamageSourcePredicate killingBlow;

		public Conditions(Identifier id, EntityPredicate.Extended player, EntityPredicate.Extended entity, DamageSourcePredicate killingBlow) {
			super(id, player);
			this.entity = entity;
			this.killingBlow = killingBlow;
		}

		public static OnKilledCriterion.Conditions createPlayerKilledEntity(EntityPredicate.Builder killedEntityPredicateBuilder) {
			return new OnKilledCriterion.Conditions(
				Criteria.PLAYER_KILLED_ENTITY.id,
				EntityPredicate.Extended.EMPTY,
				EntityPredicate.Extended.ofLegacy(killedEntityPredicateBuilder.build()),
				DamageSourcePredicate.EMPTY
			);
		}

		public static OnKilledCriterion.Conditions createPlayerKilledEntity() {
			return new OnKilledCriterion.Conditions(
				Criteria.PLAYER_KILLED_ENTITY.id, EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.EMPTY, DamageSourcePredicate.EMPTY
			);
		}

		public static OnKilledCriterion.Conditions createPlayerKilledEntity(
			EntityPredicate.Builder killedEntityPredicateBuilder, DamageSourcePredicate.Builder killingBlowBuilder
		) {
			return new OnKilledCriterion.Conditions(
				Criteria.PLAYER_KILLED_ENTITY.id,
				EntityPredicate.Extended.EMPTY,
				EntityPredicate.Extended.ofLegacy(killedEntityPredicateBuilder.build()),
				killingBlowBuilder.build()
			);
		}

		public static OnKilledCriterion.Conditions createEntityKilledPlayer() {
			return new OnKilledCriterion.Conditions(
				Criteria.ENTITY_KILLED_PLAYER.id, EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.EMPTY, DamageSourcePredicate.EMPTY
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
