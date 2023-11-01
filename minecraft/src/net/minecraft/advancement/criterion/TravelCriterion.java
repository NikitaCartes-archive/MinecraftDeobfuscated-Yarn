package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class TravelCriterion extends AbstractCriterion<TravelCriterion.Conditions> {
	public TravelCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		Optional<LocationPredicate> optional2 = LocationPredicate.fromJson(jsonObject.get("start_position"));
		Optional<DistancePredicate> optional3 = DistancePredicate.fromJson(jsonObject.get("distance"));
		return new TravelCriterion.Conditions(optional, optional2, optional3);
	}

	public void trigger(ServerPlayerEntity player, Vec3d startPos) {
		Vec3d vec3d = player.getPos();
		this.trigger(player, conditions -> conditions.matches(player.getServerWorld(), startPos, vec3d));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Optional<LocationPredicate> startPos;
		private final Optional<DistancePredicate> distance;

		public Conditions(Optional<LootContextPredicate> playerPredicate, Optional<LocationPredicate> startPos, Optional<DistancePredicate> distance) {
			super(playerPredicate);
			this.startPos = startPos;
			this.distance = distance;
		}

		public static AdvancementCriterion<TravelCriterion.Conditions> fallFromHeight(
			EntityPredicate.Builder entity, DistancePredicate distance, LocationPredicate.Builder startPos
		) {
			return Criteria.FALL_FROM_HEIGHT
				.create(
					new TravelCriterion.Conditions(
						Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(entity)), Optional.of(startPos.build()), Optional.of(distance)
					)
				);
		}

		public static AdvancementCriterion<TravelCriterion.Conditions> rideEntityInLava(EntityPredicate.Builder entity, DistancePredicate distance) {
			return Criteria.RIDE_ENTITY_IN_LAVA
				.create(new TravelCriterion.Conditions(Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(entity)), Optional.empty(), Optional.of(distance)));
		}

		public static AdvancementCriterion<TravelCriterion.Conditions> netherTravel(DistancePredicate distance) {
			return Criteria.NETHER_TRAVEL.create(new TravelCriterion.Conditions(Optional.empty(), Optional.empty(), Optional.of(distance)));
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
			this.startPos.ifPresent(startPos -> jsonObject.add("start_position", startPos.toJson()));
			this.distance.ifPresent(distance -> jsonObject.add("distance", distance.toJson()));
			return jsonObject;
		}

		public boolean matches(ServerWorld world, Vec3d pos, Vec3d endPos) {
			return this.startPos.isPresent() && !((LocationPredicate)this.startPos.get()).test(world, pos.x, pos.y, pos.z)
				? false
				: !this.distance.isPresent() || ((DistancePredicate)this.distance.get()).test(pos.x, pos.y, pos.z, endPos.x, endPos.y, endPos.z);
		}
	}
}
