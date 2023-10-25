package net.minecraft.block;

import com.mojang.serialization.MapCodec;
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

public class IceBlock extends TranslucentBlock {
	public static final MapCodec<IceBlock> CODEC = createCodec(IceBlock::new);

	@Override
	public MapCodec<? extends IceBlock> getCodec() {
		return CODEC;
	}

	public IceBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	public static BlockState getMeltedState() {
		return Blocks.WATER.getDefaultState();
	}

	@Override
	public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
		super.afterBreak(world, player, pos, state, blockEntity, tool);
		if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, tool) == 0) {
			if (world.getDimension().ultrawarm()) {
				world.removeBlock(pos, false);
				return;
			}

			BlockState blockState = world.getBlockState(pos.down());
			if (blockState.blocksMovement() || blockState.isLiquid()) {
				world.setBlockState(pos, getMeltedState());
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
			world.setBlockState(pos, getMeltedState());
			world.updateNeighbor(pos, getMeltedState().getBlock(), pos);
		}
	}
}
