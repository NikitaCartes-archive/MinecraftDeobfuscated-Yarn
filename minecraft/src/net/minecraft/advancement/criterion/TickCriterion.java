package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class TickCriterion extends AbstractCriterion<TickCriterion.Conditions> {
	public static final Identifier ID = new Identifier("tick");

	@Override
	public Identifier getId() {
		return ID;
	}

	public TickCriterion.Conditions method_9140(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		return new TickCriterion.Conditions();
	}

	public void trigger(ServerPlayerEntity player) {
		this.grant(player.getAdvancementTracker());
	}

	public static class Conditions extends AbstractCriterionConditions {
		public Conditions() {
			super(TickCriterion.ID);
		}
	}
}
