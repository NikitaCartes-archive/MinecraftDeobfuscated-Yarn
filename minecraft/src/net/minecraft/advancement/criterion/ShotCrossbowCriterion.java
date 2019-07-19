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
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ShotCrossbowCriterion implements Criterion<ShotCrossbowCriterion.Conditions> {
	private static final Identifier ID = new Identifier("shot_crossbow");
	private final Map<PlayerAdvancementTracker, ShotCrossbowCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, ShotCrossbowCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void beginTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<ShotCrossbowCriterion.Conditions> conditionsContainer) {
		ShotCrossbowCriterion.Handler handler = (ShotCrossbowCriterion.Handler)this.handlers.get(manager);
		if (handler == null) {
			handler = new ShotCrossbowCriterion.Handler(manager);
			this.handlers.put(manager, handler);
		}

		handler.add(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<ShotCrossbowCriterion.Conditions> conditionsContainer) {
		ShotCrossbowCriterion.Handler handler = (ShotCrossbowCriterion.Handler)this.handlers.get(manager);
		if (handler != null) {
			handler.remove(conditionsContainer);
			if (handler.isEmpty()) {
				this.handlers.remove(manager);
			}
		}
	}

	@Override
	public void endTracking(PlayerAdvancementTracker tracker) {
		this.handlers.remove(tracker);
	}

	public ShotCrossbowCriterion.Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
		return new ShotCrossbowCriterion.Conditions(itemPredicate);
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack) {
		ShotCrossbowCriterion.Handler handler = (ShotCrossbowCriterion.Handler)this.handlers.get(player.getAdvancementTracker());
		if (handler != null) {
			handler.trigger(stack);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final ItemPredicate item;

		public Conditions(ItemPredicate itemPredicate) {
			super(ShotCrossbowCriterion.ID);
			this.item = itemPredicate;
		}

		public static ShotCrossbowCriterion.Conditions create(ItemConvertible itemConvertible) {
			return new ShotCrossbowCriterion.Conditions(ItemPredicate.Builder.create().item(itemConvertible).build());
		}

		public boolean matches(ItemStack itemStack) {
			return this.item.test(itemStack);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("item", this.item.toJson());
			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker tracker;
		private final Set<Criterion.ConditionsContainer<ShotCrossbowCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<ShotCrossbowCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.tracker = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void add(Criterion.ConditionsContainer<ShotCrossbowCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void remove(Criterion.ConditionsContainer<ShotCrossbowCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void trigger(ItemStack itemStack) {
			List<Criterion.ConditionsContainer<ShotCrossbowCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<ShotCrossbowCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().matches(itemStack)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<ShotCrossbowCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<ShotCrossbowCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.tracker);
				}
			}
		}
	}
}
