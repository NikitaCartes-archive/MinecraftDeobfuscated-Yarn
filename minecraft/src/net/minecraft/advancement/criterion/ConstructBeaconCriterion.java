package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ConstructBeaconCriterion extends AbstractCriterion<ConstructBeaconCriterion.Conditions> {
	private static final Identifier ID = new Identifier("construct_beacon");

	@Override
	public Identifier getId() {
		return ID;
	}

	public ConstructBeaconCriterion.Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("level"));
		return new ConstructBeaconCriterion.Conditions(intRange);
	}

	public void trigger(ServerPlayerEntity player, BeaconBlockEntity beacon) {
		this.test(player.getAdvancementTracker(), conditions -> conditions.matches(beacon));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final NumberRange.IntRange level;

		public Conditions(NumberRange.IntRange level) {
			super(ConstructBeaconCriterion.ID);
			this.level = level;
		}

		public static ConstructBeaconCriterion.Conditions level(NumberRange.IntRange intRange) {
			return new ConstructBeaconCriterion.Conditions(intRange);
		}

		public boolean matches(BeaconBlockEntity beacon) {
			return this.level.test(beacon.getLevel());
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("level", this.level.toJson());
			return jsonObject;
		}
	}
}
