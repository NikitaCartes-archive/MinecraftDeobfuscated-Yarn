package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ShotCrossbowCriterion extends AbstractCriterion<ShotCrossbowCriterion.Conditions> {
	static final Identifier ID = new Identifier("shot_crossbow");

	@Override
	public Identifier getId() {
		return ID;
	}

	public ShotCrossbowCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, LootContextPredicate lootContextPredicate, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
		return new ShotCrossbowCriterion.Conditions(lootContextPredicate, itemPredicate);
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack) {
		this.trigger(player, conditions -> conditions.matches(stack));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final ItemPredicate item;

		public Conditions(LootContextPredicate player, ItemPredicate item) {
			super(ShotCrossbowCriterion.ID, player);
			this.item = item;
		}

		public static ShotCrossbowCriterion.Conditions create(ItemPredicate itemPredicate) {
			return new ShotCrossbowCriterion.Conditions(LootContextPredicate.EMPTY, itemPredicate);
		}

		public static ShotCrossbowCriterion.Conditions create(ItemConvertible item) {
			return new ShotCrossbowCriterion.Conditions(LootContextPredicate.EMPTY, ItemPredicate.Builder.create().items(item).build());
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
