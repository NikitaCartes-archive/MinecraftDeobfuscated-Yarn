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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.NumberRange;
import net.minecraft.util.math.Vec3d;

public class LevitationCriterion implements Criterion<LevitationCriterion.Conditions> {
	private static final Identifier field_9671 = new Identifier("levitation");
	private final Map<PlayerAdvancementTracker, LevitationCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, LevitationCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return field_9671;
	}

	@Override
	public void beginTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<LevitationCriterion.Conditions> conditionsContainer
	) {
		LevitationCriterion.Handler handler = (LevitationCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler == null) {
			handler = new LevitationCriterion.Handler(playerAdvancementTracker);
			this.handlers.put(playerAdvancementTracker, handler);
		}

		handler.method_9009(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<LevitationCriterion.Conditions> conditionsContainer
	) {
		LevitationCriterion.Handler handler = (LevitationCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler != null) {
			handler.method_9012(conditionsContainer);
			if (handler.isEmpty()) {
				this.handlers.remove(playerAdvancementTracker);
			}
		}
	}

	@Override
	public void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
		this.handlers.remove(playerAdvancementTracker);
	}

	public LevitationCriterion.Conditions method_9006(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		DistancePredicate distancePredicate = DistancePredicate.deserialize(jsonObject.get("distance"));
		NumberRange.Integer integer = NumberRange.Integer.fromJson(jsonObject.get("duration"));
		return new LevitationCriterion.Conditions(distancePredicate, integer);
	}

	public void method_9008(ServerPlayerEntity serverPlayerEntity, Vec3d vec3d, int i) {
		LevitationCriterion.Handler handler = (LevitationCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.method_9011(serverPlayerEntity, vec3d, i);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final DistancePredicate distance;
		private final NumberRange.Integer duration;

		public Conditions(DistancePredicate distancePredicate, NumberRange.Integer integer) {
			super(LevitationCriterion.field_9671);
			this.distance = distancePredicate;
			this.duration = integer;
		}

		public static LevitationCriterion.Conditions method_9013(DistancePredicate distancePredicate) {
			return new LevitationCriterion.Conditions(distancePredicate, NumberRange.Integer.ANY);
		}

		public boolean method_9014(ServerPlayerEntity serverPlayerEntity, Vec3d vec3d, int i) {
			return !this.distance.test(vec3d.x, vec3d.y, vec3d.z, serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z) ? false : this.duration.test(i);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("distance", this.distance.serialize());
			jsonObject.add("duration", this.duration.serialize());
			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker field_9674;
		private final Set<Criterion.ConditionsContainer<LevitationCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<LevitationCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.field_9674 = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void method_9009(Criterion.ConditionsContainer<LevitationCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void method_9012(Criterion.ConditionsContainer<LevitationCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void method_9011(ServerPlayerEntity serverPlayerEntity, Vec3d vec3d, int i) {
			List<Criterion.ConditionsContainer<LevitationCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<LevitationCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.method_797().method_9014(serverPlayerEntity, vec3d, i)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<LevitationCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<LevitationCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.field_9674);
				}
			}
		}
	}
}
