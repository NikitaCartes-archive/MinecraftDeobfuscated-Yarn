package net.minecraft.item;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class CorruptedPotatoPeelsItem extends Item {
	public CorruptedPotatoPeelsItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.isOf(Blocks.TERRE_DE_POMME) && context.getSide() == Direction.UP) {
			PlayerEntity playerEntity = context.getPlayer();
			context.getStack().decrement(1);
			world.playSound(
				playerEntity, blockPos, SoundEvents.BLOCK_CORRUPTED_PEELGRASS_BLOCK_PLACE, SoundCategory.BLOCKS, 1.0F, world.random.nextBetweenInclusive(0.9F, 1.1F)
			);
			world.setBlockState(blockPos, Blocks.CORRUPTED_PEELGRASS_BLOCK.getDefaultState());
			if (playerEntity instanceof ServerPlayerEntity serverPlayerEntity && playerEntity.getWorld().getRegistryKey().equals(World.OVERWORLD)) {
				Criteria.BRING_HOME_CORRUPTION.trigger(serverPlayerEntity);
			}

			return ActionResult.success(world.isClient);
		} else {
			return super.useOnBlock(context);
		}
	}
}
