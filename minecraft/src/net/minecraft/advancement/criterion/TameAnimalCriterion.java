package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class TameAnimalCriterion extends AbstractCriterion<TameAnimalCriterion.Conditions> {
	public TameAnimalCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		Optional<LootContextPredicate> optional2 = EntityPredicate.contextPredicateFromJson(jsonObject, "entity", advancementEntityPredicateDeserializer);
		return new TameAnimalCriterion.Conditions(optional, optional2);
	}

	public void trigger(ServerPlayerEntity player, AnimalEntity entity) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, entity);
		this.trigger(player, conditions -> conditions.matches(lootContext));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Optional<LootContextPredicate> entity;

		public Conditions(Optional<LootContextPredicate> playerPredicate, Optional<LootContextPredicate> entity) {
			super(playerPredicate);
			this.entity = entity;
		}

		public static AdvancementCriterion<TameAnimalCriterion.Conditions> any() {
			return Criteria.TAME_ANIMAL.create(new TameAnimalCriterion.Conditions(Optional.empty(), Optional.empty()));
		}

		public static AdvancementCriterion<TameAnimalCriterion.Conditions> create(EntityPredicate.Builder entity) {
			return Criteria.TAME_ANIMAL
				.create(new TameAnimalCriterion.Conditions(Optional.empty(), Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(entity))));
		}

		public boolean matches(LootContext entity) {
			return this.entity.isEmpty() || ((LootContextPredicate)this.entity.get()).test(entity);
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
			this.entity.ifPresent(entity -> jsonObject.add("entity", entity.toJson()));
			return jsonObject;
		}
	}
}
