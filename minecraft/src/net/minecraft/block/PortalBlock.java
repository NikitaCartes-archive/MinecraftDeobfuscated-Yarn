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
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class PortalBlock extends Block {
	public static final EnumProperty<Direction.Axis> AXIS = Properties.AXIS_XZ;
	protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
	protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

	public PortalBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(AXIS, Direction.Axis.X));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
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
		if (world.dimension.hasVisibleSky() && world.getGameRules().getBoolean("doMobSpawning") && random.nextInt(2000) < world.getDifficulty().getId()) {
			while (world.getBlockState(blockPos).getBlock() == this) {
				blockPos = blockPos.down();
			}

			if (world.getBlockState(blockPos).allowsSpawning(world, blockPos, EntityType.ZOMBIE_PIGMAN)) {
				Entity entity = EntityType.ZOMBIE_PIGMAN.spawn(world, null, null, null, blockPos.up(), SpawnType.field_16474, false, false);
				if (entity != null) {
					entity.portalCooldown = entity.getDefaultPortalCooldown();
				}
			}
		}
	}

	public boolean method_10352(IWorld iWorld, BlockPos blockPos) {
		PortalBlock.class_2424 lv = this.method_10351(iWorld, blockPos);
		if (lv != null) {
			lv.method_10363();
			return true;
		} else {
			return false;
		}
	}

	@Nullable
	public PortalBlock.class_2424 method_10351(IWorld iWorld, BlockPos blockPos) {
		PortalBlock.class_2424 lv = new PortalBlock.class_2424(iWorld, blockPos, Direction.Axis.X);
		if (lv.method_10360() && lv.field_11313 == 0) {
			return lv;
		} else {
			PortalBlock.class_2424 lv2 = new PortalBlock.class_2424(iWorld, blockPos, Direction.Axis.Z);
			return lv2.method_10360() && lv2.field_11313 == 0 ? lv2 : null;
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		Direction.Axis axis = direction.getAxis();
		Direction.Axis axis2 = blockState.get(AXIS);
		boolean bl = axis2 != axis && axis.isHorizontal();
		return !bl && blockState2.getBlock() != this && !new PortalBlock.class_2424(iWorld, blockPos, axis2).method_10362()
			? Blocks.field_10124.getDefaultState()
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
			if (world.getBlockState(blockPos.west()).getBlock() != this && world.getBlockState(blockPos.east()).getBlock() != this) {
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
	public ItemStack getPickStack(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return ItemStack.EMPTY;
	}

	@Override
	public BlockState rotate(BlockState blockState, Rotation rotation) {
		switch (rotation) {
			case ROT_270:
			case ROT_90:
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
		builder.with(AXIS);
	}

	public BlockPattern.Result method_10350(IWorld iWorld, BlockPos blockPos) {
		Direction.Axis axis = Direction.Axis.Z;
		PortalBlock.class_2424 lv = new PortalBlock.class_2424(iWorld, blockPos, Direction.Axis.X);
		LoadingCache<BlockPos, CachedBlockPosition> loadingCache = BlockPattern.makeCache(iWorld, true);
		if (!lv.method_10360()) {
			axis = Direction.Axis.X;
			lv = new PortalBlock.class_2424(iWorld, blockPos, Direction.Axis.Z);
		}

		if (!lv.method_10360()) {
			return new BlockPattern.Result(blockPos, Direction.NORTH, Direction.UP, loadingCache, 1, 1, 1);
		} else {
			int[] is = new int[Direction.AxisDirection.values().length];
			Direction direction = lv.field_11314.rotateYCounterclockwise();
			BlockPos blockPos2 = lv.field_11316.up(lv.method_10355() - 1);

			for (Direction.AxisDirection axisDirection : Direction.AxisDirection.values()) {
				BlockPattern.Result result = new BlockPattern.Result(
					direction.getDirection() == axisDirection ? blockPos2 : blockPos2.offset(lv.field_11314, lv.method_10356() - 1),
					Direction.get(axisDirection, axis),
					Direction.UP,
					loadingCache,
					lv.method_10356(),
					lv.method_10355(),
					1
				);

				for (int i = 0; i < lv.method_10356(); i++) {
					for (int j = 0; j < lv.method_10355(); j++) {
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
				direction.getDirection() == axisDirection2 ? blockPos2 : blockPos2.offset(lv.field_11314, lv.method_10356() - 1),
				Direction.get(axisDirection2, axis),
				Direction.UP,
				loadingCache,
				lv.method_10356(),
				lv.method_10355(),
				1
			);
		}
	}

	public static class class_2424 {
		private final IWorld field_11318;
		private final Direction.Axis field_11317;
		private final Direction field_11314;
		private final Direction field_11315;
		private int field_11313;
		@Nullable
		private BlockPos field_11316;
		private int field_11312;
		private int field_11311;

		public class_2424(IWorld iWorld, BlockPos blockPos, Direction.Axis axis) {
			this.field_11318 = iWorld;
			this.field_11317 = axis;
			if (axis == Direction.Axis.X) {
				this.field_11315 = Direction.EAST;
				this.field_11314 = Direction.WEST;
			} else {
				this.field_11315 = Direction.NORTH;
				this.field_11314 = Direction.SOUTH;
			}

			BlockPos blockPos2 = blockPos;

			while (blockPos.getY() > blockPos2.getY() - 21 && blockPos.getY() > 0 && this.method_10359(iWorld.getBlockState(blockPos.down()))) {
				blockPos = blockPos.down();
			}

			int i = this.method_10354(blockPos, this.field_11315) - 1;
			if (i >= 0) {
				this.field_11316 = blockPos.offset(this.field_11315, i);
				this.field_11311 = this.method_10354(this.field_11316, this.field_11314);
				if (this.field_11311 < 2 || this.field_11311 > 21) {
					this.field_11316 = null;
					this.field_11311 = 0;
				}
			}

			if (this.field_11316 != null) {
				this.field_11312 = this.method_10353();
			}
		}

		protected int method_10354(BlockPos blockPos, Direction direction) {
			int i;
			for (i = 0; i < 22; i++) {
				BlockPos blockPos2 = blockPos.offset(direction, i);
				if (!this.method_10359(this.field_11318.getBlockState(blockPos2)) || this.field_11318.getBlockState(blockPos2.down()).getBlock() != Blocks.field_10540) {
					break;
				}
			}

			Block block = this.field_11318.getBlockState(blockPos.offset(direction, i)).getBlock();
			return block == Blocks.field_10540 ? i : 0;
		}

		public int method_10355() {
			return this.field_11312;
		}

		public int method_10356() {
			return this.field_11311;
		}

		protected int method_10353() {
			label56:
			for (this.field_11312 = 0; this.field_11312 < 21; this.field_11312++) {
				for (int i = 0; i < this.field_11311; i++) {
					BlockPos blockPos = this.field_11316.offset(this.field_11314, i).up(this.field_11312);
					BlockState blockState = this.field_11318.getBlockState(blockPos);
					if (!this.method_10359(blockState)) {
						break label56;
					}

					Block block = blockState.getBlock();
					if (block == Blocks.field_10316) {
						this.field_11313++;
					}

					if (i == 0) {
						block = this.field_11318.getBlockState(blockPos.offset(this.field_11315)).getBlock();
						if (block != Blocks.field_10540) {
							break label56;
						}
					} else if (i == this.field_11311 - 1) {
						block = this.field_11318.getBlockState(blockPos.offset(this.field_11314)).getBlock();
						if (block != Blocks.field_10540) {
							break label56;
						}
					}
				}
			}

			for (int i = 0; i < this.field_11311; i++) {
				if (this.field_11318.getBlockState(this.field_11316.offset(this.field_11314, i).up(this.field_11312)).getBlock() != Blocks.field_10540) {
					this.field_11312 = 0;
					break;
				}
			}

			if (this.field_11312 <= 21 && this.field_11312 >= 3) {
				return this.field_11312;
			} else {
				this.field_11316 = null;
				this.field_11311 = 0;
				this.field_11312 = 0;
				return 0;
			}
		}

		protected boolean method_10359(BlockState blockState) {
			Block block = blockState.getBlock();
			return blockState.isAir() || block == Blocks.field_10036 || block == Blocks.field_10316;
		}

		public boolean method_10360() {
			return this.field_11316 != null && this.field_11311 >= 2 && this.field_11311 <= 21 && this.field_11312 >= 3 && this.field_11312 <= 21;
		}

		public void method_10363() {
			for (int i = 0; i < this.field_11311; i++) {
				BlockPos blockPos = this.field_11316.offset(this.field_11314, i);

				for (int j = 0; j < this.field_11312; j++) {
					this.field_11318.setBlockState(blockPos.up(j), Blocks.field_10316.getDefaultState().with(PortalBlock.AXIS, this.field_11317), 18);
				}
			}
		}

		private boolean method_10361() {
			return this.field_11313 >= this.field_11311 * this.field_11312;
		}

		public boolean method_10362() {
			return this.method_10360() && this.method_10361();
		}
	}
}
