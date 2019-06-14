package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class CactusBlock extends Block {
	public static final IntProperty field_10709 = Properties.field_12498;
	protected static final VoxelShape field_10711 = Block.method_9541(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);
	protected static final VoxelShape field_10710 = Block.method_9541(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

	protected CactusBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10709, Integer.valueOf(0)));
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!blockState.canPlaceAt(world, blockPos)) {
			world.breakBlock(blockPos, true);
		} else {
			BlockPos blockPos2 = blockPos.up();
			if (world.isAir(blockPos2)) {
				int i = 1;

				while (world.method_8320(blockPos.down(i)).getBlock() == this) {
					i++;
				}

				if (i < 3) {
					int j = (Integer)blockState.method_11654(field_10709);
					if (j == 15) {
						world.method_8501(blockPos2, this.method_9564());
						BlockState blockState2 = blockState.method_11657(field_10709, Integer.valueOf(0));
						world.method_8652(blockPos, blockState2, 4);
						blockState2.neighborUpdate(world, blockPos2, this, blockPos, false);
					} else {
						world.method_8652(blockPos, blockState.method_11657(field_10709, Integer.valueOf(j + 1)), 4);
					}
				}
			}
		}
	}

	@Override
	public VoxelShape method_9549(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return field_10711;
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return field_10710;
	}

	@Override
	public boolean method_9601(BlockState blockState) {
		return true;
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if (!blockState.canPlaceAt(iWorld, blockPos)) {
			iWorld.method_8397().schedule(blockPos, this, 1);
		}

		return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		for (Direction direction : Direction.Type.field_11062) {
			BlockState blockState2 = viewableWorld.method_8320(blockPos.offset(direction));
			Material material = blockState2.method_11620();
			if (material.isSolid() || viewableWorld.method_8316(blockPos.offset(direction)).matches(FluidTags.field_15518)) {
				return false;
			}
		}

		Block block = viewableWorld.method_8320(blockPos.down()).getBlock();
		return (block == Blocks.field_10029 || block == Blocks.field_10102 || block == Blocks.field_10534)
			&& !viewableWorld.method_8320(blockPos.up()).method_11620().isLiquid();
	}

	@Override
	public void method_9548(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		entity.damage(DamageSource.CACTUS, 1.0F);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.field_9174;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_10709);
	}

	@Override
	public boolean method_9516(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
