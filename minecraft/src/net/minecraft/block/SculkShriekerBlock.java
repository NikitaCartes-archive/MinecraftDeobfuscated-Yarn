package net.minecraft.block;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SculkShriekerBlockEntity;
import net.minecraft.block.entity.SculkShriekerWarningManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ShriekParticleEffect;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;

public class SculkShriekerBlock extends BlockWithEntity {
	protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
	public static final int field_36851 = 90;
	public static final BooleanProperty SHRIEKING = Properties.SHRIEKING;

	public SculkShriekerBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(SHRIEKING, Boolean.valueOf(false)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(SHRIEKING);
	}

	@Override
	public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
		if (entity instanceof PlayerEntity && world instanceof ServerWorld && entity.getType() != EntityType.WARDEN) {
			method_40792((ServerWorld)world, world.getBlockState(pos), pos);
		}

		super.onSteppedOn(world, pos, state, entity);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if ((Boolean)state.get(SHRIEKING)) {
			world.setBlockState(pos, state.with(SHRIEKING, Boolean.valueOf(false)), Block.NOTIFY_ALL);
			method_40799(world, pos).ifPresent(sculkShriekerWarningManager -> sculkShriekerWarningManager.method_40730(world, pos));
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		boolean bl = world.isReceivingRedstonePower(pos);
		if (bl && !world.isClient()) {
			ServerWorld serverWorld = (ServerWorld)world;
			method_40792(serverWorld, state, pos);
		}
	}

	public static boolean method_40795(ServerWorld serverWorld, BlockPos blockPos, BlockState blockState) {
		return !(Boolean)blockState.get(SHRIEKING)
			&& (Boolean)method_40799(serverWorld, blockPos)
				.map(sculkShriekerWarningManager -> sculkShriekerWarningManager.method_40721(serverWorld, blockPos))
				.orElse(false);
	}

	public static void method_40792(ServerWorld serverWorld, BlockState blockState, BlockPos blockPos) {
		method_40799(serverWorld, blockPos)
			.ifPresent(
				sculkShriekerWarningManager -> {
					if (sculkShriekerWarningManager.method_40723(serverWorld, blockPos, getPlayersInRange(serverWorld, blockPos))) {
						serverWorld.createAndScheduleBlockTick(blockPos, blockState.getBlock(), 90);
						serverWorld.setBlockState(blockPos, blockState.with(SHRIEKING, Boolean.valueOf(true)), Block.NOTIFY_LISTENERS);
						float f = 2.0F;
						serverWorld.playSound(
							null,
							(double)blockPos.getX(),
							(double)blockPos.getY(),
							(double)blockPos.getZ(),
							SoundEvents.BLOCK_SCULK_SHRIEKER_SHRIEK,
							SoundCategory.BLOCKS,
							2.0F,
							0.6F + serverWorld.random.nextFloat() * 0.4F
						);
						serverWorld.emitGameEvent(null, GameEvent.SCULK_SENSOR_TENDRILS_CLICKING, blockPos);

						for (int i = 0; i < 10; i++) {
							int j = i * 5;
							serverWorld.spawnParticles(
								new ShriekParticleEffect(j), (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, 1, 0.0, 0.0, 0.0, 0.0
							);
						}
					}
				}
			);
	}

	private static List<ServerPlayerEntity> getPlayersInRange(ServerWorld world, BlockPos pos) {
		Vec3d vec3d = Vec3d.ofCenter(pos);
		double d = 16.0;
		return world.getPlayers(player -> vec3d.squaredDistanceTo(player.getPos()) < 256.0);
	}

	private static Optional<SculkShriekerWarningManager> method_40799(ServerWorld serverWorld, BlockPos blockPos) {
		PlayerEntity playerEntity = serverWorld.getClosestPlayer(
			(double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 16.0, EntityPredicates.EXCEPT_SPECTATOR
		);
		return playerEntity != null ? Optional.of(playerEntity.getSculkShriekerWarningManager()) : Optional.empty();
	}

	@Override
	public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
		return SHAPE;
	}

	@Override
	public boolean hasSidedTransparency(BlockState state) {
		return true;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new SculkShriekerBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> GameEventListener getGameEventListener(World world, T blockEntity) {
		return blockEntity instanceof SculkShriekerBlockEntity ? ((SculkShriekerBlockEntity)blockEntity).getVibrationListener() : null;
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return !world.isClient
			? checkType(type, BlockEntityType.SCULK_SHRIEKER, (worldx, pos, statex, blockEntity) -> blockEntity.getVibrationListener().tick(worldx))
			: null;
	}
}
