package net.minecraft.advancement.criterion;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ConsumeItemCriterion extends AbstractCriterion<ConsumeItemCriterion.Conditions> {
	static final Identifier ID = new Identifier("consume_item");

	@Override
	public Identifier getId() {
		return ID;
	}

	public ConsumeItemCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, LootContextPredicate lootContextPredicate, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		return new ConsumeItemCriterion.Conditions(lootContextPredicate, ItemPredicate.fromJson(jsonObject.get("item")));
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack) {
		this.trigger(player, conditions -> conditions.matches(stack));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final ItemPredicate item;

		public Conditions(LootContextPredicate player, ItemPredicate item) {
			super(ConsumeItemCriterion.ID, player);
			this.item = item;
		}

		public static ConsumeItemCriterion.Conditions any() {
			return new ConsumeItemCriterion.Conditions(LootContextPredicate.EMPTY, ItemPredicate.ANY);
		}

		public static ConsumeItemCriterion.Conditions predicate(ItemPredicate predicate) {
			return new ConsumeItemCriterion.Conditions(LootContextPredicate.EMPTY, predicate);
		}

		public static ConsumeItemCriterion.Conditions item(ItemConvertible item) {
			return new ConsumeItemCriterion.Conditions(
				LootContextPredicate.EMPTY,
				new ItemPredicate(
					null,
					ImmutableSet.of(item.asItem()),
					NumberRange.IntRange.ANY,
					NumberRange.IntRange.ANY,
					EnchantmentPredicate.ARRAY_OF_ANY,
					EnchantmentPredicate.ARRAY_OF_ANY,
					null,
					NbtPredicate.ANY
				)
			);
		}

		public boolean matches(ItemStack stack) {
			return this.item.test(stack);
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.add("item", this.item.toJson());
			return jsonObject;
		}
	}
}
