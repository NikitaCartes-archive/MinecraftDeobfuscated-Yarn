package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class BlockStatePropertyLootCondition implements LootCondition {
	private final Block block;
	private final StatePredicate properties;

	private BlockStatePropertyLootCondition(Block block, StatePredicate statePredicate) {
		this.block = block;
		this.properties = statePredicate;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootContextParameters.BLOCK_STATE);
	}

	public boolean method_899(LootContext lootContext) {
		BlockState blockState = lootContext.get(LootContextParameters.BLOCK_STATE);
		return blockState != null && this.block == blockState.getBlock() && this.properties.test(blockState);
	}

	public static BlockStatePropertyLootCondition.Builder builder(Block block) {
		return new BlockStatePropertyLootCondition.Builder(block);
	}

	public static class Builder implements LootCondition.Builder {
		private final Block block;
		private StatePredicate propertyValues = StatePredicate.ANY;

		public Builder(Block block) {
			this.block = block;
		}

		public BlockStatePropertyLootCondition.Builder method_22584(StatePredicate.Builder builder) {
			this.propertyValues = builder.build();
			return this;
		}

		@Override
		public LootCondition build() {
			return new BlockStatePropertyLootCondition(this.block, this.propertyValues);
		}
	}

	public static class Factory extends LootCondition.Factory<BlockStatePropertyLootCondition> {
		protected Factory() {
			super(new Identifier("block_state_property"), BlockStatePropertyLootCondition.class);
		}

		public void method_909(
			JsonObject jsonObject, BlockStatePropertyLootCondition blockStatePropertyLootCondition, JsonSerializationContext jsonSerializationContext
		) {
			jsonObject.addProperty("block", Registry.BLOCK.getId(blockStatePropertyLootCondition.block).toString());
			jsonObject.add("properties", blockStatePropertyLootCondition.properties.toJson());
		}

		public BlockStatePropertyLootCondition method_910(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "block"));
			Block block = (Block)Registry.BLOCK.getOrEmpty(identifier).orElseThrow(() -> new IllegalArgumentException("Can't find block " + identifier));
			StatePredicate statePredicate = StatePredicate.fromJson(jsonObject.get("properties"));
			statePredicate.check(block.getStateManager(), string -> {
				throw new JsonSyntaxException("Block " + block + " has no property " + string);
			});
			return new BlockStatePropertyLootCondition(block, statePredicate);
		}
	}
}
