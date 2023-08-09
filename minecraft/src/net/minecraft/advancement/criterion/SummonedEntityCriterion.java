package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
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
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		Optional<LootContextPredicate> optional2 = EntityPredicate.contextPredicateFromJson(jsonObject, "entity", advancementEntityPredicateDeserializer);
		return new SummonedEntityCriterion.Conditions(optional, optional2);
	}

	public void trigger(ServerPlayerEntity player, Entity entity) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, entity);
		this.trigger(player, conditions -> conditions.matches(lootContext));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Optional<LootContextPredicate> entity;

		public Conditions(Optional<LootContextPredicate> playerPredicate, Optional<LootContextPredicate> entity) {
			super(SummonedEntityCriterion.ID, playerPredicate);
			this.entity = entity;
		}

		public static SummonedEntityCriterion.Conditions create(EntityPredicate.Builder summonedEntityPredicateBuilder) {
			return new SummonedEntityCriterion.Conditions(Optional.empty(), EntityPredicate.contextPredicateFromEntityPredicate(summonedEntityPredicateBuilder));
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
