package net.minecraft.block.entity;

import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Boxes;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * A piston block entity represents the block being pushed by a piston.
 */
public class PistonBlockEntity extends BlockEntity {
	private BlockState pushedBlock;
	private Direction facing;
	private boolean extending;
	private boolean source;
	private static final ThreadLocal<Direction> field_12205 = ThreadLocal.withInitial(() -> null);
	private float progress;
	private float lastProgress;
	private long savedWorldTime;
	private int field_26705;

	public PistonBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.PISTON, pos, state);
	}

	public PistonBlockEntity(BlockPos pos, BlockState state, BlockState pushedBlock, Direction facing, boolean extending, boolean source) {
		this(pos, state);
		this.pushedBlock = pushedBlock;
		this.facing = facing;
		this.extending = extending;
		this.source = source;
	}

	@Override
	public CompoundTag toInitialChunkDataNbt() {
		return this.writeNbt(new CompoundTag());
	}

	public boolean isExtending() {
		return this.extending;
	}

	public Direction getFacing() {
		return this.facing;
	}

	public boolean isSource() {
		return this.source;
	}

	public float getProgress(float tickDelta) {
		if (tickDelta > 1.0F) {
			tickDelta = 1.0F;
		}

		return MathHelper.lerp(tickDelta, this.lastProgress, this.progress);
	}

	@Environment(EnvType.CLIENT)
	public float getRenderOffsetX(float tickDelta) {
		return (float)this.facing.getOffsetX() * this.getAmountExtended(this.getProgress(tickDelta));
	}

	@Environment(EnvType.CLIENT)
	public float getRenderOffsetY(float tickDelta) {
		return (float)this.facing.getOffsetY() * this.getAmountExtended(this.getProgress(tickDelta));
	}

	@Environment(EnvType.CLIENT)
	public float getRenderOffsetZ(float tickDelta) {
		return (float)this.facing.getOffsetZ() * this.getAmountExtended(this.getProgress(tickDelta));
	}

	private float getAmountExtended(float progress) {
		return this.extending ? progress - 1.0F : 1.0F - progress;
	}

	private BlockState getHeadBlockState() {
		return !this.isExtending() && this.isSource() && this.pushedBlock.getBlock() instanceof PistonBlock
			? Blocks.PISTON_HEAD
				.getDefaultState()
				.with(PistonHeadBlock.SHORT, Boolean.valueOf(this.progress > 0.25F))
				.with(PistonHeadBlock.TYPE, this.pushedBlock.isOf(Blocks.STICKY_PISTON) ? PistonType.STICKY : PistonType.DEFAULT)
				.with(PistonHeadBlock.FACING, this.pushedBlock.get(PistonBlock.FACING))
			: this.pushedBlock;
	}

	private static void pushEntities(World world, BlockPos pos, float f, PistonBlockEntity blockEntity) {
		Direction direction = blockEntity.getMovementDirection();
		double d = (double)(f - blockEntity.progress);
		VoxelShape voxelShape = blockEntity.getHeadBlockState().getCollisionShape(world, pos);
		if (!voxelShape.isEmpty()) {
			Box box = offsetHeadBox(pos, voxelShape.getBoundingBox(), blockEntity);
			List<Entity> list = world.getOtherEntities(null, Boxes.stretch(box, direction, d).union(box));
			if (!list.isEmpty()) {
				List<Box> list2 = voxelShape.getBoundingBoxes();
				boolean bl = blockEntity.pushedBlock.isOf(Blocks.SLIME_BLOCK);
				Iterator var12 = list.iterator();

				while (true) {
					Entity entity;
					while (true) {
						if (!var12.hasNext()) {
							return;
						}

						entity = (Entity)var12.next();
						if (entity.getPistonBehavior() != PistonBehavior.IGNORE) {
							if (!bl) {
								break;
							}

							if (!(entity instanceof ServerPlayerEntity)) {
								Vec3d vec3d = entity.getVelocity();
								double e = vec3d.x;
								double g = vec3d.y;
								double h = vec3d.z;
								switch (direction.getAxis()) {
									case X:
										e = (double)direction.getOffsetX();
										break;
									case Y:
										g = (double)direction.getOffsetY();
										break;
									case Z:
										h = (double)direction.getOffsetZ();
								}

								entity.setVelocity(e, g, h);
								break;
							}
						}
					}

					double i = 0.0;

					for (Box box2 : list2) {
						Box box3 = Boxes.stretch(offsetHeadBox(pos, box2, blockEntity), direction, d);
						Box box4 = entity.getBoundingBox();
						if (box3.intersects(box4)) {
							i = Math.max(i, getIntersectionSize(box3, direction, box4));
							if (i >= d) {
								break;
							}
						}
					}

					if (!(i <= 0.0)) {
						i = Math.min(i, d) + 0.01;
						method_23672(direction, entity, i, direction);
						if (!blockEntity.extending && blockEntity.source) {
							push(pos, entity, direction, d);
						}
					}
				}
			}
		}
	}

	private static void method_23672(Direction direction, Entity entity, double d, Direction direction2) {
		field_12205.set(direction);
		entity.move(MovementType.PISTON, new Vec3d(d * (double)direction2.getOffsetX(), d * (double)direction2.getOffsetY(), d * (double)direction2.getOffsetZ()));
		field_12205.set(null);
	}

	private static void method_23674(World world, BlockPos blockPos, float f, PistonBlockEntity pistonBlockEntity) {
		if (pistonBlockEntity.isPushingHoneyBlock()) {
			Direction direction = pistonBlockEntity.getMovementDirection();
			if (direction.getAxis().isHorizontal()) {
				double d = pistonBlockEntity.pushedBlock.getCollisionShape(world, blockPos).getMax(Direction.Axis.Y);
				Box box = offsetHeadBox(blockPos, new Box(0.0, d, 0.0, 1.0, 1.5000000999999998, 1.0), pistonBlockEntity);
				double e = (double)(f - pistonBlockEntity.progress);

				for (Entity entity : world.getOtherEntities((Entity)null, box, entityx -> method_23671(box, entityx))) {
					method_23672(direction, entity, e, direction);
				}
			}
		}
	}

	private static boolean method_23671(Box box, Entity entity) {
		return entity.getPistonBehavior() == PistonBehavior.NORMAL
			&& entity.isOnGround()
			&& entity.getX() >= box.minX
			&& entity.getX() <= box.maxX
			&& entity.getZ() >= box.minZ
			&& entity.getZ() <= box.maxZ;
	}

	private boolean isPushingHoneyBlock() {
		return this.pushedBlock.isOf(Blocks.HONEY_BLOCK);
	}

	public Direction getMovementDirection() {
		return this.extending ? this.facing : this.facing.getOpposite();
	}

	private static double getIntersectionSize(Box box, Direction direction, Box box2) {
		switch (direction) {
			case EAST:
				return box.maxX - box2.minX;
			case WEST:
				return box2.maxX - box.minX;
			case UP:
			default:
				return box.maxY - box2.minY;
			case DOWN:
				return box2.maxY - box.minY;
			case SOUTH:
				return box.maxZ - box2.minZ;
			case NORTH:
				return box2.maxZ - box.minZ;
		}
	}

	private static Box offsetHeadBox(BlockPos pos, Box box, PistonBlockEntity blockEntity) {
		double d = (double)blockEntity.getAmountExtended(blockEntity.progress);
		return box.offset(
			(double)pos.getX() + d * (double)blockEntity.facing.getOffsetX(),
			(double)pos.getY() + d * (double)blockEntity.facing.getOffsetY(),
			(double)pos.getZ() + d * (double)blockEntity.facing.getOffsetZ()
		);
	}

	private static void push(BlockPos pos, Entity entity, Direction direction, double amount) {
		Box box = entity.getBoundingBox();
		Box box2 = VoxelShapes.fullCube().getBoundingBox().offset(pos);
		if (box.intersects(box2)) {
			Direction direction2 = direction.getOpposite();
			double d = getIntersectionSize(box2, direction2, box) + 0.01;
			double e = getIntersectionSize(box2, direction2, box.intersection(box2)) + 0.01;
			if (Math.abs(d - e) < 0.01) {
				d = Math.min(d, amount) + 0.01;
				method_23672(direction, entity, d, direction2);
			}
		}
	}

	public BlockState getPushedBlock() {
		return this.pushedBlock;
	}

	public void finish() {
		if (this.world != null && (this.lastProgress < 1.0F || this.world.isClient)) {
			this.progress = 1.0F;
			this.lastProgress = this.progress;
			this.world.removeBlockEntity(this.pos);
			this.markRemoved();
			if (this.world.getBlockState(this.pos).isOf(Blocks.MOVING_PISTON)) {
				BlockState blockState;
				if (this.source) {
					blockState = Blocks.AIR.getDefaultState();
				} else {
					blockState = Block.postProcessState(this.pushedBlock, this.world, this.pos);
				}

				this.world.setBlockState(this.pos, blockState, 3);
				this.world.updateNeighbor(this.pos, blockState.getBlock(), this.pos);
			}
		}
	}

	public static void tick(World world, BlockPos pos, BlockState state, PistonBlockEntity blockEntity) {
		blockEntity.savedWorldTime = world.getTime();
		blockEntity.lastProgress = blockEntity.progress;
		if (blockEntity.lastProgress >= 1.0F) {
			if (world.isClient && blockEntity.field_26705 < 5) {
				blockEntity.field_26705++;
			} else {
				world.removeBlockEntity(pos);
				blockEntity.markRemoved();
				if (blockEntity.pushedBlock != null && world.getBlockState(pos).isOf(Blocks.MOVING_PISTON)) {
					BlockState blockState = Block.postProcessState(blockEntity.pushedBlock, world, pos);
					if (blockState.isAir()) {
						world.setBlockState(pos, blockEntity.pushedBlock, 84);
						Block.replace(blockEntity.pushedBlock, blockState, world, pos, 3);
					} else {
						if (blockState.contains(Properties.WATERLOGGED) && (Boolean)blockState.get(Properties.WATERLOGGED)) {
							blockState = blockState.with(Properties.WATERLOGGED, Boolean.valueOf(false));
						}

						world.setBlockState(pos, blockState, 67);
						world.updateNeighbor(pos, blockState.getBlock(), pos);
					}
				}
			}
		} else {
			float f = blockEntity.progress + 0.5F;
			pushEntities(world, pos, f, blockEntity);
			method_23674(world, pos, f, blockEntity);
			blockEntity.progress = f;
			if (blockEntity.progress >= 1.0F) {
				blockEntity.progress = 1.0F;
			}
		}
	}

	@Override
	public void readNbt(CompoundTag tag) {
		super.readNbt(tag);
		this.pushedBlock = NbtHelper.toBlockState(tag.getCompound("blockState"));
		this.facing = Direction.byId(tag.getInt("facing"));
		this.progress = tag.getFloat("progress");
		this.lastProgress = this.progress;
		this.extending = tag.getBoolean("extending");
		this.source = tag.getBoolean("source");
	}

	@Override
	public CompoundTag writeNbt(CompoundTag tag) {
		super.writeNbt(tag);
		tag.put("blockState", NbtHelper.fromBlockState(this.pushedBlock));
		tag.putInt("facing", this.facing.getId());
		tag.putFloat("progress", this.lastProgress);
		tag.putBoolean("extending", this.extending);
		tag.putBoolean("source", this.source);
		return tag;
	}

	public VoxelShape getCollisionShape(BlockView world, BlockPos pos) {
		VoxelShape voxelShape;
		if (!this.extending && this.source) {
			voxelShape = this.pushedBlock.with(PistonBlock.EXTENDED, Boolean.valueOf(true)).getCollisionShape(world, pos);
		} else {
			voxelShape = VoxelShapes.empty();
		}

		Direction direction = (Direction)field_12205.get();
		if ((double)this.progress < 1.0 && direction == this.getMovementDirection()) {
			return voxelShape;
		} else {
			BlockState blockState;
			if (this.isSource()) {
				blockState = Blocks.PISTON_HEAD
					.getDefaultState()
					.with(PistonHeadBlock.FACING, this.facing)
					.with(PistonHeadBlock.SHORT, Boolean.valueOf(this.extending != 1.0F - this.progress < 0.25F));
			} else {
				blockState = this.pushedBlock;
			}

			float f = this.getAmountExtended(this.progress);
			double d = (double)((float)this.facing.getOffsetX() * f);
			double e = (double)((float)this.facing.getOffsetY() * f);
			double g = (double)((float)this.facing.getOffsetZ() * f);
			return VoxelShapes.union(voxelShape, blockState.getCollisionShape(world, pos).offset(d, e, g));
		}
	}

	public long getSavedWorldTime() {
		return this.savedWorldTime;
	}
}
