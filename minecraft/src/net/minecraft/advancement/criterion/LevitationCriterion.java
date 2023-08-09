package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
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
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		Optional<DistancePredicate> optional2 = DistancePredicate.fromJson(jsonObject.get("distance"));
		NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("duration"));
		return new LevitationCriterion.Conditions(optional, optional2, intRange);
	}

	public void trigger(ServerPlayerEntity player, Vec3d startPos, int duration) {
		this.trigger(player, conditions -> conditions.matches(player, startPos, duration));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Optional<DistancePredicate> distance;
		private final NumberRange.IntRange duration;

		public Conditions(Optional<LootContextPredicate> playerPredicate, Optional<DistancePredicate> distance, NumberRange.IntRange duration) {
			super(LevitationCriterion.ID, playerPredicate);
			this.distance = distance;
			this.duration = duration;
		}

		public static LevitationCriterion.Conditions create(DistancePredicate distance) {
			return new LevitationCriterion.Conditions(Optional.empty(), Optional.of(distance), NumberRange.IntRange.ANY);
		}

		public boolean matches(ServerPlayerEntity player, Vec3d distance, int duration) {
			return this.distance.isPresent()
					&& !((DistancePredicate)this.distance.get()).test(distance.x, distance.y, distance.z, player.getX(), player.getY(), player.getZ())
				? false
				: this.duration.test(duration);
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
			this.distance.ifPresent(distancePredicate -> jsonObject.add("distance", distancePredicate.toJson()));
			jsonObject.add("duration", this.duration.toJson());
			return jsonObject;
		}
	}
}
