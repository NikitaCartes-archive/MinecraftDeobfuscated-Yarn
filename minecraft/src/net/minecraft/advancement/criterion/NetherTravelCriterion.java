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
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class NetherTravelCriterion implements Criterion<NetherTravelCriterion.Conditions> {
	private static final Identifier ID = new Identifier("nether_travel");
	private final Map<PlayerAdvancementTracker, NetherTravelCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, NetherTravelCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void beginTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<NetherTravelCriterion.Conditions> conditionsContainer
	) {
		NetherTravelCriterion.Handler handler = (NetherTravelCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler == null) {
			handler = new NetherTravelCriterion.Handler(playerAdvancementTracker);
			this.handlers.put(playerAdvancementTracker, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<NetherTravelCriterion.Conditions> conditionsContainer
	) {
		NetherTravelCriterion.Handler handler = (NetherTravelCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler != null) {
			handler.removeCondition(conditionsContainer);
			if (handler.isEmpty()) {
				this.handlers.remove(playerAdvancementTracker);
			}
		}
	}

	@Override
	public void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
		this.handlers.remove(playerAdvancementTracker);
	}

	public NetherTravelCriterion.Conditions deserializeConditions(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		LocationPredicate locationPredicate = LocationPredicate.deserialize(jsonObject.get("entered"));
		LocationPredicate locationPredicate2 = LocationPredicate.deserialize(jsonObject.get("exited"));
		DistancePredicate distancePredicate = DistancePredicate.deserialize(jsonObject.get("distance"));
		return new NetherTravelCriterion.Conditions(locationPredicate, locationPredicate2, distancePredicate);
	}

	public void handle(ServerPlayerEntity serverPlayerEntity, Vec3d vec3d) {
		NetherTravelCriterion.Handler handler = (NetherTravelCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.handle(serverPlayerEntity.getServerWorld(), vec3d, serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final LocationPredicate entered;
		private final LocationPredicate exited;
		private final DistancePredicate distance;

		public Conditions(LocationPredicate locationPredicate, LocationPredicate locationPredicate2, DistancePredicate distancePredicate) {
			super(NetherTravelCriterion.ID);
			this.entered = locationPredicate;
			this.exited = locationPredicate2;
			this.distance = distancePredicate;
		}

		public static NetherTravelCriterion.Conditions distance(DistancePredicate distancePredicate) {
			return new NetherTravelCriterion.Conditions(LocationPredicate.ANY, LocationPredicate.ANY, distancePredicate);
		}

		public boolean matches(ServerWorld serverWorld, Vec3d vec3d, double d, double e, double f) {
			if (!this.entered.test(serverWorld, vec3d.x, vec3d.y, vec3d.z)) {
				return false;
			} else {
				return !this.exited.test(serverWorld, d, e, f) ? false : this.distance.test(vec3d.x, vec3d.y, vec3d.z, d, e, f);
			}
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("entered", this.entered.serialize());
			jsonObject.add("exited", this.exited.serialize());
			jsonObject.add("distance", this.distance.serialize());
			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker field_9720;
		private final Set<Criterion.ConditionsContainer<NetherTravelCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<NetherTravelCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.field_9720 = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void addCondition(Criterion.ConditionsContainer<NetherTravelCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void removeCondition(Criterion.ConditionsContainer<NetherTravelCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void handle(ServerWorld serverWorld, Vec3d vec3d, double d, double e, double f) {
			List<Criterion.ConditionsContainer<NetherTravelCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<NetherTravelCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().matches(serverWorld, vec3d, d, e, f)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<NetherTravelCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<NetherTravelCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.field_9720);
				}
			}
		}
	}
}
