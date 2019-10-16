package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class PlacedBlockCriterion extends AbstractCriterion<PlacedBlockCriterion.Conditions> {
	private static final Identifier ID = new Identifier("placed_block");

	@Override
	public Identifier getId() {
		return ID;
	}

	public PlacedBlockCriterion.Conditions method_9088(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		Block block = getBlock(jsonObject);
		StatePredicate statePredicate = StatePredicate.fromJson(jsonObject.get("state"));
		if (block != null) {
			statePredicate.check(block.getStateFactory(), string -> {
				throw new JsonSyntaxException("Block " + block + " has no property " + string + ":");
			});
		}

		LocationPredicate locationPredicate = LocationPredicate.fromJson(jsonObject.get("location"));
		ItemPredicate itemPredicate = ItemPredicate.deserialize(jsonObject.get("item"));
		return new PlacedBlockCriterion.Conditions(block, statePredicate, locationPredicate, itemPredicate);
	}

	@Nullable
	private static Block getBlock(JsonObject jsonObject) {
		if (jsonObject.has("block")) {
			Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "block"));
			return (Block)Registry.BLOCK.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + identifier + "'"));
		} else {
			return null;
		}
	}

	public void trigger(ServerPlayerEntity serverPlayerEntity, BlockPos blockPos, ItemStack itemStack) {
		BlockState blockState = serverPlayerEntity.getServerWorld().getBlockState(blockPos);
		this.test(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.matches(blockState, blockPos, serverPlayerEntity.getServerWorld(), itemStack));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Block block;
		private final StatePredicate state;
		private final LocationPredicate location;
		private final ItemPredicate item;

		public Conditions(@Nullable Block block, StatePredicate statePredicate, LocationPredicate locationPredicate, ItemPredicate itemPredicate) {
			super(PlacedBlockCriterion.ID);
			this.block = block;
			this.state = statePredicate;
			this.location = locationPredicate;
			this.item = itemPredicate;
		}

		public static PlacedBlockCriterion.Conditions block(Block block) {
			return new PlacedBlockCriterion.Conditions(block, StatePredicate.ANY, LocationPredicate.ANY, ItemPredicate.ANY);
		}

		public boolean matches(BlockState blockState, BlockPos blockPos, ServerWorld serverWorld, ItemStack itemStack) {
			if (this.block != null && blockState.getBlock() != this.block) {
				return false;
			} else if (!this.state.test(blockState)) {
				return false;
			} else {
				return !this.location.test(serverWorld, (float)blockPos.getX(), (float)blockPos.getY(), (float)blockPos.getZ()) ? false : this.item.test(itemStack);
			}
		}

		@Override
		public JsonElement toJson() {
			JsonObject jsonObject = new JsonObject();
			if (this.block != null) {
				jsonObject.addProperty("block", Registry.BLOCK.getId(this.block).toString());
			}

			jsonObject.add("state", this.state.toJson());
			jsonObject.add("location", this.location.toJson());
			jsonObject.add("item", this.item.serialize());
			return jsonObject;
		}
	}
}
