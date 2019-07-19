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
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class LevitationCriterion implements Criterion<LevitationCriterion.Conditions> {
	private static final Identifier ID = new Identifier("levitation");
	private final Map<PlayerAdvancementTracker, LevitationCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, LevitationCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void beginTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<LevitationCriterion.Conditions> conditionsContainer) {
		LevitationCriterion.Handler handler = (LevitationCriterion.Handler)this.handlers.get(manager);
		if (handler == null) {
			handler = new LevitationCriterion.Handler(manager);
			this.handlers.put(manager, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<LevitationCriterion.Conditions> conditionsContainer) {
		LevitationCriterion.Handler handler = (LevitationCriterion.Handler)this.handlers.get(manager);
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

	public LevitationCriterion.Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		DistancePredicate distancePredicate = DistancePredicate.deserialize(jsonObject.get("distance"));
		NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("duration"));
		return new LevitationCriterion.Conditions(distancePredicate, intRange);
	}

	public void trigger(ServerPlayerEntity player, Vec3d startPos, int duration) {
		LevitationCriterion.Handler handler = (LevitationCriterion.Handler)this.handlers.get(player.getAdvancementTracker());
		if (handler != null) {
			handler.handle(player, startPos, duration);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final DistancePredicate distance;
		private final NumberRange.IntRange duration;

		public Conditions(DistancePredicate distance, NumberRange.IntRange intRange) {
			super(LevitationCriterion.ID);
			this.distance = distance;
			this.duration = intRange;
		}

		public static LevitationCriterion.Conditions create(DistancePredicate distance) {
			return new LevitationCriterion.Conditions(distance, NumberRange.IntRange.ANY);
		}

		public boolean matches(ServerPlayerEntity player, Vec3d distance, int duration) {
			return !this.distance.test(distance.x, distance.y, distance.z, player.x, player.y, player.z) ? false : this.duration.test(duration);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("distance", this.distance.serialize());
			jsonObject.add("duration", this.duration.toJson());
			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker manager;
		private final Set<Criterion.ConditionsContainer<LevitationCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<LevitationCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker manager) {
			this.manager = manager;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void addCondition(Criterion.ConditionsContainer<LevitationCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void removeCondition(Criterion.ConditionsContainer<LevitationCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void handle(ServerPlayerEntity coord, Vec3d distance, int duration) {
			List<Criterion.ConditionsContainer<LevitationCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<LevitationCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().matches(coord, distance, duration)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<LevitationCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<LevitationCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.manager);
				}
			}
		}
	}
}
