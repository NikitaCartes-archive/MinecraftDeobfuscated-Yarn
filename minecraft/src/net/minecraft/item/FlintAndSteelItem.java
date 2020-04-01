package net.minecraft.item;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class FlintAndSteelItem extends Item {
	public FlintAndSteelItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		PlayerEntity playerEntity = context.getPlayer();
		IWorld iWorld = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		BlockState blockState = iWorld.getBlockState(blockPos);
		if (isIgnitable(blockState)) {
			iWorld.playSound(playerEntity, blockPos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, RANDOM.nextFloat() * 0.4F + 0.8F);
			iWorld.setBlockState(blockPos, blockState.with(Properties.LIT, Boolean.valueOf(true)), 11);
			if (playerEntity != null) {
				context.getStack().damage(1, playerEntity, p -> p.sendToolBreakStatus(context.getHand()));
			}

			return ActionResult.SUCCESS;
		} else {
			BlockPos blockPos2 = blockPos.offset(context.getSide());
			if (canIgnite(iWorld.getBlockState(blockPos2), iWorld, blockPos2)) {
				iWorld.playSound(playerEntity, blockPos2, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, RANDOM.nextFloat() * 0.4F + 0.8F);
				BlockState blockState2 = AbstractFireBlock.getState(iWorld, blockPos2);
				iWorld.setBlockState(blockPos2, blockState2, 11);
				ItemStack itemStack = context.getStack();
				if (playerEntity instanceof ServerPlayerEntity) {
					Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity)playerEntity, blockPos2, itemStack);
					itemStack.damage(1, playerEntity, p -> p.sendToolBreakStatus(context.getHand()));
				}

				return ActionResult.SUCCESS;
			} else {
				return ActionResult.FAIL;
			}
		}
	}

	public static boolean isIgnitable(BlockState state) {
		return state.getBlock() == Blocks.CAMPFIRE && !(Boolean)state.get(Properties.WATERLOGGED) && !(Boolean)state.get(Properties.LIT);
	}

	public static boolean canIgnite(BlockState block, IWorld world, BlockPos pos) {
		BlockState blockState = AbstractFireBlock.getState(world, pos);
		boolean bl = false;

		for (Direction direction : Direction.Type.HORIZONTAL) {
			if (world.getBlockState(pos.offset(direction)).getBlock() == Blocks.OBSIDIAN && NetherPortalBlock.createAreaHelper(world, pos, Blocks.NETHER_PORTAL) != null
				)
			 {
				bl = true;
			}
		}

		return block.isAir() && (blockState.canPlaceAt(world, pos) || bl);
	}
}
