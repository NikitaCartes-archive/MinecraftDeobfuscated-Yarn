package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.class_9062;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class PumpkinBlock extends Block {
	public static final MapCodec<PumpkinBlock> CODEC = createCodec(PumpkinBlock::new);

	@Override
	public MapCodec<PumpkinBlock> getCodec() {
		return CODEC;
	}

	protected PumpkinBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public class_9062 method_55765(
		ItemStack itemStack, BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult
	) {
		if (!itemStack.isOf(Items.SHEARS)) {
			return super.method_55765(itemStack, blockState, world, blockPos, playerEntity, hand, blockHitResult);
		} else if (world.isClient) {
			return class_9062.method_55644(world.isClient);
		} else {
			Direction direction = blockHitResult.getSide();
			Direction direction2 = direction.getAxis() == Direction.Axis.Y ? playerEntity.getHorizontalFacing().getOpposite() : direction;
			world.playSound(null, blockPos, SoundEvents.BLOCK_PUMPKIN_CARVE, SoundCategory.BLOCKS, 1.0F, 1.0F);
			world.setBlockState(blockPos, Blocks.CARVED_PUMPKIN.getDefaultState().with(CarvedPumpkinBlock.FACING, direction2), Block.NOTIFY_ALL_AND_REDRAW);
			ItemEntity itemEntity = new ItemEntity(
				world,
				(double)blockPos.getX() + 0.5 + (double)direction2.getOffsetX() * 0.65,
				(double)blockPos.getY() + 0.1,
				(double)blockPos.getZ() + 0.5 + (double)direction2.getOffsetZ() * 0.65,
				new ItemStack(Items.PUMPKIN_SEEDS, 4)
			);
			itemEntity.setVelocity(
				0.05 * (double)direction2.getOffsetX() + world.random.nextDouble() * 0.02, 0.05, 0.05 * (double)direction2.getOffsetZ() + world.random.nextDouble() * 0.02
			);
			world.spawnEntity(itemEntity);
			itemStack.damage(1, playerEntity, playerEntityx -> playerEntityx.sendToolBreakStatus(hand));
			world.emitGameEvent(playerEntity, GameEvent.SHEAR, blockPos);
			playerEntity.incrementStat(Stats.USED.getOrCreateStat(Items.SHEARS));
			return class_9062.method_55644(world.isClient);
		}
	}
}
