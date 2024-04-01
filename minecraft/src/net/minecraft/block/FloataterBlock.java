package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.class_9515;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.GridCarrierEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Grid;
import net.minecraft.world.World;

public class FloataterBlock extends Block implements GridTickable {
	public static final MapCodec<FloataterBlock> CODEC = createCodec(FloataterBlock::new);
	public static final DirectionProperty FACING = FacingBlock.FACING;
	public static final BooleanProperty TRIGGERED = Properties.TRIGGERED;

	@Override
	protected MapCodec<FloataterBlock> getCodec() {
		return CODEC;
	}

	protected FloataterBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(TRIGGERED, Boolean.valueOf(false)));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection());
	}

	@Override
	protected BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		boolean bl = isRecievingRedstonePower(DoorBlock.isEitherHalfReceivingRedstonePower(world, pos), world, pos);
		boolean bl2 = (Boolean)state.get(TRIGGERED);
		if (bl != bl2) {
			if (bl) {
				world.scheduleBlockTick(pos, this, 1);
				world.setBlockState(pos, state.with(TRIGGERED, Boolean.valueOf(true)), Block.NOTIFY_LISTENERS);
			} else {
				world.setBlockState(pos, state.with(TRIGGERED, Boolean.valueOf(false)), Block.NOTIFY_LISTENERS);
			}
		}
	}

	private static boolean isRecievingRedstonePower(boolean quasiPower, World world, BlockPos pos) {
		return quasiPower && world.isReceivingRedstonePower(pos);
	}

	@Override
	protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		Direction direction = state.get(FACING);
		class_9515 lv = class_9515.method_58990(world, pos, direction);
		if (lv != null) {
			GridCarrierEntity gridCarrierEntity = new GridCarrierEntity(EntityType.GRID_CARRIER, world);
			BlockPos blockPos = lv.minPos();
			gridCarrierEntity.refreshPositionAfterTeleport((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
			gridCarrierEntity.method_58953().setGrid(lv.blocks());
			gridCarrierEntity.method_58953().setBiome(world.getBiome(pos));
			gridCarrierEntity.method_58950(direction, (float)lv.engines() * 0.1F);
			lv.method_58988(world);
			world.spawnEntity(gridCarrierEntity);
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, TRIGGERED);
	}

	@Override
	public void tick(World world, Grid grid, BlockState state, BlockPos pos, Vec3d gridPos, Direction movementDirection) {
		if (world.isClient) {
			Direction direction = state.get(FACING);
			if (movementDirection == direction && (Boolean)state.get(TRIGGERED) && world.getRandom().nextBoolean()) {
				Direction direction2 = direction.getOpposite();
				if (grid.getBlockState(pos.offset(direction2)).isAir()) {
					double d = 0.5;
					gridPos = gridPos.add(0.5, 0.5, 0.5)
						.add((double)direction2.getOffsetX() * 0.5, (double)direction2.getOffsetY() * 0.5, (double)direction2.getOffsetZ() * 0.5);
					world.addParticle(ParticleTypes.CLOUD, gridPos.x, gridPos.y, gridPos.z, 0.0, 0.0, 0.0);
				}
			}
		}
	}
}
