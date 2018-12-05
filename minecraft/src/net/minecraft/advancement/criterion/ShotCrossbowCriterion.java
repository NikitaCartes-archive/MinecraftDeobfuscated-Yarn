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
import net.minecraft.item.ItemContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ShotCrossbowCriterion implements Criterion<ShotCrossbowCriterion.Conditions> {
	private static final Identifier ID = new Identifier("shot_crossbow");
	private final Map<ServerAdvancementManager, ShotCrossbowCriterion.class_2124> field_9744 = Maps.<ServerAdvancementManager, ShotCrossbowCriterion.class_2124>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void addCondition(
		ServerAdvancementManager serverAdvancementManager, Criterion.ConditionsContainer<ShotCrossbowCriterion.Conditions> conditionsContainer
	) {
		ShotCrossbowCriterion.class_2124 lv = (ShotCrossbowCriterion.class_2124)this.field_9744.get(serverAdvancementManager);
		if (lv == null) {
			lv = new ShotCrossbowCriterion.class_2124(serverAdvancementManager);
			this.field_9744.put(serverAdvancementManager, lv);
		}

		lv.method_9116(conditionsContainer);
	}

	@Override
	public void removeCondition(
		ServerAdvancementManager serverAdvancementManager, Criterion.ConditionsContainer<ShotCrossbowCriterion.Conditions> conditionsContainer
	) {
		ShotCrossbowCriterion.class_2124 lv = (ShotCrossbowCriterion.class_2124)this.field_9744.get(serverAdvancementManager);
		if (lv != null) {
			lv.method_9119(conditionsContainer);
			if (lv.method_9117()) {
				this.field_9744.remove(serverAdvancementManager);
			}
		}
	}

	@Override
	public void removePlayer(ServerAdvancementManager serverAdvancementManager) {
		this.field_9744.remove(serverAdvancementManager);
	}

	public ShotCrossbowCriterion.Conditions deserializeConditions(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		ItemPredicate itemPredicate = ItemPredicate.deserialize(jsonObject.get("item"));
		return new ShotCrossbowCriterion.Conditions(itemPredicate);
	}

	public void method_9115(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack) {
		ShotCrossbowCriterion.class_2124 lv = (ShotCrossbowCriterion.class_2124)this.field_9744.get(serverPlayerEntity.getAdvancementManager());
		if (lv != null) {
			lv.method_9118(itemStack);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final ItemPredicate item;

		public Conditions(ItemPredicate itemPredicate) {
			super(ShotCrossbowCriterion.ID);
			this.item = itemPredicate;
		}

		public static ShotCrossbowCriterion.Conditions create(ItemContainer itemContainer) {
			return new ShotCrossbowCriterion.Conditions(ItemPredicate.Builder.create().item(itemContainer).build());
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

	static class class_2124 {
		private final ServerAdvancementManager field_9746;
		private final Set<Criterion.ConditionsContainer<ShotCrossbowCriterion.Conditions>> field_9745 = Sets.<Criterion.ConditionsContainer<ShotCrossbowCriterion.Conditions>>newHashSet();

		public class_2124(ServerAdvancementManager serverAdvancementManager) {
			this.field_9746 = serverAdvancementManager;
		}

		public boolean method_9117() {
			return this.field_9745.isEmpty();
		}

		public void method_9116(Criterion.ConditionsContainer<ShotCrossbowCriterion.Conditions> conditionsContainer) {
			this.field_9745.add(conditionsContainer);
		}

		public void method_9119(Criterion.ConditionsContainer<ShotCrossbowCriterion.Conditions> conditionsContainer) {
			this.field_9745.remove(conditionsContainer);
		}

		public void method_9118(ItemStack itemStack) {
			List<Criterion.ConditionsContainer<ShotCrossbowCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<ShotCrossbowCriterion.Conditions> conditionsContainer : this.field_9745) {
				if (conditionsContainer.getConditions().matches(itemStack)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<ShotCrossbowCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<ShotCrossbowCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.field_9746);
				}
			}
		}
	}
}
