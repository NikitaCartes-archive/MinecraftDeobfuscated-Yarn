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
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public void afterBreak(
		World world, PlayerEntity playerEntity, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack
	) {
		super.afterBreak(world, playerEntity, blockPos, blockState, blockEntity, itemStack);
		if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, itemStack) == 0) {
			if (world.dimension.doesWaterVaporize()) {
				world.removeBlock(blockPos, false);
				return;
			}

			Material material = world.getBlockState(blockPos.down()).getMaterial();
			if (material.blocksMovement() || material.isLiquid()) {
				world.setBlockState(blockPos, Blocks.WATER.getDefaultState());
			}
		}
	}

	@Override
	public void onScheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
		if (serverWorld.getLightLevel(LightType.BLOCK, blockPos) > 11 - blockState.getOpacity(serverWorld, blockPos)) {
			this.melt(blockState, serverWorld, blockPos);
		}
	}

	protected void melt(BlockState blockState, World world, BlockPos blockPos) {
		if (world.dimension.doesWaterVaporize()) {
			world.removeBlock(blockPos, false);
		} else {
			world.setBlockState(blockPos, Blocks.WATER.getDefaultState());
			world.updateNeighbor(blockPos, Blocks.WATER, blockPos);
		}
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState blockState) {
		return PistonBehavior.NORMAL;
	}

	@Override
	public boolean allowsSpawning(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityType<?> entityType) {
		return entityType == EntityType.POLAR_BEAR;
	}
}
