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

public class ItemUsedOnBlockCriterion extends AbstractCriterion<ItemUsedOnBlockCriterion.Conditions> {
	private static final Identifier ID = new Identifier("item_used_on_block");

	@Override
	public Identifier getId() {
		return ID;
	}

	public ItemUsedOnBlockCriterion.Conditions conditionsFromJson(
		JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer
	) {
		LocationPredicate locationPredicate = LocationPredicate.fromJson(jsonObject.get("location"));
		ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
		return new ItemUsedOnBlockCriterion.Conditions(extended, locationPredicate, itemPredicate);
	}

	public void test(ServerPlayerEntity player, BlockPos pos, ItemStack stack) {
		BlockState blockState = player.getServerWorld().getBlockState(pos);
		this.test(player, conditions -> conditions.test(blockState, player.getServerWorld(), pos, stack));
	}

	public static class Conditions extends AbstractCriterionConditions {
		private final LocationPredicate location;
		private final ItemPredicate item;

		public Conditions(EntityPredicate.Extended extended, LocationPredicate location, ItemPredicate item) {
			super(ItemUsedOnBlockCriterion.ID, extended);
			this.location = location;
			this.item = item;
		}

		public static ItemUsedOnBlockCriterion.Conditions create(LocationPredicate.Builder builder, ItemPredicate.Builder builder2) {
			return new ItemUsedOnBlockCriterion.Conditions(EntityPredicate.Extended.EMPTY, builder.build(), builder2.build());
		}

		public boolean test(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack) {
			return !this.location.test(world, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5) ? false : this.item.test(stack);
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject jsonObject = super.toJson(predicateSerializer);
			jsonObject.add("location", this.location.toJson());
			jsonObject.add("item", this.item.toJson());
			return jsonObject;
		}
	}
}
