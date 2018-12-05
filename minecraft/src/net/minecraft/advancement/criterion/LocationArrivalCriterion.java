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
import net.minecraft.advancement.ServerAdvancementManager;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class LocationArrivalCriterion implements Criterion<LocationArrivalCriterion.Conditions> {
	private final Identifier id;
	private final Map<ServerAdvancementManager, LocationArrivalCriterion.Handler> handlers = Maps.<ServerAdvancementManager, LocationArrivalCriterion.Handler>newHashMap();

	public LocationArrivalCriterion(Identifier identifier) {
		this.id = identifier;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	@Override
	public void addCondition(
		ServerAdvancementManager serverAdvancementManager, Criterion.ConditionsContainer<LocationArrivalCriterion.Conditions> conditionsContainer
	) {
		LocationArrivalCriterion.Handler handler = (LocationArrivalCriterion.Handler)this.handlers.get(serverAdvancementManager);
		if (handler == null) {
			handler = new LocationArrivalCriterion.Handler(serverAdvancementManager);
			this.handlers.put(serverAdvancementManager, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void removeCondition(
		ServerAdvancementManager serverAdvancementManager, Criterion.ConditionsContainer<LocationArrivalCriterion.Conditions> conditionsContainer
	) {
		LocationArrivalCriterion.Handler handler = (LocationArrivalCriterion.Handler)this.handlers.get(serverAdvancementManager);
		if (handler != null) {
			handler.removeCondition(conditionsContainer);
			if (handler.isEmpty()) {
				this.handlers.remove(serverAdvancementManager);
			}
		}
	}

	@Override
	public void removePlayer(ServerAdvancementManager serverAdvancementManager) {
		this.handlers.remove(serverAdvancementManager);
	}

	public LocationArrivalCriterion.Conditions deserializeConditions(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		LocationPredicate locationPredicate = LocationPredicate.deserialize(jsonObject);
		return new LocationArrivalCriterion.Conditions(this.id, locationPredicate);
	}

	public void handle(ServerPlayerEntity serverPlayerEntity) {
		LocationArrivalCriterion.Handler handler = (LocationArrivalCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.handle(serverPlayerEntity.getServerWorld(), serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final LocationPredicate location;

		public Conditions(Identifier identifier, LocationPredicate locationPredicate) {
			super(identifier);
			this.location = locationPredicate;
		}

		public static LocationArrivalCriterion.Conditions method_9034(LocationPredicate locationPredicate) {
			return new LocationArrivalCriterion.Conditions(CriterionCriterions.LOCATION.id, locationPredicate);
		}

		public static LocationArrivalCriterion.Conditions method_9032() {
			return new LocationArrivalCriterion.Conditions(CriterionCriterions.SLEPT_IN_BED.id, LocationPredicate.ANY);
		}

		public boolean matches(ServerWorld serverWorld, double d, double e, double f) {
			return this.location.test(serverWorld, d, e, f);
		}

		@Override
		public JsonElement method_807() {
			return this.location.serialize();
		}
	}

	static class Handler {
		private final ServerAdvancementManager manager;
		private final Set<Criterion.ConditionsContainer<LocationArrivalCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<LocationArrivalCriterion.Conditions>>newHashSet();

		public Handler(ServerAdvancementManager serverAdvancementManager) {
			this.manager = serverAdvancementManager;
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

		public void handle(ServerWorld serverWorld, double d, double e, double f) {
			List<Criterion.ConditionsContainer<LocationArrivalCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<LocationArrivalCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().matches(serverWorld, d, e, f)) {
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
