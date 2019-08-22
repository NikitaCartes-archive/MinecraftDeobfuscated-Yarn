package net.minecraft.block;

import com.google.common.cache.LoadingCache;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class PortalBlock extends Block {
	public static final EnumProperty<Direction.Axis> AXIS = Properties.HORIZONTAL_AXIS;
	protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
	protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

	public PortalBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(AXIS, Direction.Axis.X));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		switch ((Direction.Axis)blockState.get(AXIS)) {
			case Z:
				return Z_SHAPE;
			case X:
			default:
				return X_SHAPE;
		}
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (world.dimension.hasVisibleSky() && world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING) && random.nextInt(2000) < world.getDifficulty().getId()) {
			while (world.getBlockState(blockPos).getBlock() == this) {
				blockPos = blockPos.down();
			}

			if (world.getBlockState(blockPos).allowsSpawning(world, blockPos, EntityType.ZOMBIE_PIGMAN)) {
				Entity entity = EntityType.ZOMBIE_PIGMAN.spawn(world, null, null, null, blockPos.up(), SpawnType.STRUCTURE, false, false);
				if (entity != null) {
					entity.portalCooldown = entity.getDefaultPortalCooldown();
				}
			}
		}
	}

	public boolean createPortalAt(IWorld iWorld, BlockPos blockPos) {
		PortalBlock.AreaHelper areaHelper = this.createAreaHelper(iWorld, blockPos);
		if (areaHelper != null) {
			areaHelper.createPortal();
			return true;
		} else {
			return false;
		}
	}

	@Nullable
	public PortalBlock.AreaHelper createAreaHelper(IWorld iWorld, BlockPos blockPos) {
		PortalBlock.AreaHelper areaHelper = new PortalBlock.AreaHelper(iWorld, blockPos, Direction.Axis.X);
		if (areaHelper.isValid() && areaHelper.foundPortalBlocks == 0) {
			return areaHelper;
		} else {
			PortalBlock.AreaHelper areaHelper2 = new PortalBlock.AreaHelper(iWorld, blockPos, Direction.Axis.Z);
			return areaHelper2.isValid() && areaHelper2.foundPortalBlocks == 0 ? areaHelper2 : null;
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		Direction.Axis axis = direction.getAxis();
		Direction.Axis axis2 = blockState.get(AXIS);
		boolean bl = axis2 != axis && axis.isHorizontal();
		return !bl && blockState2.getBlock() != this && !new PortalBlock.AreaHelper(iWorld, blockPos, axis2).wasAlreadyValid()
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		if (!entity.hasVehicle() && !entity.hasPassengers() && entity.canUsePortals()) {
			entity.setInPortal(blockPos);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (random.nextInt(100) == 0) {
			world.playSound(
				(double)blockPos.getX() + 0.5,
				(double)blockPos.getY() + 0.5,
				(double)blockPos.getZ() + 0.5,
				SoundEvents.BLOCK_PORTAL_AMBIENT,
				SoundCategory.BLOCKS,
				0.5F,
				random.nextFloat() * 0.4F + 0.8F,
				false
			);
		}

		for (int i = 0; i < 4; i++) {
			double d = (double)((float)blockPos.getX() + random.nextFloat());
			double e = (double)((float)blockPos.getY() + random.nextFloat());
			double f = (double)((float)blockPos.getZ() + random.nextFloat());
			double g = ((double)random.nextFloat() - 0.5) * 0.5;
			double h = ((double)random.nextFloat() - 0.5) * 0.5;
			double j = ((double)random.nextFloat() - 0.5) * 0.5;
			int k = random.nextInt(2) * 2 - 1;
			if (world.getBlockState(blockPos.west()).getBlock() != this && world.getBlockState(blockPos.east()).getBlock() != this) {
				d = (double)blockPos.getX() + 0.5 + 0.25 * (double)k;
				g = (double)(random.nextFloat() * 2.0F * (float)k);
			} else {
				f = (double)blockPos.getZ() + 0.5 + 0.25 * (double)k;
				j = (double)(random.nextFloat() * 2.0F * (float)k);
			}

			world.addParticle(ParticleTypes.PORTAL, d, e, f, g, h, j);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getPickStack(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return ItemStack.EMPTY;
	}

	@Override
	public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
		switch (blockRotation) {
			case COUNTERCLOCKWISE_90:
			case CLOCKWISE_90:
				switch ((Direction.Axis)blockState.get(AXIS)) {
					case Z:
						return blockState.with(AXIS, Direction.Axis.X);
					case X:
						return blockState.with(AXIS, Direction.Axis.Z);
					default:
						return blockState;
				}
			default:
				return blockState;
		}
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(AXIS);
	}

	public BlockPattern.Result findPortal(IWorld iWorld, BlockPos blockPos) {
		Direction.Axis axis = Direction.Axis.Z;
		PortalBlock.AreaHelper areaHelper = new PortalBlock.AreaHelper(iWorld, blockPos, Direction.Axis.X);
		LoadingCache<BlockPos, CachedBlockPosition> loadingCache = BlockPattern.makeCache(iWorld, true);
		if (!areaHelper.isValid()) {
			axis = Direction.Axis.X;
			areaHelper = new PortalBlock.AreaHelper(iWorld, blockPos, Direction.Axis.Z);
		}

		if (!areaHelper.isValid()) {
			return new BlockPattern.Result(blockPos, Direction.NORTH, Direction.UP, loadingCache, 1, 1, 1);
		} else {
			int[] is = new int[Direction.AxisDirection.values().length];
			Direction direction = areaHelper.negativeDir.rotateYCounterclockwise();
			BlockPos blockPos2 = areaHelper.lowerCorner.up(areaHelper.getHeight() - 1);

			for (Direction.AxisDirection axisDirection : Direction.AxisDirection.values()) {
				BlockPattern.Result result = new BlockPattern.Result(
					direction.getDirection() == axisDirection ? blockPos2 : blockPos2.offset(areaHelper.negativeDir, areaHelper.getWidth() - 1),
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
				direction.getDirection() == axisDirection2 ? blockPos2 : blockPos2.offset(areaHelper.negativeDir, areaHelper.getWidth() - 1),
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
		private final IWorld world;
		private final Direction.Axis axis;
		private final Direction negativeDir;
		private final Direction positiveDir;
		private int foundPortalBlocks;
		@Nullable
		private BlockPos lowerCorner;
		private int height;
		private int width;

		public AreaHelper(IWorld iWorld, BlockPos blockPos, Direction.Axis axis) {
			this.world = iWorld;
			this.axis = axis;
			if (axis == Direction.Axis.X) {
				this.positiveDir = Direction.EAST;
				this.negativeDir = Direction.WEST;
			} else {
				this.positiveDir = Direction.NORTH;
				this.negativeDir = Direction.SOUTH;
			}

			BlockPos blockPos2 = blockPos;

			while (blockPos.getY() > blockPos2.getY() - 21 && blockPos.getY() > 0 && this.validStateInsidePortal(iWorld.getBlockState(blockPos.down()))) {
				blockPos = blockPos.down();
			}

			int i = this.distanceToPortalEdge(blockPos, this.positiveDir) - 1;
			if (i >= 0) {
				this.lowerCorner = blockPos.offset(this.positiveDir, i);
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

		protected int distanceToPortalEdge(BlockPos blockPos, Direction direction) {
			int i;
			for (i = 0; i < 22; i++) {
				BlockPos blockPos2 = blockPos.offset(direction, i);
				if (!this.validStateInsidePortal(this.world.getBlockState(blockPos2)) || this.world.getBlockState(blockPos2.down()).getBlock() != Blocks.OBSIDIAN) {
					break;
				}
			}

			Block block = this.world.getBlockState(blockPos.offset(direction, i)).getBlock();
			return block == Blocks.OBSIDIAN ? i : 0;
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

					Block block = blockState.getBlock();
					if (block == Blocks.NETHER_PORTAL) {
						this.foundPortalBlocks++;
					}

					if (i == 0) {
						block = this.world.getBlockState(blockPos.offset(this.positiveDir)).getBlock();
						if (block != Blocks.OBSIDIAN) {
							break label56;
						}
					} else if (i == this.width - 1) {
						block = this.world.getBlockState(blockPos.offset(this.negativeDir)).getBlock();
						if (block != Blocks.OBSIDIAN) {
							break label56;
						}
					}
				}
			}

			for (int i = 0; i < this.width; i++) {
				if (this.world.getBlockState(this.lowerCorner.offset(this.negativeDir, i).up(this.height)).getBlock() != Blocks.OBSIDIAN) {
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

		protected boolean validStateInsidePortal(BlockState blockState) {
			Block block = blockState.getBlock();
			return blockState.isAir() || block == Blocks.FIRE || block == Blocks.NETHER_PORTAL;
		}

		public boolean isValid() {
			return this.lowerCorner != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
		}

		public void createPortal() {
			for (int i = 0; i < this.width; i++) {
				BlockPos blockPos = this.lowerCorner.offset(this.negativeDir, i);

				for (int j = 0; j < this.height; j++) {
					this.world.setBlockState(blockPos.up(j), Blocks.NETHER_PORTAL.getDefaultState().with(PortalBlock.AXIS, this.axis), 18);
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
