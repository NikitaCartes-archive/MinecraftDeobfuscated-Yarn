package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

public class VillagerTradeCriterion extends AbstractCriterion<VillagerTradeCriterion.Conditions> {
	public VillagerTradeCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		Optional<LootContextPredicate> optional2 = EntityPredicate.contextPredicateFromJson(jsonObject, "villager", advancementEntityPredicateDeserializer);
		Optional<ItemPredicate> optional3 = ItemPredicate.fromJson(jsonObject.get("item"));
		return new VillagerTradeCriterion.Conditions(optional, optional2, optional3);
	}

	public void trigger(ServerPlayerEntity player, MerchantEntity merchant, ItemStack stack) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, merchant);
		this.trigger(player, conditions -> conditions.matches(lootContext, stack));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Optional<LootContextPredicate> villager;
		private final Optional<ItemPredicate> item;

		public Conditions(Optional<LootContextPredicate> playerPredicate, Optional<LootContextPredicate> villager, Optional<ItemPredicate> item) {
			super(playerPredicate);
			this.villager = villager;
			this.item = item;
		}

		public static AdvancementCriterion<VillagerTradeCriterion.Conditions> any() {
			return Criteria.VILLAGER_TRADE.create(new VillagerTradeCriterion.Conditions(Optional.empty(), Optional.empty(), Optional.empty()));
		}

		public static AdvancementCriterion<VillagerTradeCriterion.Conditions> create(EntityPredicate.Builder playerPredicate) {
			return Criteria.VILLAGER_TRADE
				.create(
					new VillagerTradeCriterion.Conditions(
						Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(playerPredicate)), Optional.empty(), Optional.empty()
					)
				);
		}

		public boolean matches(LootContext villager, ItemStack stack) {
			return this.villager.isPresent() && !((LootContextPredicate)this.villager.get()).test(villager)
				? false
				: !this.item.isPresent() || ((ItemPredicate)this.item.get()).test(stack);
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
			this.item.ifPresent(itemPredicate -> jsonObject.add("item", itemPredicate.toJson()));
			this.villager.ifPresent(lootContextPredicate -> jsonObject.add("villager", lootContextPredicate.toJson()));
			return jsonObject;
		}
	}
}
