package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class TameAnimalCriterion extends AbstractCriterion<TameAnimalCriterion.Conditions> {
	static final Identifier ID = new Identifier("tame_animal");

	@Override
	public Identifier getId() {
		return ID;
	}

	public TameAnimalCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, LootContextPredicate lootContextPredicate, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		LootContextPredicate lootContextPredicate2 = EntityPredicate.contextPredicateFromJson(jsonObject, "entity", advancementEntityPredicateDeserializer);
		return new TameAnimalCriterion.Conditions(lootContextPredicate, lootContextPredicate2);
	}

	public void trigger(ServerPlayerEntity player, AnimalEntity entity) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, entity);
		this.trigger(player, conditions -> conditions.matches(lootContext));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final LootContextPredicate entity;

		public Conditions(LootContextPredicate player, LootContextPredicate entity) {
			super(TameAnimalCriterion.ID, player);
			this.entity = entity;
		}

		public static TameAnimalCriterion.Conditions any() {
			return new TameAnimalCriterion.Conditions(LootContextPredicate.EMPTY, LootContextPredicate.EMPTY);
		}

		public static TameAnimalCriterion.Conditions create(EntityPredicate entity) {
			return new TameAnimalCriterion.Conditions(LootContextPredicate.EMPTY, EntityPredicate.asLootContextPredicate(entity));
		}

		public boolean matches(LootContext tamedEntityContext) {
			return this.entity.test(tamedEntityContext);
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.add("entity", this.entity.toJson(predicateSerializer));
			return jsonObject;
		}
	}
}
