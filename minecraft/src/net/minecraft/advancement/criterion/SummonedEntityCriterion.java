package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class SummonedEntityCriterion extends AbstractCriterion<SummonedEntityCriterion.Conditions> {
	private static final Identifier ID = new Identifier("summoned_entity");

	@Override
	public Identifier getId() {
		return ID;
	}

	public SummonedEntityCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		EntityPredicate.Extended extended2 = EntityPredicate.Extended.getInJson(jsonObject, "entity", advancementEntityPredicateDeserializer);
		return new SummonedEntityCriterion.Conditions(extended, extended2);
	}

	public void trigger(ServerPlayerEntity player, Entity entity) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, entity);
		this.test(player, conditions -> conditions.matches(lootContext));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final EntityPredicate.Extended entity;

		public Conditions(EntityPredicate.Extended player, EntityPredicate.Extended entity) {
			super(SummonedEntityCriterion.ID, player);
			this.entity = entity;
		}

		public static SummonedEntityCriterion.Conditions create(EntityPredicate.Builder summonedEntityPredicateBuilder) {
			return new SummonedEntityCriterion.Conditions(EntityPredicate.Extended.EMPTY, EntityPredicate.Extended.ofLegacy(summonedEntityPredicateBuilder.build()));
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
