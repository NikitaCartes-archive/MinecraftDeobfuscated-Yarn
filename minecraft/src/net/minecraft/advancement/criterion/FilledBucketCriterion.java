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

public class FilledBucketCriterion implements Criterion<FilledBucketCriterion.Conditions> {
	private static final Identifier ID = new Identifier("filled_bucket");
	private final Map<PlayerAdvancementTracker, FilledBucketCriterion.Handler> field_9613 = Maps.<PlayerAdvancementTracker, FilledBucketCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void beginTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<FilledBucketCriterion.Conditions> conditionsContainer
	) {
		FilledBucketCriterion.Handler handler = (FilledBucketCriterion.Handler)this.field_9613.get(playerAdvancementTracker);
		if (handler == null) {
			handler = new FilledBucketCriterion.Handler(playerAdvancementTracker);
			this.field_9613.put(playerAdvancementTracker, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<FilledBucketCriterion.Conditions> conditionsContainer
	) {
		FilledBucketCriterion.Handler handler = (FilledBucketCriterion.Handler)this.field_9613.get(playerAdvancementTracker);
		if (handler != null) {
			handler.removeCondition(conditionsContainer);
			if (handler.isEmpty()) {
				this.field_9613.remove(playerAdvancementTracker);
			}
		}
	}

	@Override
	public void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
		this.field_9613.remove(playerAdvancementTracker);
	}

	public FilledBucketCriterion.Conditions method_8931(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		ItemPredicate itemPredicate = ItemPredicate.deserialize(jsonObject.get("item"));
		return new FilledBucketCriterion.Conditions(itemPredicate);
	}

	public void handle(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack) {
		FilledBucketCriterion.Handler handler = (FilledBucketCriterion.Handler)this.field_9613.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.handle(itemStack);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final ItemPredicate item;

		public Conditions(ItemPredicate itemPredicate) {
			super(FilledBucketCriterion.ID);
			this.item = itemPredicate;
		}

		public static FilledBucketCriterion.Conditions create(ItemPredicate itemPredicate) {
			return new FilledBucketCriterion.Conditions(itemPredicate);
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
		private final Set<Criterion.ConditionsContainer<FilledBucketCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<FilledBucketCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.manager = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void addCondition(Criterion.ConditionsContainer<FilledBucketCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void removeCondition(Criterion.ConditionsContainer<FilledBucketCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void handle(ItemStack itemStack) {
			List<Criterion.ConditionsContainer<FilledBucketCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<FilledBucketCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().matches(itemStack)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<FilledBucketCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<FilledBucketCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.manager);
				}
			}
		}
	}
}
