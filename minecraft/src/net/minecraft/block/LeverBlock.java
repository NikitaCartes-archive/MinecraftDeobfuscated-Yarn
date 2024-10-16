package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.block.OrientationHelper;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;

public class LeverBlock extends WallMountedBlock {
	public static final MapCodec<LeverBlock> CODEC = createCodec(LeverBlock::new);
	public static final BooleanProperty POWERED = Properties.POWERED;
	protected static final int field_31184 = 6;
	protected static final int field_31185 = 6;
	protected static final int field_31186 = 8;
	protected static final VoxelShape NORTH_WALL_SHAPE = Block.createCuboidShape(5.0, 4.0, 10.0, 11.0, 12.0, 16.0);
	protected static final VoxelShape SOUTH_WALL_SHAPE = Block.createCuboidShape(5.0, 4.0, 0.0, 11.0, 12.0, 6.0);
	protected static final VoxelShape WEST_WALL_SHAPE = Block.createCuboidShape(10.0, 4.0, 5.0, 16.0, 12.0, 11.0);
	protected static final VoxelShape EAST_WALL_SHAPE = Block.createCuboidShape(0.0, 4.0, 5.0, 6.0, 12.0, 11.0);
	protected static final VoxelShape FLOOR_Z_AXIS_SHAPE = Block.createCuboidShape(5.0, 0.0, 4.0, 11.0, 6.0, 12.0);
	protected static final VoxelShape FLOOR_X_AXIS_SHAPE = Block.createCuboidShape(4.0, 0.0, 5.0, 12.0, 6.0, 11.0);
	protected static final VoxelShape CEILING_Z_AXIS_SHAPE = Block.createCuboidShape(5.0, 10.0, 4.0, 11.0, 16.0, 12.0);
	protected static final VoxelShape CEILING_X_AXIS_SHAPE = Block.createCuboidShape(4.0, 10.0, 5.0, 12.0, 16.0, 11.0);

	@Override
	public MapCodec<LeverBlock> getCodec() {
		return CODEC;
	}

	protected LeverBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, Boolean.valueOf(false)).with(FACE, BlockFace.WALL));
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		switch ((BlockFace)state.get(FACE)) {
			case FLOOR:
				switch (((Direction)state.get(FACING)).getAxis()) {
					case X:
						return FLOOR_X_AXIS_SHAPE;
					case Z:
					default:
						return FLOOR_Z_AXIS_SHAPE;
				}
			case WALL:
				switch ((Direction)state.get(FACING)) {
					case EAST:
						return EAST_WALL_SHAPE;
					case WEST:
						return WEST_WALL_SHAPE;
					case SOUTH:
						return SOUTH_WALL_SHAPE;
					case NORTH:
					default:
						return NORTH_WALL_SHAPE;
				}
			case CEILING:
			default:
				switch (((Direction)state.get(FACING)).getAxis()) {
					case X:
						return CEILING_X_AXIS_SHAPE;
					case Z:
					default:
						return CEILING_Z_AXIS_SHAPE;
				}
		}
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (world.isClient) {
			BlockState blockState = state.cycle(POWERED);
			if ((Boolean)blockState.get(POWERED)) {
				spawnParticles(blockState, world, pos, 1.0F);
			}
		} else {
			this.togglePower(state, world, pos, null);
		}

		return ActionResult.SUCCESS;
	}

	@Override
	protected void onExploded(BlockState state, ServerWorld world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
		if (explosion.canTriggerBlocks()) {
			this.togglePower(state, world, pos, null);
		}

		super.onExploded(state, world, pos, explosion, stackMerger);
	}

	public void togglePower(BlockState state, World world, BlockPos pos, @Nullable PlayerEntity player) {
		state = state.cycle(POWERED);
		world.setBlockState(pos, state, Block.NOTIFY_ALL);
		this.updateNeighbors(state, world, pos);
		playClickSound(player, world, pos, state);
		world.emitGameEvent(player, state.get(POWERED) ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, pos);
	}

	protected static void playClickSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos, BlockState state) {
		float f = state.get(POWERED) ? 0.6F : 0.5F;
		world.playSound(player, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, f);
	}

	private static void spawnParticles(BlockState state, WorldAccess world, BlockPos pos, float alpha) {
		Direction direction = ((Direction)state.get(FACING)).getOpposite();
		Direction direction2 = getDirection(state).getOpposite();
		double d = (double)pos.getX() + 0.5 + 0.1 * (double)direction.getOffsetX() + 0.2 * (double)direction2.getOffsetX();
		double e = (double)pos.getY() + 0.5 + 0.1 * (double)direction.getOffsetY() + 0.2 * (double)direction2.getOffsetY();
		double f = (double)pos.getZ() + 0.5 + 0.1 * (double)direction.getOffsetZ() + 0.2 * (double)direction2.getOffsetZ();
		world.addParticle(new DustParticleEffect(16711680, alpha), d, e, f, 0.0, 0.0, 0.0);
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if ((Boolean)state.get(POWERED) && random.nextFloat() < 0.25F) {
			spawnParticles(state, world, pos, 0.5F);
		}
	}

	@Override
	protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!moved && !state.isOf(newState.getBlock())) {
			if ((Boolean)state.get(POWERED)) {
				this.updateNeighbors(state, world, pos);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.get(POWERED) ? 15 : 0;
	}

	@Override
	protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.get(POWERED) && getDirection(state) == direction ? 15 : 0;
	}

	@Override
	protected boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	private void updateNeighbors(BlockState state, World world, BlockPos pos) {
		Direction direction = getDirection(state).getOpposite();
		WireOrientation wireOrientation = OrientationHelper.getEmissionOrientation(
			world, direction, direction.getAxis().isHorizontal() ? Direction.UP : state.get(FACING)
		);
		world.updateNeighborsAlways(pos, this, wireOrientation);
		world.updateNeighborsAlways(pos.offset(direction), this, wireOrientation);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACE, FACING, POWERED);
	}
}
