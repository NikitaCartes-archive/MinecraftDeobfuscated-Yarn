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
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.NumberRange;

public class ConstructBeaconCriterion implements Criterion<ConstructBeaconCriterion.Conditions> {
	private static final Identifier field_9504 = new Identifier("construct_beacon");
	private final Map<PlayerAdvancementTracker, ConstructBeaconCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, ConstructBeaconCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return field_9504;
	}

	@Override
	public void beginTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<ConstructBeaconCriterion.Conditions> conditionsContainer
	) {
		ConstructBeaconCriterion.Handler handler = (ConstructBeaconCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler == null) {
			handler = new ConstructBeaconCriterion.Handler(playerAdvancementTracker);
			this.handlers.put(playerAdvancementTracker, handler);
		}

		handler.method_8813(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<ConstructBeaconCriterion.Conditions> conditionsContainer
	) {
		ConstructBeaconCriterion.Handler handler = (ConstructBeaconCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler != null) {
			handler.method_8816(conditionsContainer);
			if (handler.isEmpty()) {
				this.handlers.remove(playerAdvancementTracker);
			}
		}
	}

	@Override
	public void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
		this.handlers.remove(playerAdvancementTracker);
	}

	public ConstructBeaconCriterion.Conditions method_8811(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		NumberRange.Integer integer = NumberRange.Integer.fromJson(jsonObject.get("level"));
		return new ConstructBeaconCriterion.Conditions(integer);
	}

	public void method_8812(ServerPlayerEntity serverPlayerEntity, BeaconBlockEntity beaconBlockEntity) {
		ConstructBeaconCriterion.Handler handler = (ConstructBeaconCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.method_8814(beaconBlockEntity);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final NumberRange.Integer level;

		public Conditions(NumberRange.Integer integer) {
			super(ConstructBeaconCriterion.field_9504);
			this.level = integer;
		}

		public static ConstructBeaconCriterion.Conditions level(NumberRange.Integer integer) {
			return new ConstructBeaconCriterion.Conditions(integer);
		}

		public boolean method_8817(BeaconBlockEntity beaconBlockEntity) {
			return this.level.test(beaconBlockEntity.getLevel());
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("level", this.level.serialize());
			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker field_9507;
		private final Set<Criterion.ConditionsContainer<ConstructBeaconCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<ConstructBeaconCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.field_9507 = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void method_8813(Criterion.ConditionsContainer<ConstructBeaconCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void method_8816(Criterion.ConditionsContainer<ConstructBeaconCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void method_8814(BeaconBlockEntity beaconBlockEntity) {
			List<Criterion.ConditionsContainer<ConstructBeaconCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<ConstructBeaconCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.method_797().method_8817(beaconBlockEntity)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<ConstructBeaconCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<ConstructBeaconCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.field_9507);
				}
			}
		}
	}
}
