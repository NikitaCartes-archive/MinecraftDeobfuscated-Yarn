package net.minecraft.block;

import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class DragonEggBlock extends FallingBlock {
	protected static final VoxelShape SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

	public DragonEggBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, EntityContext context) {
		return SHAPE;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		this.teleport(state, world, pos);
		return ActionResult.SUCCESS;
	}

	@Override
	public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		this.teleport(state, world, pos);
	}

	private void teleport(BlockState state, World world, BlockPos pos) {
		for (int i = 0; i < 1000; i++) {
			BlockPos blockPos = pos.add(
				world.random.nextInt(16) - world.random.nextInt(16), world.random.nextInt(8) - world.random.nextInt(8), world.random.nextInt(16) - world.random.nextInt(16)
			);
			if (world.getBlockState(blockPos).isAir()) {
				if (world.isClient) {
					for (int j = 0; j < 128; j++) {
						double d = world.random.nextDouble();
						float f = (world.random.nextFloat() - 0.5F) * 0.2F;
						float g = (world.random.nextFloat() - 0.5F) * 0.2F;
						float h = (world.random.nextFloat() - 0.5F) * 0.2F;
						double e = MathHelper.lerp(d, (double)blockPos.getX(), (double)pos.getX()) + (world.random.nextDouble() - 0.5) + 0.5;
						double k = MathHelper.lerp(d, (double)blockPos.getY(), (double)pos.getY()) + world.random.nextDouble() - 0.5;
						double l = MathHelper.lerp(d, (double)blockPos.getZ(), (double)pos.getZ()) + (world.random.nextDouble() - 0.5) + 0.5;
						world.addParticle(ParticleTypes.PORTAL, e, k, l, (double)f, (double)g, (double)h);
					}
				} else {
					world.setBlockState(blockPos, state, 2);
					world.removeBlock(pos, false);
				}

				return;
			}
		}
	}

	@Override
	public int getTickRate(WorldView world) {
		return 5;
	}

	@Override
	public boolean canPlaceAtSide(BlockState state, BlockView world, BlockPos pos, BlockPlacementEnvironment env) {
		return false;
	}
}
