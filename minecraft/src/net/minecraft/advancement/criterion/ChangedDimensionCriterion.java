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
	private static final Identifier field_9493 = new Identifier("changed_dimension");
	private final Map<PlayerAdvancementTracker, ChangedDimensionCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, ChangedDimensionCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return field_9493;
	}

	@Override
	public void beginTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<ChangedDimensionCriterion.Conditions> conditionsContainer
	) {
		ChangedDimensionCriterion.Handler handler = (ChangedDimensionCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler == null) {
			handler = new ChangedDimensionCriterion.Handler(playerAdvancementTracker);
			this.handlers.put(playerAdvancementTracker, handler);
		}

		handler.method_8795(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<ChangedDimensionCriterion.Conditions> conditionsContainer
	) {
		ChangedDimensionCriterion.Handler handler = (ChangedDimensionCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler != null) {
			handler.method_8798(conditionsContainer);
			if (handler.isEmpty()) {
				this.handlers.remove(playerAdvancementTracker);
			}
		}
	}

	@Override
	public void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
		this.handlers.remove(playerAdvancementTracker);
	}

	public ChangedDimensionCriterion.Conditions method_8793(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		DimensionType dimensionType = jsonObject.has("from") ? DimensionType.method_12483(new Identifier(JsonHelper.getString(jsonObject, "from"))) : null;
		DimensionType dimensionType2 = jsonObject.has("to") ? DimensionType.method_12483(new Identifier(JsonHelper.getString(jsonObject, "to"))) : null;
		return new ChangedDimensionCriterion.Conditions(dimensionType, dimensionType2);
	}

	public void method_8794(ServerPlayerEntity serverPlayerEntity, DimensionType dimensionType, DimensionType dimensionType2) {
		ChangedDimensionCriterion.Handler handler = (ChangedDimensionCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.method_8797(dimensionType, dimensionType2);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		@Nullable
		private final DimensionType field_9497;
		@Nullable
		private final DimensionType field_9498;

		public Conditions(@Nullable DimensionType dimensionType, @Nullable DimensionType dimensionType2) {
			super(ChangedDimensionCriterion.field_9493);
			this.field_9497 = dimensionType;
			this.field_9498 = dimensionType2;
		}

		public static ChangedDimensionCriterion.Conditions method_8799(DimensionType dimensionType) {
			return new ChangedDimensionCriterion.Conditions(null, dimensionType);
		}

		public boolean method_8800(DimensionType dimensionType, DimensionType dimensionType2) {
			return this.field_9497 != null && this.field_9497 != dimensionType ? false : this.field_9498 == null || this.field_9498 == dimensionType2;
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			if (this.field_9497 != null) {
				jsonObject.addProperty("from", DimensionType.method_12485(this.field_9497).toString());
			}

			if (this.field_9498 != null) {
				jsonObject.addProperty("to", DimensionType.method_12485(this.field_9498).toString());
			}

			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker field_9496;
		private final Set<Criterion.ConditionsContainer<ChangedDimensionCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<ChangedDimensionCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.field_9496 = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void method_8795(Criterion.ConditionsContainer<ChangedDimensionCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void method_8798(Criterion.ConditionsContainer<ChangedDimensionCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void method_8797(DimensionType dimensionType, DimensionType dimensionType2) {
			List<Criterion.ConditionsContainer<ChangedDimensionCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<ChangedDimensionCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.method_797().method_8800(dimensionType, dimensionType2)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<ChangedDimensionCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<ChangedDimensionCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.field_9496);
				}
			}
		}
	}
}
