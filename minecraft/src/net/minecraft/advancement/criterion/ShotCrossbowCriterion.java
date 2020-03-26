package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ShotCrossbowCriterion extends AbstractCriterion<ShotCrossbowCriterion.Conditions> {
	private static final Identifier ID = new Identifier("shot_crossbow");

	@Override
	public Identifier getId() {
		return ID;
	}

	public ShotCrossbowCriterion.Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
		return new ShotCrossbowCriterion.Conditions(itemPredicate);
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack) {
		this.test(player.getAdvancementTracker(), conditions -> conditions.matches(stack));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final ItemPredicate item;

		public Conditions(ItemPredicate item) {
			super(ShotCrossbowCriterion.ID);
			this.item = item;
		}

		public static ShotCrossbowCriterion.Conditions create(ItemConvertible item) {
			return new ShotCrossbowCriterion.Conditions(ItemPredicate.Builder.create().item(item).build());
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
