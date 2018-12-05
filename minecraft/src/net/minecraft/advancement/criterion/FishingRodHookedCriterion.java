package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancement.ServerAdvancementManager;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.projectile.FishHookEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class FishingRodHookedCriterion implements Criterion<FishingRodHookedCriterion.Conditions> {
	private static final Identifier ID = new Identifier("fishing_rod_hooked");
	private final Map<ServerAdvancementManager, FishingRodHookedCriterion.class_2059> field_9618 = Maps.<ServerAdvancementManager, FishingRodHookedCriterion.class_2059>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void addCondition(
		ServerAdvancementManager serverAdvancementManager, Criterion.ConditionsContainer<FishingRodHookedCriterion.Conditions> conditionsContainer
	) {
		FishingRodHookedCriterion.class_2059 lv = (FishingRodHookedCriterion.class_2059)this.field_9618.get(serverAdvancementManager);
		if (lv == null) {
			lv = new FishingRodHookedCriterion.class_2059(serverAdvancementManager);
			this.field_9618.put(serverAdvancementManager, lv);
		}

		lv.method_8943(conditionsContainer);
	}

	@Override
	public void removeCondition(
		ServerAdvancementManager serverAdvancementManager, Criterion.ConditionsContainer<FishingRodHookedCriterion.Conditions> conditionsContainer
	) {
		FishingRodHookedCriterion.class_2059 lv = (FishingRodHookedCriterion.class_2059)this.field_9618.get(serverAdvancementManager);
		if (lv != null) {
			lv.method_8945(conditionsContainer);
			if (lv.method_8944()) {
				this.field_9618.remove(serverAdvancementManager);
			}
		}
	}

	@Override
	public void removePlayer(ServerAdvancementManager serverAdvancementManager) {
		this.field_9618.remove(serverAdvancementManager);
	}

	public FishingRodHookedCriterion.Conditions deserializeConditions(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		ItemPredicate itemPredicate = ItemPredicate.deserialize(jsonObject.get("rod"));
		EntityPredicate entityPredicate = EntityPredicate.deserialize(jsonObject.get("entity"));
		ItemPredicate itemPredicate2 = ItemPredicate.deserialize(jsonObject.get("item"));
		return new FishingRodHookedCriterion.Conditions(itemPredicate, entityPredicate, itemPredicate2);
	}

	public void method_8939(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack, FishHookEntity fishHookEntity, Collection<ItemStack> collection) {
		FishingRodHookedCriterion.class_2059 lv = (FishingRodHookedCriterion.class_2059)this.field_9618.get(serverPlayerEntity.getAdvancementManager());
		if (lv != null) {
			lv.method_8942(serverPlayerEntity, itemStack, fishHookEntity, collection);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final ItemPredicate rod;
		private final EntityPredicate entity;
		private final ItemPredicate item;

		public Conditions(ItemPredicate itemPredicate, EntityPredicate entityPredicate, ItemPredicate itemPredicate2) {
			super(FishingRodHookedCriterion.ID);
			this.rod = itemPredicate;
			this.entity = entityPredicate;
			this.item = itemPredicate2;
		}

		public static FishingRodHookedCriterion.Conditions create(ItemPredicate itemPredicate, EntityPredicate entityPredicate, ItemPredicate itemPredicate2) {
			return new FishingRodHookedCriterion.Conditions(itemPredicate, entityPredicate, itemPredicate2);
		}

		public boolean matches(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack, FishHookEntity fishHookEntity, Collection<ItemStack> collection) {
			if (!this.rod.test(itemStack)) {
				return false;
			} else if (!this.entity.test(serverPlayerEntity, fishHookEntity.hookedEntity)) {
				return false;
			} else {
				if (this.item != ItemPredicate.ANY) {
					boolean bl = false;
					if (fishHookEntity.hookedEntity instanceof ItemEntity) {
						ItemEntity itemEntity = (ItemEntity)fishHookEntity.hookedEntity;
						if (this.item.test(itemEntity.getStack())) {
							bl = true;
						}
					}

					for (ItemStack itemStack2 : collection) {
						if (this.item.test(itemStack2)) {
							bl = true;
							break;
						}
					}

					if (!bl) {
						return false;
					}
				}

				return true;
			}
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("rod", this.rod.serialize());
			jsonObject.add("entity", this.entity.serialize());
			jsonObject.add("item", this.item.serialize());
			return jsonObject;
		}
	}

	static class class_2059 {
		private final ServerAdvancementManager field_9620;
		private final Set<Criterion.ConditionsContainer<FishingRodHookedCriterion.Conditions>> field_9619 = Sets.<Criterion.ConditionsContainer<FishingRodHookedCriterion.Conditions>>newHashSet();

		public class_2059(ServerAdvancementManager serverAdvancementManager) {
			this.field_9620 = serverAdvancementManager;
		}

		public boolean method_8944() {
			return this.field_9619.isEmpty();
		}

		public void method_8943(Criterion.ConditionsContainer<FishingRodHookedCriterion.Conditions> conditionsContainer) {
			this.field_9619.add(conditionsContainer);
		}

		public void method_8945(Criterion.ConditionsContainer<FishingRodHookedCriterion.Conditions> conditionsContainer) {
			this.field_9619.remove(conditionsContainer);
		}

		public void method_8942(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack, FishHookEntity fishHookEntity, Collection<ItemStack> collection) {
			List<Criterion.ConditionsContainer<FishingRodHookedCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<FishingRodHookedCriterion.Conditions> conditionsContainer : this.field_9619) {
				if (conditionsContainer.getConditions().matches(serverPlayerEntity, itemStack, fishHookEntity, collection)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<FishingRodHookedCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<FishingRodHookedCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.field_9620);
				}
			}
		}
	}
}
