package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class SlideDownBlockCriterion extends AbstractCriterion<SlideDownBlockCriterion.Conditions> {
	private static final Identifier ID = new Identifier("slide_down_block");

	@Override
	public Identifier getId() {
		return ID;
	}

	public SlideDownBlockCriterion.Conditions method_23911(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		Block block = getBlock(jsonObject);
		StatePredicate statePredicate = StatePredicate.fromJson(jsonObject.get("state"));
		if (block != null) {
			statePredicate.check(block.getStateManager(), key -> {
				throw new JsonSyntaxException("Block " + block + " has no property " + key);
			});
		}

		return new SlideDownBlockCriterion.Conditions(block, statePredicate);
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

	public void test(ServerPlayerEntity player, BlockState state) {
		this.test(player.getAdvancementTracker(), conditions -> conditions.test(state));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Block block;
		private final StatePredicate state;

		public Conditions(@Nullable Block block, StatePredicate state) {
			super(SlideDownBlockCriterion.ID);
			this.block = block;
			this.state = state;
		}

		public static SlideDownBlockCriterion.Conditions create(Block block) {
			return new SlideDownBlockCriterion.Conditions(block, StatePredicate.ANY);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			if (this.block != null) {
				jsonObject.addProperty("block", Registry.BLOCK.getId(this.block).toString());
			}

			jsonObject.add("state", this.state.toJson());
			return jsonObject;
		}

		public boolean test(BlockState state) {
			return this.block != null && state.getBlock() != this.block ? false : this.state.test(state);
		}
	}
}
