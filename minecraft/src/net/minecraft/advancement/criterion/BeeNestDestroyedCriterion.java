package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class BeeNestDestroyedCriterion extends AbstractCriterion<BeeNestDestroyedCriterion.Conditions> {
	public BeeNestDestroyedCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		Block block = getBlock(jsonObject);
		Optional<ItemPredicate> optional2 = ItemPredicate.fromJson(jsonObject.get("item"));
		NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("num_bees_inside"));
		return new BeeNestDestroyedCriterion.Conditions(optional, block, optional2, intRange);
	}

	@Nullable
	private static Block getBlock(JsonObject root) {
		if (root.has("block")) {
			Identifier identifier = new Identifier(JsonHelper.getString(root, "block"));
			return (Block)Registries.BLOCK.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + identifier + "'"));
		} else {
			return null;
		}
	}

	public void trigger(ServerPlayerEntity player, BlockState state, ItemStack stack, int beeCount) {
		this.trigger(player, conditions -> conditions.test(state, stack, beeCount));
	}

	public static class Conditions extends AbstractCriterionConditions {
		@Nullable
		private final Block block;
		private final Optional<ItemPredicate> item;
		private final NumberRange.IntRange beeCount;

		public Conditions(Optional<LootContextPredicate> playerPredicate, @Nullable Block block, Optional<ItemPredicate> item, NumberRange.IntRange beeCount) {
			super(playerPredicate);
			this.block = block;
			this.item = item;
			this.beeCount = beeCount;
		}

		public static AdvancementCriterion<BeeNestDestroyedCriterion.Conditions> create(
			Block block, ItemPredicate.Builder itemPredicateBuilder, NumberRange.IntRange beeCountRange
		) {
			return Criteria.BEE_NEST_DESTROYED
				.create(new BeeNestDestroyedCriterion.Conditions(Optional.empty(), block, Optional.of(itemPredicateBuilder.build()), beeCountRange));
		}

		public boolean test(BlockState state, ItemStack stack, int count) {
			if (this.block != null && !state.isOf(this.block)) {
				return false;
			} else {
				return this.item.isPresent() && !((ItemPredicate)this.item.get()).test(stack) ? false : this.beeCount.test(count);
			}
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
			if (this.block != null) {
				jsonObject.addProperty("block", Registries.BLOCK.getId(this.block).toString());
			}

			this.item.ifPresent(item -> jsonObject.add("item", item.toJson()));
			jsonObject.add("num_bees_inside", this.beeCount.toJson());
			return jsonObject;
		}
	}
}
