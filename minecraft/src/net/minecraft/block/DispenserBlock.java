package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.PositionImpl;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DispenserBlock extends Block {
	public static final DirectionProperty FACING = FacingBlock.FACING;
	public static final BooleanProperty TRIGGERED = Properties.TRIGGERED;
	private static final Map<Item, DispenserBehavior> BEHAVIORS = Util.make(
		new Object2ObjectOpenHashMap<>(), map -> map.defaultReturnValue(new ItemDispenserBehavior())
	);
	private static final int SCHEDULED_TICK_DELAY = 4;

	protected DispenserBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(TRIGGERED, Boolean.valueOf(false)));
	}

	public static void registerBehavior(ItemConvertible provider, DispenserBehavior behavior) {
	}

	protected Vec3d method_42874(BlockState blockState) {
		return Vec3d.of(((Direction)blockState.get(FACING)).getVector());
	}

	protected void dispense(ServerWorld world, BlockPos pos, BlockState blockState) {
		BlockPos blockPos = pos.offset(blockState.get(FACING));
		BlockState blockState2 = world.getBlockState(blockPos);
		if (!blockState2.isAir() && (!blockState2.isOf(Blocks.WATER) && !blockState2.isOf(Blocks.LAVA) || blockState2.getFluidState().isStill())) {
			FallingBlockEntity var6 = FallingBlockEntity.method_42818(world, blockPos, blockState2, this.method_42874(blockState).add(0.0, 0.1, 0.0));
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		boolean bl = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up());
		boolean bl2 = (Boolean)state.get(TRIGGERED);
		if (bl && !bl2) {
			world.createAndScheduleBlockTick(pos, this, 4);
			world.setBlockState(pos, state.with(TRIGGERED, Boolean.valueOf(true)), Block.NO_REDRAW);
		} else if (!bl && bl2) {
			world.setBlockState(pos, state.with(TRIGGERED, Boolean.valueOf(false)), Block.NO_REDRAW);
		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		this.dispense(world, pos, state);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
	}

	public static Position getOutputLocation(BlockPointer pointer) {
		Direction direction = pointer.getBlockState().get(FACING);
		double d = pointer.getX() + 0.7 * (double)direction.getOffsetX();
		double e = pointer.getY() + 0.7 * (double)direction.getOffsetY();
		double f = pointer.getZ() + 0.7 * (double)direction.getOffsetZ();
		return new PositionImpl(d, e, f);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
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
		builder.add(FACING, TRIGGERED);
	}
}
