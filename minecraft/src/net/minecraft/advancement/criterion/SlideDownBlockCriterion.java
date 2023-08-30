package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class SlideDownBlockCriterion extends AbstractCriterion<SlideDownBlockCriterion.Conditions> {
	public SlideDownBlockCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, Optional<LootContextPredicate> optional, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		Block block = getBlock(jsonObject);
		Optional<StatePredicate> optional2 = StatePredicate.fromJson(jsonObject.get("state"));
		if (block != null) {
			optional2.ifPresent(statePredicate -> statePredicate.check(block.getStateManager(), key -> {
					throw new JsonSyntaxException("Block " + block + " has no property " + key);
				}));
		}

		return new SlideDownBlockCriterion.Conditions(optional, block, optional2);
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
		private final Optional<StatePredicate> state;

		public Conditions(Optional<LootContextPredicate> playerPredicate, @Nullable Block block, Optional<StatePredicate> state) {
			super(playerPredicate);
			this.block = block;
			this.state = state;
		}

		public static AdvancementCriterion<SlideDownBlockCriterion.Conditions> create(Block block) {
			return Criteria.SLIDE_DOWN_BLOCK.create(new SlideDownBlockCriterion.Conditions(Optional.empty(), block, Optional.empty()));
		}

		@Override
		public JsonObject toJson() {
			JsonObject jsonObject = super.toJson();
			if (this.block != null) {
				jsonObject.addProperty("block", Registries.BLOCK.getId(this.block).toString());
			}

			this.state.ifPresent(statePredicate -> jsonObject.add("state", statePredicate.toJson()));
			return jsonObject;
		}

		public boolean test(BlockState state) {
			return this.block != null && !state.isOf(this.block) ? false : !this.state.isPresent() || ((StatePredicate)this.state.get()).test(state);
		}
	}
}
