package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class TameAnimalCriterion extends AbstractCriterion<TameAnimalCriterion.Conditions> {
	private static final Identifier ID = new Identifier("tame_animal");

	@Override
	public Identifier getId() {
		return ID;
	}

	public TameAnimalCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		EntityPredicate.Extended extended2 = EntityPredicate.Extended.getInJson(jsonObject, "entity", advancementEntityPredicateDeserializer);
		return new TameAnimalCriterion.Conditions(extended, extended2);
	}

	public void trigger(ServerPlayerEntity player, AnimalEntity entity) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, entity);
		this.test(player, conditions -> conditions.matches(lootContext));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate.Extended entity;

		public Conditions(EntityPredicate.Extended player, EntityPredicate.Extended entity) {
			super(TameAnimalCriterion.ID, player);
			this.entity = entity;
		}

		public static TameAnimalCriterion.Conditions any() {
			return new TameAnimalCriterion.Conditions(EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.EMPTY);
		}

		public static TameAnimalCriterion.Conditions create(EntityPredicate entity) {
			return new TameAnimalCriterion.Conditions(EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.ofLegacy(entity));
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
