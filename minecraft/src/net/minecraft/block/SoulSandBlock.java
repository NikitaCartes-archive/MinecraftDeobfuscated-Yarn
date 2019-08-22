package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class SoulSandBlock extends Block {
	protected static final VoxelShape COLLISION_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0);

	public SoulSandBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return COLLISION_SHAPE;
	}

	@Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		entity.setVelocity(entity.getVelocity().multiply(0.4, 1.0, 0.4));
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		BubbleColumnBlock.update(world, blockPos.up(), false);
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		world.getBlockTickScheduler().schedule(blockPos, this, this.getTickRate(world));
	}

	@Override
	public boolean isSimpleFullBlock(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return true;
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
		return 20;
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		world.getBlockTickScheduler().schedule(blockPos, this, this.getTickRate(world));
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}

	@Override
	public boolean allowsSpawning(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityType<?> entityType) {
		return true;
	}
}
