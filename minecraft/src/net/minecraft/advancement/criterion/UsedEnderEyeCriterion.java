package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancement.ServerAdvancementManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.NumberRange;
import net.minecraft.util.math.BlockPos;

public class UsedEnderEyeCriterion implements Criterion<UsedEnderEyeCriterion.Conditions> {
	private static final Identifier id = new Identifier("used_ender_eye");
	private final Map<ServerAdvancementManager, UsedEnderEyeCriterion.Handler> handlers = Maps.<ServerAdvancementManager, UsedEnderEyeCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return id;
	}

	@Override
	public void addCondition(
		ServerAdvancementManager serverAdvancementManager, Criterion.ConditionsContainer<UsedEnderEyeCriterion.Conditions> conditionsContainer
	) {
		UsedEnderEyeCriterion.Handler handler = (UsedEnderEyeCriterion.Handler)this.handlers.get(serverAdvancementManager);
		if (handler == null) {
			handler = new UsedEnderEyeCriterion.Handler(serverAdvancementManager);
			this.handlers.put(serverAdvancementManager, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void removeCondition(
		ServerAdvancementManager serverAdvancementManager, Criterion.ConditionsContainer<UsedEnderEyeCriterion.Conditions> conditionsContainer
	) {
		UsedEnderEyeCriterion.Handler handler = (UsedEnderEyeCriterion.Handler)this.handlers.get(serverAdvancementManager);
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

	public UsedEnderEyeCriterion.Conditions deserializeConditions(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		NumberRange.Float float_ = NumberRange.Float.fromJson(jsonObject.get("distance"));
		return new UsedEnderEyeCriterion.Conditions(float_);
	}

	public void handle(ServerPlayerEntity serverPlayerEntity, BlockPos blockPos) {
		UsedEnderEyeCriterion.Handler handler = (UsedEnderEyeCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			double d = serverPlayerEntity.x - (double)blockPos.getX();
			double e = serverPlayerEntity.z - (double)blockPos.getZ();
			handler.handle(d * d + e * e);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final NumberRange.Float distance;

		public Conditions(NumberRange.Float float_) {
			super(UsedEnderEyeCriterion.id);
			this.distance = float_;
		}

		public boolean matches(double d) {
			return this.distance.matchesSquared(d);
		}
	}

	static class Handler {
		private final ServerAdvancementManager manager;
		private final Set<Criterion.ConditionsContainer<UsedEnderEyeCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<UsedEnderEyeCriterion.Conditions>>newHashSet();

		public Handler(ServerAdvancementManager serverAdvancementManager) {
			this.manager = serverAdvancementManager;
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
