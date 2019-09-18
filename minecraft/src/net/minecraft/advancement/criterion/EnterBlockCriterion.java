package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.class_4558;
import net.minecraft.class_4559;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class EnterBlockCriterion extends class_4558<EnterBlockCriterion.Conditions> {
	private static final Identifier ID = new Identifier("enter_block");

	@Override
	public Identifier getId() {
		return ID;
	}

	public EnterBlockCriterion.Conditions method_8883(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		Block block = method_22466(jsonObject);
		class_4559 lv = class_4559.method_22519(jsonObject.get("state"));
		if (block != null) {
			lv.method_22516(block.getStateFactory(), string -> {
				throw new JsonSyntaxException("Block " + block + " has no property " + string);
			});
		}

		return new EnterBlockCriterion.Conditions(block, lv);
	}

	@Nullable
	private static Block method_22466(JsonObject jsonObject) {
		if (jsonObject.has("block")) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "block"));
			return (Block)Registry.BLOCK.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + identifier + "'"));
		} else {
			return null;
		}
	}

	public void handle(ServerPlayerEntity serverPlayerEntity, BlockState blockState) {
		this.method_22510(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.matches(blockState));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Block block;
		private final class_4559 state;

		public Conditions(@Nullable Block block, class_4559 arg) {
			super(EnterBlockCriterion.ID);
			this.block = block;
			this.state = arg;
		}

		public static EnterBlockCriterion.Conditions block(Block block) {
			return new EnterBlockCriterion.Conditions(block, class_4559.field_20736);
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			if (this.block != null) {
				jsonObject.addProperty("block", Registry.BLOCK.getId(this.block).toString());
			}

			jsonObject.add("state", this.state.method_22513());
			return jsonObject;
		}

		public boolean matches(BlockState blockState) {
			return this.block != null && blockState.getBlock() != this.block ? false : this.state.method_22514(blockState);
		}
	}
}
