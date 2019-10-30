package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class LocationArrivalCriterion extends AbstractCriterion<LocationArrivalCriterion.Conditions> {
	private final Identifier id;

	public LocationArrivalCriterion(Identifier id) {
		this.id = id;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	public LocationArrivalCriterion.Conditions method_9026(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		LocationPredicate locationPredicate = LocationPredicate.fromJson(jsonObject);
		return new LocationArrivalCriterion.Conditions(this.id, locationPredicate);
	}

	public void trigger(ServerPlayerEntity player) {
		this.test(player.getAdvancementManager(), conditions -> conditions.matches(player.getServerWorld(), player.getX(), player.getY(), player.getZ()));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final LocationPredicate location;

		public Conditions(Identifier id, LocationPredicate location) {
			super(id);
			this.location = location;
		}

		public static LocationArrivalCriterion.Conditions create(LocationPredicate location) {
			return new LocationArrivalCriterion.Conditions(Criterions.LOCATION.id, location);
		}

		public static LocationArrivalCriterion.Conditions createSleptInBed() {
			return new LocationArrivalCriterion.Conditions(Criterions.SLEPT_IN_BED.id, LocationPredicate.ANY);
		}

		public static LocationArrivalCriterion.Conditions createHeroOfTheVillage() {
			return new LocationArrivalCriterion.Conditions(Criterions.HERO_OF_THE_VILLAGE.id, LocationPredicate.ANY);
		}

		public boolean matches(ServerWorld world, double x, double y, double z) {
			return this.location.test(world, x, y, z);
		}

		@Override
		public JsonElement toJson() {
			return this.location.toJson();
		}
	}
}
