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
	private static final Identifier field_9612 = new Identifier("filled_bucket");
	private final Map<PlayerAdvancementTracker, FilledBucketCriterion.class_2055> field_9613 = Maps.<PlayerAdvancementTracker, FilledBucketCriterion.class_2055>newHashMap();

	@Override
	public Identifier getId() {
		return field_9612;
	}

	@Override
	public void beginTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<FilledBucketCriterion.Conditions> conditionsContainer
	) {
		FilledBucketCriterion.class_2055 lv = (FilledBucketCriterion.class_2055)this.field_9613.get(playerAdvancementTracker);
		if (lv == null) {
			lv = new FilledBucketCriterion.class_2055(playerAdvancementTracker);
			this.field_9613.put(playerAdvancementTracker, lv);
		}

		lv.method_8933(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<FilledBucketCriterion.Conditions> conditionsContainer
	) {
		FilledBucketCriterion.class_2055 lv = (FilledBucketCriterion.class_2055)this.field_9613.get(playerAdvancementTracker);
		if (lv != null) {
			lv.method_8936(conditionsContainer);
			if (lv.method_8934()) {
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

	public void method_8932(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack) {
		FilledBucketCriterion.class_2055 lv = (FilledBucketCriterion.class_2055)this.field_9613.get(serverPlayerEntity.getAdvancementManager());
		if (lv != null) {
			lv.method_8935(itemStack);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final ItemPredicate field_9616;

		public Conditions(ItemPredicate itemPredicate) {
			super(FilledBucketCriterion.field_9612);
			this.field_9616 = itemPredicate;
		}

		public static FilledBucketCriterion.Conditions method_8937(ItemPredicate itemPredicate) {
			return new FilledBucketCriterion.Conditions(itemPredicate);
		}

		public boolean method_8938(ItemStack itemStack) {
			return this.field_9616.test(itemStack);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("item", this.field_9616.serialize());
			return jsonObject;
		}
	}

	static class class_2055 {
		private final PlayerAdvancementTracker field_9615;
		private final Set<Criterion.ConditionsContainer<FilledBucketCriterion.Conditions>> field_9614 = Sets.<Criterion.ConditionsContainer<FilledBucketCriterion.Conditions>>newHashSet();

		public class_2055(PlayerAdvancementTracker playerAdvancementTracker) {
			this.field_9615 = playerAdvancementTracker;
		}

		public boolean method_8934() {
			return this.field_9614.isEmpty();
		}

		public void method_8933(Criterion.ConditionsContainer<FilledBucketCriterion.Conditions> conditionsContainer) {
			this.field_9614.add(conditionsContainer);
		}

		public void method_8936(Criterion.ConditionsContainer<FilledBucketCriterion.Conditions> conditionsContainer) {
			this.field_9614.remove(conditionsContainer);
		}

		public void method_8935(ItemStack itemStack) {
			List<Criterion.ConditionsContainer<FilledBucketCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<FilledBucketCriterion.Conditions> conditionsContainer : this.field_9614) {
				if (conditionsContainer.method_797().method_8938(itemStack)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<FilledBucketCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<FilledBucketCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.field_9615);
				}
			}
		}
	}
}
