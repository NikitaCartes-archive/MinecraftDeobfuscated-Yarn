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
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ConstructBeaconCriterion implements Criterion<ConstructBeaconCriterion.Conditions> {
	private static final Identifier ID = new Identifier("construct_beacon");
	private final Map<PlayerAdvancementTracker, ConstructBeaconCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, ConstructBeaconCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void beginTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<ConstructBeaconCriterion.Conditions> conditionsContainer) {
		ConstructBeaconCriterion.Handler handler = (ConstructBeaconCriterion.Handler)this.handlers.get(manager);
		if (handler == null) {
			handler = new ConstructBeaconCriterion.Handler(manager);
			this.handlers.put(manager, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<ConstructBeaconCriterion.Conditions> conditionsContainer) {
		ConstructBeaconCriterion.Handler handler = (ConstructBeaconCriterion.Handler)this.handlers.get(manager);
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

	public ConstructBeaconCriterion.Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("level"));
		return new ConstructBeaconCriterion.Conditions(intRange);
	}

	public void trigger(ServerPlayerEntity player, BeaconBlockEntity beacon) {
		ConstructBeaconCriterion.Handler handler = (ConstructBeaconCriterion.Handler)this.handlers.get(player.getAdvancementTracker());
		if (handler != null) {
			handler.handle(beacon);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final NumberRange.IntRange level;

		public Conditions(NumberRange.IntRange intRange) {
			super(ConstructBeaconCriterion.ID);
			this.level = intRange;
		}

		public static ConstructBeaconCriterion.Conditions level(NumberRange.IntRange intRange) {
			return new ConstructBeaconCriterion.Conditions(intRange);
		}

		public boolean matches(BeaconBlockEntity beaconBlockEntity) {
			return this.level.test(beaconBlockEntity.getLevel());
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("level", this.level.toJson());
			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker manager;
		private final Set<Criterion.ConditionsContainer<ConstructBeaconCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<ConstructBeaconCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker manager) {
			this.manager = manager;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void addCondition(Criterion.ConditionsContainer<ConstructBeaconCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void removeCondition(Criterion.ConditionsContainer<ConstructBeaconCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void handle(BeaconBlockEntity beaconBlockEntity) {
			List<Criterion.ConditionsContainer<ConstructBeaconCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<ConstructBeaconCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().matches(beaconBlockEntity)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<ConstructBeaconCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<ConstructBeaconCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.manager);
				}
			}
		}
	}
}
