package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class IceBlock extends TransparentBlock {
	public IceBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
		super.afterBreak(world, player, pos, state, blockEntity, tool);
		if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, tool) == 0) {
			if (world.getDimension().ultrawarm()) {
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
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (world.getLightLevel(LightType.BLOCK, pos) > 11 - state.getOpacity(world, pos)) {
			this.melt(state, world, pos);
		}
	}

	protected void melt(BlockState state, World world, BlockPos pos) {
		if (world.getDimension().ultrawarm()) {
			world.removeBlock(pos, false);
		} else {
			world.setBlockState(pos, Blocks.WATER.getDefaultState());
			world.updateNeighbor(pos, Blocks.WATER, pos);
		}
	}
}
