package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class StartedRidingCriterion extends AbstractCriterion<StartedRidingCriterion.Conditions> {
	public StartedRidingCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		return new StartedRidingCriterion.Conditions(optional);
	}

	public void trigger(ServerPlayerEntity player) {
		this.trigger(player, conditions -> true);
	}

	public static class Conditions extends AbstractCriterionConditions {
		public Conditions(Optional<LootContextPredicate> optional) {
			super(optional);
		}

		public static AdvancementCriterion<StartedRidingCriterion.Conditions> create(EntityPredicate.Builder player) {
			return Criteria.STARTED_RIDING.create(new StartedRidingCriterion.Conditions(Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(player))));
		}
	}
}
