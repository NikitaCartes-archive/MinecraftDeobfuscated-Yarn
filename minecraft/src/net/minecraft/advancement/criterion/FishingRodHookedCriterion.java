package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Collection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class FishingRodHookedCriterion extends AbstractCriterion<FishingRodHookedCriterion.Conditions> {
	static final Identifier ID = new Identifier("fishing_rod_hooked");

	@Override
	public Identifier getId() {
		return ID;
	}

	public FishingRodHookedCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, LootContextPredicate lootContextPredicate, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("rod"));
		LootContextPredicate lootContextPredicate2 = EntityPredicate.contextPredicateFromJson(jsonObject, "entity", advancementEntityPredicateDeserializer);
		ItemPredicate itemPredicate2 = ItemPredicate.fromJson(jsonObject.get("item"));
		return new FishingRodHookedCriterion.Conditions(lootContextPredicate, itemPredicate, lootContextPredicate2, itemPredicate2);
	}

	public void trigger(ServerPlayerEntity player, ItemStack rod, FishingBobberEntity bobber, Collection<ItemStack> fishingLoots) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(
			player, (Entity)(bobber.getHookedEntity() != null ? bobber.getHookedEntity() : bobber)
		);
		this.trigger(player, conditions -> conditions.matches(rod, lootContext, fishingLoots));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final ItemPredicate rod;
		private final LootContextPredicate hookedEntity;
		private final ItemPredicate caughtItem;

		public Conditions(LootContextPredicate player, ItemPredicate rod, LootContextPredicate hookedEntity, ItemPredicate caughtItem) {
			super(FishingRodHookedCriterion.ID, player);
			this.rod = rod;
			this.hookedEntity = hookedEntity;
			this.caughtItem = caughtItem;
		}

		public static FishingRodHookedCriterion.Conditions create(ItemPredicate rod, EntityPredicate bobber, ItemPredicate item) {
			return new FishingRodHookedCriterion.Conditions(LootContextPredicate.EMPTY, rod, EntityPredicate.asLootContextPredicate(bobber), item);
		}

		public boolean matches(ItemStack rod, LootContext hookedEntityContext, Collection<ItemStack> fishingLoots) {
			if (!this.rod.test(rod)) {
				return false;
			} else if (!this.hookedEntity.test(hookedEntityContext)) {
				return false;
			} else {
				if (this.caughtItem != ItemPredicate.ANY) {
					boolean bl = false;
					Entity entity = hookedEntityContext.get(LootContextParameters.THIS_ENTITY);
					if (entity instanceof ItemEntity itemEntity && this.caughtItem.test(itemEntity.getStack())) {
						bl = true;
					}

					for (ItemStack itemStack : fishingLoots) {
						if (this.caughtItem.test(itemStack)) {
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
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.add("rod", this.rod.toJson());
			jsonObject.add("entity", this.hookedEntity.toJson(predicateSerializer));
			jsonObject.add("item", this.caughtItem.toJson());
			return jsonObject;
		}
	}
}
