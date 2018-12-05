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
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.NumberRange;
import net.minecraft.util.math.Vec3d;

public class LevitationCriterion implements Criterion<LevitationCriterion.Conditions> {
	private static final Identifier ID = new Identifier("levitation");
	private final Map<ServerAdvancementManager, LevitationCriterion.Handler> handlers = Maps.<ServerAdvancementManager, LevitationCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void addCondition(ServerAdvancementManager serverAdvancementManager, Criterion.ConditionsContainer<LevitationCriterion.Conditions> conditionsContainer) {
		LevitationCriterion.Handler handler = (LevitationCriterion.Handler)this.handlers.get(serverAdvancementManager);
		if (handler == null) {
			handler = new LevitationCriterion.Handler(serverAdvancementManager);
			this.handlers.put(serverAdvancementManager, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void removeCondition(
		ServerAdvancementManager serverAdvancementManager, Criterion.ConditionsContainer<LevitationCriterion.Conditions> conditionsContainer
	) {
		LevitationCriterion.Handler handler = (LevitationCriterion.Handler)this.handlers.get(serverAdvancementManager);
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

	public LevitationCriterion.Conditions deserializeConditions(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		DistancePredicate distancePredicate = DistancePredicate.deserialize(jsonObject.get("distance"));
		NumberRange.Integer integer = NumberRange.Integer.fromJson(jsonObject.get("duration"));
		return new LevitationCriterion.Conditions(distancePredicate, integer);
	}

	public void handle(ServerPlayerEntity serverPlayerEntity, Vec3d vec3d, int i) {
		LevitationCriterion.Handler handler = (LevitationCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.handle(serverPlayerEntity, vec3d, i);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final DistancePredicate distance;
		private final NumberRange.Integer duration;

		public Conditions(DistancePredicate distancePredicate, NumberRange.Integer integer) {
			super(LevitationCriterion.ID);
			this.distance = distancePredicate;
			this.duration = integer;
		}

		public static LevitationCriterion.Conditions method_9013(DistancePredicate distancePredicate) {
			return new LevitationCriterion.Conditions(distancePredicate, NumberRange.Integer.ANY);
		}

		public boolean matches(ServerPlayerEntity serverPlayerEntity, Vec3d vec3d, int i) {
			return !this.distance.test(vec3d.x, vec3d.y, vec3d.z, serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z) ? false : this.duration.test(i);
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("distance", this.distance.serialize());
			jsonObject.add("duration", this.duration.serialize());
			return jsonObject;
		}
	}

	static class Handler {
		private final ServerAdvancementManager manager;
		private final Set<Criterion.ConditionsContainer<LevitationCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<LevitationCriterion.Conditions>>newHashSet();

		public Handler(ServerAdvancementManager serverAdvancementManager) {
			this.manager = serverAdvancementManager;
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

		public void handle(ServerPlayerEntity serverPlayerEntity, Vec3d vec3d, int i) {
			List<Criterion.ConditionsContainer<LevitationCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<LevitationCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().matches(serverPlayerEntity, vec3d, i)) {
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
