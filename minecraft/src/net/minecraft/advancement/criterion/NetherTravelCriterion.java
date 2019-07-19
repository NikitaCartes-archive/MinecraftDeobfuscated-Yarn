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
	public void beginTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<NetherTravelCriterion.Conditions> conditionsContainer) {
		NetherTravelCriterion.Handler handler = (NetherTravelCriterion.Handler)this.handlers.get(manager);
		if (handler == null) {
			handler = new NetherTravelCriterion.Handler(manager);
			this.handlers.put(manager, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<NetherTravelCriterion.Conditions> conditionsContainer) {
		NetherTravelCriterion.Handler handler = (NetherTravelCriterion.Handler)this.handlers.get(manager);
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

	public NetherTravelCriterion.Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		LocationPredicate locationPredicate = LocationPredicate.fromJson(jsonObject.get("entered"));
		LocationPredicate locationPredicate2 = LocationPredicate.fromJson(jsonObject.get("exited"));
		DistancePredicate distancePredicate = DistancePredicate.deserialize(jsonObject.get("distance"));
		return new NetherTravelCriterion.Conditions(locationPredicate, locationPredicate2, distancePredicate);
	}

	public void trigger(ServerPlayerEntity player, Vec3d enteredPos) {
		NetherTravelCriterion.Handler handler = (NetherTravelCriterion.Handler)this.handlers.get(player.getAdvancementTracker());
		if (handler != null) {
			handler.handle(player.getServerWorld(), enteredPos, player.x, player.y, player.z);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final LocationPredicate entered;
		private final LocationPredicate exited;
		private final DistancePredicate distance;

		public Conditions(LocationPredicate entered, LocationPredicate exited, DistancePredicate distance) {
			super(NetherTravelCriterion.ID);
			this.entered = entered;
			this.exited = exited;
			this.distance = distance;
		}

		public static NetherTravelCriterion.Conditions distance(DistancePredicate distance) {
			return new NetherTravelCriterion.Conditions(LocationPredicate.ANY, LocationPredicate.ANY, distance);
		}

		public boolean matches(ServerWorld world, Vec3d entered, double exitedX, double exitedY, double exitedZ) {
			if (!this.entered.test(world, entered.x, entered.y, entered.z)) {
				return false;
			} else {
				return !this.exited.test(world, exitedX, exitedY, exitedZ) ? false : this.distance.test(entered.x, entered.y, entered.z, exitedX, exitedY, exitedZ);
			}
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("entered", this.entered.toJson());
			jsonObject.add("exited", this.exited.toJson());
			jsonObject.add("distance", this.distance.serialize());
			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker manager;
		private final Set<Criterion.ConditionsContainer<NetherTravelCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<NetherTravelCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker manager) {
			this.manager = manager;
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

		public void handle(ServerWorld world, Vec3d entered, double exitedX, double exitedY, double exitedZ) {
			List<Criterion.ConditionsContainer<NetherTravelCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<NetherTravelCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().matches(world, entered, exitedX, exitedY, exitedZ)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<NetherTravelCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<NetherTravelCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.manager);
				}
			}
		}
	}
}
