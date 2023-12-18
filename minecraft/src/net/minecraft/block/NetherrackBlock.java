package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class NetherrackBlock extends Block implements Fertilizable {
	public static final MapCodec<NetherrackBlock> CODEC = createCodec(NetherrackBlock::new);

	@Override
	public MapCodec<NetherrackBlock> getCodec() {
		return CODEC;
	}

	public NetherrackBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
		if (!world.getBlockState(pos.up()).isTransparent(world, pos)) {
			return false;
		} else {
			for (BlockPos blockPos : BlockPos.iterate(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
				if (world.getBlockState(blockPos).isIn(BlockTags.NYLIUM)) {
					return true;
				}
			}

			return false;
		}
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		boolean bl = false;
		boolean bl2 = false;

		for (BlockPos blockPos : BlockPos.iterate(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
			BlockState blockState = world.getBlockState(blockPos);
			if (blockState.isOf(Blocks.WARPED_NYLIUM)) {
				bl2 = true;
			}

			if (blockState.isOf(Blocks.CRIMSON_NYLIUM)) {
				bl = true;
			}

			if (bl2 && bl) {
				break;
			}
		}

		if (bl2 && bl) {
			world.setBlockState(pos, random.nextBoolean() ? Blocks.WARPED_NYLIUM.getDefaultState() : Blocks.CRIMSON_NYLIUM.getDefaultState(), Block.NOTIFY_ALL);
		} else if (bl2) {
			world.setBlockState(pos, Blocks.WARPED_NYLIUM.getDefaultState(), Block.NOTIFY_ALL);
		} else if (bl) {
			world.setBlockState(pos, Blocks.CRIMSON_NYLIUM.getDefaultState(), Block.NOTIFY_ALL);
		}
	}

	@Override
	public Fertilizable.FertilizableType getFertilizableType() {
		return Fertilizable.FertilizableType.NEIGHBOR_SPREADER;
	}
}
