package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class EnchantedItemCriterion extends AbstractCriterion<EnchantedItemCriterion.Conditions> {
	private static final Identifier ID = new Identifier("enchanted_item");

	@Override
	public Identifier getId() {
		return ID;
	}

	public EnchantedItemCriterion.Conditions method_8872(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		ItemPredicate itemPredicate = ItemPredicate.deserialize(jsonObject.get("item"));
		NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("levels"));
		return new EnchantedItemCriterion.Conditions(itemPredicate, intRange);
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack, int levels) {
		this.test(player.getAdvancementManager(), conditions -> conditions.matches(stack, levels));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final ItemPredicate item;
		private final NumberRange.IntRange levels;

		public Conditions(ItemPredicate item, NumberRange.IntRange intRange) {
			super(EnchantedItemCriterion.ID);
			this.item = item;
			this.levels = intRange;
		}

		public static EnchantedItemCriterion.Conditions any() {
			return new EnchantedItemCriterion.Conditions(ItemPredicate.ANY, NumberRange.IntRange.ANY);
		}

		public boolean matches(ItemStack stack, int levels) {
			return !this.item.test(stack) ? false : this.levels.test(levels);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("item", this.item.serialize());
			jsonObject.add("levels", this.levels.toJson());
			return jsonObject;
		}
	}
}
