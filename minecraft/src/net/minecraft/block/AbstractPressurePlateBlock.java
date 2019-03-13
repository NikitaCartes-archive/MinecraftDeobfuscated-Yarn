package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public abstract class AbstractPressurePlateBlock extends Block {
	protected static final VoxelShape field_9942 = Block.method_9541(1.0, 0.0, 1.0, 15.0, 0.5, 15.0);
	protected static final VoxelShape field_9943 = Block.method_9541(1.0, 0.0, 1.0, 15.0, 1.0, 15.0);
	protected static final BoundingBox field_9941 = new BoundingBox(0.125, 0.0, 0.125, 0.875, 0.25, 0.875);

	protected AbstractPressurePlateBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return this.method_9435(blockState) > 0 ? field_9942 : field_9943;
	}

	@Override
	public int getTickRate(ViewableWorld viewableWorld) {
		return 20;
	}

	@Override
	public boolean canMobSpawnInside() {
		return true;
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return direction == Direction.DOWN && !blockState.method_11591(iWorld, blockPos)
			? Blocks.field_10124.method_9564()
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		BlockState blockState2 = viewableWorld.method_8320(blockPos2);
		return blockState2.method_11631(viewableWorld, blockPos2) || blockState2.getBlock().method_9525(BlockTags.field_16584);
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!world.isClient) {
			int i = this.method_9435(blockState);
			if (i > 0) {
				this.method_9433(world, blockPos, blockState, i);
			}
		}
	}

	@Override
	public void method_9548(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		if (!world.isClient) {
			int i = this.method_9435(blockState);
			if (i == 0) {
				this.method_9433(world, blockPos, blockState, i);
			}
		}
	}

	protected void method_9433(World world, BlockPos blockPos, BlockState blockState, int i) {
		int j = this.method_9434(world, blockPos);
		boolean bl = i > 0;
		boolean bl2 = j > 0;
		if (i != j) {
			blockState = this.method_9432(blockState, j);
			world.method_8652(blockPos, blockState, 2);
			this.method_9437(world, blockPos);
			world.method_16109(blockPos);
		}

		if (!bl2 && bl) {
			this.method_9438(world, blockPos);
		} else if (bl2 && !bl) {
			this.method_9436(world, blockPos);
		}

		if (bl2) {
			world.method_8397().method_8676(new BlockPos(blockPos), this, this.getTickRate(world));
		}
	}

	protected abstract void method_9436(IWorld iWorld, BlockPos blockPos);

	protected abstract void method_9438(IWorld iWorld, BlockPos blockPos);

	@Override
	public void method_9536(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (!bl && blockState.getBlock() != blockState2.getBlock()) {
			if (this.method_9435(blockState) > 0) {
				this.method_9437(world, blockPos);
			}

			super.method_9536(blockState, world, blockPos, blockState2, bl);
		}
	}

	protected void method_9437(World world, BlockPos blockPos) {
		world.method_8452(blockPos, this);
		world.method_8452(blockPos.down(), this);
	}

	@Override
	public int method_9524(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return this.method_9435(blockState);
	}

	@Override
	public int method_9603(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return direction == Direction.UP ? this.method_9435(blockState) : 0;
	}

	@Override
	public boolean method_9506(BlockState blockState) {
		return true;
	}

	@Override
	public PistonBehavior method_9527(BlockState blockState) {
		return PistonBehavior.field_15971;
	}

	protected abstract int method_9434(World world, BlockPos blockPos);

	protected abstract int method_9435(BlockState blockState);

	protected abstract BlockState method_9432(BlockState blockState, int i);
}
