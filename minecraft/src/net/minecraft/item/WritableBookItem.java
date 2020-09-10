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
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.isOf(Blocks.LECTERN)) {
			return LecternBlock.putBookIfAbsent(world, blockPos, blockState, context.getStack()) ? ActionResult.success(world.isClient) : ActionResult.PASS;
		} else {
			return ActionResult.PASS;
		}
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		user.openEditBookScreen(itemStack, hand);
		user.incrementStat(Stats.USED.getOrCreateStat(this));
		return TypedActionResult.success(itemStack, world.isClient());
	}

	public static boolean isValid(@Nullable CompoundTag tag) {
		if (tag == null) {
			return false;
		} else if (!tag.contains("pages", 9)) {
			return false;
		} else {
			ListTag listTag = tag.getList("pages", 8);

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
