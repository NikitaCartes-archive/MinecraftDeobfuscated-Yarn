package net.minecraft.block.entity;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4623;
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
import net.minecraft.state.property.Properties;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class PistonBlockEntity extends BlockEntity implements Tickable {
	private BlockState pushedBlock;
	private Direction facing;
	private boolean extending;
	private boolean source;
	private static final ThreadLocal<Direction> field_12205 = ThreadLocal.withInitial(() -> null);
	private float progress;
	private float lastProgress;
	private long savedWorldTime;

	public PistonBlockEntity() {
		super(BlockEntityType.PISTON);
	}

	public PistonBlockEntity(BlockState blockState, Direction direction, boolean bl, boolean bl2) {
		this();
		this.pushedBlock = blockState;
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

	public float getProgress(float f) {
		if (f > 1.0F) {
			f = 1.0F;
		}

		return MathHelper.lerp(f, this.lastProgress, this.progress);
	}

	@Environment(EnvType.CLIENT)
	public float getRenderOffsetX(float f) {
		return (float)this.facing.getOffsetX() * this.getAmountExtended(this.getProgress(f));
	}

	@Environment(EnvType.CLIENT)
	public float getRenderOffsetY(float f) {
		return (float)this.facing.getOffsetY() * this.getAmountExtended(this.getProgress(f));
	}

	@Environment(EnvType.CLIENT)
	public float getRenderOffsetZ(float f) {
		return (float)this.facing.getOffsetZ() * this.getAmountExtended(this.getProgress(f));
	}

	private float getAmountExtended(float f) {
		return this.extending ? f - 1.0F : 1.0F - f;
	}

	private BlockState getHeadBlockState() {
		return !this.isExtending() && this.isSource() && this.pushedBlock.getBlock() instanceof PistonBlock
			? Blocks.PISTON_HEAD
				.getDefaultState()
				.with(PistonHeadBlock.TYPE, this.pushedBlock.getBlock() == Blocks.STICKY_PISTON ? PistonType.STICKY : PistonType.DEFAULT)
				.with(PistonHeadBlock.FACING, this.pushedBlock.get(PistonBlock.FACING))
			: this.pushedBlock;
	}

	private void pushEntities(float f) {
		Direction direction = this.getMovementDirection();
		double d = (double)(f - this.progress);
		VoxelShape voxelShape = this.getHeadBlockState().getCollisionShape(this.world, this.getPos());
		if (!voxelShape.isEmpty()) {
			List<Box> list = voxelShape.getBoundingBoxes();
			Box box = this.offsetHeadBox(this.getApproximateHeadBox(list));
			Box box2 = class_4623.method_23362(box, direction, d).union(box);
			List<Entity> list2 = this.world.getEntities(null, box2);
			if (!list2.isEmpty()) {
				boolean bl = this.method_23366();
				boolean bl2 = this.pushedBlock.getBlock() == Blocks.SLIME_BLOCK;

				for (Entity entity : list2) {
					if (entity.getPistonBehavior() != PistonBehavior.IGNORE) {
						if (bl2) {
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
						}

						double i = 0.0;

						for (Box box3 : list) {
							Box box4 = entity.getBoundingBox();
							List<Box> list3 = class_4623.method_23363(bl, this.offsetHeadBox(box3), direction, d);
							Box box5 = this.method_23365(box4, list3);
							if (box5 != null) {
								i = Math.max(i, this.getIntersectionSize(box5, direction, box4));
								if (i >= d) {
									break;
								}
							}
						}

						if (!(i <= 0.0)) {
							i = Math.min(i, d) + 0.01;
							field_12205.set(direction);
							entity.move(MovementType.PISTON, new Vec3d(i * (double)direction.getOffsetX(), i * (double)direction.getOffsetY(), i * (double)direction.getOffsetZ()));
							field_12205.set(null);
							if (!this.extending && this.source) {
								this.push(entity, direction, d);
							}
						}
					}
				}
			}
		}
	}

	@Nullable
	private Box method_23365(Box box, List<Box> list) {
		for (Box box2 : list) {
			if (box.intersects(box2)) {
				return box2;
			}
		}

		return null;
	}

	private boolean method_23366() {
		if (method_23364(this.pushedBlock.getBlock())) {
			return true;
		} else {
			BlockEntity blockEntity = this.world.getBlockEntity(this.getPos().method_10074());
			if (blockEntity instanceof PistonBlockEntity) {
				BlockState blockState = ((PistonBlockEntity)blockEntity).getPushedBlock();
				double d = this.pushedBlock.getCollisionShape(this.world, this.getPos()).getMaximum(Direction.Axis.Y);
				if (d <= 0.5 && method_23364(blockState.getBlock())) {
					return true;
				}
			}

			return false;
		}
	}

	private static boolean method_23364(Block block) {
		return block == Blocks.HONEY_BLOCK;
	}

	public Direction getMovementDirection() {
		return this.extending ? this.facing : this.facing.getOpposite();
	}

	private Box getApproximateHeadBox(List<Box> list) {
		double d = 0.0;
		double e = 0.0;
		double f = 0.0;
		double g = 1.0;
		double h = 1.0;
		double i = 1.0;

		for (Box box : list) {
			d = Math.min(box.minX, d);
			e = Math.min(box.minY, e);
			f = Math.min(box.minZ, f);
			g = Math.max(box.maxX, g);
			h = Math.max(box.maxY, h);
			i = Math.max(box.maxZ, i);
		}

		return new Box(d, e, f, g, h, i);
	}

	private double getIntersectionSize(Box box, Direction direction, Box box2) {
		switch (direction.getAxis()) {
			case X:
				return getXIntersectionSize(box, direction, box2);
			case Y:
			default:
				return getYIntersectionSize(box, direction, box2);
			case Z:
				return getZIntersectionSize(box, direction, box2);
		}
	}

	private Box offsetHeadBox(Box box) {
		double d = (double)this.getAmountExtended(this.progress);
		return box.offset(
			(double)this.pos.getX() + d * (double)this.facing.getOffsetX(),
			(double)this.pos.getY() + d * (double)this.facing.getOffsetY(),
			(double)this.pos.getZ() + d * (double)this.facing.getOffsetZ()
		);
	}

	private void push(Entity entity, Direction direction, double d) {
		Box box = entity.getBoundingBox();
		Box box2 = VoxelShapes.fullCube().getBoundingBox().offset(this.pos);
		if (box.intersects(box2)) {
			Direction direction2 = direction.getOpposite();
			double e = this.getIntersectionSize(box2, direction2, box) + 0.01;
			double f = this.getIntersectionSize(box2, direction2, box.intersection(box2)) + 0.01;
			if (Math.abs(e - f) < 0.01) {
				e = Math.min(e, d) + 0.01;
				field_12205.set(direction);
				entity.move(MovementType.PISTON, new Vec3d(e * (double)direction2.getOffsetX(), e * (double)direction2.getOffsetY(), e * (double)direction2.getOffsetZ()));
				field_12205.set(null);
			}
		}
	}

	private static double getXIntersectionSize(Box box, Direction direction, Box box2) {
		return direction.getDirection() == Direction.AxisDirection.POSITIVE ? box.maxX - box2.minX : box2.maxX - box.minX;
	}

	private static double getYIntersectionSize(Box box, Direction direction, Box box2) {
		return direction.getDirection() == Direction.AxisDirection.POSITIVE ? box.maxY - box2.minY : box2.maxY - box.minY;
	}

	private static double getZIntersectionSize(Box box, Direction direction, Box box2) {
		return direction.getDirection() == Direction.AxisDirection.POSITIVE ? box.maxZ - box2.minZ : box2.maxZ - box.minZ;
	}

	public BlockState getPushedBlock() {
		return this.pushedBlock;
	}

	public void finish() {
		if (this.lastProgress < 1.0F && this.world != null) {
			this.progress = 1.0F;
			this.lastProgress = this.progress;
			this.world.removeBlockEntity(this.pos);
			this.markRemoved();
			if (this.world.getBlockState(this.pos).getBlock() == Blocks.MOVING_PISTON) {
				BlockState blockState;
				if (this.source) {
					blockState = Blocks.AIR.getDefaultState();
				} else {
					blockState = Block.getRenderingState(this.pushedBlock, this.world, this.pos);
				}

				this.world.setBlockState(this.pos, blockState, 3);
				this.world.updateNeighbor(this.pos, blockState.getBlock(), this.pos);
			}
		}
	}

	@Override
	public void tick() {
		this.savedWorldTime = this.world.getTime();
		this.lastProgress = this.progress;
		if (this.lastProgress >= 1.0F) {
			this.world.removeBlockEntity(this.pos);
			this.markRemoved();
			if (this.pushedBlock != null && this.world.getBlockState(this.pos).getBlock() == Blocks.MOVING_PISTON) {
				BlockState blockState = Block.getRenderingState(this.pushedBlock, this.world, this.pos);
				if (blockState.isAir()) {
					this.world.setBlockState(this.pos, this.pushedBlock, 84);
					Block.replaceBlock(this.pushedBlock, blockState, this.world, this.pos, 3);
				} else {
					if (blockState.contains(Properties.WATERLOGGED) && (Boolean)blockState.get(Properties.WATERLOGGED)) {
						blockState = blockState.with(Properties.WATERLOGGED, Boolean.valueOf(false));
					}

					this.world.setBlockState(this.pos, blockState, 67);
					this.world.updateNeighbor(this.pos, blockState.getBlock(), this.pos);
				}
			}
		} else {
			float f = this.progress + 0.5F;
			this.pushEntities(f);
			this.progress = f;
			if (this.progress >= 1.0F) {
				this.progress = 1.0F;
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
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		compoundTag.put("blockState", NbtHelper.fromBlockState(this.pushedBlock));
		compoundTag.putInt("facing", this.facing.getId());
		compoundTag.putFloat("progress", this.lastProgress);
		compoundTag.putBoolean("extending", this.extending);
		compoundTag.putBoolean("source", this.source);
		return compoundTag;
	}

	public VoxelShape getCollisionShape(BlockView blockView, BlockPos blockPos) {
		VoxelShape voxelShape;
		if (!this.extending && this.source) {
			voxelShape = this.pushedBlock.with(PistonBlock.EXTENDED, Boolean.valueOf(true)).getCollisionShape(blockView, blockPos);
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
					.with(PistonHeadBlock.SHORT, Boolean.valueOf(this.extending != 1.0F - this.progress < 4.0F));
			} else {
				blockState = this.pushedBlock;
			}

			float f = this.getAmountExtended(this.progress);
			double d = (double)((float)this.facing.getOffsetX() * f);
			double e = (double)((float)this.facing.getOffsetY() * f);
			double g = (double)((float)this.facing.getOffsetZ() * f);
			return VoxelShapes.union(voxelShape, blockState.getCollisionShape(blockView, blockPos).offset(d, e, g));
		}
	}

	public long getSavedWorldTime() {
		return this.savedWorldTime;
	}
}
