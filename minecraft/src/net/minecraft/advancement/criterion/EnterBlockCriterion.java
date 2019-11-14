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

public class EnterBlockCriterion extends AbstractCriterion<EnterBlockCriterion.Conditions> {
	private static final Identifier ID = new Identifier("enter_block");

	@Override
	public Identifier getId() {
		return ID;
	}

	public EnterBlockCriterion.Conditions method_8883(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		Block block = getBlock(jsonObject);
		StatePredicate statePredicate = StatePredicate.fromJson(jsonObject.get("state"));
		if (block != null) {
			statePredicate.check(block.getStateManager(), name -> {
				throw new JsonSyntaxException("Block " + block + " has no property " + name);
			});
		}

		return new EnterBlockCriterion.Conditions(block, statePredicate);
	}

	@Nullable
	private static Block getBlock(JsonObject obj) {
		if (obj.has("block")) {
			Identifier identifier = new Identifier(JsonHelper.getString(obj, "block"));
			return (Block)Registry.BLOCK.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + identifier + "'"));
		} else {
			return null;
		}
	}

	public void trigger(ServerPlayerEntity player, BlockState state) {
		this.test(player.getAdvancementTracker(), conditions -> conditions.matches(state));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Block block;
		private final StatePredicate state;

		public Conditions(@Nullable Block block, StatePredicate state) {
			super(EnterBlockCriterion.ID);
			this.block = block;
			this.state = state;
		}

		public static EnterBlockCriterion.Conditions block(Block block) {
			return new EnterBlockCriterion.Conditions(block, StatePredicate.ANY);
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

		public boolean matches(BlockState state) {
			return this.block != null && state.getBlock() != this.block ? false : this.state.test(state);
		}
	}
}
