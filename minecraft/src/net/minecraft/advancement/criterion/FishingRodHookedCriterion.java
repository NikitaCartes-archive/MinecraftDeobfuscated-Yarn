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
	public void beginTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<FishingRodHookedCriterion.Conditions> conditionsContainer) {
		FishingRodHookedCriterion.Handler handler = (FishingRodHookedCriterion.Handler)this.handlers.get(manager);
		if (handler == null) {
			handler = new FishingRodHookedCriterion.Handler(manager);
			this.handlers.put(manager, handler);
		}

		handler.addCondition(conditionsContainer);
	}

	@Override
	public void endTrackingCondition(PlayerAdvancementTracker manager, Criterion.ConditionsContainer<FishingRodHookedCriterion.Conditions> conditionsContainer) {
		FishingRodHookedCriterion.Handler handler = (FishingRodHookedCriterion.Handler)this.handlers.get(manager);
		if (handler != null) {
			handler.removeCondition(conditionsContainer);
			if (handler.isEmpty()) {
				this.handlers.remove(manager);
			}
		}
	}

	@Override
	public void endTracking(PlayerAdvancementTracker tracker) {
		this.handlers.remove(tracker);
	}

	public FishingRodHookedCriterion.Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("rod"));
		EntityPredicate entityPredicate = EntityPredicate.fromJson(jsonObject.get("entity"));
		ItemPredicate itemPredicate2 = ItemPredicate.fromJson(jsonObject.get("item"));
		return new FishingRodHookedCriterion.Conditions(itemPredicate, entityPredicate, itemPredicate2);
	}

	public void trigger(ServerPlayerEntity player, ItemStack rodStack, FishingBobberEntity bobber, Collection<ItemStack> fishingLoots) {
		FishingRodHookedCriterion.Handler handler = (FishingRodHookedCriterion.Handler)this.handlers.get(player.getAdvancementTracker());
		if (handler != null) {
			handler.handle(player, rodStack, bobber, fishingLoots);
		}
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final ItemPredicate rod;
		private final EntityPredicate bobber;
		private final ItemPredicate item;

		public Conditions(ItemPredicate rod, EntityPredicate bobber, ItemPredicate item) {
			super(FishingRodHookedCriterion.ID);
			this.rod = rod;
			this.bobber = bobber;
			this.item = item;
		}

		public static FishingRodHookedCriterion.Conditions create(ItemPredicate rod, EntityPredicate bobber, ItemPredicate item) {
			return new FishingRodHookedCriterion.Conditions(rod, bobber, item);
		}

		public boolean matches(ServerPlayerEntity player, ItemStack rodStack, FishingBobberEntity bobber, Collection<ItemStack> collection) {
			if (!this.rod.test(rodStack)) {
				return false;
			} else if (!this.bobber.test(player, bobber.hookedEntity)) {
				return false;
			} else {
				if (this.item != ItemPredicate.ANY) {
					boolean bl = false;
					if (bobber.hookedEntity instanceof ItemEntity) {
						ItemEntity itemEntity = (ItemEntity)bobber.hookedEntity;
						if (this.item.test(itemEntity.getStack())) {
							bl = true;
						}
					}

					for (ItemStack itemStack : collection) {
						if (this.item.test(itemStack)) {
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
			jsonObject.add("rod", this.rod.toJson());
			jsonObject.add("entity", this.bobber.serialize());
			jsonObject.add("item", this.item.toJson());
			return jsonObject;
		}
	}

	static class Handler {
		private final PlayerAdvancementTracker manager;
		private final Set<Criterion.ConditionsContainer<FishingRodHookedCriterion.Conditions>> conditions = Sets.<Criterion.ConditionsContainer<FishingRodHookedCriterion.Conditions>>newHashSet();

		public Handler(PlayerAdvancementTracker manager) {
			this.manager = manager;
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

		public void handle(ServerPlayerEntity player, ItemStack rodStack, FishingBobberEntity entity, Collection<ItemStack> collection) {
			List<Criterion.ConditionsContainer<FishingRodHookedCriterion.Conditions>> list = null;

			for (Criterion.ConditionsContainer<FishingRodHookedCriterion.Conditions> conditionsContainer : this.conditions) {
				if (conditionsContainer.getConditions().matches(player, rodStack, entity, collection)) {
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
