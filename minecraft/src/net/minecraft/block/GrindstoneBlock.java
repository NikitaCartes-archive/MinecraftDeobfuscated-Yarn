package net.minecraft.block;

import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class GrindstoneBlock extends WallMountedBlock {
	public static final VoxelShape field_16379 = Block.createCuboidShape(2.0, 0.0, 6.0, 4.0, 7.0, 10.0);
	public static final VoxelShape field_16392 = Block.createCuboidShape(12.0, 0.0, 6.0, 14.0, 7.0, 10.0);
	public static final VoxelShape field_16366 = Block.createCuboidShape(2.0, 7.0, 5.0, 4.0, 13.0, 11.0);
	public static final VoxelShape field_16339 = Block.createCuboidShape(12.0, 7.0, 5.0, 14.0, 13.0, 11.0);
	public static final VoxelShape field_16348 = VoxelShapes.union(field_16379, field_16366);
	public static final VoxelShape field_16365 = VoxelShapes.union(field_16392, field_16339);
	public static final VoxelShape field_16385 = VoxelShapes.union(field_16348, field_16365);
	public static final VoxelShape NORTH_SOUTH_SHAPE = VoxelShapes.union(field_16385, Block.createCuboidShape(4.0, 4.0, 2.0, 12.0, 16.0, 14.0));
	public static final VoxelShape field_16373 = Block.createCuboidShape(6.0, 0.0, 2.0, 10.0, 7.0, 4.0);
	public static final VoxelShape field_16346 = Block.createCuboidShape(6.0, 0.0, 12.0, 10.0, 7.0, 14.0);
	public static final VoxelShape field_16343 = Block.createCuboidShape(5.0, 7.0, 2.0, 11.0, 13.0, 4.0);
	public static final VoxelShape field_16374 = Block.createCuboidShape(5.0, 7.0, 12.0, 11.0, 13.0, 14.0);
	public static final VoxelShape field_16386 = VoxelShapes.union(field_16373, field_16343);
	public static final VoxelShape field_16378 = VoxelShapes.union(field_16346, field_16374);
	public static final VoxelShape field_16362 = VoxelShapes.union(field_16386, field_16378);
	public static final VoxelShape EAST_WEST_SHAPE = VoxelShapes.union(field_16362, Block.createCuboidShape(2.0, 4.0, 4.0, 14.0, 16.0, 12.0));
	public static final VoxelShape field_16352 = Block.createCuboidShape(2.0, 6.0, 0.0, 4.0, 10.0, 7.0);
	public static final VoxelShape field_16377 = Block.createCuboidShape(12.0, 6.0, 0.0, 14.0, 10.0, 7.0);
	public static final VoxelShape field_16393 = Block.createCuboidShape(2.0, 5.0, 7.0, 4.0, 11.0, 13.0);
	public static final VoxelShape field_16371 = Block.createCuboidShape(12.0, 5.0, 7.0, 14.0, 11.0, 13.0);
	public static final VoxelShape field_16340 = VoxelShapes.union(field_16352, field_16393);
	public static final VoxelShape field_16354 = VoxelShapes.union(field_16377, field_16371);
	public static final VoxelShape field_16369 = VoxelShapes.union(field_16340, field_16354);
	public static final VoxelShape SOUTH_WALL_SHAPE = VoxelShapes.union(field_16369, Block.createCuboidShape(4.0, 2.0, 4.0, 12.0, 14.0, 16.0));
	public static final VoxelShape field_16363 = Block.createCuboidShape(2.0, 6.0, 7.0, 4.0, 10.0, 16.0);
	public static final VoxelShape field_16347 = Block.createCuboidShape(12.0, 6.0, 7.0, 14.0, 10.0, 16.0);
	public static final VoxelShape field_16401 = Block.createCuboidShape(2.0, 5.0, 3.0, 4.0, 11.0, 9.0);
	public static final VoxelShape field_16367 = Block.createCuboidShape(12.0, 5.0, 3.0, 14.0, 11.0, 9.0);
	public static final VoxelShape field_16388 = VoxelShapes.union(field_16363, field_16401);
	public static final VoxelShape field_16396 = VoxelShapes.union(field_16347, field_16367);
	public static final VoxelShape field_16368 = VoxelShapes.union(field_16388, field_16396);
	public static final VoxelShape NORTH_WALL_SHAPE = VoxelShapes.union(field_16368, Block.createCuboidShape(4.0, 2.0, 0.0, 12.0, 14.0, 12.0));
	public static final VoxelShape field_16342 = Block.createCuboidShape(7.0, 6.0, 2.0, 16.0, 10.0, 4.0);
	public static final VoxelShape field_16358 = Block.createCuboidShape(7.0, 6.0, 12.0, 16.0, 10.0, 14.0);
	public static final VoxelShape field_16390 = Block.createCuboidShape(3.0, 5.0, 2.0, 9.0, 11.0, 4.0);
	public static final VoxelShape field_16382 = Block.createCuboidShape(3.0, 5.0, 12.0, 9.0, 11.0, 14.0);
	public static final VoxelShape field_16359 = VoxelShapes.union(field_16342, field_16390);
	public static final VoxelShape field_16351 = VoxelShapes.union(field_16358, field_16382);
	public static final VoxelShape field_16344 = VoxelShapes.union(field_16359, field_16351);
	public static final VoxelShape WEST_WALL_SHAPE = VoxelShapes.union(field_16344, Block.createCuboidShape(0.0, 2.0, 4.0, 12.0, 14.0, 12.0));
	public static final VoxelShape field_16394 = Block.createCuboidShape(0.0, 6.0, 2.0, 9.0, 10.0, 4.0);
	public static final VoxelShape field_16375 = Block.createCuboidShape(0.0, 6.0, 12.0, 9.0, 10.0, 14.0);
	public static final VoxelShape field_16345 = Block.createCuboidShape(7.0, 5.0, 2.0, 13.0, 11.0, 4.0);
	public static final VoxelShape field_16350 = Block.createCuboidShape(7.0, 5.0, 12.0, 13.0, 11.0, 14.0);
	public static final VoxelShape field_16372 = VoxelShapes.union(field_16394, field_16345);
	public static final VoxelShape field_16381 = VoxelShapes.union(field_16375, field_16350);
	public static final VoxelShape field_16391 = VoxelShapes.union(field_16372, field_16381);
	public static final VoxelShape EAST_WALL_SHAPE = VoxelShapes.union(field_16391, Block.createCuboidShape(4.0, 2.0, 4.0, 16.0, 14.0, 12.0));
	public static final VoxelShape field_16341 = Block.createCuboidShape(2.0, 9.0, 6.0, 4.0, 16.0, 10.0);
	public static final VoxelShape field_16355 = Block.createCuboidShape(12.0, 9.0, 6.0, 14.0, 16.0, 10.0);
	public static final VoxelShape field_16384 = Block.createCuboidShape(2.0, 3.0, 5.0, 4.0, 9.0, 11.0);
	public static final VoxelShape field_16400 = Block.createCuboidShape(12.0, 3.0, 5.0, 14.0, 9.0, 11.0);
	public static final VoxelShape field_16364 = VoxelShapes.union(field_16341, field_16384);
	public static final VoxelShape field_16349 = VoxelShapes.union(field_16355, field_16400);
	public static final VoxelShape field_16397 = VoxelShapes.union(field_16364, field_16349);
	public static final VoxelShape NORTH_SOUTH_HANGING_SHAPE = VoxelShapes.union(field_16397, Block.createCuboidShape(4.0, 0.0, 2.0, 12.0, 12.0, 14.0));
	public static final VoxelShape field_16387 = Block.createCuboidShape(6.0, 9.0, 2.0, 10.0, 16.0, 4.0);
	public static final VoxelShape field_16398 = Block.createCuboidShape(6.0, 9.0, 12.0, 10.0, 16.0, 14.0);
	public static final VoxelShape field_16357 = Block.createCuboidShape(5.0, 3.0, 2.0, 11.0, 9.0, 4.0);
	public static final VoxelShape field_16353 = Block.createCuboidShape(5.0, 3.0, 12.0, 11.0, 9.0, 14.0);
	public static final VoxelShape field_16395 = VoxelShapes.union(field_16387, field_16357);
	public static final VoxelShape field_16360 = VoxelShapes.union(field_16398, field_16353);
	public static final VoxelShape field_16389 = VoxelShapes.union(field_16395, field_16360);
	public static final VoxelShape EAST_WEST_HANGING_SHAPE = VoxelShapes.union(field_16389, Block.createCuboidShape(2.0, 0.0, 4.0, 14.0, 12.0, 12.0));
	private static final TranslatableText TITLE = new TranslatableText("container.grindstone_title");

	protected GrindstoneBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(FACE, WallMountLocation.WALL));
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	private VoxelShape getShape(BlockState state) {
		Direction direction = state.get(FACING);
		switch ((WallMountLocation)state.get(FACE)) {
			case FLOOR:
				if (direction != Direction.NORTH && direction != Direction.SOUTH) {
					return EAST_WEST_SHAPE;
				}

				return NORTH_SOUTH_SHAPE;
			case WALL:
				if (direction == Direction.NORTH) {
					return NORTH_WALL_SHAPE;
				} else if (direction == Direction.SOUTH) {
					return SOUTH_WALL_SHAPE;
				} else {
					if (direction == Direction.EAST) {
						return EAST_WALL_SHAPE;
					}

					return WEST_WALL_SHAPE;
				}
			case CEILING:
				if (direction != Direction.NORTH && direction != Direction.SOUTH) {
					return EAST_WEST_HANGING_SHAPE;
				}

				return NORTH_SOUTH_HANGING_SHAPE;
			default:
				return EAST_WEST_SHAPE;
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return this.getShape(state);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return this.getShape(state);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return true;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
			player.incrementStat(Stats.INTERACT_WITH_GRINDSTONE);
			return ActionResult.CONSUME;
		}
	}

	@Override
	public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
		return new SimpleNamedScreenHandlerFactory(
			(i, playerInventory, playerEntity) -> new GrindstoneScreenHandler(i, playerInventory, ScreenHandlerContext.create(world, pos)), TITLE
		);
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, FACE);
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}
}
