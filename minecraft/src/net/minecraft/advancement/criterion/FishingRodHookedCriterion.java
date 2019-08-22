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
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class FishingRodHookedCriterion implements Criterion<FishingRodHookedCriterion.Conditions> {
	private static final Identifier ID = new Identifier("fishing_rod_hooked");
	private final Map<PlayerAdvancementTracker, FishingRodHookedCriterion.Handler> handlers = Maps.<PlayerAdvancementTracker, FishingRodHookedCriterion.Handler>newHashMap();

	@Override
	public Identifier getId() {
		return ID;
	}

	@Override
	public void beginTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<FishingRodHookedCriterion.Conditions> conditionsContainer
	) {
		FishingRodHookedCriterion.Handler handler = (FishingRodHookedCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler == null) {
			handler = new FishingRodHookedCriterion.Handler(playerAdvancementTracker);
			this.handlers.put(playerAdvancementTracker, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(
		PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<FishingRodHookedCriterion.Conditions> conditionsContainer
	) {
		FishingRodHookedCriterion.Handler handler = (FishingRodHookedCriterion.Handler)this.handlers.get(playerAdvancementTracker);
		if (handler != null) {
			handler.removeCondition(conditionsContainer);
			if (handler.isEmpty()) {
				this.handlers.remove(playerAdvancementTracker);
			}
		}
	}

	@Override
	public void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
		this.handlers.remove(playerAdvancementTracker);
	}

	public FishingRodHookedCriterion.Conditions method_8941(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		ItemPredicate itemPredicate = ItemPredicate.deserialize(jsonObject.get("rod"));
		EntityPredicate entityPredicate = EntityPredicate.deserialize(jsonObject.get("entity"));
		ItemPredicate itemPredicate2 = ItemPredicate.deserialize(jsonObject.get("item"));
		return new FishingRodHookedCriterion.Conditions(itemPredicate, entityPredicate, itemPredicate2);
	}

	public void handle(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack, FishingBobberEntity fishingBobberEntity, Collection<ItemStack> collection) {
		FishingRodHookedCriterion.Handler handler = (FishingRodHookedCriterion.Handler)this.handlers.get(serverPlayerEntity.getAdvancementManager());
		if (handler != null) {
			handler.handle(serverPlayerEntity, itemStack, fishingBobberEntity, collection);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final ItemPredicate rod;
		private final EntityPredicate bobber;
		private final ItemPredicate item;

		public Conditions(ItemPredicate itemPredicate, EntityPredicate entityPredicate, ItemPredicate itemPredicate2) {
			super(FishingRodHookedCriterion.ID);
			this.rod = itemPredicate;
			this.bobber = entityPredicate;
			this.item = itemPredicate2;
		}

		public static FishingRodHookedCriterion.Conditions create(ItemPredicate itemPredicate, EntityPredicate entityPredicate, ItemPredicate itemPredicate2) {
			return new FishingRodHookedCriterion.Conditions(itemPredicate, entityPredicate, itemPredicate2);
		}

		public boolean matches(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack, FishingBobberEntity fishingBobberEntity, Collection<ItemStack> collection) {
			if (!this.rod.test(itemStack)) {
				return false;
			} else if (!this.bobber.test(serverPlayerEntity, fishingBobberEntity.hookedEntity)) {
				return false;
			} else {
				if (this.item != ItemPredicate.ANY) {
					boolean bl = false;
					if (fishingBobberEntity.hookedEntity instanceof ItemEntity) {
						ItemEntity itemEntity = (ItemEntity)fishingBobberEntity.hookedEntity;
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
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("rod", this.rod.serialize());
			jsonObject.add("entity", this.bobber.serialize());
			jsonObject.add("item", this.item.serialize());
			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker manager;
		private final Set<Criterion.ConditionsContainer<FishingRodHookedCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<FishingRodHookedCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
			this.manager = playerAdvancementTracker;
		}

		public boolean isEmpty() {
			return this.conditions.isEmpty();
		}

		public void addCondition(Criterion.ConditionsContainer<FishingRodHookedCriterion.Conditions> conditionsContainer) {
			this.conditions.add(conditionsContainer);
		}

		public void removeCondition(Criterion.ConditionsContainer<FishingRodHookedCriterion.Conditions> conditionsContainer) {
			this.conditions.remove(conditionsContainer);
		}

		public void handle(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack, FishingBobberEntity fishingBobberEntity, Collection<ItemStack> collection) {
			List<Criterion.ConditionsContainer<FishingRodHookedCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<FishingRodHookedCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().matches(serverPlayerEntity, itemStack, fishingBobberEntity, collection)) {
					if (list == null) {
						list = Lists.<Criterion.ConditionsContainer<FishingRodHookedCriterion.Conditions>>newArrayList();
					}

					list.add(conditionsContainer);
				}
			}

			if (list != null) {
				for (Criterion.ConditionsContainer<FishingRodHookedCriterion.Conditions> conditionsContainerx : list) {
					conditionsContainerx.apply(this.manager);
				}
			}
		}
	}
}
