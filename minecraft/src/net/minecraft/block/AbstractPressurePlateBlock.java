package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;

public abstract class AbstractPressurePlateBlock extends Block {
	protected static final VoxelShape PRESSED_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 0.5, 15.0);
	protected static final VoxelShape DEFAULT_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 1.0, 15.0);
	protected static final Box BOX = new Box(0.0625, 0.0, 0.0625, 0.9375, 0.25, 0.9375);
	protected final BlockSetType blockSetType;

	protected AbstractPressurePlateBlock(AbstractBlock.Settings settings, BlockSetType blockSetType) {
		super(settings.sounds(blockSetType.soundType()));
		this.blockSetType = blockSetType;
	}

	@Override
	protected abstract MapCodec<? extends AbstractPressurePlateBlock> getCodec();

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return this.getRedstoneOutput(state) > 0 ? PRESSED_SHAPE : DEFAULT_SHAPE;
	}

	protected int getTickRate() {
		return 20;
	}

	@Override
	public boolean canMobSpawnInside(BlockState state) {
		return true;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		return direction == Direction.DOWN && !state.canPlaceAt(world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		BlockPos blockPos = pos.down();
		return hasTopRim(world, blockPos) || sideCoversSmallSquare(world, blockPos, Direction.UP);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		int i = this.getRedstoneOutput(state);
		if (i > 0) {
			this.updatePlateState(null, world, pos, state, i);
		}
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!world.isClient) {
			int i = this.getRedstoneOutput(state);
			if (i == 0) {
				this.updatePlateState(entity, world, pos, state, i);
			}
		}
	}

	private void updatePlateState(@Nullable Entity entity, World world, BlockPos pos, BlockState state, int output) {
		int i = this.getRedstoneOutput(world, pos);
		boolean bl = output > 0;
		boolean bl2 = i > 0;
		if (output != i) {
			BlockState blockState = this.setRedstoneOutput(state, i);
			world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
			this.updateNeighbors(world, pos);
			world.scheduleBlockRerenderIfNeeded(pos, state, blockState);
		}

		if (!bl2 && bl) {
			world.playSound(null, pos, this.blockSetType.pressurePlateClickOff(), SoundCategory.BLOCKS);
			world.emitGameEvent(entity, GameEvent.BLOCK_DEACTIVATE, pos);
		} else if (bl2 && !bl) {
			world.playSound(null, pos, this.blockSetType.pressurePlateClickOn(), SoundCategory.BLOCKS);
			world.emitGameEvent(entity, GameEvent.BLOCK_ACTIVATE, pos);
		}

		if (bl2) {
			world.scheduleBlockTick(new BlockPos(pos), this, this.getTickRate());
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!moved && !state.isOf(newState.getBlock())) {
			if (this.getRedstoneOutput(state) > 0) {
				this.updateNeighbors(world, pos);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	protected void updateNeighbors(World world, BlockPos pos) {
		world.updateNeighborsAlways(pos, this);
		world.updateNeighborsAlways(pos.down(), this);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return this.getRedstoneOutput(state);
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return direction == Direction.UP ? this.getRedstoneOutput(state) : 0;
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	protected static int getEntityCount(World world, Box box, Class<? extends Entity> entityClass) {
		return world.getEntitiesByClass(entityClass, box, EntityPredicates.EXCEPT_SPECTATOR.and(entity -> !entity.canAvoidTraps())).size();
	}

	protected abstract int getRedstoneOutput(World world, BlockPos pos);

	protected abstract int getRedstoneOutput(BlockState state);

	protected abstract BlockState setRedstoneOutput(BlockState state, int rsOut);
}
