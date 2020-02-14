package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class IceBlock extends TransparentBlock {
	public IceBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		super.afterBreak(world, player, pos, state, blockEntity, stack);
		if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) == 0) {
			if (world.dimension.doesWaterVaporize()) {
				world.removeBlock(pos, false);
				return;
			}

			Material material = world.getBlockState(pos.down()).getMaterial();
			if (material.blocksMovement() || material.isLiquid()) {
				world.setBlockState(pos, Blocks.WATER.getDefaultState());
			}
		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (world.getLightLevel(LightType.BLOCK, pos) > 11 - state.getOpacity(world, pos)) {
			this.melt(state, world, pos);
		}
	}

	protected void melt(BlockState state, World world, BlockPos pos) {
		if (world.dimension.doesWaterVaporize()) {
			world.removeBlock(pos, false);
		} else {
			world.setBlockState(pos, Blocks.WATER.getDefaultState());
			world.updateNeighbor(pos, Blocks.WATER, pos);
		}
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.NORMAL;
	}

	@Override
	public boolean allowsSpawning(BlockState state, BlockView world, BlockPos pos, EntityType<?> type) {
		return type == EntityType.POLAR_BEAR;
	}
}
