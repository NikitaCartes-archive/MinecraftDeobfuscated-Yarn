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
	protected static final VoxelShape field_11521 = Block.method_9541(0.0, 0.0, 0.0, 16.0, 14.0, 16.0);

	public SoulSandBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape method_9549(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return field_11521;
	}

	@Override
	public void method_9548(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		entity.method_18799(entity.method_18798().multiply(0.4, 1.0, 0.4));
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		BubbleColumnBlock.update(world, blockPos.up(), false);
	}

	@Override
	public void method_9612(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		world.method_8397().schedule(blockPos, this, this.getTickRate(world));
	}

	@Override
	public boolean method_9521(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return true;
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
		return 20;
	}

	@Override
	public void method_9615(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		world.method_8397().schedule(blockPos, this, this.getTickRate(world));
	}

	@Override
	public boolean method_9516(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}

	@Override
	public boolean method_9523(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityType<?> entityType) {
		return true;
	}
}
