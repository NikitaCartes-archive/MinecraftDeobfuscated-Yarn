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

	public ConstructBeaconCriterion.Conditions method_8811(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("level"));
		return new ConstructBeaconCriterion.Conditions(intRange);
	}

	public void handle(ServerPlayerEntity serverPlayerEntity, BeaconBlockEntity beaconBlockEntity) {
		this.test(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.matches(beaconBlockEntity));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final NumberRange.IntRange level;

		public Conditions(NumberRange.IntRange intRange) {
			super(ConstructBeaconCriterion.ID);
			this.level = intRange;
		}

		public static ConstructBeaconCriterion.Conditions level(NumberRange.IntRange intRange) {
			return new ConstructBeaconCriterion.Conditions(intRange);
		}

		public boolean matches(BeaconBlockEntity beaconBlockEntity) {
			return this.level.test(beaconBlockEntity.getLevel());
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("level", this.level.toJson());
			return jsonObject;
		}
	}
}
