package net.minecraft.block;

import com.google.common.cache.LoadingCache;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class NetherPortalBlock extends Block {
	public static final EnumProperty<Direction.Axis> AXIS = Properties.HORIZONTAL_AXIS;
	protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
	protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

	public NetherPortalBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(AXIS, Direction.Axis.X));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		switch ((Direction.Axis)state.get(AXIS)) {
			case Z:
				return Z_SHAPE;
			case X:
			default:
				return X_SHAPE;
		}
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (world.getDimension().isNatural() && world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING) && random.nextInt(2000) < world.getDifficulty().getId()) {
			while (world.getBlockState(pos).isOf(this)) {
				pos = pos.down();
			}

			if (world.getBlockState(pos).allowsSpawning(world, pos, EntityType.ZOMBIFIED_PIGLIN)) {
				Entity entity = EntityType.ZOMBIFIED_PIGLIN.spawn(world, null, null, null, pos.up(), SpawnReason.STRUCTURE, false, false);
				if (entity != null) {
					entity.method_30229();
				}
			}
		}
	}

	public static boolean createPortalAt(WorldAccess worldAccess, BlockPos blockPos) {
		NetherPortalBlock.AreaHelper areaHelper = createAreaHelper(worldAccess, blockPos);
		if (areaHelper != null) {
			areaHelper.createPortal();
			return true;
		} else {
			return false;
		}
	}

	@Nullable
	public static NetherPortalBlock.AreaHelper createAreaHelper(WorldAccess worldAccess, BlockPos blockPos) {
		NetherPortalBlock.AreaHelper areaHelper = new NetherPortalBlock.AreaHelper(worldAccess, blockPos, Direction.Axis.X);
		if (areaHelper.isValid() && areaHelper.foundPortalBlocks == 0) {
			return areaHelper;
		} else {
			NetherPortalBlock.AreaHelper areaHelper2 = new NetherPortalBlock.AreaHelper(worldAccess, blockPos, Direction.Axis.Z);
			return areaHelper2.isValid() && areaHelper2.foundPortalBlocks == 0 ? areaHelper2 : null;
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		Direction.Axis axis = direction.getAxis();
		Direction.Axis axis2 = state.get(AXIS);
		boolean bl = axis2 != axis && axis.isHorizontal();
		return !bl && !newState.isOf(this) && !new NetherPortalBlock.AreaHelper(world, pos, axis2).wasAlreadyValid()
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!entity.hasVehicle() && !entity.hasPassengers() && entity.canUsePortals()) {
			entity.setInNetherPortal(pos);
		}
	}

	@Environment(EnvType.CLIENT)
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

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
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

	public static BlockPattern.Result findPortal(WorldAccess worldAccess, BlockPos world) {
		Direction.Axis axis = Direction.Axis.Z;
		NetherPortalBlock.AreaHelper areaHelper = new NetherPortalBlock.AreaHelper(worldAccess, world, Direction.Axis.X);
		LoadingCache<BlockPos, CachedBlockPosition> loadingCache = BlockPattern.makeCache(worldAccess, true);
		if (!areaHelper.isValid()) {
			axis = Direction.Axis.X;
			areaHelper = new NetherPortalBlock.AreaHelper(worldAccess, world, Direction.Axis.Z);
		}

		if (!areaHelper.isValid()) {
			return new BlockPattern.Result(world, Direction.NORTH, Direction.UP, loadingCache, 1, 1, 1);
		} else {
			int[] is = new int[Direction.AxisDirection.values().length];
			Direction direction = areaHelper.negativeDir.rotateYCounterclockwise();
			BlockPos blockPos = areaHelper.lowerCorner.up(areaHelper.getHeight() - 1);

			for (Direction.AxisDirection axisDirection : Direction.AxisDirection.values()) {
				BlockPattern.Result result = new BlockPattern.Result(
					direction.getDirection() == axisDirection ? blockPos : blockPos.offset(areaHelper.negativeDir, areaHelper.getWidth() - 1),
					Direction.get(axisDirection, axis),
					Direction.UP,
					loadingCache,
					areaHelper.getWidth(),
					areaHelper.getHeight(),
					1
				);

				for (int i = 0; i < areaHelper.getWidth(); i++) {
					for (int j = 0; j < areaHelper.getHeight(); j++) {
						CachedBlockPosition cachedBlockPosition = result.translate(i, j, 1);
						if (!cachedBlockPosition.getBlockState().isAir()) {
							is[axisDirection.ordinal()]++;
						}
					}
				}
			}

			Direction.AxisDirection axisDirection2 = Direction.AxisDirection.POSITIVE;

			for (Direction.AxisDirection axisDirection3 : Direction.AxisDirection.values()) {
				if (is[axisDirection3.ordinal()] < is[axisDirection2.ordinal()]) {
					axisDirection2 = axisDirection3;
				}
			}

			return new BlockPattern.Result(
				direction.getDirection() == axisDirection2 ? blockPos : blockPos.offset(areaHelper.negativeDir, areaHelper.getWidth() - 1),
				Direction.get(axisDirection2, axis),
				Direction.UP,
				loadingCache,
				areaHelper.getWidth(),
				areaHelper.getHeight(),
				1
			);
		}
	}

	public static class AreaHelper {
		private final WorldAccess world;
		private final Direction.Axis axis;
		private final Direction negativeDir;
		private final Direction positiveDir;
		private int foundPortalBlocks;
		@Nullable
		private BlockPos lowerCorner;
		private int height;
		private int width;

		public AreaHelper(WorldAccess world, BlockPos pos, Direction.Axis axis) {
			this.world = world;
			this.axis = axis;
			if (axis == Direction.Axis.X) {
				this.positiveDir = Direction.EAST;
				this.negativeDir = Direction.WEST;
			} else {
				this.positiveDir = Direction.NORTH;
				this.negativeDir = Direction.SOUTH;
			}

			BlockPos blockPos = pos;

			while (pos.getY() > blockPos.getY() - 21 && pos.getY() > 0 && this.validStateInsidePortal(world.getBlockState(pos.down()))) {
				pos = pos.down();
			}

			int i = this.distanceToPortalEdge(pos, this.positiveDir) - 1;
			if (i >= 0) {
				this.lowerCorner = pos.offset(this.positiveDir, i);
				this.width = this.distanceToPortalEdge(this.lowerCorner, this.negativeDir);
				if (this.width < 2 || this.width > 21) {
					this.lowerCorner = null;
					this.width = 0;
				}
			}

			if (this.lowerCorner != null) {
				this.height = this.findHeight();
			}
		}

		protected int distanceToPortalEdge(BlockPos pos, Direction dir) {
			int i;
			for (i = 0; i < 22; i++) {
				BlockPos blockPos = pos.offset(dir, i);
				if (!this.validStateInsidePortal(this.world.getBlockState(blockPos)) || !this.world.getBlockState(blockPos.down()).isOf(Blocks.OBSIDIAN)) {
					break;
				}
			}

			return this.world.getBlockState(pos.offset(dir, i)).isOf(Blocks.OBSIDIAN) ? i : 0;
		}

		public int getHeight() {
			return this.height;
		}

		public int getWidth() {
			return this.width;
		}

		protected int findHeight() {
			label56:
			for (this.height = 0; this.height < 21; this.height++) {
				for (int i = 0; i < this.width; i++) {
					BlockPos blockPos = this.lowerCorner.offset(this.negativeDir, i).up(this.height);
					BlockState blockState = this.world.getBlockState(blockPos);
					if (!this.validStateInsidePortal(blockState)) {
						break label56;
					}

					if (blockState.isOf(Blocks.NETHER_PORTAL)) {
						this.foundPortalBlocks++;
					}

					if (i == 0
						? !this.world.getBlockState(blockPos.offset(this.positiveDir)).isOf(Blocks.OBSIDIAN)
						: i == this.width - 1 && !this.world.getBlockState(blockPos.offset(this.negativeDir)).isOf(Blocks.OBSIDIAN)) {
						break label56;
					}
				}
			}

			for (int i = 0; i < this.width; i++) {
				if (!this.world.getBlockState(this.lowerCorner.offset(this.negativeDir, i).up(this.height)).isOf(Blocks.OBSIDIAN)) {
					this.height = 0;
					break;
				}
			}

			if (this.height <= 21 && this.height >= 3) {
				return this.height;
			} else {
				this.lowerCorner = null;
				this.width = 0;
				this.height = 0;
				return 0;
			}
		}

		protected boolean validStateInsidePortal(BlockState state) {
			return state.isAir() || state.isIn(BlockTags.FIRE) || state.isOf(Blocks.NETHER_PORTAL);
		}

		public boolean isValid() {
			return this.lowerCorner != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
		}

		public void createPortal() {
			for (int i = 0; i < this.width; i++) {
				BlockPos blockPos = this.lowerCorner.offset(this.negativeDir, i);

				for (int j = 0; j < this.height; j++) {
					this.world.setBlockState(blockPos.up(j), Blocks.NETHER_PORTAL.getDefaultState().with(NetherPortalBlock.AXIS, this.axis), 18);
				}
			}
		}

		private boolean portalAlreadyExisted() {
			return this.foundPortalBlocks >= this.width * this.height;
		}

		public boolean wasAlreadyValid() {
			return this.isValid() && this.portalAlreadyExisted();
		}
	}
}
