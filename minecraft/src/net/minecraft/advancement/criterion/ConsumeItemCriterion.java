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
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.NumberRange;

public class ConsumeItemCriterion implements Criterion<ConsumeItemCriterion.Conditions> {
	private static final Identifier field_9509 = new Identifier("consume_item");
	private final Map<PlayerAdvancementTracker, ConsumeItemCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, ConsumeItemCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return field_9509;
	}

	@Override
	public void beginTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<ConsumeItemCriterion.Conditions> conditionsContainer
	) {
		ConsumeItemCriterion.Handler handler = (ConsumeItemCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler == null) {
			handler = new ConsumeItemCriterion.Handler(playerAdvancementTracker);
			this.handlers.put(playerAdvancementTracker, handler);
		}

		handler.method_8822(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<ConsumeItemCriterion.Conditions> conditionsContainer
	) {
		ConsumeItemCriterion.Handler handler = (ConsumeItemCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler != null) {
			handler.method_8825(conditionsContainer);
			if (handler.isEmpty()) {
				this.handlers.remove(playerAdvancementTracker);
			}
		}
	}

	@Override
	public void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
		this.handlers.remove(playerAdvancementTracker);
	}

	public ConsumeItemCriterion.Conditions method_8820(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		return new ConsumeItemCriterion.Conditions(ItemPredicate.deserialize(jsonObject.get("item")));
	}

	public void method_8821(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack) {
		ConsumeItemCriterion.Handler handler = (ConsumeItemCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.method_8824(itemStack);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final ItemPredicate field_9513;

		public Conditions(ItemPredicate itemPredicate) {
			super(ConsumeItemCriterion.field_9509);
			this.field_9513 = itemPredicate;
		}

		public static ConsumeItemCriterion.Conditions any() {
			return new ConsumeItemCriterion.Conditions(ItemPredicate.ANY);
		}

		public static ConsumeItemCriterion.Conditions method_8828(ItemProvider itemProvider) {
			return new ConsumeItemCriterion.Conditions(
				new ItemPredicate(null, itemProvider.getItem(), NumberRange.Integer.ANY, NumberRange.Integer.ANY, new EnchantmentPredicate[0], null, NbtPredicate.ANY)
			);
		}

		public boolean method_8826(ItemStack itemStack) {
			return this.field_9513.test(itemStack);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("item", this.field_9513.serialize());
			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker field_9512;
		private final Set<Criterion.ConditionsContainer<ConsumeItemCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<ConsumeItemCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.field_9512 = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void method_8822(Criterion.ConditionsContainer<ConsumeItemCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void method_8825(Criterion.ConditionsContainer<ConsumeItemCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void method_8824(ItemStack itemStack) {
			List<Criterion.ConditionsContainer<ConsumeItemCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<ConsumeItemCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.method_797().method_8826(itemStack)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<ConsumeItemCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<ConsumeItemCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.field_9512);
				}
			}
		}
	}
}
