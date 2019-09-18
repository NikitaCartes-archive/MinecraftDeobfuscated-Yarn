package net.minecraft.world.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Set;
import net.minecraft.class_4559;
import net.minecraft.class_4570;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameter;
import net.minecraft.world.loot.context.LootContextParameters;

public class BlockStatePropertyLootCondition implements class_4570 {
	private final Block block;
	private final class_4559 properties;

	private BlockStatePropertyLootCondition(Block block, class_4559 arg) {
		this.block = block;
		this.properties = arg;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootContextParameters.BLOCK_STATE);
	}

	public boolean method_899(LootContext lootContext) {
		BlockState blockState = lootContext.get(LootContextParameters.BLOCK_STATE);
		return blockState != null && this.block == blockState.getBlock() && this.properties.method_22514(blockState);
	}

	public static BlockStatePropertyLootCondition.Builder builder(Block block) {
		return new BlockStatePropertyLootCondition.Builder(block);
	}

	public static class Builder implements class_4570.Builder {
		private final Block block;
		private class_4559 propertyValues = class_4559.field_20736;

		public Builder(Block block) {
			this.block = block;
		}

		public BlockStatePropertyLootCondition.Builder method_22584(class_4559.class_4560 arg) {
			this.propertyValues = arg.method_22528();
			return this;
		}

		@Override
		public class_4570 build() {
			return new BlockStatePropertyLootCondition(this.block, this.propertyValues);
		}
	}

	public static class Factory extends class_4570.Factory<BlockStatePropertyLootCondition> {
		protected Factory() {
			super(new Identifier("block_state_property"), BlockStatePropertyLootCondition.class);
		}

		public void method_909(
			JsonObject jsonObject, BlockStatePropertyLootCondition blockStatePropertyLootCondition, JsonSerializationContext jsonSerializationContext
		) {
			jsonObject.addProperty("block", Registry.BLOCK.getId(blockStatePropertyLootCondition.block).toString());
			jsonObject.add("properties", blockStatePropertyLootCondition.properties.method_22513());
		}

		public BlockStatePropertyLootCondition method_910(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "block"));
			Block block = (Block)Registry.BLOCK.getOrEmpty(identifier).orElseThrow(() -> new IllegalArgumentException("Can't find block " + identifier));
			class_4559 lv = class_4559.method_22519(jsonObject.get("properties"));
			lv.method_22516(block.getStateFactory(), string -> {
				throw new JsonSyntaxException("Block " + block + " has no property " + string);
			});
			return new BlockStatePropertyLootCondition(block, lv);
		}
	}
}
