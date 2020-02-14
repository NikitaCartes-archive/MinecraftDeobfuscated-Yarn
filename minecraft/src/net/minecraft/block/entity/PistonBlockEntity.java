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
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Boxes;
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
				.with(PistonHeadBlock.TYPE, this.pushedBlock.getBlock() == Blocks.STICKY_PISTON ? PistonType.STICKY : PistonType.DEFAULT)
				.with(PistonHeadBlock.FACING, this.pushedBlock.get(PistonBlock.FACING))
			: this.pushedBlock;
	}

	private void pushEntities(float nextProgress) {
		Direction direction = this.getMovementDirection();
		double d = (double)(nextProgress - this.progress);
		VoxelShape voxelShape = this.getHeadBlockState().getCollisionShape(this.world, this.getPos());
		if (!voxelShape.isEmpty()) {
			Box box = this.offsetHeadBox(voxelShape.getBoundingBox());
			List<Entity> list = this.world.getEntities(null, Boxes.stretch(box, direction, d).union(box));
			if (!list.isEmpty()) {
				List<Box> list2 = voxelShape.getBoundingBoxes();
				boolean bl = this.pushedBlock.getBlock() == Blocks.SLIME_BLOCK;

				for (Entity entity : list) {
					if (entity.getPistonBehavior() != PistonBehavior.IGNORE) {
						if (bl) {
							Vec3d vec3d = entity.getVelocity();
							double e = vec3d.x;
							double f = vec3d.y;
							double g = vec3d.z;
							switch (direction.getAxis()) {
								case X:
									e = (double)direction.getOffsetX();
									break;
								case Y:
									f = (double)direction.getOffsetY();
									break;
								case Z:
									g = (double)direction.getOffsetZ();
							}

							entity.setVelocity(e, f, g);
							if (entity instanceof ServerPlayerEntity) {
								((ServerPlayerEntity)entity).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(entity));
							}
						}

						double h = 0.0;

						for (Box box2 : list2) {
							Box box3 = Boxes.stretch(this.offsetHeadBox(box2), direction, d);
							Box box4 = entity.getBoundingBox();
							if (box3.intersects(box4)) {
								h = Math.max(h, getIntersectionSize(box3, direction, box4));
								if (h >= d) {
									break;
								}
							}
						}

						if (!(h <= 0.0)) {
							h = Math.min(h, d) + 0.01;
							method_23672(direction, entity, h, direction);
							if (!this.extending && this.source) {
								this.push(entity, direction, d);
							}
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

	private void method_23674(float f) {
		if (this.method_23364()) {
			Direction direction = this.getMovementDirection();
			if (direction.getAxis().isHorizontal()) {
				double d = this.pushedBlock.getCollisionShape(this.world, this.pos).getMaximum(Direction.Axis.Y);
				Box box = this.offsetHeadBox(new Box(0.0, d, 0.0, 1.0, 1.5000000999999998, 1.0));
				double e = (double)(f - this.progress);

				for (Entity entity : this.world.getEntities((Entity)null, box, entityx -> method_23671(box, entityx))) {
					method_23672(direction, entity, e, direction);
				}
			}
		}
	}

	private static boolean method_23671(Box box, Entity entity) {
		return entity.getPistonBehavior() == PistonBehavior.NORMAL
			&& entity.onGround
			&& entity.getX() >= box.x1
			&& entity.getX() <= box.x2
			&& entity.getZ() >= box.z1
			&& entity.getZ() <= box.z2;
	}

	private boolean method_23364() {
		return this.pushedBlock.getBlock() == Blocks.HONEY_BLOCK;
	}

	public Direction getMovementDirection() {
		return this.extending ? this.facing : this.facing.getOpposite();
	}

	private static double getIntersectionSize(Box box, Direction direction, Box box2) {
		switch (direction) {
			case EAST:
				return box.x2 - box2.x1;
			case WEST:
				return box2.x2 - box.x1;
			case UP:
			default:
				return box.y2 - box2.y1;
			case DOWN:
				return box2.y2 - box.y1;
			case SOUTH:
				return box.z2 - box2.z1;
			case NORTH:
				return box2.z2 - box.z1;
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

	private void push(Entity entity, Direction direction, double amount) {
		Box box = entity.getBoundingBox();
		Box box2 = VoxelShapes.fullCube().getBoundingBox().offset(this.pos);
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
			this.method_23674(f);
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
			return VoxelShapes.union(voxelShape, blockState.getCollisionShape(blockView, blockPos).offset(d, e, g));
		}
	}

	public long getSavedWorldTime() {
		return this.savedWorldTime;
	}
}
