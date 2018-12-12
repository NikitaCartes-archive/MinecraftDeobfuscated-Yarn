package net.minecraft.block;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PumpkinBlock extends GourdBlock {
	protected PumpkinBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public boolean activate(
		BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, Direction direction, float f, float g, float h
	) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.getItem() == Items.field_8868) {
			if (!world.isClient) {
				Direction direction2 = direction.getAxis() == Direction.Axis.Y ? playerEntity.getHorizontalFacing().getOpposite() : direction;
				world.playSound(null, blockPos, SoundEvents.field_14619, SoundCategory.field_15245, 1.0F, 1.0F);
				world.setBlockState(blockPos, Blocks.field_10147.getDefaultState().with(PumpkinCarvedBlock.field_10748, direction2), 11);
				ItemEntity itemEntity = new ItemEntity(
					world,
					(double)blockPos.getX() + 0.5 + (double)direction2.getOffsetX() * 0.65,
					(double)blockPos.getY() + 0.1,
					(double)blockPos.getZ() + 0.5 + (double)direction2.getOffsetZ() * 0.65,
					new ItemStack(Items.field_8706, 4)
				);
				itemEntity.velocityX = 0.05 * (double)direction2.getOffsetX() + world.random.nextDouble() * 0.02;
				itemEntity.velocityY = 0.05;
				itemEntity.velocityZ = 0.05 * (double)direction2.getOffsetZ() + world.random.nextDouble() * 0.02;
				world.spawnEntity(itemEntity);
				itemStack.applyDamage(1, playerEntity);
			}

			return true;
		} else {
			return super.activate(blockState, world, blockPos, playerEntity, hand, direction, f, g, h);
		}
	}

	@Override
	public StemBlock method_10679() {
		return (StemBlock)Blocks.field_9984;
	}

	@Override
	public StemAttachedBlock method_10680() {
		return (StemAttachedBlock)Blocks.field_10331;
	}
}
