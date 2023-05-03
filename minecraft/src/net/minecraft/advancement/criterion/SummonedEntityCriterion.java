package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class SummonedEntityCriterion extends AbstractCriterion<SummonedEntityCriterion.Conditions> {
	static final Identifier ID = new Identifier("summoned_entity");

	@Override
	public Identifier getId() {
		return ID;
	}

	public SummonedEntityCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, LootContextPredicate lootContextPredicate, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		LootContextPredicate lootContextPredicate2 = EntityPredicate.contextPredicateFromJson(jsonObject, "entity", advancementEntityPredicateDeserializer);
		return new SummonedEntityCriterion.Conditions(lootContextPredicate, lootContextPredicate2);
	}

	public void trigger(ServerPlayerEntity player, Entity entity) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, entity);
		this.trigger(player, conditions -> conditions.matches(lootContext));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final LootContextPredicate entity;

		public Conditions(LootContextPredicate player, LootContextPredicate entity) {
			super(SummonedEntityCriterion.ID, player);
			this.entity = entity;
		}

		public static SummonedEntityCriterion.Conditions create(EntityPredicate.Builder summonedEntityPredicateBuilder) {
			return new SummonedEntityCriterion.Conditions(LootContextPredicate.EMPTY, EntityPredicate.asLootContextPredicate(summonedEntityPredicateBuilder.build()));
		}

		public boolean matches(LootContext summonedEntityContext) {
			return this.entity.test(summonedEntityContext);
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.add("entity", this.entity.toJson(predicateSerializer));
			return jsonObject;
		}
	}
}
