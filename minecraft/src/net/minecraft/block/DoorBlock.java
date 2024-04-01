package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;

public class DoorBlock extends Block {
	public static final MapCodec<DoorBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(BlockSetType.CODEC.fieldOf("block_set_type").forGetter(DoorBlock::getBlockSetType), createSettingsCodec())
				.apply(instance, DoorBlock::new)
	);
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final BooleanProperty OPEN = Properties.OPEN;
	public static final EnumProperty<DoorHinge> HINGE = Properties.DOOR_HINGE;
	public static final BooleanProperty POWERED = Properties.POWERED;
	public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;
	protected static final float field_31083 = 3.0F;
	protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
	protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
	private final BlockSetType blockSetType;

	public static boolean isEitherHalfReceivingRedstonePower(World world, BlockPos pos) {
		return world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up());
	}

	@Override
	public MapCodec<? extends DoorBlock> getCodec() {
		return CODEC;
	}

	protected DoorBlock(BlockSetType type, AbstractBlock.Settings settings) {
		super(settings.sounds(type.soundType()));
		this.blockSetType = type;
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(FACING, Direction.NORTH)
				.with(OPEN, Boolean.valueOf(false))
				.with(HINGE, DoorHinge.LEFT)
				.with(POWERED, Boolean.valueOf(false))
				.with(HALF, DoubleBlockHalf.LOWER)
		);
	}

	public BlockSetType getBlockSetType() {
		return this.blockSetType;
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		Direction direction = state.get(FACING);
		boolean bl = !(Boolean)state.get(OPEN);
		boolean bl2 = state.get(HINGE) == DoorHinge.RIGHT;

		return switch (direction) {
			case SOUTH -> bl ? NORTH_SHAPE : (bl2 ? WEST_SHAPE : EAST_SHAPE);
			case WEST -> bl ? EAST_SHAPE : (bl2 ? NORTH_SHAPE : SOUTH_SHAPE);
			case NORTH -> bl ? SOUTH_SHAPE : (bl2 ? EAST_SHAPE : WEST_SHAPE);
			default -> bl ? WEST_SHAPE : (bl2 ? SOUTH_SHAPE : NORTH_SHAPE);
		};
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		DoubleBlockHalf doubleBlockHalf = state.get(HALF);
		if (direction.getAxis() != Direction.Axis.Y || doubleBlockHalf == DoubleBlockHalf.LOWER != (direction == Direction.UP)) {
			return doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canPlaceAt(world, pos)
				? Blocks.AIR.getDefaultState()
				: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
		} else {
			return neighborState.getBlock() instanceof DoorBlock && neighborState.get(HALF) != doubleBlockHalf
				? neighborState.with(HALF, doubleBlockHalf)
				: Blocks.AIR.getDefaultState();
		}
	}

	@Override
	protected void onExploded(BlockState state, World world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
		if (explosion.getDestructionType() == Explosion.DestructionType.TRIGGER_BLOCK
			&& state.get(HALF) == DoubleBlockHalf.LOWER
			&& !world.isClient()
			&& this.blockSetType.canOpenByWindCharge()
			&& !(Boolean)state.get(POWERED)) {
			this.setOpen(null, world, state, pos, !this.isOpen(state));
		}

		super.onExploded(state, world, pos, explosion, stackMerger);
	}

	@Override
	public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!world.isClient && (player.isCreative() || !player.canHarvest(state))) {
			TallPlantBlock.onBreakInCreative(world, pos, state, player);
		}

		return super.onBreak(world, pos, state, player);
	}

	@Override
	protected boolean canPathfindThrough(BlockState state, NavigationType type) {
		return switch (type) {
			case LAND, AIR -> state.get(OPEN);
			case WATER -> false;
		};
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockPos blockPos = ctx.getBlockPos();
		World world = ctx.getWorld();
		if (blockPos.getY() < world.getTopY() - 1 && world.getBlockState(blockPos.up()).canReplace(ctx)) {
			boolean bl = isEitherHalfReceivingRedstonePower(world, blockPos);
			return this.getDefaultState()
				.with(FACING, ctx.getHorizontalPlayerFacing())
				.with(HINGE, this.getHinge(ctx))
				.with(POWERED, Boolean.valueOf(bl))
				.with(OPEN, Boolean.valueOf(bl))
				.with(HALF, DoubleBlockHalf.LOWER);
		} else {
			return null;
		}
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		world.setBlockState(pos.up(), state.with(HALF, DoubleBlockHalf.UPPER), Block.NOTIFY_ALL);
	}

	private DoorHinge getHinge(ItemPlacementContext ctx) {
		BlockView blockView = ctx.getWorld();
		BlockPos blockPos = ctx.getBlockPos();
		Direction direction = ctx.getHorizontalPlayerFacing();
		BlockPos blockPos2 = blockPos.up();
		Direction direction2 = direction.rotateYCounterclockwise();
		BlockPos blockPos3 = blockPos.offset(direction2);
		BlockState blockState = blockView.getBlockState(blockPos3);
		BlockPos blockPos4 = blockPos2.offset(direction2);
		BlockState blockState2 = blockView.getBlockState(blockPos4);
		Direction direction3 = direction.rotateYClockwise();
		BlockPos blockPos5 = blockPos.offset(direction3);
		BlockState blockState3 = blockView.getBlockState(blockPos5);
		BlockPos blockPos6 = blockPos2.offset(direction3);
		BlockState blockState4 = blockView.getBlockState(blockPos6);
		int i = (blockState.isFullCube(blockView, blockPos3) ? -1 : 0)
			+ (blockState2.isFullCube(blockView, blockPos4) ? -1 : 0)
			+ (blockState3.isFullCube(blockView, blockPos5) ? 1 : 0)
			+ (blockState4.isFullCube(blockView, blockPos6) ? 1 : 0);
		boolean bl = blockState.isOf(this) && blockState.get(HALF) == DoubleBlockHalf.LOWER;
		boolean bl2 = blockState3.isOf(this) && blockState3.get(HALF) == DoubleBlockHalf.LOWER;
		if ((!bl || bl2) && i <= 0) {
			if ((!bl2 || bl) && i >= 0) {
				int j = direction.getOffsetX();
				int k = direction.getOffsetZ();
				Vec3d vec3d = ctx.getHitPos();
				double d = vec3d.x - (double)blockPos.getX();
				double e = vec3d.z - (double)blockPos.getZ();
				return (j >= 0 || !(e < 0.5)) && (j <= 0 || !(e > 0.5)) && (k >= 0 || !(d > 0.5)) && (k <= 0 || !(d < 0.5)) ? DoorHinge.LEFT : DoorHinge.RIGHT;
			} else {
				return DoorHinge.LEFT;
			}
		} else {
			return DoorHinge.RIGHT;
		}
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (!this.blockSetType.canOpenByHand()) {
			return ActionResult.PASS;
		} else {
			state = state.cycle(OPEN);
			world.setBlockState(pos, state, Block.NOTIFY_LISTENERS | Block.REDRAW_ON_MAIN_THREAD);
			this.playOpenCloseSound(player, world, pos, (Boolean)state.get(OPEN));
			world.emitGameEvent(player, this.isOpen(state) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
			return ActionResult.success(world.isClient);
		}
	}

	public boolean isOpen(BlockState state) {
		return (Boolean)state.get(OPEN);
	}

	public void setOpen(@Nullable Entity entity, World world, BlockState state, BlockPos pos, boolean open) {
		if (state.isOf(this) && (Boolean)state.get(OPEN) != open) {
			world.setBlockState(pos, state.with(OPEN, Boolean.valueOf(open)), Block.NOTIFY_LISTENERS | Block.REDRAW_ON_MAIN_THREAD);
			this.playOpenCloseSound(entity, world, pos, open);
			world.emitGameEvent(entity, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
		}
	}

	@Override
	protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		boolean bl = world.isReceivingRedstonePower(pos)
			|| world.isReceivingRedstonePower(pos.offset(state.get(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
		if (!this.getDefaultState().isOf(sourceBlock) && bl != (Boolean)state.get(POWERED)) {
			if (bl != (Boolean)state.get(OPEN)) {
				this.playOpenCloseSound(null, world, pos, bl);
				world.emitGameEvent(null, bl ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
			}

			world.setBlockState(pos, state.with(POWERED, Boolean.valueOf(bl)).with(OPEN, Boolean.valueOf(bl)), Block.NOTIFY_LISTENERS);
		}
	}

	@Override
	protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		BlockPos blockPos = pos.down();
		BlockState blockState = world.getBlockState(blockPos);
		return state.get(HALF) == DoubleBlockHalf.LOWER ? blockState.isSideSolidFullSquare(world, blockPos, Direction.UP) : blockState.isOf(this);
	}

	private void playOpenCloseSound(@Nullable Entity entity, World world, BlockPos pos, boolean open) {
		world.playSound(
			entity, pos, open ? this.blockSetType.doorOpen() : this.blockSetType.doorClose(), SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.1F + 0.9F
		);
	}

	@Override
	protected BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, BlockMirror mirror) {
		return mirror == BlockMirror.NONE ? state : state.rotate(mirror.getRotation(state.get(FACING))).cycle(HINGE);
	}

	@Override
	protected long getRenderingSeed(BlockState state, BlockPos pos) {
		return MathHelper.hashCode(pos.getX(), pos.down(state.get(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), pos.getZ());
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(HALF, FACING, OPEN, HINGE, POWERED);
	}

	public static boolean canOpenByHand(World world, BlockPos pos) {
		return canOpenByHand(world.getBlockState(pos));
	}

	public static boolean canOpenByHand(BlockState state) {
		if (state.getBlock() instanceof DoorBlock doorBlock && doorBlock.getBlockSetType().canOpenByHand()) {
			return true;
		}

		return false;
	}
}
