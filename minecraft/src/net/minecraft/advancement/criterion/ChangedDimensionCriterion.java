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
import javax.annotation.Nullable;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.dimension.DimensionType;

public class ChangedDimensionCriterion implements Criterion<ChangedDimensionCriterion.Conditions> {
	private static final Identifier ID = new Identifier("changed_dimension");
	private final Map<PlayerAdvancementTracker, ChangedDimensionCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, ChangedDimensionCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void beginTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<ChangedDimensionCriterion.Conditions> conditionsContainer) {
		ChangedDimensionCriterion.Handler handler = (ChangedDimensionCriterion.Handler)this.handlers.get(manager);
		if (handler == null) {
			handler = new ChangedDimensionCriterion.Handler(manager);
			this.handlers.put(manager, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<ChangedDimensionCriterion.Conditions> conditionsContainer) {
		ChangedDimensionCriterion.Handler handler = (ChangedDimensionCriterion.Handler)this.handlers.get(manager);
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

	public ChangedDimensionCriterion.Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		DimensionType dimensionType = jsonObject.has("from") ? DimensionType.byId(new Identifier(JsonHelper.getString(jsonObject, "from"))) : null;
		DimensionType dimensionType2 = jsonObject.has("to") ? DimensionType.byId(new Identifier(JsonHelper.getString(jsonObject, "to"))) : null;
		return new ChangedDimensionCriterion.Conditions(dimensionType, dimensionType2);
	}

	public void trigger(ServerPlayerEntity player, DimensionType from, DimensionType to) {
		ChangedDimensionCriterion.Handler handler = (ChangedDimensionCriterion.Handler)this.handlers.get(player.getAdvancementTracker());
		if (handler != null) {
			handler.handle(from, to);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		@Nullable
		private final DimensionType from;
		@Nullable
		private final DimensionType to;

		public Conditions(@Nullable DimensionType from, @Nullable DimensionType to) {
			super(ChangedDimensionCriterion.ID);
			this.from = from;
			this.to = to;
		}

		public static ChangedDimensionCriterion.Conditions to(DimensionType dimensionType) {
			return new ChangedDimensionCriterion.Conditions(null, dimensionType);
		}

		public boolean matches(DimensionType from, DimensionType to) {
			return this.from != null && this.from != from ? false : this.to == null || this.to == to;
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			if (this.from != null) {
				jsonObject.addProperty("from", DimensionType.getId(this.from).toString());
			}

			if (this.to != null) {
				jsonObject.addProperty("to", DimensionType.getId(this.to).toString());
			}

			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker manager;
		private final Set<Criterion.ConditionsContainer<ChangedDimensionCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<ChangedDimensionCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker manager) {
			this.manager = manager;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void addCondition(Criterion.ConditionsContainer<ChangedDimensionCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void removeCondition(Criterion.ConditionsContainer<ChangedDimensionCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void handle(DimensionType from, DimensionType to) {
			List<Criterion.ConditionsContainer<ChangedDimensionCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<ChangedDimensionCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().matches(from, to)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<ChangedDimensionCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<ChangedDimensionCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.manager);
				}
			}
		}
	}
}
