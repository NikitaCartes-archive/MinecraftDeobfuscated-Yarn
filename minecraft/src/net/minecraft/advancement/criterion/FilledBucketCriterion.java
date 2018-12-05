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
import net.minecraft.advancement.ServerAdvancementManager;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class FilledBucketCriterion implements Criterion<FilledBucketCriterion.Conditions> {
	private static final Identifier ID = new Identifier("filled_bucket");
	private final Map<ServerAdvancementManager, FilledBucketCriterion.class_2055> field_9613 = Maps.<ServerAdvancementManager, FilledBucketCriterion.class_2055>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void addCondition(
		ServerAdvancementManager serverAdvancementManager, Criterion.ConditionsContainer<FilledBucketCriterion.Conditions> conditionsContainer
	) {
		FilledBucketCriterion.class_2055 lv = (FilledBucketCriterion.class_2055)this.field_9613.get(serverAdvancementManager);
		if (lv == null) {
			lv = new FilledBucketCriterion.class_2055(serverAdvancementManager);
			this.field_9613.put(serverAdvancementManager, lv);
		}

		lv.method_8933(conditionsContainer);
	}

	@Override
	public void removeCondition(
		ServerAdvancementManager serverAdvancementManager, Criterion.ConditionsContainer<FilledBucketCriterion.Conditions> conditionsContainer
	) {
		FilledBucketCriterion.class_2055 lv = (FilledBucketCriterion.class_2055)this.field_9613.get(serverAdvancementManager);
		if (lv != null) {
			lv.method_8936(conditionsContainer);
			if (lv.method_8934()) {
				this.field_9613.remove(serverAdvancementManager);
			}
		}
	}

	@Override
	public void removePlayer(ServerAdvancementManager serverAdvancementManager) {
		this.field_9613.remove(serverAdvancementManager);
	}

	public FilledBucketCriterion.Conditions deserializeConditions(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
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
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("item", this.item.serialize());
			return jsonObject;
		}
	}

	static class class_2055 {
		private final ServerAdvancementManager field_9615;
		private final Set<Criterion.ConditionsContainer<FilledBucketCriterion.Conditions>> field_9614 = Sets.<Criterion.ConditionsContainer<FilledBucketCriterion.Conditions>>newHashSet();

		public class_2055(ServerAdvancementManager serverAdvancementManager) {
			this.field_9615 = serverAdvancementManager;
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
				if (conditionsContainer.getConditions().matches(itemStack)) {
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
