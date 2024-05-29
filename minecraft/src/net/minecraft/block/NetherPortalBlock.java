package net.minecraft.block;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.NetherPortal;
import org.slf4j.Logger;

public class NetherPortalBlock extends Block implements Portal {
	public static final MapCodec<NetherPortalBlock> CODEC = createCodec(NetherPortalBlock::new);
	public static final EnumProperty<Direction.Axis> AXIS = Properties.HORIZONTAL_AXIS;
	private static final Logger LOGGER = LogUtils.getLogger();
	protected static final int field_31196 = 2;
	protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
	protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

	@Override
	public MapCodec<NetherPortalBlock> getCodec() {
		return CODEC;
	}

	public NetherPortalBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(AXIS, Direction.Axis.X));
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		switch ((Direction.Axis)state.get(AXIS)) {
			case Z:
				return Z_SHAPE;
			case X:
			default:
				return X_SHAPE;
		}
	}

	@Override
	protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (world.getDimension().natural() && world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING) && random.nextInt(2000) < world.getDifficulty().getId()) {
			while (world.getBlockState(pos).isOf(this)) {
				pos = pos.down();
			}

			if (world.getBlockState(pos).allowsSpawning(world, pos, EntityType.ZOMBIFIED_PIGLIN)) {
				Entity entity = EntityType.ZOMBIFIED_PIGLIN.spawn(world, pos.up(), SpawnReason.STRUCTURE);
				if (entity != null) {
					entity.resetPortalCooldown();
				}
			}
		}
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		Direction.Axis axis = direction.getAxis();
		Direction.Axis axis2 = state.get(AXIS);
		boolean bl = axis2 != axis && axis.isHorizontal();
		return !bl && !neighborState.isOf(this) && !new NetherPortal(world, pos, axis2).wasAlreadyValid()
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (entity.canUsePortals()) {
			entity.tryUsePortal(this, pos);
		}
	}

	@Override
	public int getPortalDelay(ServerWorld world, Entity entity) {
		return entity instanceof PlayerEntity playerEntity
			? Math.max(
				1,
				world.getGameRules()
					.getInt(playerEntity.getAbilities().invulnerable ? GameRules.PLAYERS_NETHER_PORTAL_CREATIVE_DELAY : GameRules.PLAYERS_NETHER_PORTAL_DEFAULT_DELAY)
			)
			: 0;
	}

	@Nullable
	@Override
	public TeleportTarget createTeleportTarget(ServerWorld world, Entity entity, BlockPos pos) {
		RegistryKey<World> registryKey = world.getRegistryKey() == World.NETHER ? World.OVERWORLD : World.NETHER;
		ServerWorld serverWorld = world.getServer().getWorld(registryKey);
		boolean bl = serverWorld.getRegistryKey() == World.NETHER;
		WorldBorder worldBorder = serverWorld.getWorldBorder();
		double d = DimensionType.getCoordinateScaleFactor(world.getDimension(), serverWorld.getDimension());
		BlockPos blockPos = worldBorder.clamp(entity.getX() * d, entity.getY(), entity.getZ() * d);
		return this.getOrCreateExitPortalTarget(serverWorld, entity, pos, blockPos, bl, worldBorder);
	}

	@Nullable
	private TeleportTarget getOrCreateExitPortalTarget(
		ServerWorld world, Entity entity, BlockPos pos, BlockPos scaledPos, boolean inNether, WorldBorder worldBorder
	) {
		Optional<BlockPos> optional = world.getPortalForcer().getPortalPos(scaledPos, inNether, worldBorder);
		BlockLocating.Rectangle rectangle;
		TeleportTarget.PostDimensionTransition postDimensionTransition;
		if (optional.isPresent()) {
			BlockPos blockPos = (BlockPos)optional.get();
			BlockState blockState = world.getBlockState(blockPos);
			rectangle = BlockLocating.getLargestRectangle(
				blockPos, blockState.get(Properties.HORIZONTAL_AXIS), 21, Direction.Axis.Y, 21, posx -> world.getBlockState(posx) == blockState
			);
			postDimensionTransition = TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(entityx -> entityx.addPortalChunkTicketAt(blockPos));
		} else {
			Direction.Axis axis = (Direction.Axis)entity.getWorld().getBlockState(pos).getOrEmpty(AXIS).orElse(Direction.Axis.X);
			Optional<BlockLocating.Rectangle> optional2 = world.getPortalForcer().createPortal(scaledPos, axis);
			if (optional2.isEmpty()) {
				LOGGER.error("Unable to create a portal, likely target out of worldborder");
				return null;
			}

			rectangle = (BlockLocating.Rectangle)optional2.get();
			postDimensionTransition = TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET);
		}

		return getExitPortalTarget(entity, pos, rectangle, world, postDimensionTransition);
	}

	private static TeleportTarget getExitPortalTarget(
		Entity entity, BlockPos pos, BlockLocating.Rectangle exitPortalRectangle, ServerWorld world, TeleportTarget.PostDimensionTransition postDimensionTransition
	) {
		BlockState blockState = entity.getWorld().getBlockState(pos);
		Direction.Axis axis;
		Vec3d vec3d;
		if (blockState.contains(Properties.HORIZONTAL_AXIS)) {
			axis = blockState.get(Properties.HORIZONTAL_AXIS);
			BlockLocating.Rectangle rectangle = BlockLocating.getLargestRectangle(
				pos, axis, 21, Direction.Axis.Y, 21, posx -> entity.getWorld().getBlockState(posx) == blockState
			);
			vec3d = entity.positionInPortal(axis, rectangle);
		} else {
			axis = Direction.Axis.X;
			vec3d = new Vec3d(0.5, 0.0, 0.0);
		}

		return getExitPortalTarget(world, exitPortalRectangle, axis, vec3d, entity, entity.getVelocity(), entity.getYaw(), entity.getPitch(), postDimensionTransition);
	}

	private static TeleportTarget getExitPortalTarget(
		ServerWorld world,
		BlockLocating.Rectangle exitPortalRectangle,
		Direction.Axis axis,
		Vec3d positionInPortal,
		Entity entity,
		Vec3d velocity,
		float yaw,
		float pitch,
		TeleportTarget.PostDimensionTransition postDimensionTransition
	) {
		BlockPos blockPos = exitPortalRectangle.lowerLeft;
		BlockState blockState = world.getBlockState(blockPos);
		Direction.Axis axis2 = (Direction.Axis)blockState.getOrEmpty(Properties.HORIZONTAL_AXIS).orElse(Direction.Axis.X);
		double d = (double)exitPortalRectangle.width;
		double e = (double)exitPortalRectangle.height;
		EntityDimensions entityDimensions = entity.getDimensions(entity.getPose());
		int i = axis == axis2 ? 0 : 90;
		Vec3d vec3d = axis == axis2 ? velocity : new Vec3d(velocity.z, velocity.y, -velocity.x);
		double f = (double)entityDimensions.width() / 2.0 + (d - (double)entityDimensions.width()) * positionInPortal.getX();
		double g = (e - (double)entityDimensions.height()) * positionInPortal.getY();
		double h = 0.5 + positionInPortal.getZ();
		boolean bl = axis2 == Direction.Axis.X;
		Vec3d vec3d2 = new Vec3d((double)blockPos.getX() + (bl ? f : h), (double)blockPos.getY() + g, (double)blockPos.getZ() + (bl ? h : f));
		Vec3d vec3d3 = NetherPortal.findOpenPosition(vec3d2, world, entity, entityDimensions);
		return new TeleportTarget(world, vec3d3, vec3d, yaw + (float)i, pitch, postDimensionTransition);
	}

	@Override
	public Portal.Effect getPortalEffect() {
		return Portal.Effect.CONFUSION;
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (random.nextInt(100) == 0) {
			world.playSound(
				(double)pos.getX() + 0.5,
				(double)pos.getY() + 0.5,
				(double)pos.getZ() + 0.5,
				SoundEvents.BLOCK_PORTAL_AMBIENT,
				SoundCategory.BLOCKS,
				0.5F,
				random.nextFloat() * 0.4F + 0.8F,
				false
			);
		}

		for (int i = 0; i < 4; i++) {
			double d = (double)pos.getX() + random.nextDouble();
			double e = (double)pos.getY() + random.nextDouble();
			double f = (double)pos.getZ() + random.nextDouble();
			double g = ((double)random.nextFloat() - 0.5) * 0.5;
			double h = ((double)random.nextFloat() - 0.5) * 0.5;
			double j = ((double)random.nextFloat() - 0.5) * 0.5;
			int k = random.nextInt(2) * 2 - 1;
			if (!world.getBlockState(pos.west()).isOf(this) && !world.getBlockState(pos.east()).isOf(this)) {
				d = (double)pos.getX() + 0.5 + 0.25 * (double)k;
				g = (double)(random.nextFloat() * 2.0F * (float)k);
			} else {
				f = (double)pos.getZ() + 0.5 + 0.25 * (double)k;
				j = (double)(random.nextFloat() * 2.0F * (float)k);
			}

			world.addParticle(ParticleTypes.PORTAL, d, e, f, g, h, j);
		}
	}

	@Override
	public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	protected BlockState rotate(BlockState state, BlockRotation rotation) {
		switch (rotation) {
			case COUNTERCLOCKWISE_90:
			case CLOCKWISE_90:
				switch ((Direction.Axis)state.get(AXIS)) {
					case Z:
						return state.with(AXIS, Direction.Axis.X);
					case X:
						return state.with(AXIS, Direction.Axis.Z);
					default:
						return state;
				}
			default:
				return state;
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(AXIS);
	}
}
