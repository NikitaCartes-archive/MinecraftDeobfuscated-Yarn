package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class BlockUsedCriterion extends AbstractCriterion<BlockUsedCriterion.Conditions> {
	private static final Identifier id = new Identifier("item_used_on_block");

	@Override
	public Identifier getId() {
		return id;
	}

	public BlockUsedCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		LocationPredicate locationPredicate = LocationPredicate.fromJson(jsonObject.get("location"));
		ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
		return new BlockUsedCriterion.Conditions(extended, locationPredicate, itemPredicate);
	}

	public void test(ServerPlayerEntity player, BlockPos pos, ItemStack stack) {
		BlockState blockState = player.getServerWorld().getBlockState(pos);
		this.test(player, conditions -> conditions.test(blockState, player.getServerWorld(), pos, stack));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final LocationPredicate field_24495;
		private final ItemPredicate item;

		public Conditions(EntityPredicate.Extended extended, LocationPredicate locationPredicate, ItemPredicate itemPredicate) {
			super(BlockUsedCriterion.id, extended);
			this.field_24495 = locationPredicate;
			this.item = itemPredicate;
		}

		public static BlockUsedCriterion.Conditions method_27981(LocationPredicate.Builder builder, ItemPredicate.Builder builder2) {
			return new BlockUsedCriterion.Conditions(EntityPredicate.Extended.EMPTY, builder.build(), builder2.build());
		}

		public boolean test(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack) {
			return !this.field_24495.test(world, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5) ? false : this.item.test(stack);
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.add("location", this.field_24495.toJson());
			jsonObject.add("item", this.item.toJson());
			return jsonObject;
		}
	}
}
