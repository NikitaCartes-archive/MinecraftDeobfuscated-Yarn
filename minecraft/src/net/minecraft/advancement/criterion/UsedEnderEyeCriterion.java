package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class UsedEnderEyeCriterion implements Criterion<UsedEnderEyeCriterion.Conditions> {
	private static final Identifier id = new Identifier("used_ender_eye");
	private final Map<PlayerAdvancementTracker, UsedEnderEyeCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, UsedEnderEyeCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return id;
	}

	@Override
	public void beginTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<UsedEnderEyeCriterion.Conditions> conditionsContainer) {
		UsedEnderEyeCriterion.Handler handler = (UsedEnderEyeCriterion.Handler)this.handlers.get(manager);
		if (handler == null) {
			handler = new UsedEnderEyeCriterion.Handler(manager);
			this.handlers.put(manager, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<UsedEnderEyeCriterion.Conditions> conditionsContainer) {
		UsedEnderEyeCriterion.Handler handler = (UsedEnderEyeCriterion.Handler)this.handlers.get(manager);
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

	public UsedEnderEyeCriterion.Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		NumberRange.FloatRange floatRange = NumberRange.FloatRange.fromJson(jsonObject.get("distance"));
		return new UsedEnderEyeCriterion.Conditions(floatRange);
	}

	public void trigger(ServerPlayerEntity player, BlockPos strongholdPos) {
		UsedEnderEyeCriterion.Handler handler = (UsedEnderEyeCriterion.Handler)this.handlers.get(player.getAdvancementTracker());
		if (handler != null) {
			double d = player.x - (double)strongholdPos.getX();
			double e = player.z - (double)strongholdPos.getZ();
			handler.handle(d * d + e * e);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final NumberRange.FloatRange distance;

		public Conditions(NumberRange.FloatRange floatRange) {
			super(UsedEnderEyeCriterion.id);
			this.distance = floatRange;
		}

		public boolean matches(double d) {
			return this.distance.matchesSquared(d);
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker manager;
		private final Set<Criterion.ConditionsContainer<UsedEnderEyeCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<UsedEnderEyeCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.manager = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void addCondition(Criterion.ConditionsContainer<UsedEnderEyeCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void removeCondition(Criterion.ConditionsContainer<UsedEnderEyeCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void handle(double d) {
			List<Criterion.ConditionsContainer<UsedEnderEyeCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<UsedEnderEyeCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().matches(d)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<UsedEnderEyeCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<UsedEnderEyeCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.manager);
				}
			}
		}
	}
}
