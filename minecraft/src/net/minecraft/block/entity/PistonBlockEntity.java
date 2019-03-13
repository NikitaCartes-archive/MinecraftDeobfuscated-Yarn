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
	private BlockState field_12204;
	private Direction field_12201;
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
		super(BlockEntityType.PISTON);
	}

	public PistonBlockEntity(BlockState blockState, Direction direction, boolean bl, boolean bl2) {
		this();
		this.field_12204 = blockState;
		this.field_12201 = direction;
		this.extending = bl;
		this.source = bl2;
	}

	@Override
	public CompoundTag method_16887() {
		return this.method_11007(new CompoundTag());
	}

	public boolean isExtending() {
		return this.extending;
	}

	public Direction method_11498() {
		return this.field_12201;
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
		return (float)this.field_12201.getOffsetX() * this.method_11504(this.getProgress(f));
	}

	@Environment(EnvType.CLIENT)
	public float getRenderOffsetY(float f) {
		return (float)this.field_12201.getOffsetY() * this.method_11504(this.getProgress(f));
	}

	@Environment(EnvType.CLIENT)
	public float getRenderOffsetZ(float f) {
		return (float)this.field_12201.getOffsetZ() * this.method_11504(this.getProgress(f));
	}

	private float method_11504(float f) {
		return this.extending ? f - 1.0F : 1.0F - f;
	}

	private BlockState method_11496() {
		return !this.isExtending() && this.isSource()
			? Blocks.field_10379
				.method_9564()
				.method_11657(PistonHeadBlock.field_12224, this.field_12204.getBlock() == Blocks.field_10615 ? PistonType.field_12634 : PistonType.field_12637)
				.method_11657(PistonHeadBlock.field_10927, this.field_12204.method_11654(PistonBlock.field_10927))
			: this.field_12204;
	}

	private void method_11503(float f) {
		Direction direction = this.method_11506();
		double d = (double)(f - this.nextProgress);
		VoxelShape voxelShape = this.method_11496().method_11628(this.world, this.method_11016());
		if (!voxelShape.isEmpty()) {
			List<BoundingBox> list = voxelShape.getBoundingBoxList();
			BoundingBox boundingBox = this.method_11500(this.method_11509(list));
			List<Entity> list2 = this.world.method_8335(null, this.method_11502(boundingBox, direction, d).union(boundingBox));
			if (!list2.isEmpty()) {
				boolean bl = this.field_12204.getBlock() == Blocks.field_10030;

				for (int i = 0; i < list2.size(); i++) {
					Entity entity = (Entity)list2.get(i);
					if (entity.method_5657() != PistonBehavior.field_15975) {
						if (bl) {
							Vec3d vec3d = entity.method_18798();
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
							BoundingBox boundingBox3 = entity.method_5829();
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
							entity.method_5784(
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
		return this.extending ? this.field_12201 : this.field_12201.getOpposite();
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
			(double)this.field_11867.getX() + d * (double)this.field_12201.getOffsetX(),
			(double)this.field_11867.getY() + d * (double)this.field_12201.getOffsetY(),
			(double)this.field_11867.getZ() + d * (double)this.field_12201.getOffsetZ()
		);
	}

	private BoundingBox method_11502(BoundingBox boundingBox, Direction direction, double d) {
		double e = d * (double)direction.getDirection().offset();
		double f = Math.min(e, 0.0);
		double g = Math.max(e, 0.0);
		switch (direction) {
			case WEST:
				return new BoundingBox(boundingBox.minX + f, boundingBox.minY, boundingBox.minZ, boundingBox.minX + g, boundingBox.maxY, boundingBox.maxZ);
			case EAST:
				return new BoundingBox(boundingBox.maxX + f, boundingBox.minY, boundingBox.minZ, boundingBox.maxX + g, boundingBox.maxY, boundingBox.maxZ);
			case DOWN:
				return new BoundingBox(boundingBox.minX, boundingBox.minY + f, boundingBox.minZ, boundingBox.maxX, boundingBox.minY + g, boundingBox.maxZ);
			case UP:
			default:
				return new BoundingBox(boundingBox.minX, boundingBox.maxY + f, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY + g, boundingBox.maxZ);
			case NORTH:
				return new BoundingBox(boundingBox.minX, boundingBox.minY, boundingBox.minZ + f, boundingBox.maxX, boundingBox.maxY, boundingBox.minZ + g);
			case SOUTH:
				return new BoundingBox(boundingBox.minX, boundingBox.minY, boundingBox.maxZ + f, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ + g);
		}
	}

	private void method_11514(Entity entity, Direction direction, double d) {
		BoundingBox boundingBox = entity.method_5829();
		BoundingBox boundingBox2 = VoxelShapes.method_1077().getBoundingBox().method_996(this.field_11867);
		if (boundingBox.intersects(boundingBox2)) {
			Direction direction2 = direction.getOpposite();
			double e = this.method_11497(boundingBox2, direction2, boundingBox) + 0.01;
			double f = this.method_11497(boundingBox2, direction2, boundingBox.intersection(boundingBox2)) + 0.01;
			if (Math.abs(e - f) < 0.01) {
				e = Math.min(e, d) + 0.01;
				field_12205.set(direction);
				entity.method_5784(
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

	public BlockState method_11495() {
		return this.field_12204;
	}

	public void method_11513() {
		if (this.progress < 1.0F && this.world != null) {
			this.nextProgress = 1.0F;
			this.progress = this.nextProgress;
			this.world.method_8544(this.field_11867);
			this.invalidate();
			if (this.world.method_8320(this.field_11867).getBlock() == Blocks.field_10008) {
				BlockState blockState;
				if (this.source) {
					blockState = Blocks.field_10124.method_9564();
				} else {
					blockState = Block.method_9510(this.field_12204, this.world, this.field_11867);
				}

				this.world.method_8652(this.field_11867, blockState, 3);
				this.world.method_8492(this.field_11867, blockState.getBlock(), this.field_11867);
			}
		}
	}

	@Override
	public void tick() {
		this.savedWorldTime = this.world.getTime();
		this.progress = this.nextProgress;
		if (this.progress >= 1.0F) {
			this.world.method_8544(this.field_11867);
			this.invalidate();
			if (this.field_12204 != null && this.world.method_8320(this.field_11867).getBlock() == Blocks.field_10008) {
				BlockState blockState = Block.method_9510(this.field_12204, this.world, this.field_11867);
				if (blockState.isAir()) {
					this.world.method_8652(this.field_11867, this.field_12204, 84);
					Block.method_9611(this.field_12204, blockState, this.world, this.field_11867, 3);
				} else {
					if (blockState.method_11570(Properties.field_12508) && (Boolean)blockState.method_11654(Properties.field_12508)) {
						blockState = blockState.method_11657(Properties.field_12508, Boolean.valueOf(false));
					}

					this.world.method_8652(this.field_11867, blockState, 67);
					this.world.method_8492(this.field_11867, blockState.getBlock(), this.field_11867);
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
	public void method_11014(CompoundTag compoundTag) {
		super.method_11014(compoundTag);
		this.field_12204 = TagHelper.deserializeBlockState(compoundTag.getCompound("blockState"));
		this.field_12201 = Direction.byId(compoundTag.getInt("facing"));
		this.nextProgress = compoundTag.getFloat("progress");
		this.progress = this.nextProgress;
		this.extending = compoundTag.getBoolean("extending");
		this.source = compoundTag.getBoolean("source");
	}

	@Override
	public CompoundTag method_11007(CompoundTag compoundTag) {
		super.method_11007(compoundTag);
		compoundTag.method_10566("blockState", TagHelper.serializeBlockState(this.field_12204));
		compoundTag.putInt("facing", this.field_12201.getId());
		compoundTag.putFloat("progress", this.progress);
		compoundTag.putBoolean("extending", this.extending);
		compoundTag.putBoolean("source", this.source);
		return compoundTag;
	}

	public VoxelShape method_11512(BlockView blockView, BlockPos blockPos) {
		VoxelShape voxelShape;
		if (!this.extending && this.source) {
			voxelShape = this.field_12204.method_11657(PistonBlock.field_12191, Boolean.valueOf(true)).method_11628(blockView, blockPos);
		} else {
			voxelShape = VoxelShapes.method_1073();
		}

		Direction direction = (Direction)field_12205.get();
		if ((double)this.nextProgress < 1.0 && direction == this.method_11506()) {
			return voxelShape;
		} else {
			BlockState blockState;
			if (this.isSource()) {
				blockState = Blocks.field_10379
					.method_9564()
					.method_11657(PistonHeadBlock.field_10927, this.field_12201)
					.method_11657(PistonHeadBlock.field_12227, Boolean.valueOf(this.extending != 1.0F - this.nextProgress < 4.0F));
			} else {
				blockState = this.field_12204;
			}

			float f = this.method_11504(this.nextProgress);
			double d = (double)((float)this.field_12201.getOffsetX() * f);
			double e = (double)((float)this.field_12201.getOffsetY() * f);
			double g = (double)((float)this.field_12201.getOffsetZ() * f);
			return VoxelShapes.method_1084(voxelShape, blockState.method_11628(blockView, blockPos).offset(d, e, g));
		}
	}

	public long getSavedWorldTime() {
		return this.savedWorldTime;
	}
}
