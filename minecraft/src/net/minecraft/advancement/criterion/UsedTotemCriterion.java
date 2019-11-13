package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class UsedTotemCriterion extends AbstractCriterion<UsedTotemCriterion.Conditions> {
	private static final Identifier ID = new Identifier("used_totem");

	@Override
	public Identifier getId() {
		return ID;
	}

	public UsedTotemCriterion.Conditions method_9163(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
		return new UsedTotemCriterion.Conditions(itemPredicate);
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack) {
		this.test(player.getAdvancementManager(), conditions -> conditions.matches(stack));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final ItemPredicate item;

		public Conditions(ItemPredicate item) {
			super(UsedTotemCriterion.ID);
			this.item = item;
		}

		public static UsedTotemCriterion.Conditions create(ItemConvertible itemConvertible) {
			return new UsedTotemCriterion.Conditions(ItemPredicate.Builder.create().item(itemConvertible).build());
		}

		public boolean matches(ItemStack stack) {
			return this.item.test(stack);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("item", this.item.toJson());
			return jsonObject;
		}
	}
}
