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
import net.minecraft.state.property.Properties;
import net.minecraft.util.TagHelper;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
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
		protected Direction method_11516() {
			return null;
		}
	};
	private float nextProgress;
	private float progress;
	private long savedWorldTime;

	public PistonBlockEntity() {
		super(BlockEntityType.field_11897);
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

		return MathHelper.lerp(f, this.progress, this.nextProgress);
	}

	@Environment(EnvType.CLIENT)
	public float getRenderOffsetX(float f) {
		return (float)this.facing.getOffsetX() * this.method_11504(this.getProgress(f));
	}

	@Environment(EnvType.CLIENT)
	public float getRenderOffsetY(float f) {
		return (float)this.facing.getOffsetY() * this.method_11504(this.getProgress(f));
	}

	@Environment(EnvType.CLIENT)
	public float getRenderOffsetZ(float f) {
		return (float)this.facing.getOffsetZ() * this.method_11504(this.getProgress(f));
	}

	private float method_11504(float f) {
		return this.extending ? f - 1.0F : 1.0F - f;
	}

	private BlockState method_11496() {
		return !this.isExtending() && this.isSource() && this.pushedBlock.getBlock() instanceof PistonBlock
			? Blocks.field_10379
				.getDefaultState()
				.with(PistonHeadBlock.TYPE, this.pushedBlock.getBlock() == Blocks.field_10615 ? PistonType.field_12634 : PistonType.field_12637)
				.with(PistonHeadBlock.FACING, this.pushedBlock.get(PistonBlock.FACING))
			: this.pushedBlock;
	}

	private void method_11503(float f) {
		Direction direction = this.method_11506();
		double d = (double)(f - this.nextProgress);
		VoxelShape voxelShape = this.method_11496().getCollisionShape(this.world, this.getPos());
		if (!voxelShape.isEmpty()) {
			List<BoundingBox> list = voxelShape.getBoundingBoxes();
			BoundingBox boundingBox = this.method_11500(this.method_11509(list));
			List<Entity> list2 = this.world.getEntities(null, this.method_11502(boundingBox, direction, d).union(boundingBox));
			if (!list2.isEmpty()) {
				boolean bl = this.pushedBlock.getBlock() == Blocks.field_10030;

				for (int i = 0; i < list2.size(); i++) {
					Entity entity = (Entity)list2.get(i);
					if (entity.getPistonBehavior() != PistonBehavior.field_15975) {
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
							BoundingBox boundingBox2 = this.method_11502(this.method_11500((BoundingBox)list.get(k)), direction, d);
							BoundingBox boundingBox3 = entity.getBoundingBox();
							if (boundingBox2.intersects(boundingBox3)) {
								j = Math.max(j, this.method_11497(boundingBox2, direction, boundingBox3));
								if (j >= d) {
									break;
								}
							}
						}

						if (!(j <= 0.0)) {
							j = Math.min(j, d) + 0.01;
							field_12205.set(direction);
							entity.move(
								MovementType.field_6310, new Vec3d(j * (double)direction.getOffsetX(), j * (double)direction.getOffsetY(), j * (double)direction.getOffsetZ())
							);
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

	private BoundingBox method_11509(List<BoundingBox> list) {
		double d = 0.0;
		double e = 0.0;
		double f = 0.0;
		double g = 1.0;
		double h = 1.0;
		double i = 1.0;

		for (BoundingBox boundingBox : list) {
			d = Math.min(boundingBox.minX, d);
			e = Math.min(boundingBox.minY, e);
			f = Math.min(boundingBox.minZ, f);
			g = Math.max(boundingBox.maxX, g);
			h = Math.max(boundingBox.maxY, h);
			i = Math.max(boundingBox.maxZ, i);
		}

		return new BoundingBox(d, e, f, g, h, i);
	}

	private double method_11497(BoundingBox boundingBox, Direction direction, BoundingBox boundingBox2) {
		switch (direction.getAxis()) {
			case X:
				return method_11493(boundingBox, direction, boundingBox2);
			case Y:
			default:
				return method_11510(boundingBox, direction, boundingBox2);
			case Z:
				return method_11505(boundingBox, direction, boundingBox2);
		}
	}

	private BoundingBox method_11500(BoundingBox boundingBox) {
		double d = (double)this.method_11504(this.nextProgress);
		return boundingBox.offset(
			(double)this.pos.getX() + d * (double)this.facing.getOffsetX(),
			(double)this.pos.getY() + d * (double)this.facing.getOffsetY(),
			(double)this.pos.getZ() + d * (double)this.facing.getOffsetZ()
		);
	}

	private BoundingBox method_11502(BoundingBox boundingBox, Direction direction, double d) {
		double e = d * (double)direction.getDirection().offset();
		double f = Math.min(e, 0.0);
		double g = Math.max(e, 0.0);
		switch (direction) {
			case field_11039:
				return new BoundingBox(boundingBox.minX + f, boundingBox.minY, boundingBox.minZ, boundingBox.minX + g, boundingBox.maxY, boundingBox.maxZ);
			case field_11034:
				return new BoundingBox(boundingBox.maxX + f, boundingBox.minY, boundingBox.minZ, boundingBox.maxX + g, boundingBox.maxY, boundingBox.maxZ);
			case field_11033:
				return new BoundingBox(boundingBox.minX, boundingBox.minY + f, boundingBox.minZ, boundingBox.maxX, boundingBox.minY + g, boundingBox.maxZ);
			case field_11036:
			default:
				return new BoundingBox(boundingBox.minX, boundingBox.maxY + f, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY + g, boundingBox.maxZ);
			case field_11043:
				return new BoundingBox(boundingBox.minX, boundingBox.minY, boundingBox.minZ + f, boundingBox.maxX, boundingBox.maxY, boundingBox.minZ + g);
			case field_11035:
				return new BoundingBox(boundingBox.minX, boundingBox.minY, boundingBox.maxZ + f, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ + g);
		}
	}

	private void method_11514(Entity entity, Direction direction, double d) {
		BoundingBox boundingBox = entity.getBoundingBox();
		BoundingBox boundingBox2 = VoxelShapes.fullCube().getBoundingBox().offset(this.pos);
		if (boundingBox.intersects(boundingBox2)) {
			Direction direction2 = direction.getOpposite();
			double e = this.method_11497(boundingBox2, direction2, boundingBox) + 0.01;
			double f = this.method_11497(boundingBox2, direction2, boundingBox.intersection(boundingBox2)) + 0.01;
			if (Math.abs(e - f) < 0.01) {
				e = Math.min(e, d) + 0.01;
				field_12205.set(direction);
				entity.move(
					MovementType.field_6310, new Vec3d(e * (double)direction2.getOffsetX(), e * (double)direction2.getOffsetY(), e * (double)direction2.getOffsetZ())
				);
				field_12205.set(null);
			}
		}
	}

	private static double method_11493(BoundingBox boundingBox, Direction direction, BoundingBox boundingBox2) {
		return direction.getDirection() == Direction.AxisDirection.POSITIVE ? boundingBox.maxX - boundingBox2.minX : boundingBox2.maxX - boundingBox.minX;
	}

	private static double method_11510(BoundingBox boundingBox, Direction direction, BoundingBox boundingBox2) {
		return direction.getDirection() == Direction.AxisDirection.POSITIVE ? boundingBox.maxY - boundingBox2.minY : boundingBox2.maxY - boundingBox.minY;
	}

	private static double method_11505(BoundingBox boundingBox, Direction direction, BoundingBox boundingBox2) {
		return direction.getDirection() == Direction.AxisDirection.POSITIVE ? boundingBox.maxZ - boundingBox2.minZ : boundingBox2.maxZ - boundingBox.minZ;
	}

	public BlockState getPushedBlock() {
		return this.pushedBlock;
	}

	public void finish() {
		if (this.progress < 1.0F && this.world != null) {
			this.nextProgress = 1.0F;
			this.progress = this.nextProgress;
			this.world.removeBlockEntity(this.pos);
			this.invalidate();
			if (this.world.getBlockState(this.pos).getBlock() == Blocks.field_10008) {
				BlockState blockState;
				if (this.source) {
					blockState = Blocks.field_10124.getDefaultState();
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
		this.progress = this.nextProgress;
		if (this.progress >= 1.0F) {
			this.world.removeBlockEntity(this.pos);
			this.invalidate();
			if (this.pushedBlock != null && this.world.getBlockState(this.pos).getBlock() == Blocks.field_10008) {
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
			float f = this.nextProgress + 0.5F;
			this.method_11503(f);
			this.nextProgress = f;
			if (this.nextProgress >= 1.0F) {
				this.nextProgress = 1.0F;
			}
		}
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.pushedBlock = TagHelper.deserializeBlockState(compoundTag.getCompound("blockState"));
		this.facing = Direction.byId(compoundTag.getInt("facing"));
		this.nextProgress = compoundTag.getFloat("progress");
		this.progress = this.nextProgress;
		this.extending = compoundTag.getBoolean("extending");
		this.source = compoundTag.getBoolean("source");
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		compoundTag.put("blockState", TagHelper.serializeBlockState(this.pushedBlock));
		compoundTag.putInt("facing", this.facing.getId());
		compoundTag.putFloat("progress", this.progress);
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
		if ((double)this.nextProgress < 1.0 && direction == this.method_11506()) {
			return voxelShape;
		} else {
			BlockState blockState;
			if (this.isSource()) {
				blockState = Blocks.field_10379
					.getDefaultState()
					.with(PistonHeadBlock.FACING, this.facing)
					.with(PistonHeadBlock.SHORT, Boolean.valueOf(this.extending != 1.0F - this.nextProgress < 4.0F));
			} else {
				blockState = this.pushedBlock;
			}

			float f = this.method_11504(this.nextProgress);
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
