package net.minecraft.block.entity;

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
	private static final ThreadLocal<Direction> field_12205 = new ThreadLocal<Direction>() {
		protected Direction initialValue() {
			return null;
		}
	};
	private float progress;
	private float lastProgress;
	private long savedWorldTime;

	public PistonBlockEntity() {
		super(BlockEntityType.PISTON);
	}

	public PistonBlockEntity(BlockState pushedBlock, Direction facing, boolean extending, boolean source) {
		this();
		this.pushedBlock = pushedBlock;
		this.facing = facing;
		this.extending = extending;
		this.source = source;
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
		return (float)this.facing.getOffsetX() * this.method_11504(this.getProgress(tickDelta));
	}

	@Environment(EnvType.CLIENT)
	public float getRenderOffsetY(float tickDelta) {
		return (float)this.facing.getOffsetY() * this.method_11504(this.getProgress(tickDelta));
	}

	@Environment(EnvType.CLIENT)
	public float getRenderOffsetZ(float tickDelta) {
		return (float)this.facing.getOffsetZ() * this.method_11504(this.getProgress(tickDelta));
	}

	private float method_11504(float f) {
		return this.extending ? f - 1.0F : 1.0F - f;
	}

	private BlockState method_11496() {
		return !this.isExtending() && this.isSource() && this.pushedBlock.getBlock() instanceof PistonBlock
			? Blocks.PISTON_HEAD
				.getDefaultState()
				.with(PistonHeadBlock.TYPE, this.pushedBlock.getBlock() == Blocks.STICKY_PISTON ? PistonType.STICKY : PistonType.DEFAULT)
				.with(PistonHeadBlock.FACING, this.pushedBlock.get(PistonBlock.FACING))
			: this.pushedBlock;
	}

	private void method_11503(float f) {
		Direction direction = this.method_11506();
		double d = (double)(f - this.progress);
		VoxelShape voxelShape = this.method_11496().getCollisionShape(this.world, this.getPos());
		if (!voxelShape.isEmpty()) {
			List<Box> list = voxelShape.getBoundingBoxes();
			Box box = this.method_11500(this.method_11509(list));
			List<Entity> list2 = this.world.getEntities(null, this.method_11502(box, direction, d).union(box));
			if (!list2.isEmpty()) {
				boolean bl = this.pushedBlock.getBlock() == Blocks.SLIME_BLOCK;

				for (int i = 0; i < list2.size(); i++) {
					Entity entity = (Entity)list2.get(i);
					if (entity.getPistonBehavior() != PistonBehavior.IGNORE) {
						if (bl) {
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

						double j = 0.0;

						for (int k = 0; k < list.size(); k++) {
							Box box2 = this.method_11502(this.method_11500((Box)list.get(k)), direction, d);
							Box box3 = entity.getBoundingBox();
							if (box2.intersects(box3)) {
								j = Math.max(j, this.method_11497(box2, direction, box3));
								if (j >= d) {
									break;
								}
							}
						}

						if (!(j <= 0.0)) {
							j = Math.min(j, d) + 0.01;
							field_12205.set(direction);
							entity.move(MovementType.PISTON, new Vec3d(j * (double)direction.getOffsetX(), j * (double)direction.getOffsetY(), j * (double)direction.getOffsetZ()));
							field_12205.set(null);
							if (!this.extending && this.source) {
								this.method_11514(entity, direction, d);
							}
						}
					}
				}
			}
		}
	}

	public Direction method_11506() {
		return this.extending ? this.facing : this.facing.getOpposite();
	}

	private Box method_11509(List<Box> list) {
		double d = 0.0;
		double e = 0.0;
		double f = 0.0;
		double g = 1.0;
		double h = 1.0;
		double i = 1.0;

		for (Box box : list) {
			d = Math.min(box.x1, d);
			e = Math.min(box.y1, e);
			f = Math.min(box.z1, f);
			g = Math.max(box.x2, g);
			h = Math.max(box.y2, h);
			i = Math.max(box.z2, i);
		}

		return new Box(d, e, f, g, h, i);
	}

	private double method_11497(Box box, Direction direction, Box box2) {
		switch (direction.getAxis()) {
			case X:
				return method_11493(box, direction, box2);
			case Y:
			default:
				return method_11510(box, direction, box2);
			case Z:
				return method_11505(box, direction, box2);
		}
	}

	private Box method_11500(Box box) {
		double d = (double)this.method_11504(this.progress);
		return box.offset(
			(double)this.pos.getX() + d * (double)this.facing.getOffsetX(),
			(double)this.pos.getY() + d * (double)this.facing.getOffsetY(),
			(double)this.pos.getZ() + d * (double)this.facing.getOffsetZ()
		);
	}

	private Box method_11502(Box box, Direction direction, double d) {
		double e = d * (double)direction.getDirection().offset();
		double f = Math.min(e, 0.0);
		double g = Math.max(e, 0.0);
		switch (direction) {
			case WEST:
				return new Box(box.x1 + f, box.y1, box.z1, box.x1 + g, box.y2, box.z2);
			case EAST:
				return new Box(box.x2 + f, box.y1, box.z1, box.x2 + g, box.y2, box.z2);
			case DOWN:
				return new Box(box.x1, box.y1 + f, box.z1, box.x2, box.y1 + g, box.z2);
			case UP:
			default:
				return new Box(box.x1, box.y2 + f, box.z1, box.x2, box.y2 + g, box.z2);
			case NORTH:
				return new Box(box.x1, box.y1, box.z1 + f, box.x2, box.y2, box.z1 + g);
			case SOUTH:
				return new Box(box.x1, box.y1, box.z2 + f, box.x2, box.y2, box.z2 + g);
		}
	}

	private void method_11514(Entity entity, Direction direction, double d) {
		Box box = entity.getBoundingBox();
		Box box2 = VoxelShapes.fullCube().getBoundingBox().offset(this.pos);
		if (box.intersects(box2)) {
			Direction direction2 = direction.getOpposite();
			double e = this.method_11497(box2, direction2, box) + 0.01;
			double f = this.method_11497(box2, direction2, box.intersection(box2)) + 0.01;
			if (Math.abs(e - f) < 0.01) {
				e = Math.min(e, d) + 0.01;
				field_12205.set(direction);
				entity.move(MovementType.PISTON, new Vec3d(e * (double)direction2.getOffsetX(), e * (double)direction2.getOffsetY(), e * (double)direction2.getOffsetZ()));
				field_12205.set(null);
			}
		}
	}

	private static double method_11493(Box box, Direction direction, Box box2) {
		return direction.getDirection() == Direction.AxisDirection.POSITIVE ? box.x2 - box2.x1 : box2.x2 - box.x1;
	}

	private static double method_11510(Box box, Direction direction, Box box2) {
		return direction.getDirection() == Direction.AxisDirection.POSITIVE ? box.y2 - box2.y1 : box2.y2 - box.y1;
	}

	private static double method_11505(Box box, Direction direction, Box box2) {
		return direction.getDirection() == Direction.AxisDirection.POSITIVE ? box.z2 - box2.z1 : box2.z2 - box.z1;
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
			this.method_11503(f);
			this.progress = f;
			if (this.progress >= 1.0F) {
				this.progress = 1.0F;
			}
		}
	}

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		this.pushedBlock = NbtHelper.toBlockState(tag.getCompound("blockState"));
		this.facing = Direction.byId(tag.getInt("facing"));
		this.progress = tag.getFloat("progress");
		this.lastProgress = this.progress;
		this.extending = tag.getBoolean("extending");
		this.source = tag.getBoolean("source");
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

	public VoxelShape getCollisionShape(BlockView blockView, BlockPos blockPos) {
		VoxelShape voxelShape;
		if (!this.extending && this.source) {
			voxelShape = this.pushedBlock.with(PistonBlock.EXTENDED, Boolean.valueOf(true)).getCollisionShape(blockView, blockPos);
		} else {
			voxelShape = VoxelShapes.empty();
		}

		Direction direction = (Direction)field_12205.get();
		if ((double)this.progress < 1.0 && direction == this.method_11506()) {
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

			float f = this.method_11504(this.progress);
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
