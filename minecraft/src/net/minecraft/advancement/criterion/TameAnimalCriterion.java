package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
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
			super(TameAnimalCriterion.ID, playerPredicate);
			this.entity = entity;
		}

		public static TameAnimalCriterion.Conditions any() {
			return new TameAnimalCriterion.Conditions(Optional.empty(), Optional.empty());
		}

		public static TameAnimalCriterion.Conditions create(Optional<EntityPredicate> entity) {
			return new TameAnimalCriterion.Conditions(Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(entity));
		}

		public boolean matches(LootContext entity) {
			return this.entity.isEmpty() || ((LootContextPredicate)this.entity.get()).test(entity);
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
			this.entity.ifPresent(lootContextPredicate -> jsonObject.add("entity", lootContextPredicate.toJson()));
			return jsonObject;
		}
	}
}
