package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancement.ServerAdvancementManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class TickCriterion implements Criterion<TickCriterion.Conditions> {
	public static final Identifier ID = new Identifier("tick");
	private final Map<ServerAdvancementManager, TickCriterion.Handler> handlers = Maps.<ServerAdvancementManager, TickCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void addCondition(ServerAdvancementManager serverAdvancementManager, Criterion.ConditionsContainer<TickCriterion.Conditions> conditionsContainer) {
		TickCriterion.Handler handler = (TickCriterion.Handler)this.handlers.get(serverAdvancementManager);
		if (handler == null) {
			handler = new TickCriterion.Handler(serverAdvancementManager);
			this.handlers.put(serverAdvancementManager, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void removeCondition(ServerAdvancementManager serverAdvancementManager, Criterion.ConditionsContainer<TickCriterion.Conditions> conditionsContainer) {
		TickCriterion.Handler handler = (TickCriterion.Handler)this.handlers.get(serverAdvancementManager);
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

	public TickCriterion.Conditions deserializeConditions(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		return new TickCriterion.Conditions();
	}

	public void handle(ServerPlayerEntity serverPlayerEntity) {
		TickCriterion.Handler handler = (TickCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
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
		private final ServerAdvancementManager manager;
		private final Set<Criterion.ConditionsContainer<TickCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<TickCriterion.Conditions>>newHashSet();

		public Handler(ServerAdvancementManager serverAdvancementManager) {
			this.manager = serverAdvancementManager;
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
