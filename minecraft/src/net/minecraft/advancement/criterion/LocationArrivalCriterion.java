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
	private final Identifier field_9694;
	private final Map<PlayerAdvancementTracker, LocationArrivalCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, LocationArrivalCriterion.Handler>newHashMap();

	public LocationArrivalCriterion(Identifier identifier) {
		this.field_9694 = identifier;
	}

	@Override
	public Identifier getId() {
		return this.field_9694;
	}

	@Override
	public void beginTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<LocationArrivalCriterion.Conditions> conditionsContainer
	) {
		LocationArrivalCriterion.Handler handler = (LocationArrivalCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler == null) {
			handler = new LocationArrivalCriterion.Handler(playerAdvancementTracker);
			this.handlers.put(playerAdvancementTracker, handler);
		}

		handler.method_9028(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<LocationArrivalCriterion.Conditions> conditionsContainer
	) {
		LocationArrivalCriterion.Handler handler = (LocationArrivalCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler != null) {
			handler.method_9030(conditionsContainer);
			if (handler.isEmpty()) {
				this.handlers.remove(playerAdvancementTracker);
			}
		}
	}

	@Override
	public void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
		this.handlers.remove(playerAdvancementTracker);
	}

	public LocationArrivalCriterion.Conditions method_9026(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		LocationPredicate locationPredicate = LocationPredicate.deserialize(jsonObject);
		return new LocationArrivalCriterion.Conditions(this.field_9694, locationPredicate);
	}

	public void method_9027(ServerPlayerEntity serverPlayerEntity) {
		LocationArrivalCriterion.Handler handler = (LocationArrivalCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.method_9031(serverPlayerEntity.getServerWorld(), serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final LocationPredicate location;

		public Conditions(Identifier identifier, LocationPredicate locationPredicate) {
			super(identifier);
			this.location = locationPredicate;
		}

		public static LocationArrivalCriterion.Conditions method_9034(LocationPredicate locationPredicate) {
			return new LocationArrivalCriterion.Conditions(Criterions.LOCATION.field_9694, locationPredicate);
		}

		public static LocationArrivalCriterion.Conditions method_9032() {
			return new LocationArrivalCriterion.Conditions(Criterions.SLEPT_IN_BED.field_9694, LocationPredicate.ANY);
		}

		public boolean method_9033(ServerWorld serverWorld, double d, double e, double f) {
			return this.location.method_9018(serverWorld, d, e, f);
		}

		@Override
		public JsonElement toJson() {
			return this.location.serialize();
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker field_9697;
		private final Set<Criterion.ConditionsContainer<LocationArrivalCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<LocationArrivalCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.field_9697 = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void method_9028(Criterion.ConditionsContainer<LocationArrivalCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void method_9030(Criterion.ConditionsContainer<LocationArrivalCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void method_9031(ServerWorld serverWorld, double d, double e, double f) {
			List<Criterion.ConditionsContainer<LocationArrivalCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<LocationArrivalCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.method_797().method_9033(serverWorld, d, e, f)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<LocationArrivalCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<LocationArrivalCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.field_9697);
				}
			}
		}
	}
}
