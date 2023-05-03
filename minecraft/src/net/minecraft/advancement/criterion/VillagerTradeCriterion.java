package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class VillagerTradeCriterion extends AbstractCriterion<VillagerTradeCriterion.Conditions> {
	static final Identifier ID = new Identifier("villager_trade");

	@Override
	public Identifier getId() {
		return ID;
	}

	public VillagerTradeCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, LootContextPredicate lootContextPredicate, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		LootContextPredicate lootContextPredicate2 = EntityPredicate.contextPredicateFromJson(jsonObject, "villager", advancementEntityPredicateDeserializer);
		ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
		return new VillagerTradeCriterion.Conditions(lootContextPredicate, lootContextPredicate2, itemPredicate);
	}

	public void trigger(ServerPlayerEntity player, MerchantEntity merchant, ItemStack stack) {
		LootContext lootContext = EntityPredicate.createAdvancementEntityLootContext(player, merchant);
		this.trigger(player, conditions -> conditions.matches(lootContext, stack));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final LootContextPredicate villager;
		private final ItemPredicate item;

		public Conditions(LootContextPredicate player, LootContextPredicate villager, ItemPredicate item) {
			super(VillagerTradeCriterion.ID, player);
			this.villager = villager;
			this.item = item;
		}

		public static VillagerTradeCriterion.Conditions any() {
			return new VillagerTradeCriterion.Conditions(LootContextPredicate.EMPTY, LootContextPredicate.EMPTY, ItemPredicate.ANY);
		}

		public static VillagerTradeCriterion.Conditions create(EntityPredicate.Builder playerPredicate) {
			return new VillagerTradeCriterion.Conditions(EntityPredicate.asLootContextPredicate(playerPredicate.build()), LootContextPredicate.EMPTY, ItemPredicate.ANY);
		}

		public boolean matches(LootContext merchantContext, ItemStack stack) {
			return !this.villager.test(merchantContext) ? false : this.item.test(stack);
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.add("item", this.item.toJson());
			jsonObject.add("villager", this.villager.toJson(predicateSerializer));
			return jsonObject;
		}
	}
}
