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
import net.minecraft.advancement.ServerAdvancementManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.dimension.DimensionType;

public class ChangedDimensionCriterion implements Criterion<ChangedDimensionCriterion.Conditions> {
	private static final Identifier ID = new Identifier("changed_dimension");
	private final Map<ServerAdvancementManager, ChangedDimensionCriterion.Handler> handlers = Maps.<ServerAdvancementManager, ChangedDimensionCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void addCondition(
		ServerAdvancementManager serverAdvancementManager, Criterion.ConditionsContainer<ChangedDimensionCriterion.Conditions> conditionsContainer
	) {
		ChangedDimensionCriterion.Handler handler = (ChangedDimensionCriterion.Handler)this.handlers.get(serverAdvancementManager);
		if (handler == null) {
			handler = new ChangedDimensionCriterion.Handler(serverAdvancementManager);
			this.handlers.put(serverAdvancementManager, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void removeCondition(
		ServerAdvancementManager serverAdvancementManager, Criterion.ConditionsContainer<ChangedDimensionCriterion.Conditions> conditionsContainer
	) {
		ChangedDimensionCriterion.Handler handler = (ChangedDimensionCriterion.Handler)this.handlers.get(serverAdvancementManager);
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

	public ChangedDimensionCriterion.Conditions deserializeConditions(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		DimensionType dimensionType = jsonObject.has("from") ? DimensionType.byId(new Identifier(JsonHelper.getString(jsonObject, "from"))) : null;
		DimensionType dimensionType2 = jsonObject.has("to") ? DimensionType.byId(new Identifier(JsonHelper.getString(jsonObject, "to"))) : null;
		return new ChangedDimensionCriterion.Conditions(dimensionType, dimensionType2);
	}

	public void handle(ServerPlayerEntity serverPlayerEntity, DimensionType dimensionType, DimensionType dimensionType2) {
		ChangedDimensionCriterion.Handler handler = (ChangedDimensionCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.handle(dimensionType, dimensionType2);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		@Nullable
		private final DimensionType from;
		@Nullable
		private final DimensionType to;

		public Conditions(@Nullable DimensionType dimensionType, @Nullable DimensionType dimensionType2) {
			super(ChangedDimensionCriterion.ID);
			this.from = dimensionType;
			this.to = dimensionType2;
		}

		public static ChangedDimensionCriterion.Conditions to(DimensionType dimensionType) {
			return new ChangedDimensionCriterion.Conditions(null, dimensionType);
		}

		public boolean matches(DimensionType dimensionType, DimensionType dimensionType2) {
			return this.from != null && this.from != dimensionType ? false : this.to == null || this.to == dimensionType2;
		}

		@Override
		public JsonElement method_807() {
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
		private final ServerAdvancementManager manager;
		private final Set<Criterion.ConditionsContainer<ChangedDimensionCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<ChangedDimensionCriterion.Conditions>>newHashSet();

		public Handler(ServerAdvancementManager serverAdvancementManager) {
			this.manager = serverAdvancementManager;
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

		public void handle(DimensionType dimensionType, DimensionType dimensionType2) {
			List<Criterion.ConditionsContainer<ChangedDimensionCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<ChangedDimensionCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().matches(dimensionType, dimensionType2)) {
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
