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

	public PlacedBlockCriterion.Conditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		Block block = getBlock(jsonObject);
		StatePredicate statePredicate = StatePredicate.fromJson(jsonObject.get("state"));
		if (block != null) {
			statePredicate.check(block.getStateManager(), name -> {
				throw new JsonSyntaxException("Block " + block + " has no property " + name + ":");
			});
		}

		LocationPredicate locationPredicate = LocationPredicate.fromJson(jsonObject.get("location"));
		ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
		return new PlacedBlockCriterion.Conditions(block, statePredicate, locationPredicate, itemPredicate);
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

	public void trigger(ServerPlayerEntity player, BlockPos blockPos, ItemStack stack) {
		BlockState blockState = player.getServerWorld().getBlockState(blockPos);
		this.test(player.getAdvancementTracker(), conditions -> conditions.matches(blockState, blockPos, player.getServerWorld(), stack));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final Block block;
		private final StatePredicate state;
		private final LocationPredicate location;
		private final ItemPredicate item;

		public Conditions(@Nullable Block block, StatePredicate state, LocationPredicate location, ItemPredicate item) {
			super(PlacedBlockCriterion.ID);
			this.block = block;
			this.state = state;
			this.location = location;
			this.item = item;
		}

		public static PlacedBlockCriterion.Conditions block(Block block) {
			return new PlacedBlockCriterion.Conditions(block, StatePredicate.ANY, LocationPredicate.ANY, ItemPredicate.ANY);
		}

		public boolean matches(BlockState state, BlockPos pos, ServerWorld world, ItemStack stack) {
			if (this.block != null && state.getBlock() != this.block) {
				return false;
			} else if (!this.state.test(state)) {
				return false;
			} else {
				return !this.location.test(world, (float)pos.getX(), (float)pos.getY(), (float)pos.getZ()) ? false : this.item.test(stack);
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
			jsonObject.add("item", this.item.toJson());
			return jsonObject;
		}
	}
}
