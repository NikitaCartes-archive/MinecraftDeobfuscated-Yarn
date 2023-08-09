package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
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
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		return new StartedRidingCriterion.Conditions(optional);
	}

	public void trigger(ServerPlayerEntity player) {
		this.trigger(player, conditions -> true);
	}

	public static class Conditions extends AbstractCriterionConditions {
		public Conditions(Optional<LootContextPredicate> playerPredicate) {
			super(StartedRidingCriterion.ID, playerPredicate);
		}

		public static StartedRidingCriterion.Conditions create(EntityPredicate.Builder player) {
			return new StartedRidingCriterion.Conditions(EntityPredicate.contextPredicateFromEntityPredicate(player));
		}
	}
}
