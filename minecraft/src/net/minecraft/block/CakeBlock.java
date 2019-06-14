package net.minecraft.block;

import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class CakeBlock extends Block {
	public static final IntProperty field_10739 = Properties.field_12505;
	protected static final VoxelShape[] field_10738 = new VoxelShape[]{
		Block.method_9541(1.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		Block.method_9541(3.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		Block.method_9541(5.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		Block.method_9541(7.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		Block.method_9541(9.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		Block.method_9541(11.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		Block.method_9541(13.0, 0.0, 1.0, 15.0, 8.0, 15.0)
	};

	protected CakeBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10739, Integer.valueOf(0)));
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return field_10738[blockState.method_11654(field_10739)];
	}

	@Override
	public boolean method_9534(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (!world.isClient) {
			return this.method_9719(world, blockPos, blockState, playerEntity);
		} else {
			ItemStack itemStack = playerEntity.getStackInHand(hand);
			return this.method_9719(world, blockPos, blockState, playerEntity) || itemStack.isEmpty();
		}
	}

	private boolean method_9719(IWorld iWorld, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		if (!playerEntity.canConsume(false)) {
			return false;
		} else {
			playerEntity.incrementStat(Stats.field_15369);
			playerEntity.getHungerManager().add(2, 0.1F);
			int i = (Integer)blockState.method_11654(field_10739);
			if (i < 6) {
				iWorld.method_8652(blockPos, blockState.method_11657(field_10739, Integer.valueOf(i + 1)), 3);
			} else {
				iWorld.clearBlockState(blockPos, false);
			}

			return true;
		}
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return direction == Direction.field_11033 && !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.field_10124.method_9564()
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return viewableWorld.method_8320(blockPos.down()).method_11620().isSolid();
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_10739);
	}

	@Override
	public int method_9572(BlockState blockState, World world, BlockPos blockPos) {
		return (7 - (Integer)blockState.method_11654(field_10739)) * 2;
	}

	@Override
	public boolean method_9498(BlockState blockState) {
		return true;
	}

	@Override
	public boolean method_9516(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
