package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class BeeNestDestroyedCriterion extends AbstractCriterion<BeeNestDestroyedCriterion.Conditions> {
	private static final Identifier ID = new Identifier("bee_nest_destroyed");

	@Override
	public Identifier getId() {
		return ID;
	}

	public BeeNestDestroyedCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		Block block = getBlock(jsonObject);
		ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
		NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("num_bees_inside"));
		return new BeeNestDestroyedCriterion.Conditions(extended, block, itemPredicate, intRange);
	}

	@Nullable
	private static Block getBlock(JsonObject root) {
		if (root.has("block")) {
			Identifier identifier = new Identifier(JsonHelper.getString(root, "block"));
			return (Block)Registry.BLOCK.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + identifier + "'"));
		} else {
			return null;
		}
	}

	public void test(ServerPlayerEntity player, Block block, ItemStack stack, int beeCount) {
		this.test(player, conditions -> conditions.test(block, stack, beeCount));
	}

	public static class Conditions extends AbstractCriterionConditions {
		@Nullable
		private final Block block;
		private final ItemPredicate item;
		private final NumberRange.IntRange beeCount;

		public Conditions(EntityPredicate.Extended player, @Nullable Block block, ItemPredicate item, NumberRange.IntRange beeCount) {
			super(BeeNestDestroyedCriterion.ID, player);
			this.block = block;
			this.item = item;
			this.beeCount = beeCount;
		}

		public static BeeNestDestroyedCriterion.Conditions create(Block block, ItemPredicate.Builder itemPredicateBuilder, NumberRange.IntRange beeCountRange) {
			return new BeeNestDestroyedCriterion.Conditions(EntityPredicate.Extended.EMPTY, block, itemPredicateBuilder.build(), beeCountRange);
		}

		public boolean test(Block block, ItemStack stack, int count) {
			if (this.block != null && block != this.block) {
				return false;
			} else {
				return !this.item.test(stack) ? false : this.beeCount.test(count);
			}
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			if (this.block != null) {
				jsonObject.addProperty("block", Registry.BLOCK.getId(this.block).toString());
			}

			jsonObject.add("item", this.item.toJson());
			jsonObject.add("num_bees_inside", this.beeCount.toJson());
			return jsonObject;
		}
	}
}
