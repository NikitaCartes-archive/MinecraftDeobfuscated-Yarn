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

	public LocationArrivalCriterion(Identifier identifier) {
		this.id = identifier;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	public LocationArrivalCriterion.Conditions method_9026(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		LocationPredicate locationPredicate = LocationPredicate.fromJson(jsonObject);
		return new LocationArrivalCriterion.Conditions(this.id, locationPredicate);
	}

	public void handle(ServerPlayerEntity serverPlayerEntity) {
		this.test(
			serverPlayerEntity.getAdvancementManager(),
			conditions -> conditions.matches(serverPlayerEntity.getServerWorld(), serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z)
		);
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final LocationPredicate location;

		public Conditions(Identifier identifier, LocationPredicate locationPredicate) {
			super(identifier);
			this.location = locationPredicate;
		}

		public static LocationArrivalCriterion.Conditions create(LocationPredicate locationPredicate) {
			return new LocationArrivalCriterion.Conditions(Criterions.LOCATION.id, locationPredicate);
		}

		public static LocationArrivalCriterion.Conditions createSleptInBed() {
			return new LocationArrivalCriterion.Conditions(Criterions.SLEPT_IN_BED.id, LocationPredicate.ANY);
		}

		public static LocationArrivalCriterion.Conditions createHeroOfTheVillage() {
			return new LocationArrivalCriterion.Conditions(Criterions.HERO_OF_THE_VILLAGE.id, LocationPredicate.ANY);
		}

		public boolean matches(ServerWorld serverWorld, double d, double e, double f) {
			return this.location.test(serverWorld, d, e, f);
		}

		@Override
		public JsonElement toJson() {
			return this.location.toJson();
		}
	}
}
