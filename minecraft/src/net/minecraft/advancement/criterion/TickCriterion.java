package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class TickCriterion extends AbstractCriterion<TickCriterion.Conditions> {
	public static final Identifier ID = new Identifier("tick");

	@Override
	public Identifier getId() {
		return ID;
	}

	public TickCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		return new TickCriterion.Conditions(extended);
	}

	public void trigger(ServerPlayerEntity player) {
		this.test(player, conditions -> true);
	}

	public static class Conditions extends AbstractCriterionConditions {
		public Conditions(EntityPredicate.Extended player) {
			super(TickCriterion.ID, player);
		}
	}
}
