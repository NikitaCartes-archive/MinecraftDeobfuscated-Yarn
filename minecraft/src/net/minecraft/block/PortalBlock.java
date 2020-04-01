package net.minecraft.block;

import com.google.common.cache.LoadingCache;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.NetherPortalBlockEntity;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.gui.screen.ingame.BookContent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionHashHelper;

public class PortalBlock extends Block {
	public static final EnumProperty<Direction.Axis> AXIS = Properties.HORIZONTAL_AXIS;
	protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
	protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

	public PortalBlock(AbstractBlock.Settings settings) {
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

	public static boolean tryCreatePortal(IWorld world, BlockPos pos, Block frame) {
		PortalBlock.AreaHelper areaHelper = createAreaHelper(world, pos, frame);
		if (areaHelper != null) {
			areaHelper.createPortal();
			return true;
		} else {
			return false;
		}
	}

	@Nullable
	public static PortalBlock.AreaHelper createAreaHelper(IWorld world, BlockPos pos, Block frame) {
		PortalBlock.AreaHelper areaHelper = new PortalBlock.AreaHelper(world, pos, Direction.Axis.X, frame);
		if (areaHelper.isValid() && areaHelper.foundPortalBlocks == 0) {
			return areaHelper;
		} else {
			PortalBlock.AreaHelper areaHelper2 = new PortalBlock.AreaHelper(world, pos, Direction.Axis.Z, frame);
			return areaHelper2.isValid() && areaHelper2.foundPortalBlocks == 0 ? areaHelper2 : null;
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, IWorld world, BlockPos pos, BlockPos posFrom) {
		Direction.Axis axis = direction.getAxis();
		Direction.Axis axis2 = state.get(AXIS);
		boolean bl = axis2 != axis && axis.isHorizontal();
		return !bl && newState.getBlock() != this && !new PortalBlock.AreaHelper(world, pos, axis2, this).wasAlreadyValid()
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (entity instanceof ItemEntity) {
			ItemStack itemStack = ((ItemEntity)entity).getStack();
			if (itemStack.getItem() == Items.WRITTEN_BOOK || itemStack.getItem() == Items.WRITABLE_BOOK) {
				BookContent bookContent = BookContent.fromStack(itemStack);
				String string = (String)IntStream.range(0, bookContent.getPageCount())
					.mapToObj(bookContent::getPage)
					.map(Text::getString)
					.collect(Collectors.joining("\n"));
				if (!string.isEmpty()) {
					int i = DimensionHashHelper.getHash(string);
					this.teleportTo(world, pos, state, i);
					entity.remove();
				}

				return;
			}
		}

		if (!entity.hasVehicle() && !entity.hasPassengers() && entity.canUsePortals()) {
			entity.setInNetherPortal(pos, this);
		}
	}

	private void teleportTo(World world, BlockPos pos, BlockState state, int dimension) {
		Set<BlockPos> set = Sets.<BlockPos>newHashSet();
		Queue<BlockPos> queue = Queues.<BlockPos>newArrayDeque();
		Direction.Axis axis = state.get(AXIS);
		BlockState blockState = Blocks.NEITHER_PORTAL.getDefaultState().with(AXIS, axis);
		Direction direction;
		Direction direction2;
		switch (axis) {
			case Z:
			default:
				direction = Direction.UP;
				direction2 = Direction.SOUTH;
				break;
			case X:
				direction = Direction.UP;
				direction2 = Direction.EAST;
				break;
			case Y:
				direction = Direction.SOUTH;
				direction2 = Direction.EAST;
		}

		Direction direction3 = direction.getOpposite();
		Direction direction4 = direction2.getOpposite();
		queue.add(pos);

		BlockPos blockPos;
		while ((blockPos = (BlockPos)queue.poll()) != null) {
			set.add(blockPos);
			BlockState blockState2 = world.getBlockState(blockPos);
			if (blockState2 == state) {
				world.setBlockState(blockPos, blockState, 18);
				BlockEntity blockEntity = world.getBlockEntity(blockPos);
				if (blockEntity instanceof NetherPortalBlockEntity) {
					((NetherPortalBlockEntity)blockEntity).setDimension(dimension);
				}

				BlockPos blockPos2 = blockPos.offset(direction);
				if (!set.contains(blockPos2)) {
					queue.add(blockPos2);
				}

				blockPos2 = blockPos.offset(direction3);
				if (!set.contains(blockPos2)) {
					queue.add(blockPos2);
				}

				blockPos2 = blockPos.offset(direction2);
				if (!set.contains(blockPos2)) {
					queue.add(blockPos2);
				}

				blockPos2 = blockPos.offset(direction4);
				if (!set.contains(blockPos2)) {
					queue.add(blockPos2);
				}
			}
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
			double d = (double)pos.getX() + (double)random.nextFloat();
			double e = (double)pos.getY() + (double)random.nextFloat();
			double f = (double)pos.getZ() + (double)random.nextFloat();
			double g = ((double)random.nextFloat() - 0.5) * 0.5;
			double h = ((double)random.nextFloat() - 0.5) * 0.5;
			double j = ((double)random.nextFloat() - 0.5) * 0.5;
			int k = random.nextInt(2) * 2 - 1;
			if (world.getBlockState(pos.west()).getBlock() != this && world.getBlockState(pos.east()).getBlock() != this) {
				d = (double)pos.getX() + 0.5 + 0.25 * (double)k;
				g = (double)(random.nextFloat() * 2.0F * (float)k);
			} else {
				f = (double)pos.getZ() + 0.5 + 0.25 * (double)k;
				j = (double)(random.nextFloat() * 2.0F * (float)k);
			}

			world.addParticle(this.createParticleEffect(state, world, pos), d, e, f, g, h, j);
		}
	}

	@Environment(EnvType.CLIENT)
	protected ParticleEffect createParticleEffect(BlockState state, World world, BlockPos pos) {
		return ParticleTypes.PORTAL;
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

	public static BlockPattern.Result method_26482(IWorld iWorld, BlockPos blockPos, Block block) {
		Direction.Axis axis = Direction.Axis.Z;
		PortalBlock.AreaHelper areaHelper = new PortalBlock.AreaHelper(iWorld, blockPos, Direction.Axis.X, block);
		LoadingCache<BlockPos, CachedBlockPosition> loadingCache = BlockPattern.makeCache(iWorld, true);
		if (!areaHelper.isValid()) {
			axis = Direction.Axis.X;
			areaHelper = new PortalBlock.AreaHelper(iWorld, blockPos, Direction.Axis.Z, block);
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
		private final Block frame;

		public AreaHelper(IWorld world, BlockPos pos, Direction.Axis axis, Block frame) {
			this.world = world;
			this.axis = axis;
			this.frame = frame;
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
				if (!this.validStateInsidePortal(this.world.getBlockState(blockPos)) || this.world.getBlockState(blockPos.down()).getBlock() != Blocks.OBSIDIAN) {
					break;
				}
			}

			Block block = this.world.getBlockState(pos.offset(dir, i)).getBlock();
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
					if (block == this.frame) {
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

		protected boolean validStateInsidePortal(BlockState state) {
			Block block = state.getBlock();
			return state.isAir() || state.isIn(BlockTags.FIRE) || block == this.frame;
		}

		public boolean isValid() {
			return this.lowerCorner != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
		}

		public void createPortal() {
			for (int i = 0; i < this.width; i++) {
				BlockPos blockPos = this.lowerCorner.offset(this.negativeDir, i);

				for (int j = 0; j < this.height; j++) {
					this.world.setBlockState(blockPos.up(j), this.frame.getDefaultState().with(PortalBlock.AXIS, this.axis), 18);
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
