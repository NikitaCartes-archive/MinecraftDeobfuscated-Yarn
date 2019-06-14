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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class PistonBlockEntity extends BlockEntity implements Tickable {
	private BlockState field_12204;
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
		this.field_12204 = blockState;
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
		return !this.isExtending() && this.isSource() && this.field_12204.getBlock() instanceof PistonBlock
			? Blocks.field_10379
				.method_9564()
				.method_11657(PistonHeadBlock.field_12224, this.field_12204.getBlock() == Blocks.field_10615 ? PistonType.field_12634 : PistonType.field_12637)
				.method_11657(PistonHeadBlock.field_10927, this.field_12204.method_11654(PistonBlock.field_10927))
			: this.field_12204;
	}

	private void method_11503(float f) {
		Direction direction = this.method_11506();
		double d = (double)(f - this.nextProgress);
		VoxelShape voxelShape = this.method_11496().method_11628(this.world, this.getPos());
		if (!voxelShape.isEmpty()) {
			List<Box> list = voxelShape.getBoundingBoxes();
			Box box = this.method_11500(this.method_11509(list));
			List<Entity> list2 = this.world.method_8335(null, this.method_11502(box, direction, d).union(box));
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
							Box box2 = this.method_11502(this.method_11500((Box)list.get(k)), direction, d);
							Box box3 = entity.method_5829();
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
			d = Math.min(box.minX, d);
			e = Math.min(box.minY, e);
			f = Math.min(box.minZ, f);
			g = Math.max(box.maxX, g);
			h = Math.max(box.maxY, h);
			i = Math.max(box.maxZ, i);
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
		double d = (double)this.method_11504(this.nextProgress);
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
			case field_11039:
				return new Box(box.minX + f, box.minY, box.minZ, box.minX + g, box.maxY, box.maxZ);
			case field_11034:
				return new Box(box.maxX + f, box.minY, box.minZ, box.maxX + g, box.maxY, box.maxZ);
			case field_11033:
				return new Box(box.minX, box.minY + f, box.minZ, box.maxX, box.minY + g, box.maxZ);
			case field_11036:
			default:
				return new Box(box.minX, box.maxY + f, box.minZ, box.maxX, box.maxY + g, box.maxZ);
			case field_11043:
				return new Box(box.minX, box.minY, box.minZ + f, box.maxX, box.maxY, box.minZ + g);
			case field_11035:
				return new Box(box.minX, box.minY, box.maxZ + f, box.maxX, box.maxY, box.maxZ + g);
		}
	}

	private void method_11514(Entity entity, Direction direction, double d) {
		Box box = entity.method_5829();
		Box box2 = VoxelShapes.method_1077().getBoundingBox().offset(this.pos);
		if (box.intersects(box2)) {
			Direction direction2 = direction.getOpposite();
			double e = this.method_11497(box2, direction2, box) + 0.01;
			double f = this.method_11497(box2, direction2, box.intersection(box2)) + 0.01;
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

	private static double method_11493(Box box, Direction direction, Box box2) {
		return direction.getDirection() == Direction.AxisDirection.POSITIVE ? box.maxX - box2.minX : box2.maxX - box.minX;
	}

	private static double method_11510(Box box, Direction direction, Box box2) {
		return direction.getDirection() == Direction.AxisDirection.POSITIVE ? box.maxY - box2.minY : box2.maxY - box.minY;
	}

	private static double method_11505(Box box, Direction direction, Box box2) {
		return direction.getDirection() == Direction.AxisDirection.POSITIVE ? box.maxZ - box2.minZ : box2.maxZ - box.minZ;
	}

	public BlockState method_11495() {
		return this.field_12204;
	}

	public void finish() {
		if (this.progress < 1.0F && this.world != null) {
			this.nextProgress = 1.0F;
			this.progress = this.nextProgress;
			this.world.removeBlockEntity(this.pos);
			this.invalidate();
			if (this.world.method_8320(this.pos).getBlock() == Blocks.field_10008) {
				BlockState blockState;
				if (this.source) {
					blockState = Blocks.field_10124.method_9564();
				} else {
					blockState = Block.method_9510(this.field_12204, this.world, this.pos);
				}

				this.world.method_8652(this.pos, blockState, 3);
				this.world.method_8492(this.pos, blockState.getBlock(), this.pos);
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
			if (this.field_12204 != null && this.world.method_8320(this.pos).getBlock() == Blocks.field_10008) {
				BlockState blockState = Block.method_9510(this.field_12204, this.world, this.pos);
				if (blockState.isAir()) {
					this.world.method_8652(this.pos, this.field_12204, 84);
					Block.method_9611(this.field_12204, blockState, this.world, this.pos, 3);
				} else {
					if (blockState.method_11570(Properties.field_12508) && (Boolean)blockState.method_11654(Properties.field_12508)) {
						blockState = blockState.method_11657(Properties.field_12508, Boolean.valueOf(false));
					}

					this.world.method_8652(this.pos, blockState, 67);
					this.world.method_8492(this.pos, blockState.getBlock(), this.pos);
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
		this.field_12204 = TagHelper.deserializeBlockState(compoundTag.getCompound("blockState"));
		this.facing = Direction.byId(compoundTag.getInt("facing"));
		this.nextProgress = compoundTag.getFloat("progress");
		this.progress = this.nextProgress;
		this.extending = compoundTag.getBoolean("extending");
		this.source = compoundTag.getBoolean("source");
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		compoundTag.put("blockState", TagHelper.serializeBlockState(this.field_12204));
		compoundTag.putInt("facing", this.facing.getId());
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
					.method_11657(PistonHeadBlock.field_10927, this.facing)
					.method_11657(PistonHeadBlock.field_12227, Boolean.valueOf(this.extending != 1.0F - this.nextProgress < 4.0F));
			} else {
				blockState = this.field_12204;
			}

			float f = this.method_11504(this.nextProgress);
			double d = (double)((float)this.facing.getOffsetX() * f);
			double e = (double)((float)this.facing.getOffsetY() * f);
			double g = (double)((float)this.facing.getOffsetZ() * f);
			return VoxelShapes.method_1084(voxelShape, blockState.method_11628(blockView, blockPos).offset(d, e, g));
		}
	}

	public long getSavedWorldTime() {
		return this.savedWorldTime;
	}
}
