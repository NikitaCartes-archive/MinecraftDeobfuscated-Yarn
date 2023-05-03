package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class StartedRidingCriterion extends AbstractCriterion<StartedRidingCriterion.Conditions> {
	static final Identifier ID = new Identifier("started_riding");

	@Override
	public Identifier getId() {
		return ID;
	}

	public StartedRidingCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, LootContextPredicate lootContextPredicate, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		return new StartedRidingCriterion.Conditions(lootContextPredicate);
	}

	public void trigger(ServerPlayerEntity player) {
		this.trigger(player, conditions -> true);
	}

	public static class Conditions extends AbstractCriterionConditions {
		public Conditions(LootContextPredicate player) {
			super(StartedRidingCriterion.ID, player);
		}

		public static StartedRidingCriterion.Conditions create(EntityPredicate.Builder player) {
			return new StartedRidingCriterion.Conditions(EntityPredicate.asLootContextPredicate(player.build()));
		}
	}
}
