package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.class_4558;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.NumberRange;
import net.minecraft.util.math.Vec3d;

public class LevitationCriterion extends class_4558<LevitationCriterion.Conditions> {
	private static final Identifier ID = new Identifier("levitation");

	@Override
	public Identifier getId() {
		return ID;
	}

	public LevitationCriterion.Conditions method_9006(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		DistancePredicate distancePredicate = DistancePredicate.deserialize(jsonObject.get("distance"));
		NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("duration"));
		return new LevitationCriterion.Conditions(distancePredicate, intRange);
	}

	public void handle(ServerPlayerEntity serverPlayerEntity, Vec3d vec3d, int i) {
		this.method_22510(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.matches(serverPlayerEntity, vec3d, i));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final DistancePredicate distance;
		private final NumberRange.IntRange duration;

		public Conditions(DistancePredicate distancePredicate, NumberRange.IntRange intRange) {
			super(LevitationCriterion.ID);
			this.distance = distancePredicate;
			this.duration = intRange;
		}

		public static LevitationCriterion.Conditions create(DistancePredicate distancePredicate) {
			return new LevitationCriterion.Conditions(distancePredicate, NumberRange.IntRange.ANY);
		}

		public boolean matches(ServerPlayerEntity serverPlayerEntity, Vec3d vec3d, int i) {
			return !this.distance.test(vec3d.x, vec3d.y, vec3d.z, serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z) ? false : this.duration.test(i);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("distance", this.distance.serialize());
			jsonObject.add("duration", this.duration.serialize());
			return jsonObject;
		}
	}
}
