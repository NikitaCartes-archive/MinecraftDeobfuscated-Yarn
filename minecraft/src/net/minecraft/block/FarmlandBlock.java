package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class FarmlandBlock extends Block {
	public static final IntProperty MOISTURE = Properties.MOISTURE;
	protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 15.0, 16.0);

	protected FarmlandBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(MOISTURE, Integer.valueOf(0)));
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if (direction == Direction.field_11036 && !blockState.canPlaceAt(iWorld, blockPos)) {
			iWorld.getBlockTickScheduler().schedule(blockPos, this, 1);
		}

		return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockState blockState2 = viewableWorld.getBlockState(blockPos.up());
		return !blockState2.getMaterial().isSolid() || blockState2.getBlock() instanceof FenceGateBlock;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return !this.getDefaultState().canPlaceAt(itemPlacementContext.getWorld(), itemPlacementContext.getBlockPos())
			? Blocks.field_10566.getDefaultState()
			: super.getPlacementState(itemPlacementContext);
	}

	@Override
	public boolean hasSidedTransparency(BlockState blockState) {
		return true;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return SHAPE;
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!blockState.canPlaceAt(world, blockPos)) {
			setToDirt(blockState, world, blockPos);
		} else {
			int i = (Integer)blockState.get(MOISTURE);
			if (!isWaterNearby(world, blockPos) && !world.hasRain(blockPos.up())) {
				if (i > 0) {
					world.setBlockState(blockPos, blockState.with(MOISTURE, Integer.valueOf(i - 1)), 2);
				} else if (!hasCrop(world, blockPos)) {
					setToDirt(blockState, world, blockPos);
				}
			} else if (i < 7) {
				world.setBlockState(blockPos, blockState.with(MOISTURE, Integer.valueOf(7)), 2);
			}
		}
	}

	@Override
	public void onLandedUpon(World world, BlockPos blockPos, Entity entity, float f) {
		if (!world.isClient
			&& world.random.nextFloat() < f - 0.5F
			&& entity instanceof LivingEntity
			&& (entity instanceof PlayerEntity || world.getGameRules().getBoolean(GameRules.field_19388))
			&& entity.getWidth() * entity.getWidth() * entity.getHeight() > 0.512F) {
			setToDirt(world.getBlockState(blockPos), world, blockPos);
		}

		super.onLandedUpon(world, blockPos, entity, f);
	}

	public static void setToDirt(BlockState blockState, World world, BlockPos blockPos) {
		world.setBlockState(blockPos, pushEntitiesUpBeforeBlockChange(blockState, Blocks.field_10566.getDefaultState(), world, blockPos));
	}

	private static boolean hasCrop(BlockView blockView, BlockPos blockPos) {
		Block block = blockView.getBlockState(blockPos.up()).getBlock();
		return block instanceof CropBlock || block instanceof StemBlock || block instanceof AttachedStemBlock;
	}

	private static boolean isWaterNearby(ViewableWorld viewableWorld, BlockPos blockPos) {
		for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-4, 0, -4), blockPos.add(4, 1, 4))) {
			if (viewableWorld.getFluidState(blockPos2).matches(FluidTags.field_15517)) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(MOISTURE);
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
