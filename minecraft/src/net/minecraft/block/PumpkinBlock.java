package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
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
	protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!stack.isOf(Items.SHEARS)) {
			return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
		} else if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			Direction direction = hit.getSide();
			Direction direction2 = direction.getAxis() == Direction.Axis.Y ? player.getHorizontalFacing().getOpposite() : direction;
			world.playSound(null, pos, SoundEvents.BLOCK_PUMPKIN_CARVE, SoundCategory.BLOCKS, 1.0F, 1.0F);
			world.setBlockState(pos, Blocks.CARVED_PUMPKIN.getDefaultState().with(CarvedPumpkinBlock.FACING, direction2), Block.NOTIFY_ALL_AND_REDRAW);
			ItemEntity itemEntity = new ItemEntity(
				world,
				(double)pos.getX() + 0.5 + (double)direction2.getOffsetX() * 0.65,
				(double)pos.getY() + 0.1,
				(double)pos.getZ() + 0.5 + (double)direction2.getOffsetZ() * 0.65,
				new ItemStack(Items.PUMPKIN_SEEDS, 4)
			);
			itemEntity.setVelocity(
				0.05 * (double)direction2.getOffsetX() + world.random.nextDouble() * 0.02, 0.05, 0.05 * (double)direction2.getOffsetZ() + world.random.nextDouble() * 0.02
			);
			world.spawnEntity(itemEntity);
			stack.damage(1, player, LivingEntity.getSlotForHand(hand));
			world.emitGameEvent(player, GameEvent.SHEAR, pos);
			player.incrementStat(Stats.USED.getOrCreateStat(Items.SHEARS));
			return ActionResult.SUCCESS;
		}
	}
}
