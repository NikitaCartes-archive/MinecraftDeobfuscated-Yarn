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

	public PistonBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(BlockEntityType.PISTON, blockPos, blockState);
	}

	public PistonBlockEntity(BlockPos blockPos, BlockState blockState, BlockState blockState2, Direction direction, boolean bl, boolean bl2) {
		this(blockPos, blockState);
		this.pushedBlock = blockState2;
		this.facing = direction;
		this.extending = bl;
		this.source = bl2;
	}

	@Override
	public CompoundTag toInitialChunkDataTag() {
		return this.toTag(new CompoundTag());
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

	private static void pushEntities(World world, BlockPos blockPos, float f, PistonBlockEntity pistonBlockEntity) {
		Direction direction = pistonBlockEntity.getMovementDirection();
		double d = (double)(f - pistonBlockEntity.progress);
		VoxelShape voxelShape = pistonBlockEntity.getHeadBlockState().getCollisionShape(world, blockPos);
		if (!voxelShape.isEmpty()) {
			Box box = offsetHeadBox(blockPos, voxelShape.getBoundingBox(), pistonBlockEntity);
			List<Entity> list = world.getOtherEntities(null, Boxes.stretch(box, direction, d).union(box));
			if (!list.isEmpty()) {
				List<Box> list2 = voxelShape.getBoundingBoxes();
				boolean bl = pistonBlockEntity.pushedBlock.isOf(Blocks.SLIME_BLOCK);
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
						Box box3 = Boxes.stretch(offsetHeadBox(blockPos, box2, pistonBlockEntity), direction, d);
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
						if (!pistonBlockEntity.extending && pistonBlockEntity.source) {
							push(blockPos, entity, direction, d);
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

	private static Box offsetHeadBox(BlockPos blockPos, Box box, PistonBlockEntity pistonBlockEntity) {
		double d = (double)pistonBlockEntity.getAmountExtended(pistonBlockEntity.progress);
		return box.offset(
			(double)blockPos.getX() + d * (double)pistonBlockEntity.facing.getOffsetX(),
			(double)blockPos.getY() + d * (double)pistonBlockEntity.facing.getOffsetY(),
			(double)blockPos.getZ() + d * (double)pistonBlockEntity.facing.getOffsetZ()
		);
	}

	private static void push(BlockPos blockPos, Entity entity, Direction direction, double amount) {
		Box box = entity.getBoundingBox();
		Box box2 = VoxelShapes.fullCube().getBoundingBox().offset(blockPos);
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

	public static void tick(World world, BlockPos blockPos, BlockState blockState, PistonBlockEntity pistonBlockEntity) {
		pistonBlockEntity.savedWorldTime = world.getTime();
		pistonBlockEntity.lastProgress = pistonBlockEntity.progress;
		if (pistonBlockEntity.lastProgress >= 1.0F) {
			if (world.isClient && pistonBlockEntity.field_26705 < 5) {
				pistonBlockEntity.field_26705++;
			} else {
				world.removeBlockEntity(blockPos);
				pistonBlockEntity.markRemoved();
				if (pistonBlockEntity.pushedBlock != null && world.getBlockState(blockPos).isOf(Blocks.MOVING_PISTON)) {
					BlockState blockState2 = Block.postProcessState(pistonBlockEntity.pushedBlock, world, blockPos);
					if (blockState2.isAir()) {
						world.setBlockState(blockPos, pistonBlockEntity.pushedBlock, 84);
						Block.replace(pistonBlockEntity.pushedBlock, blockState2, world, blockPos, 3);
					} else {
						if (blockState2.contains(Properties.WATERLOGGED) && (Boolean)blockState2.get(Properties.WATERLOGGED)) {
							blockState2 = blockState2.with(Properties.WATERLOGGED, Boolean.valueOf(false));
						}

						world.setBlockState(blockPos, blockState2, 67);
						world.updateNeighbor(blockPos, blockState2.getBlock(), blockPos);
					}
				}
			}
		} else {
			float f = pistonBlockEntity.progress + 0.5F;
			pushEntities(world, blockPos, f, pistonBlockEntity);
			method_23674(world, blockPos, f, pistonBlockEntity);
			pistonBlockEntity.progress = f;
			if (pistonBlockEntity.progress >= 1.0F) {
				pistonBlockEntity.progress = 1.0F;
			}
		}
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.pushedBlock = NbtHelper.toBlockState(compoundTag.getCompound("blockState"));
		this.facing = Direction.byId(compoundTag.getInt("facing"));
		this.progress = compoundTag.getFloat("progress");
		this.lastProgress = this.progress;
		this.extending = compoundTag.getBoolean("extending");
		this.source = compoundTag.getBoolean("source");
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
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

	@Environment(EnvType.CLIENT)
	@Override
	public double getSquaredRenderDistance() {
		return 68.0;
	}
}
