package net.minecraft.block;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.class_9797;
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

public class NetherPortalBlock extends Block implements class_9797 {
	public static final MapCodec<NetherPortalBlock> CODEC = createCodec(NetherPortalBlock::new);
	public static final EnumProperty<Direction.Axis> AXIS = Properties.HORIZONTAL_AXIS;
	private static final Logger field_52060 = LogUtils.getLogger();
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
			entity.method_60697(this, pos);
		}
	}

	@Override
	public int method_60772(ServerWorld serverWorld, Entity entity) {
		return entity instanceof PlayerEntity playerEntity
			? Math.max(
				1,
				serverWorld.getGameRules()
					.getInt(playerEntity.getAbilities().invulnerable ? GameRules.PLAYERS_NETHER_PORTAL_CREATIVE_DELAY : GameRules.PLAYERS_NETHER_PORTAL_DEFAULT_DELAY)
			)
			: 0;
	}

	@Nullable
	@Override
	public TeleportTarget method_60770(ServerWorld serverWorld, Entity entity, BlockPos blockPos) {
		RegistryKey<World> registryKey = serverWorld.getRegistryKey() == World.NETHER ? World.OVERWORLD : World.NETHER;
		ServerWorld serverWorld2 = serverWorld.getServer().getWorld(registryKey);
		boolean bl = serverWorld2.getRegistryKey() == World.NETHER;
		WorldBorder worldBorder = serverWorld2.getWorldBorder();
		double d = DimensionType.getCoordinateScaleFactor(serverWorld.getDimension(), serverWorld2.getDimension());
		BlockPos blockPos2 = worldBorder.clamp(entity.getX() * d, entity.getY(), entity.getZ() * d);
		return this.method_60773(serverWorld2, entity, blockPos, blockPos2, bl, worldBorder);
	}

	@Nullable
	private TeleportTarget method_60773(ServerWorld serverWorld, Entity entity, BlockPos blockPos, BlockPos blockPos2, boolean bl, WorldBorder worldBorder) {
		Optional<BlockLocating.Rectangle> optional = serverWorld.getPortalForcer().getPortalRect(blockPos2, bl, worldBorder);
		if (optional.isEmpty()) {
			Direction.Axis axis = (Direction.Axis)entity.getWorld().getBlockState(blockPos).getOrEmpty(AXIS).orElse(Direction.Axis.X);
			Optional<BlockLocating.Rectangle> optional2 = serverWorld.getPortalForcer().createPortal(blockPos2, axis);
			if (optional2.isEmpty()) {
				field_52060.error("Unable to create a portal, likely target out of worldborder");
				return null;
			} else {
				return method_60777(entity, blockPos, (BlockLocating.Rectangle)optional2.get(), serverWorld);
			}
		} else {
			return (TeleportTarget)optional.map(rectangle -> method_60777(entity, blockPos, rectangle, serverWorld)).orElse(null);
		}
	}

	private static TeleportTarget method_60777(Entity entity, BlockPos blockPos, BlockLocating.Rectangle rectangle, ServerWorld serverWorld) {
		BlockState blockState = entity.getWorld().getBlockState(blockPos);
		Direction.Axis axis;
		Vec3d vec3d;
		if (blockState.contains(Properties.HORIZONTAL_AXIS)) {
			axis = blockState.get(Properties.HORIZONTAL_AXIS);
			BlockLocating.Rectangle rectangle2 = BlockLocating.getLargestRectangle(
				blockPos, axis, 21, Direction.Axis.Y, 21, blockPosx -> entity.getWorld().getBlockState(blockPosx) == blockState
			);
			vec3d = entity.positionInPortal(axis, rectangle2);
		} else {
			axis = Direction.Axis.X;
			vec3d = new Vec3d(0.5, 0.0, 0.0);
		}

		return method_60774(serverWorld, rectangle, axis, vec3d, entity, entity.getVelocity(), entity.getYaw(), entity.getPitch());
	}

	private static TeleportTarget method_60774(
		ServerWorld serverWorld, BlockLocating.Rectangle rectangle, Direction.Axis axis, Vec3d vec3d, Entity entity, Vec3d vec3d2, float f, float g
	) {
		BlockPos blockPos = rectangle.lowerLeft;
		BlockState blockState = serverWorld.getBlockState(blockPos);
		Direction.Axis axis2 = (Direction.Axis)blockState.getOrEmpty(Properties.HORIZONTAL_AXIS).orElse(Direction.Axis.X);
		double d = (double)rectangle.width;
		double e = (double)rectangle.height;
		EntityDimensions entityDimensions = entity.getDimensions(entity.getPose());
		int i = axis == axis2 ? 0 : 90;
		Vec3d vec3d3 = axis == axis2 ? vec3d2 : new Vec3d(vec3d2.z, vec3d2.y, -vec3d2.x);
		double h = (double)entityDimensions.width() / 2.0 + (d - (double)entityDimensions.width()) * vec3d.getX();
		double j = (e - (double)entityDimensions.height()) * vec3d.getY();
		double k = 0.5 + vec3d.getZ();
		boolean bl = axis2 == Direction.Axis.X;
		Vec3d vec3d4 = new Vec3d((double)blockPos.getX() + (bl ? h : k), (double)blockPos.getY() + j, (double)blockPos.getZ() + (bl ? k : h));
		Vec3d vec3d5 = NetherPortal.findOpenPosition(vec3d4, serverWorld, entity, entityDimensions);
		return new TeleportTarget(serverWorld, vec3d5, vec3d3, f + (float)i, g);
	}

	@Override
	public class_9797.class_9798 method_60778() {
		return class_9797.class_9798.CONFUSION;
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
