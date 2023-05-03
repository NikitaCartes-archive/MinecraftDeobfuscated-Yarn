package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class TravelCriterion extends AbstractCriterion<TravelCriterion.Conditions> {
	final Identifier id;

	public TravelCriterion(Identifier id) {
		this.id = id;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	public TravelCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, LootContextPredicate lootContextPredicate, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		LocationPredicate locationPredicate = LocationPredicate.fromJson(jsonObject.get("start_position"));
		DistancePredicate distancePredicate = DistancePredicate.fromJson(jsonObject.get("distance"));
		return new TravelCriterion.Conditions(this.id, lootContextPredicate, locationPredicate, distancePredicate);
	}

	public void trigger(ServerPlayerEntity player, Vec3d startPos) {
		Vec3d vec3d = player.getPos();
		this.trigger(player, conditions -> conditions.matches(player.getServerWorld(), startPos, vec3d));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final LocationPredicate startPos;
		private final DistancePredicate distance;

		public Conditions(Identifier id, LootContextPredicate entity, LocationPredicate startPos, DistancePredicate distance) {
			super(id, entity);
			this.startPos = startPos;
			this.distance = distance;
		}

		public static TravelCriterion.Conditions fallFromHeight(EntityPredicate.Builder entity, DistancePredicate distance, LocationPredicate startPos) {
			return new TravelCriterion.Conditions(Criteria.FALL_FROM_HEIGHT.id, EntityPredicate.asLootContextPredicate(entity.build()), startPos, distance);
		}

		public static TravelCriterion.Conditions rideEntityInLava(EntityPredicate.Builder entity, DistancePredicate distance) {
			return new TravelCriterion.Conditions(
				Criteria.RIDE_ENTITY_IN_LAVA.id, EntityPredicate.asLootContextPredicate(entity.build()), LocationPredicate.ANY, distance
			);
		}

		public static TravelCriterion.Conditions netherTravel(DistancePredicate distance) {
			return new TravelCriterion.Conditions(Criteria.NETHER_TRAVEL.id, LootContextPredicate.EMPTY, LocationPredicate.ANY, distance);
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.add("start_position", this.startPos.toJson());
			jsonObject.add("distance", this.distance.toJson());
			return jsonObject;
		}

		public boolean matches(ServerWorld world, Vec3d startPos, Vec3d endPos) {
			return !this.startPos.test(world, startPos.x, startPos.y, startPos.z)
				? false
				: this.distance.test(startPos.x, startPos.y, startPos.z, endPos.x, endPos.y, endPos.z);
		}
	}
}
