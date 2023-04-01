package net.minecraft.item;

import net.minecraft.class_8293;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.CandleCakeBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class FlintAndSteelItem extends Item {
	public FlintAndSteelItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		PlayerEntity playerEntity = context.getPlayer();
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		if (class_8293.field_43626.method_50116()) {
			if (!world.isClient) {
				if (playerEntity != null) {
					context.getStack().damage(1, playerEntity, p -> p.sendToolBreakStatus(context.getHand()));
				}

				TntBlock.primeTnt(world, blockPos, playerEntity, blockState);
				world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
			}

			return ActionResult.success(world.isClient());
		} else if (!CampfireBlock.canBeLit(blockState) && !CandleBlock.canBeLit(blockState) && !CandleCakeBlock.canBeLit(blockState)) {
			BlockPos blockPos2 = blockPos.offset(context.getSide());
			if (AbstractFireBlock.canPlaceAt(world, blockPos2, context.getHorizontalPlayerFacing())) {
				world.playSound(playerEntity, blockPos2, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
				BlockState blockState2 = AbstractFireBlock.getState(world, blockPos2);
				world.setBlockState(blockPos2, blockState2, Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
				world.emitGameEvent(playerEntity, GameEvent.BLOCK_PLACE, blockPos);
				ItemStack itemStack = context.getStack();
				if (playerEntity instanceof ServerPlayerEntity) {
					Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity)playerEntity, blockPos2, itemStack);
					itemStack.damage(1, playerEntity, user -> user.sendToolBreakStatus(context.getHand()));
				}

				return ActionResult.success(world.isClient());
			} else {
				return ActionResult.FAIL;
			}
		} else {
			world.playSound(playerEntity, blockPos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
			world.setBlockState(blockPos, blockState.with(Properties.LIT, Boolean.valueOf(true)), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
			world.emitGameEvent(playerEntity, GameEvent.BLOCK_CHANGE, blockPos);
			if (playerEntity != null) {
				context.getStack().damage(1, playerEntity, user -> user.sendToolBreakStatus(context.getHand()));
			}

			return ActionResult.success(world.isClient());
		}
	}
}
