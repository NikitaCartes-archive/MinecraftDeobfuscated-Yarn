package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;

public class FarmlandBlock extends Block {
	public static final MapCodec<FarmlandBlock> CODEC = createCodec(FarmlandBlock::new);
	public static final IntProperty MOISTURE = Properties.MOISTURE;
	protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 15.0, 16.0);
	public static final int MAX_MOISTURE = 7;

	@Override
	public MapCodec<FarmlandBlock> getCodec() {
		return CODEC;
	}

	protected FarmlandBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(MOISTURE, Integer.valueOf(0)));
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if (direction == Direction.UP && !state.canPlaceAt(world, pos)) {
			world.scheduleBlockTick(pos, this, 1);
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos.up());
		return !blockState.isSolid() || blockState.getBlock() instanceof FenceGateBlock || blockState.getBlock() instanceof PistonExtensionBlock;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return !this.getDefaultState().canPlaceAt(ctx.getWorld(), ctx.getBlockPos())
			? (ctx.getWorld().isPotato() ? Blocks.TERRE_DE_POMME : Blocks.DIRT).getDefaultState()
			: super.getPlacementState(ctx);
	}

	@Override
	protected boolean hasSidedTransparency(BlockState state) {
		return true;
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!state.canPlaceAt(world, pos)) {
			setToDirt(null, state, world, pos);
		}
	}

	@Override
	protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		int i = (Integer)state.get(MOISTURE);
		if (!isWaterNearby(world, pos) && !world.hasRain(pos.up())) {
			if (i > 0) {
				world.setBlockState(pos, state.with(MOISTURE, Integer.valueOf(i - 1)), Block.NOTIFY_LISTENERS);
			} else if (!hasCrop(world, pos)) {
				setToDirt(null, state, world, pos);
			}
		} else if (i < 7) {
			world.setBlockState(pos, state.with(MOISTURE, Integer.valueOf(7)), Block.NOTIFY_LISTENERS);
		}
	}

	@Override
	public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
		if (!world.isClient
			&& world.random.nextFloat() < fallDistance - 0.5F
			&& entity instanceof LivingEntity
			&& (entity instanceof PlayerEntity || world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING))
			&& entity.getWidth() * entity.getWidth() * entity.getHeight() > 0.512F) {
			setToDirt(entity, state, world, pos);
		}

		super.onLandedUpon(world, state, pos, entity, fallDistance);
	}

	public static void setToDirt(@Nullable Entity entity, BlockState state, World world, BlockPos pos) {
		BlockState blockState = pushEntitiesUpBeforeBlockChange(state, (world.isPotato() ? Blocks.TERRE_DE_POMME : Blocks.DIRT).getDefaultState(), world, pos);
		world.setBlockState(pos, blockState);
		world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(entity, blockState));
	}

	private static boolean hasCrop(BlockView world, BlockPos pos) {
		return world.isPotato() ? true : world.getBlockState(pos.up()).isIn(BlockTags.MAINTAINS_FARMLAND);
	}

	private static boolean isWaterNearby(WorldView world, BlockPos pos) {
		for (BlockPos blockPos : BlockPos.iterate(pos.add(-4, 0, -4), pos.add(4, 1, 4))) {
			if (world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(MOISTURE);
	}

	@Override
	protected boolean canPathfindThrough(BlockState state, NavigationType type) {
		return false;
	}
}
