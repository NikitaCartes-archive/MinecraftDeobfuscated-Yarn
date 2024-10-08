package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CreakingHeartBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class CreakingHeartBlock extends BlockWithEntity {
	public static final MapCodec<CreakingHeartBlock> CODEC = createCodec(CreakingHeartBlock::new);
	public static final EnumProperty<Direction.Axis> AXIS = Properties.AXIS;
	public static final EnumProperty<CreakingHeartBlock.Creaking> CREAKING = Properties.CREAKING;

	@Override
	public MapCodec<CreakingHeartBlock> getCodec() {
		return CODEC;
	}

	protected CreakingHeartBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(AXIS, Direction.Axis.Y).with(CREAKING, CreakingHeartBlock.Creaking.DISABLED));
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new CreakingHeartBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		if (world.isClient) {
			return null;
		} else {
			return state.get((Property<T>)CREAKING) != CreakingHeartBlock.Creaking.DISABLED
				? validateTicker(type, BlockEntityType.CREAKING_HEART, CreakingHeartBlockEntity::tick)
				: null;
		}
	}

	public static boolean isWorldNaturalAndNight(World world) {
		return world.getDimension().natural() && world.isNight();
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (isWorldNaturalAndNight(world)) {
			if (state.get(CREAKING) != CreakingHeartBlock.Creaking.DISABLED) {
				if (random.nextInt(16) == 0 && isSurroundedByPaleOakLogs(world, pos)) {
					world.playSound((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), SoundEvents.BLOCK_CREAKING_HEART_IDLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
				}
			}
		}
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state,
		WorldView world,
		ScheduledTickView tickView,
		BlockPos pos,
		Direction direction,
		BlockPos neighborPos,
		BlockState neighborState,
		Random random
	) {
		BlockState blockState = super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
		return enableIfValid(blockState, world, pos);
	}

	private static BlockState enableIfValid(BlockState state, WorldView world, BlockPos pos) {
		boolean bl = shouldBeEnabled(state, world, pos);
		CreakingHeartBlock.Creaking creaking = state.get(CREAKING);
		return bl && creaking == CreakingHeartBlock.Creaking.DISABLED ? state.with(CREAKING, CreakingHeartBlock.Creaking.DORMANT) : state;
	}

	public static boolean shouldBeEnabled(BlockState state, WorldView world, BlockPos pos) {
		Direction.Axis axis = state.get(AXIS);

		for (Direction direction : axis.getDirections()) {
			BlockState blockState = world.getBlockState(pos.offset(direction));
			if (!blockState.isIn(BlockTags.PALE_OAK_LOGS) || blockState.get(AXIS) != axis) {
				return false;
			}
		}

		return true;
	}

	private static boolean isSurroundedByPaleOakLogs(WorldAccess world, BlockPos pos) {
		for (Direction direction : Direction.values()) {
			BlockPos blockPos = pos.offset(direction);
			BlockState blockState = world.getBlockState(blockPos);
			if (!blockState.isIn(BlockTags.PALE_OAK_LOGS)) {
				return false;
			}
		}

		return true;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return enableIfValid(this.getDefaultState().with(AXIS, ctx.getSide().getAxis()), ctx.getWorld(), ctx.getBlockPos());
	}

	@Override
	protected BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	protected BlockState rotate(BlockState state, BlockRotation rotation) {
		return PillarBlock.changeRotation(state, rotation);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(AXIS, CREAKING);
	}

	@Override
	protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (world.getBlockEntity(pos) instanceof CreakingHeartBlockEntity creakingHeartBlockEntity) {
			creakingHeartBlockEntity.onBreak(null);
		}

		super.onStateReplaced(state, world, pos, newState, moved);
	}

	@Override
	public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (world.getBlockEntity(pos) instanceof CreakingHeartBlockEntity creakingHeartBlockEntity) {
			creakingHeartBlockEntity.onBreak(player.getDamageSources().playerAttack(player));
		}

		return super.onBreak(world, pos, state, player);
	}

	@Override
	protected boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		if (state.get(CREAKING) != CreakingHeartBlock.Creaking.ACTIVE) {
			return 0;
		} else {
			return world.getBlockEntity(pos) instanceof CreakingHeartBlockEntity creakingHeartBlockEntity ? creakingHeartBlockEntity.getComparatorOutput() : 0;
		}
	}

	public static enum Creaking implements StringIdentifiable {
		DISABLED("disabled"),
		DORMANT("dormant"),
		ACTIVE("active");

		private final String name;

		private Creaking(final String name) {
			this.name = name;
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}
