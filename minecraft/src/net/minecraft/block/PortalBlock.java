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
	public static final EnumProperty<Direction.Axis> field_11310 = Properties.field_12529;
	protected static final VoxelShape field_11309 = Block.method_9541(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
	protected static final VoxelShape field_11308 = Block.method_9541(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

	public PortalBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11310, Direction.Axis.X));
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		switch ((Direction.Axis)blockState.method_11654(field_11310)) {
			case Z:
				return field_11308;
			case X:
			default:
				return field_11309;
		}
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (world.field_9247.hasVisibleSky() && world.getGameRules().getBoolean(GameRules.field_19390) && random.nextInt(2000) < world.getDifficulty().getId()) {
			while (world.method_8320(blockPos).getBlock() == this) {
				blockPos = blockPos.down();
			}

			if (world.method_8320(blockPos).allowsSpawning(world, blockPos, EntityType.field_6050)) {
				Entity entity = EntityType.field_6050.method_5899(world, null, null, null, blockPos.up(), SpawnType.field_16474, false, false);
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
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		Direction.Axis axis = direction.getAxis();
		Direction.Axis axis2 = blockState.method_11654(field_11310);
		boolean bl = axis2 != axis && axis.isHorizontal();
		return !bl && blockState2.getBlock() != this && !new PortalBlock.AreaHelper(iWorld, blockPos, axis2).wasAlreadyValid()
			? Blocks.field_10124.method_9564()
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.field_9179;
	}

	@Override
	public void method_9548(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		if (!entity.hasVehicle() && !entity.hasPassengers() && entity.canUsePortals()) {
			entity.setInPortal(blockPos);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (random.nextInt(100) == 0) {
			world.playSound(
				(double)blockPos.getX() + 0.5,
				(double)blockPos.getY() + 0.5,
				(double)blockPos.getZ() + 0.5,
				SoundEvents.field_14802,
				SoundCategory.field_15245,
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
			if (world.method_8320(blockPos.west()).getBlock() != this && world.method_8320(blockPos.east()).getBlock() != this) {
				d = (double)blockPos.getX() + 0.5 + 0.25 * (double)k;
				g = (double)(random.nextFloat() * 2.0F * (float)k);
			} else {
				f = (double)blockPos.getZ() + 0.5 + 0.25 * (double)k;
				j = (double)(random.nextFloat() * 2.0F * (float)k);
			}

			world.addParticle(ParticleTypes.field_11214, d, e, f, g, h, j);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack method_9574(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return ItemStack.EMPTY;
	}

	@Override
	public BlockState method_9598(BlockState blockState, BlockRotation blockRotation) {
		switch (blockRotation) {
			case field_11465:
			case field_11463:
				switch ((Direction.Axis)blockState.method_11654(field_11310)) {
					case Z:
						return blockState.method_11657(field_11310, Direction.Axis.X);
					case X:
						return blockState.method_11657(field_11310, Direction.Axis.Z);
					default:
						return blockState;
				}
			default:
				return blockState;
		}
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11310);
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
			return new BlockPattern.Result(blockPos, Direction.field_11043, Direction.field_11036, loadingCache, 1, 1, 1);
		} else {
			int[] is = new int[Direction.AxisDirection.values().length];
			Direction direction = areaHelper.negativeDir.rotateYCounterclockwise();
			BlockPos blockPos2 = areaHelper.lowerCorner.up(areaHelper.getHeight() - 1);

			for (Direction.AxisDirection axisDirection : Direction.AxisDirection.values()) {
				BlockPattern.Result result = new BlockPattern.Result(
					direction.getDirection() == axisDirection ? blockPos2 : blockPos2.offset(areaHelper.negativeDir, areaHelper.getWidth() - 1),
					Direction.get(axisDirection, axis),
					Direction.field_11036,
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
				Direction.field_11036,
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
				this.positiveDir = Direction.field_11034;
				this.negativeDir = Direction.field_11039;
			} else {
				this.positiveDir = Direction.field_11043;
				this.negativeDir = Direction.field_11035;
			}

			BlockPos blockPos2 = blockPos;

			while (blockPos.getY() > blockPos2.getY() - 21 && blockPos.getY() > 0 && this.method_10359(iWorld.method_8320(blockPos.down()))) {
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
				if (!this.method_10359(this.world.method_8320(blockPos2)) || this.world.method_8320(blockPos2.down()).getBlock() != Blocks.field_10540) {
					break;
				}
			}

			Block block = this.world.method_8320(blockPos.offset(direction, i)).getBlock();
			return block == Blocks.field_10540 ? i : 0;
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
					BlockState blockState = this.world.method_8320(blockPos);
					if (!this.method_10359(blockState)) {
						break label56;
					}

					Block block = blockState.getBlock();
					if (block == Blocks.field_10316) {
						this.foundPortalBlocks++;
					}

					if (i == 0) {
						block = this.world.method_8320(blockPos.offset(this.positiveDir)).getBlock();
						if (block != Blocks.field_10540) {
							break label56;
						}
					} else if (i == this.width - 1) {
						block = this.world.method_8320(blockPos.offset(this.negativeDir)).getBlock();
						if (block != Blocks.field_10540) {
							break label56;
						}
					}
				}
			}

			for (int i = 0; i < this.width; i++) {
				if (this.world.method_8320(this.lowerCorner.offset(this.negativeDir, i).up(this.height)).getBlock() != Blocks.field_10540) {
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

		protected boolean method_10359(BlockState blockState) {
			Block block = blockState.getBlock();
			return blockState.isAir() || block == Blocks.field_10036 || block == Blocks.field_10316;
		}

		public boolean isValid() {
			return this.lowerCorner != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
		}

		public void createPortal() {
			for (int i = 0; i < this.width; i++) {
				BlockPos blockPos = this.lowerCorner.offset(this.negativeDir, i);

				for (int j = 0; j < this.height; j++) {
					this.world.method_8652(blockPos.up(j), Blocks.field_10316.method_9564().method_11657(PortalBlock.field_11310, this.axis), 18);
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
