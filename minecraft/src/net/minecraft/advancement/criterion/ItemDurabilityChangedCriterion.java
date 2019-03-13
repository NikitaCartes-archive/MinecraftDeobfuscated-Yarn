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
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.NumberRange;

public class ItemDurabilityChangedCriterion implements Criterion<ItemDurabilityChangedCriterion.Conditions> {
	private static final Identifier field_9633 = new Identifier("item_durability_changed");
	private final Map<PlayerAdvancementTracker, ItemDurabilityChangedCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, ItemDurabilityChangedCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return field_9633;
	}

	@Override
	public void beginTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<ItemDurabilityChangedCriterion.Conditions> conditionsContainer
	) {
		ItemDurabilityChangedCriterion.Handler handler = (ItemDurabilityChangedCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler == null) {
			handler = new ItemDurabilityChangedCriterion.Handler(playerAdvancementTracker);
			this.handlers.put(playerAdvancementTracker, handler);
		}

		handler.method_8963(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<ItemDurabilityChangedCriterion.Conditions> conditionsContainer
	) {
		ItemDurabilityChangedCriterion.Handler handler = (ItemDurabilityChangedCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler != null) {
			handler.method_8966(conditionsContainer);
			if (handler.isEmpty()) {
				this.handlers.remove(playerAdvancementTracker);
			}
		}
	}

	@Override
	public void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
		this.handlers.remove(playerAdvancementTracker);
	}

	public ItemDurabilityChangedCriterion.Conditions method_8962(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		ItemPredicate itemPredicate = ItemPredicate.deserialize(jsonObject.get("item"));
		NumberRange.Integer integer = NumberRange.Integer.fromJson(jsonObject.get("durability"));
		NumberRange.Integer integer2 = NumberRange.Integer.fromJson(jsonObject.get("delta"));
		return new ItemDurabilityChangedCriterion.Conditions(itemPredicate, integer, integer2);
	}

	public void method_8960(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack, int i) {
		ItemDurabilityChangedCriterion.Handler handler = (ItemDurabilityChangedCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.method_8965(itemStack, i);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final ItemPredicate field_9637;
		private final NumberRange.Integer durability;
		private final NumberRange.Integer delta;

		public Conditions(ItemPredicate itemPredicate, NumberRange.Integer integer, NumberRange.Integer integer2) {
			super(ItemDurabilityChangedCriterion.field_9633);
			this.field_9637 = itemPredicate;
			this.durability = integer;
			this.delta = integer2;
		}

		public static ItemDurabilityChangedCriterion.Conditions method_8967(ItemPredicate itemPredicate, NumberRange.Integer integer) {
			return new ItemDurabilityChangedCriterion.Conditions(itemPredicate, integer, NumberRange.Integer.ANY);
		}

		public boolean method_8968(ItemStack itemStack, int i) {
			if (!this.field_9637.test(itemStack)) {
				return false;
			} else {
				return !this.durability.test(itemStack.getDurability() - i) ? false : this.delta.test(itemStack.getDamage() - i);
			}
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("item", this.field_9637.serialize());
			jsonObject.add("durability", this.durability.serialize());
			jsonObject.add("delta", this.delta.serialize());
			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker field_9636;
		private final Set<Criterion.ConditionsContainer<ItemDurabilityChangedCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<ItemDurabilityChangedCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.field_9636 = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void method_8963(Criterion.ConditionsContainer<ItemDurabilityChangedCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void method_8966(Criterion.ConditionsContainer<ItemDurabilityChangedCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void method_8965(ItemStack itemStack, int i) {
			List<Criterion.ConditionsContainer<ItemDurabilityChangedCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<ItemDurabilityChangedCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.method_797().method_8968(itemStack, i)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<ItemDurabilityChangedCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<ItemDurabilityChangedCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.field_9636);
				}
			}
		}
	}
}
