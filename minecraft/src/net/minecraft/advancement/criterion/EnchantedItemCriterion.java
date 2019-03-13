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

public class EnchantedItemCriterion implements Criterion<EnchantedItemCriterion.Conditions> {
	private static final Identifier field_9563 = new Identifier("enchanted_item");
	private final Map<PlayerAdvancementTracker, EnchantedItemCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, EnchantedItemCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return field_9563;
	}

	@Override
	public void beginTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<EnchantedItemCriterion.Conditions> conditionsContainer
	) {
		EnchantedItemCriterion.Handler handler = (EnchantedItemCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler == null) {
			handler = new EnchantedItemCriterion.Handler(playerAdvancementTracker);
			this.handlers.put(playerAdvancementTracker, handler);
		}

		handler.method_8873(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<EnchantedItemCriterion.Conditions> conditionsContainer
	) {
		EnchantedItemCriterion.Handler handler = (EnchantedItemCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler != null) {
			handler.method_8876(conditionsContainer);
			if (handler.isEmpty()) {
				this.handlers.remove(playerAdvancementTracker);
			}
		}
	}

	@Override
	public void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
		this.handlers.remove(playerAdvancementTracker);
	}

	public EnchantedItemCriterion.Conditions method_8872(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		ItemPredicate itemPredicate = ItemPredicate.deserialize(jsonObject.get("item"));
		NumberRange.Integer integer = NumberRange.Integer.fromJson(jsonObject.get("levels"));
		return new EnchantedItemCriterion.Conditions(itemPredicate, integer);
	}

	public void method_8870(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack, int i) {
		EnchantedItemCriterion.Handler handler = (EnchantedItemCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.method_8875(itemStack, i);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final ItemPredicate field_9567;
		private final NumberRange.Integer levels;

		public Conditions(ItemPredicate itemPredicate, NumberRange.Integer integer) {
			super(EnchantedItemCriterion.field_9563);
			this.field_9567 = itemPredicate;
			this.levels = integer;
		}

		public static EnchantedItemCriterion.Conditions any() {
			return new EnchantedItemCriterion.Conditions(ItemPredicate.ANY, NumberRange.Integer.ANY);
		}

		public boolean method_8878(ItemStack itemStack, int i) {
			return !this.field_9567.test(itemStack) ? false : this.levels.test(i);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("item", this.field_9567.serialize());
			jsonObject.add("levels", this.levels.serialize());
			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker field_9566;
		private final Set<Criterion.ConditionsContainer<EnchantedItemCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<EnchantedItemCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.field_9566 = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void method_8873(Criterion.ConditionsContainer<EnchantedItemCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void method_8876(Criterion.ConditionsContainer<EnchantedItemCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void method_8875(ItemStack itemStack, int i) {
			List<Criterion.ConditionsContainer<EnchantedItemCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<EnchantedItemCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.method_797().method_8878(itemStack, i)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<EnchantedItemCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<EnchantedItemCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.field_9566);
				}
			}
		}
	}
}
