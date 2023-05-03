package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class SlideDownBlockCriterion extends AbstractCriterion<SlideDownBlockCriterion.Conditions> {
	static final Identifier ID = new Identifier("slide_down_block");

	@Override
	public Identifier getId() {
		return ID;
	}

	public SlideDownBlockCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, LootContextPredicate lootContextPredicate, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		Block block = getBlock(jsonObject);
		StatePredicate statePredicate = StatePredicate.fromJson(jsonObject.get("state"));
		if (block != null) {
			statePredicate.check(block.getStateManager(), key -> {
				throw new JsonSyntaxException("Block " + block + " has no property " + key);
			});
		}

		return new SlideDownBlockCriterion.Conditions(lootContextPredicate, block, statePredicate);
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

	public void trigger(ServerPlayerEntity player, BlockState state) {
		this.trigger(player, conditions -> conditions.test(state));
	}

	public static class Conditions extends AbstractCriterionConditions {
		@Nullable
		private final Block block;
		private final StatePredicate state;

		public Conditions(LootContextPredicate player, @Nullable Block block, StatePredicate state) {
			super(SlideDownBlockCriterion.ID, player);
			this.block = block;
			this.state = state;
		}

		public static SlideDownBlockCriterion.Conditions create(Block block) {
			return new SlideDownBlockCriterion.Conditions(LootContextPredicate.EMPTY, block, StatePredicate.ANY);
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			if (this.block != null) {
				jsonObject.addProperty("block", Registries.BLOCK.getId(this.block).toString());
			}

			jsonObject.add("state", this.state.toJson());
			return jsonObject;
		}

		public boolean test(BlockState state) {
			return this.block != null && !state.isOf(this.block) ? false : this.state.test(state);
		}
	}
}
