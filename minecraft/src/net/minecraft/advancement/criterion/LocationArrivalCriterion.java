package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class LocationArrivalCriterion implements Criterion<LocationArrivalCriterion.Conditions> {
	private final Identifier id;
	private final Map<PlayerAdvancementTracker, LocationArrivalCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, LocationArrivalCriterion.Handler>newHashMap();

	public LocationArrivalCriterion(Identifier identifier) {
		this.id = identifier;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	@Override
	public void beginTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<LocationArrivalCriterion.Conditions> conditionsContainer) {
		LocationArrivalCriterion.Handler handler = (LocationArrivalCriterion.Handler)this.handlers.get(manager);
		if (handler == null) {
			handler = new LocationArrivalCriterion.Handler(manager);
			this.handlers.put(manager, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<LocationArrivalCriterion.Conditions> conditionsContainer) {
		LocationArrivalCriterion.Handler handler = (LocationArrivalCriterion.Handler)this.handlers.get(manager);
		if (handler != null) {
			handler.removeCondition(conditionsContainer);
			if (handler.isEmpty()) {
				this.handlers.remove(manager);
			}
		}
	}

	@Override
	public void endTracking(PlayerAdvancementTracker tracker) {
		this.handlers.remove(tracker);
	}

	public LocationArrivalCriterion.Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		LocationPredicate locationPredicate = LocationPredicate.fromJson(jsonObject);
		return new LocationArrivalCriterion.Conditions(this.id, locationPredicate);
	}

	public void trigger(ServerPlayerEntity player) {
		LocationArrivalCriterion.Handler handler = (LocationArrivalCriterion.Handler)this.handlers.get(player.getAdvancementTracker());
		if (handler != null) {
			handler.handle(player.getServerWorld(), player.x, player.y, player.z);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final LocationPredicate location;

		public Conditions(Identifier identifier, LocationPredicate location) {
			super(identifier);
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

	static class Handler {
		private final PlayerAdvancementTracker manager;
		private final Set<Criterion.ConditionsContainer<LocationArrivalCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<LocationArrivalCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker manager) {
			this.manager = manager;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void addCondition(Criterion.ConditionsContainer<LocationArrivalCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void removeCondition(Criterion.ConditionsContainer<LocationArrivalCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void handle(ServerWorld world, double x, double y, double z) {
			List<Criterion.ConditionsContainer<LocationArrivalCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<LocationArrivalCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().matches(world, x, y, z)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<LocationArrivalCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<LocationArrivalCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.manager);
				}
			}
		}
	}
}
