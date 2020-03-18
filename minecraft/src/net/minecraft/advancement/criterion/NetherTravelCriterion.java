package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class NetherTravelCriterion extends AbstractCriterion<NetherTravelCriterion.Conditions> {
	private static final Identifier ID = new Identifier("nether_travel");

	@Override
	public Identifier getId() {
		return ID;
	}

	public NetherTravelCriterion.Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		LocationPredicate locationPredicate = LocationPredicate.fromJson(jsonObject.get("entered"));
		LocationPredicate locationPredicate2 = LocationPredicate.fromJson(jsonObject.get("exited"));
		DistancePredicate distancePredicate = DistancePredicate.deserialize(jsonObject.get("distance"));
		return new NetherTravelCriterion.Conditions(locationPredicate, locationPredicate2, distancePredicate);
	}

	public void trigger(ServerPlayerEntity player, Vec3d enteredPos) {
		this.test(player.getAdvancementTracker(), conditions -> conditions.matches(player.getServerWorld(), enteredPos, player.getX(), player.getY(), player.getZ()));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final LocationPredicate enteredPos;
		private final LocationPredicate exitedPos;
		private final DistancePredicate distance;

		public Conditions(LocationPredicate entered, LocationPredicate exited, DistancePredicate distance) {
			super(NetherTravelCriterion.ID);
			this.enteredPos = entered;
			this.exitedPos = exited;
			this.distance = distance;
		}

		public static NetherTravelCriterion.Conditions distance(DistancePredicate distance) {
			return new NetherTravelCriterion.Conditions(LocationPredicate.ANY, LocationPredicate.ANY, distance);
		}

		public boolean matches(ServerWorld world, Vec3d enteredPos, double exitedPosX, double exitedPosY, double exitedPosZ) {
			if (!this.enteredPos.test(world, enteredPos.x, enteredPos.y, enteredPos.z)) {
				return false;
			} else {
				return !this.exitedPos.test(world, exitedPosX, exitedPosY, exitedPosZ)
					? false
					: this.distance.test(enteredPos.x, enteredPos.y, enteredPos.z, exitedPosX, exitedPosY, exitedPosZ);
			}
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("entered", this.enteredPos.toJson());
			jsonObject.add("exited", this.exitedPos.toJson());
			jsonObject.add("distance", this.distance.toJson());
			return jsonObject;
		}
	}
}
