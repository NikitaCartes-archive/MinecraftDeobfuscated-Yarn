package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
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
		JsonObject jsonObject, LootContextPredicate lootContextPredicate, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		DamagePredicate damagePredicate = DamagePredicate.fromJson(jsonObject.get("damage"));
		LootContextPredicate lootContextPredicate2 = EntityPredicate.contextPredicateFromJson(jsonObject, "entity", advancementEntityPredicateDeserializer);
		return new PlayerHurtEntityCriterion.Conditions(lootContextPredicate, damagePredicate, lootContextPredicate2);
	}

	public void trigger(ServerPlayerEntity player, Entity entity, DamageSource damage, float dealt, float taken, boolean blocked) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, entity);
		this.trigger(player, conditions -> conditions.matches(player, lootContext, damage, dealt, taken, blocked));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final DamagePredicate damage;
		private final LootContextPredicate entity;

		public Conditions(LootContextPredicate player, DamagePredicate damage, LootContextPredicate entity) {
			super(PlayerHurtEntityCriterion.ID, player);
			this.damage = damage;
			this.entity = entity;
		}

		public static PlayerHurtEntityCriterion.Conditions create() {
			return new PlayerHurtEntityCriterion.Conditions(LootContextPredicate.EMPTY, DamagePredicate.ANY, LootContextPredicate.EMPTY);
		}

		public static PlayerHurtEntityCriterion.Conditions create(DamagePredicate damagePredicate) {
			return new PlayerHurtEntityCriterion.Conditions(LootContextPredicate.EMPTY, damagePredicate, LootContextPredicate.EMPTY);
		}

		public static PlayerHurtEntityCriterion.Conditions create(DamagePredicate.Builder damagePredicateBuilder) {
			return new PlayerHurtEntityCriterion.Conditions(LootContextPredicate.EMPTY, damagePredicateBuilder.build(), LootContextPredicate.EMPTY);
		}

		public static PlayerHurtEntityCriterion.Conditions create(EntityPredicate hurtEntityPredicate) {
			return new PlayerHurtEntityCriterion.Conditions(LootContextPredicate.EMPTY, DamagePredicate.ANY, EntityPredicate.asLootContextPredicate(hurtEntityPredicate));
		}

		public static PlayerHurtEntityCriterion.Conditions create(DamagePredicate damagePredicate, EntityPredicate hurtEntityPredicate) {
			return new PlayerHurtEntityCriterion.Conditions(LootContextPredicate.EMPTY, damagePredicate, EntityPredicate.asLootContextPredicate(hurtEntityPredicate));
		}

		public static PlayerHurtEntityCriterion.Conditions create(DamagePredicate.Builder damagePredicateBuilder, EntityPredicate hurtEntityPredicate) {
			return new PlayerHurtEntityCriterion.Conditions(
				LootContextPredicate.EMPTY, damagePredicateBuilder.build(), EntityPredicate.asLootContextPredicate(hurtEntityPredicate)
			);
		}

		public boolean matches(ServerPlayerEntity player, LootContext entityContext, DamageSource source, float dealt, float taken, boolean blocked) {
			return !this.damage.test(player, source, dealt, taken, blocked) ? false : this.entity.test(entityContext);
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.add("damage", this.damage.toJson());
			jsonObject.add("entity", this.entity.toJson(predicateSerializer));
			return jsonObject;
		}
	}
}
