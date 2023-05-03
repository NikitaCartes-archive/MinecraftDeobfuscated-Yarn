package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class LevitationCriterion extends AbstractCriterion<LevitationCriterion.Conditions> {
	static final Identifier ID = new Identifier("levitation");

	@Override
	public Identifier getId() {
		return ID;
	}

	public LevitationCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, LootContextPredicate lootContextPredicate, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		DistancePredicate distancePredicate = DistancePredicate.fromJson(jsonObject.get("distance"));
		NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("duration"));
		return new LevitationCriterion.Conditions(lootContextPredicate, distancePredicate, intRange);
	}

	public void trigger(ServerPlayerEntity player, Vec3d startPos, int duration) {
		this.trigger(player, conditions -> conditions.matches(player, startPos, duration));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final DistancePredicate distance;
		private final NumberRange.IntRange duration;

		public Conditions(LootContextPredicate player, DistancePredicate distance, NumberRange.IntRange duration) {
			super(LevitationCriterion.ID, player);
			this.distance = distance;
			this.duration = duration;
		}

		public static LevitationCriterion.Conditions create(DistancePredicate distance) {
			return new LevitationCriterion.Conditions(LootContextPredicate.EMPTY, distance, NumberRange.IntRange.ANY);
		}

		public boolean matches(ServerPlayerEntity player, Vec3d startPos, int duration) {
			return !this.distance.test(startPos.x, startPos.y, startPos.z, player.getX(), player.getY(), player.getZ()) ? false : this.duration.test(duration);
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.add("distance", this.distance.toJson());
			jsonObject.add("duration", this.duration.toJson());
			return jsonObject;
		}
	}
}
