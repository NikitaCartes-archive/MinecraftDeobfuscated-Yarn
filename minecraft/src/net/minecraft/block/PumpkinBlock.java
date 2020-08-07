package net.minecraft.block;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PumpkinBlock extends GourdBlock {
	protected PumpkinBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.getItem() == Items.field_8868) {
			if (!world.isClient) {
				Direction direction = hit.getSide();
				Direction direction2 = direction.getAxis() == Direction.Axis.field_11052 ? player.getHorizontalFacing().getOpposite() : direction;
				world.playSound(null, pos, SoundEvents.field_14619, SoundCategory.field_15245, 1.0F, 1.0F);
				world.setBlockState(pos, Blocks.field_10147.getDefaultState().with(CarvedPumpkinBlock.FACING, direction2), 11);
				ItemEntity itemEntity = new ItemEntity(
					world,
					(double)pos.getX() + 0.5 + (double)direction2.getOffsetX() * 0.65,
					(double)pos.getY() + 0.1,
					(double)pos.getZ() + 0.5 + (double)direction2.getOffsetZ() * 0.65,
					new ItemStack(Items.field_8706, 4)
				);
				itemEntity.setVelocity(
					0.05 * (double)direction2.getOffsetX() + world.random.nextDouble() * 0.02, 0.05, 0.05 * (double)direction2.getOffsetZ() + world.random.nextDouble() * 0.02
				);
				world.spawnEntity(itemEntity);
				itemStack.damage(1, player, playerEntity -> playerEntity.sendToolBreakStatus(hand));
			}

			return ActionResult.success(world.isClient);
		} else {
			return super.onUse(state, world, pos, player, hand, hit);
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
