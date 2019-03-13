package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LecternBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WritableBookItem extends Item {
	public WritableBookItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult method_7884(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.method_8045();
		BlockPos blockPos = itemUsageContext.method_8037();
		BlockState blockState = world.method_8320(blockPos);
		if (blockState.getBlock() == Blocks.field_16330) {
			return LecternBlock.method_17472(world, blockPos, blockState, itemUsageContext.getItemStack()) ? ActionResult.field_5812 : ActionResult.PASS;
		} else {
			return ActionResult.PASS;
		}
	}

	@Override
	public TypedActionResult<ItemStack> method_7836(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.method_5998(hand);
		playerEntity.method_7315(itemStack, hand);
		playerEntity.method_7259(Stats.field_15372.getOrCreateStat(this));
		return new TypedActionResult<>(ActionResult.field_5812, itemStack);
	}

	public static boolean method_8047(@Nullable CompoundTag compoundTag) {
		if (compoundTag == null) {
			return false;
		} else if (!compoundTag.containsKey("pages", 9)) {
			return false;
		} else {
			ListTag listTag = compoundTag.method_10554("pages", 8);

			for (int i = 0; i < listTag.size(); i++) {
				String string = listTag.getString(i);
				if (string.length() > 32767) {
					return false;
				}
			}

			return true;
		}
	}
}
