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
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class EnchantedItemCriterion implements Criterion<EnchantedItemCriterion.Conditions> {
	private static final Identifier ID = new Identifier("enchanted_item");
	private final Map<PlayerAdvancementTracker, EnchantedItemCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, EnchantedItemCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void beginTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<EnchantedItemCriterion.Conditions> conditionsContainer) {
		EnchantedItemCriterion.Handler handler = (EnchantedItemCriterion.Handler)this.handlers.get(manager);
		if (handler == null) {
			handler = new EnchantedItemCriterion.Handler(manager);
			this.handlers.put(manager, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<EnchantedItemCriterion.Conditions> conditionsContainer) {
		EnchantedItemCriterion.Handler handler = (EnchantedItemCriterion.Handler)this.handlers.get(manager);
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

	public EnchantedItemCriterion.Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
		NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("levels"));
		return new EnchantedItemCriterion.Conditions(itemPredicate, intRange);
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack, int levels) {
		EnchantedItemCriterion.Handler handler = (EnchantedItemCriterion.Handler)this.handlers.get(player.getAdvancementTracker());
		if (handler != null) {
			handler.handle(stack, levels);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final ItemPredicate item;
		private final NumberRange.IntRange levels;

		public Conditions(ItemPredicate item, NumberRange.IntRange intRange) {
			super(EnchantedItemCriterion.ID);
			this.item = item;
			this.levels = intRange;
		}

		public static EnchantedItemCriterion.Conditions any() {
			return new EnchantedItemCriterion.Conditions(ItemPredicate.ANY, NumberRange.IntRange.ANY);
		}

		public boolean matches(ItemStack stack, int level) {
			return !this.item.test(stack) ? false : this.levels.test(level);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("item", this.item.toJson());
			jsonObject.add("levels", this.levels.toJson());
			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker manager;
		private final Set<Criterion.ConditionsContainer<EnchantedItemCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<EnchantedItemCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker manager) {
			this.manager = manager;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void addCondition(Criterion.ConditionsContainer<EnchantedItemCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void removeCondition(Criterion.ConditionsContainer<EnchantedItemCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void handle(ItemStack level, int i) {
			List<Criterion.ConditionsContainer<EnchantedItemCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<EnchantedItemCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().matches(level, i)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<EnchantedItemCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<EnchantedItemCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.manager);
				}
			}
		}
	}
}
