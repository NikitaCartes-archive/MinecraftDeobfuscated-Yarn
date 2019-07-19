package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class TickCriterion implements Criterion<TickCriterion.Conditions> {
	public static final Identifier ID = new Identifier("tick");
	private final Map<PlayerAdvancementTracker, TickCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, TickCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void beginTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<TickCriterion.Conditions> conditionsContainer) {
		TickCriterion.Handler handler = (TickCriterion.Handler)this.handlers.get(manager);
		if (handler == null) {
			handler = new TickCriterion.Handler(manager);
			this.handlers.put(manager, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<TickCriterion.Conditions> conditionsContainer) {
		TickCriterion.Handler handler = (TickCriterion.Handler)this.handlers.get(manager);
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

	public TickCriterion.Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		return new TickCriterion.Conditions();
	}

	public void trigger(ServerPlayerEntity player) {
		TickCriterion.Handler handler = (TickCriterion.Handler)this.handlers.get(player.getAdvancementTracker());
		if (handler != null) {
			handler.handle();
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		public Conditions() {
			super(TickCriterion.ID);
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker manager;
		private final Set<Criterion.ConditionsContainer<TickCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<TickCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.manager = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void addCondition(Criterion.ConditionsContainer<TickCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void removeCondition(Criterion.ConditionsContainer<TickCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void handle() {
			for (Criterion.ConditionsContainer<TickCriterion.Conditions> conditionsContainer : Lists.newArrayList(this.conditions)) {
				conditionsContainer.apply(this.manager);
			}
		}
	}
}
