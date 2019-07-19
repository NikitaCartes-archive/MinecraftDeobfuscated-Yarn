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

public class ItemDurabilityChangedCriterion implements Criterion<ItemDurabilityChangedCriterion.Conditions> {
	private static final Identifier ID = new Identifier("item_durability_changed");
	private final Map<PlayerAdvancementTracker, ItemDurabilityChangedCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, ItemDurabilityChangedCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void beginTrackingCondition(
		PlayerAdvancementTracker manager, Criterion.ConditionsContainer<ItemDurabilityChangedCriterion.Conditions> conditionsContainer
	) {
		ItemDurabilityChangedCriterion.Handler handler = (ItemDurabilityChangedCriterion.Handler)this.handlers.get(manager);
		if (handler == null) {
			handler = new ItemDurabilityChangedCriterion.Handler(manager);
			this.handlers.put(manager, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker manager, Criterion.ConditionsContainer<ItemDurabilityChangedCriterion.Conditions> conditionsContainer
	) {
		ItemDurabilityChangedCriterion.Handler handler = (ItemDurabilityChangedCriterion.Handler)this.handlers.get(manager);
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

	public ItemDurabilityChangedCriterion.Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
		NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("durability"));
		NumberRange.IntRange intRange2 = NumberRange.IntRange.fromJson(jsonObject.get("delta"));
		return new ItemDurabilityChangedCriterion.Conditions(itemPredicate, intRange, intRange2);
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack, int damage) {
		ItemDurabilityChangedCriterion.Handler handler = (ItemDurabilityChangedCriterion.Handler)this.handlers.get(player.getAdvancementTracker());
		if (handler != null) {
			handler.handle(stack, damage);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final ItemPredicate item;
		private final NumberRange.IntRange durability;
		private final NumberRange.IntRange delta;

		public Conditions(ItemPredicate item, NumberRange.IntRange intRange, NumberRange.IntRange intRange2) {
			super(ItemDurabilityChangedCriterion.ID);
			this.item = item;
			this.durability = intRange;
			this.delta = intRange2;
		}

		public static ItemDurabilityChangedCriterion.Conditions create(ItemPredicate item, NumberRange.IntRange intRange) {
			return new ItemDurabilityChangedCriterion.Conditions(item, intRange, NumberRange.IntRange.ANY);
		}

		public boolean matches(ItemStack item, int durability) {
			if (!this.item.test(item)) {
				return false;
			} else {
				return !this.durability.test(item.getMaxDamage() - durability) ? false : this.delta.test(item.getDamage() - durability);
			}
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("item", this.item.toJson());
			jsonObject.add("durability", this.durability.toJson());
			jsonObject.add("delta", this.delta.toJson());
			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker manager;
		private final Set<Criterion.ConditionsContainer<ItemDurabilityChangedCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<ItemDurabilityChangedCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker manager) {
			this.manager = manager;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void addCondition(Criterion.ConditionsContainer<ItemDurabilityChangedCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void removeCondition(Criterion.ConditionsContainer<ItemDurabilityChangedCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void handle(ItemStack stack, int durability) {
			List<Criterion.ConditionsContainer<ItemDurabilityChangedCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<ItemDurabilityChangedCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().matches(stack, durability)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<ItemDurabilityChangedCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<ItemDurabilityChangedCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.manager);
				}
			}
		}
	}
}
