package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class StartedRidingCriterion extends AbstractCriterion<StartedRidingCriterion.Conditions> {
	static final Identifier ID = new Identifier("started_riding");

	@Override
	public Identifier getId() {
		return ID;
	}

	public StartedRidingCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		return new StartedRidingCriterion.Conditions(extended);
	}

	public void test(ServerPlayerEntity player) {
		this.test(player, conditions -> true);
	}

	public static class Conditions extends AbstractCriterionConditions {
		public Conditions(EntityPredicate.Extended player) {
			super(StartedRidingCriterion.ID, player);
		}

		public static StartedRidingCriterion.Conditions create(EntityPredicate.Builder player) {
			return new StartedRidingCriterion.Conditions(EntityPredicate.Extended.ofLegacy(player.build()));
		}
	}
}
