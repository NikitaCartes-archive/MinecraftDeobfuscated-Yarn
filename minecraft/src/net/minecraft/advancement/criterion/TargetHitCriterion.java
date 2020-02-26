package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class TargetHitCriterion extends AbstractCriterion<TargetHitCriterion.Conditions> {
	private static final Identifier ID = new Identifier("target_hit");

	@Override
	public Identifier getId() {
		return ID;
	}

	public TargetHitCriterion.Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("signalStrength"));
		return new TargetHitCriterion.Conditions(intRange);
	}

	public void trigger(ServerPlayerEntity player, int signalStrength) {
		this.test(player.getAdvancementTracker(), conditions -> conditions.test(signalStrength));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final NumberRange.IntRange signalStrength;

		public Conditions(NumberRange.IntRange signalStrength) {
			super(TargetHitCriterion.ID);
			this.signalStrength = signalStrength;
		}

		public static TargetHitCriterion.Conditions create(NumberRange.IntRange signalStrength) {
			return new TargetHitCriterion.Conditions(signalStrength);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("signalStrength", this.signalStrength.toJson());
			return jsonObject;
		}

		public boolean test(int signalStrength) {
			return this.signalStrength.test(signalStrength);
		}
	}
}
