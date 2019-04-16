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
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class UsedTotemCriterion implements Criterion<UsedTotemCriterion.Conditions> {
	private static final Identifier ID = new Identifier("used_totem");
	private final Map<PlayerAdvancementTracker, UsedTotemCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, UsedTotemCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void beginTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<UsedTotemCriterion.Conditions> conditionsContainer
	) {
		UsedTotemCriterion.Handler handler = (UsedTotemCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler == null) {
			handler = new UsedTotemCriterion.Handler(playerAdvancementTracker);
			this.handlers.put(playerAdvancementTracker, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<UsedTotemCriterion.Conditions> conditionsContainer
	) {
		UsedTotemCriterion.Handler handler = (UsedTotemCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler != null) {
			handler.removeCondition(conditionsContainer);
			if (handler.isEmpty()) {
				this.handlers.remove(playerAdvancementTracker);
			}
		}
	}

	@Override
	public void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
		this.handlers.remove(playerAdvancementTracker);
	}

	public UsedTotemCriterion.Conditions method_9163(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		ItemPredicate itemPredicate = ItemPredicate.deserialize(jsonObject.get("item"));
		return new UsedTotemCriterion.Conditions(itemPredicate);
	}

	public void handle(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack) {
		UsedTotemCriterion.Handler handler = (UsedTotemCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.handle(itemStack);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final ItemPredicate item;

		public Conditions(ItemPredicate itemPredicate) {
			super(UsedTotemCriterion.ID);
			this.item = itemPredicate;
		}

		public static UsedTotemCriterion.Conditions create(ItemProvider itemProvider) {
			return new UsedTotemCriterion.Conditions(ItemPredicate.Builder.create().item(itemProvider).build());
		}

		public boolean matches(ItemStack itemStack) {
			return this.item.test(itemStack);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("item", this.item.serialize());
			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker manager;
		private final Set<Criterion.ConditionsContainer<UsedTotemCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<UsedTotemCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.manager = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void addCondition(Criterion.ConditionsContainer<UsedTotemCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void removeCondition(Criterion.ConditionsContainer<UsedTotemCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void handle(ItemStack itemStack) {
			List<Criterion.ConditionsContainer<UsedTotemCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<UsedTotemCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().matches(itemStack)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<UsedTotemCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<UsedTotemCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.manager);
				}
			}
		}
	}
}
