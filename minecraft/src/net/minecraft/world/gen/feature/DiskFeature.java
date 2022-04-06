package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class DiskFeature extends Feature<DiskFeatureConfig> {
	public DiskFeature(Codec<DiskFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DiskFeatureConfig> context) {
		DiskFeatureConfig diskFeatureConfig = context.getConfig();
		BlockPos blockPos = context.getOrigin();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		boolean bl = false;
		int i = blockPos.getY();
		int j = i + diskFeatureConfig.halfHeight();
		int k = i - diskFeatureConfig.halfHeight() - 1;
		int l = diskFeatureConfig.radius().get(context.getRandom());
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-l, 0, -l), blockPos.add(l, 0, l))) {
			int m = blockPos2.getX() - blockPos.getX();
			int n = blockPos2.getZ() - blockPos.getZ();
			if (m * m + n * n <= l * l) {
				bl |= this.placeBlock(diskFeatureConfig, structureWorldAccess, j, k, mutable.set(blockPos2));
			}
		}

		return bl;
	}

	protected boolean placeBlock(DiskFeatureConfig config, StructureWorldAccess world, int topY, int bottomY, BlockPos.Mutable pos) {
		boolean bl = false;
		boolean bl2 = false;
		boolean bl3 = config.state().getBlock() instanceof FallingBlock;

		for (int i = topY; i >= bottomY; i--) {
			pos.setY(i);
			BlockState blockState = world.getBlockState(pos);
			boolean bl4 = false;
			if (i > bottomY && this.anyTargetsMatch(config, blockState)) {
				world.setBlockState(pos, config.state(), Block.NOTIFY_LISTENERS);
				this.markBlocksAboveForPostProcessing(world, pos);
				bl2 = true;
				bl4 = true;
			}

			if (bl3 && bl && blockState.isAir()) {
				world.setBlockState(pos.move(Direction.UP), this.getSandstone(config), Block.NOTIFY_LISTENERS);
			}

			bl = bl4;
		}

		return bl2;
	}

	protected BlockState getSandstone(DiskFeatureConfig config) {
		return config.state().isOf(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.getDefaultState() : Blocks.SANDSTONE.getDefaultState();
	}

	protected boolean anyTargetsMatch(DiskFeatureConfig config, BlockState state) {
		for (BlockState blockState : config.targets()) {
			if (blockState.isOf(state.getBlock())) {
				return true;
			}
		}

		return false;
	}
}
