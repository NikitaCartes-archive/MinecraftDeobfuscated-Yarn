package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class FishingRodHookedCriterion extends AbstractCriterion<FishingRodHookedCriterion.Conditions> {
	public FishingRodHookedCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		Optional<ItemPredicate> optional2 = ItemPredicate.fromJson(jsonObject.get("rod"));
		Optional<LootContextPredicate> optional3 = EntityPredicate.contextPredicateFromJson(jsonObject, "entity", advancementEntityPredicateDeserializer);
		Optional<ItemPredicate> optional4 = ItemPredicate.fromJson(jsonObject.get("item"));
		return new FishingRodHookedCriterion.Conditions(optional, optional2, optional3, optional4);
	}

	public void trigger(ServerPlayerEntity player, ItemStack rod, FishingBobberEntity bobber, Collection<ItemStack> fishingLoots) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(
			player, (Entity)(bobber.getHookedEntity() != null ? bobber.getHookedEntity() : bobber)
		);
		this.trigger(player, conditions -> conditions.matches(rod, lootContext, fishingLoots));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Optional<ItemPredicate> rod;
		private final Optional<LootContextPredicate> hookedEntity;
		private final Optional<ItemPredicate> caughtItem;

		public Conditions(
			Optional<LootContextPredicate> playerPredicate, Optional<ItemPredicate> rod, Optional<LootContextPredicate> hookedEntity, Optional<ItemPredicate> caughtItem
		) {
			super(playerPredicate);
			this.rod = rod;
			this.hookedEntity = hookedEntity;
			this.caughtItem = caughtItem;
		}

		public static AdvancementCriterion<FishingRodHookedCriterion.Conditions> create(
			Optional<ItemPredicate> rod, Optional<EntityPredicate> hookedEntity, Optional<ItemPredicate> caughtItem
		) {
			return Criteria.FISHING_ROD_HOOKED
				.create(new FishingRodHookedCriterion.Conditions(Optional.empty(), rod, EntityPredicate.contextPredicateFromEntityPredicate(hookedEntity), caughtItem));
		}

		public boolean matches(ItemStack rodStack, LootContext hookedEntity, Collection<ItemStack> fishingLoots) {
			if (this.rod.isPresent() && !((ItemPredicate)this.rod.get()).test(rodStack)) {
				return false;
			} else if (this.hookedEntity.isPresent() && !((LootContextPredicate)this.hookedEntity.get()).test(hookedEntity)) {
				return false;
			} else {
				if (this.caughtItem.isPresent()) {
					boolean bl = false;
					Entity entity = hookedEntity.get(LootContextParameters.THIS_ENTITY);
					if (entity instanceof ItemEntity itemEntity && ((ItemPredicate)this.caughtItem.get()).test(itemEntity.getStack())) {
						bl = true;
					}

					for (ItemStack itemStack : fishingLoots) {
						if (((ItemPredicate)this.caughtItem.get()).test(itemStack)) {
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
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
			this.rod.ifPresent(rod -> jsonObject.add("rod", rod.toJson()));
			this.hookedEntity.ifPresent(hookedEntity -> jsonObject.add("entity", hookedEntity.toJson()));
			this.caughtItem.ifPresent(caughtItem -> jsonObject.add("item", caughtItem.toJson()));
			return jsonObject;
		}
	}
}
