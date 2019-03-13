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
	private static final Identifier field_9773 = new Identifier("used_totem");
	private final Map<PlayerAdvancementTracker, UsedTotemCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, UsedTotemCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return field_9773;
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

		handler.method_9166(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<UsedTotemCriterion.Conditions> conditionsContainer
	) {
		UsedTotemCriterion.Handler handler = (UsedTotemCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler != null) {
			handler.method_9169(conditionsContainer);
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

	public void method_9165(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack) {
		UsedTotemCriterion.Handler handler = (UsedTotemCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.handle(itemStack);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final ItemPredicate item;

		public Conditions(ItemPredicate itemPredicate) {
			super(UsedTotemCriterion.field_9773);
			this.item = itemPredicate;
		}

		public static UsedTotemCriterion.Conditions method_9170(ItemProvider itemProvider) {
			return new UsedTotemCriterion.Conditions(ItemPredicate.Builder.create().method_8977(itemProvider).build());
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
		private final PlayerAdvancementTracker field_9776;
		private final Set<Criterion.ConditionsContainer<UsedTotemCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<UsedTotemCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.field_9776 = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void method_9166(Criterion.ConditionsContainer<UsedTotemCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void method_9169(Criterion.ConditionsContainer<UsedTotemCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void handle(ItemStack itemStack) {
			List<Criterion.ConditionsContainer<UsedTotemCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<UsedTotemCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.method_797().matches(itemStack)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<UsedTotemCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<UsedTotemCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.field_9776);
				}
			}
		}
	}
}
