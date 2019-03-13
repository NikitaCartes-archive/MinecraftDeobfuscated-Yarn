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
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		ItemStack itemStack = playerEntity.method_5998(hand);
		if (itemStack.getItem() == Items.field_8868) {
			if (!world.isClient) {
				Direction direction = blockHitResult.method_17780();
				Direction direction2 = direction.getAxis() == Direction.Axis.Y ? playerEntity.method_5735().getOpposite() : direction;
				world.method_8396(null, blockPos, SoundEvents.field_14619, SoundCategory.field_15245, 1.0F, 1.0F);
				world.method_8652(blockPos, Blocks.field_10147.method_9564().method_11657(CarvedPumpkinBlock.field_10748, direction2), 11);
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
				itemStack.applyDamage(1, playerEntity);
			}

			return true;
		} else {
			return super.method_9534(blockState, world, blockPos, playerEntity, hand, blockHitResult);
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
