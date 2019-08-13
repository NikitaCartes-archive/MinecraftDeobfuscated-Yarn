package net.minecraft.block;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PumpkinBlock extends GourdBlock {
	protected PumpkinBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public boolean activate(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.getItem() == Items.field_8868) {
			if (!world.isClient) {
				Direction direction = blockHitResult.getSide();
				Direction direction2 = direction.getAxis() == Direction.Axis.field_11052 ? playerEntity.getHorizontalFacing().getOpposite() : direction;
				world.playSound(null, blockPos, SoundEvents.field_14619, SoundCategory.field_15245, 1.0F, 1.0F);
				world.setBlockState(blockPos, Blocks.field_10147.getDefaultState().with(CarvedPumpkinBlock.FACING, direction2), 11);
				ItemEntity itemEntity = new ItemEntity(
					world,
					(double)blockPos.getX() + 0.5 + (double)direction2.getOffsetX() * 0.65,
					(double)blockPos.getY() + 0.1,
					(double)blockPos.getZ() + 0.5 + (double)direction2.getOffsetZ() * 0.65,
					new ItemStack(Items.field_8706, 4)
				);
				itemEntity.setVelocity(
					0.05 * (double)direction2.getOffsetX() + world.random.nextDouble() * 0.02, 0.05, 0.05 * (double)direction2.getOffsetZ() + world.random.nextDouble() * 0.02
				);
				world.spawnEntity(itemEntity);
				itemStack.damage(1, playerEntity, playerEntityx -> playerEntityx.sendToolBreakStatus(hand));
			}

			return true;
		} else {
			return super.activate(blockState, world, blockPos, playerEntity, hand, blockHitResult);
		}
	}

	@Override
	public StemBlock getStem() {
		return (StemBlock)Blocks.field_9984;
	}

	@Override
	public AttachedStemBlock getAttachedStem() {
		return (AttachedStemBlock)Blocks.field_10331;
	}
}
