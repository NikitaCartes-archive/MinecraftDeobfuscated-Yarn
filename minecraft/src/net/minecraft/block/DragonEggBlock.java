package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class DragonEggBlock extends FallingBlock {
	protected static final VoxelShape field_10950 = Block.createCubeShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

	public DragonEggBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return field_10950;
	}

	@Override
	public boolean activate(
		BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, Direction direction, float f, float g, float h
	) {
		this.method_10047(blockState, world, blockPos);
		return true;
	}

	@Override
	public void onBlockBreakStart(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
		this.method_10047(blockState, world, blockPos);
	}

	private void method_10047(BlockState blockState, World world, BlockPos blockPos) {
		for (int i = 0; i < 1000; i++) {
			BlockPos blockPos2 = blockPos.add(
				world.random.nextInt(16) - world.random.nextInt(16), world.random.nextInt(8) - world.random.nextInt(8), world.random.nextInt(16) - world.random.nextInt(16)
			);
			if (world.getBlockState(blockPos2).isAir()) {
				if (world.isClient) {
					for (int j = 0; j < 128; j++) {
						double d = world.random.nextDouble();
						float f = (world.random.nextFloat() - 0.5F) * 0.2F;
						float g = (world.random.nextFloat() - 0.5F) * 0.2F;
						float h = (world.random.nextFloat() - 0.5F) * 0.2F;
						double e = MathHelper.lerp(d, (double)blockPos2.getX(), (double)blockPos.getX()) + (world.random.nextDouble() - 0.5) + 0.5;
						double k = MathHelper.lerp(d, (double)blockPos2.getY(), (double)blockPos.getY()) + world.random.nextDouble() - 0.5;
						double l = MathHelper.lerp(d, (double)blockPos2.getZ(), (double)blockPos.getZ()) + (world.random.nextDouble() - 0.5) + 0.5;
						world.method_8406(ParticleTypes.field_11214, e, k, l, (double)f, (double)g, (double)h);
					}
				} else {
					world.setBlockState(blockPos2, blockState, 2);
					world.clearBlockState(blockPos);
				}

				return;
			}
		}
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
		return 5;
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, PlacementEnvironment placementEnvironment) {
		return false;
	}
}
