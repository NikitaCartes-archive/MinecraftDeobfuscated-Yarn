package net.minecraft.entity.ai.goal;

import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.Material;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class DoorInteractGoal extends Goal {
	protected MobEntity mob;
	protected BlockPos doorPos = BlockPos.ORIGIN;
	protected boolean field_6412;
	private boolean shouldStop;
	private float field_6410;
	private float field_6409;

	public DoorInteractGoal(MobEntity mobEntity) {
		this.mob = mobEntity;
		if (!(mobEntity.getNavigation() instanceof MobNavigation)) {
			throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
		}
	}

	protected boolean method_6256() {
		if (!this.field_6412) {
			return false;
		} else {
			BlockState blockState = this.mob.world.getBlockState(this.doorPos);
			if (!(blockState.getBlock() instanceof DoorBlock)) {
				this.field_6412 = false;
				return false;
			} else {
				return (Boolean)blockState.get(DoorBlock.OPEN);
			}
		}
	}

	protected void setDoorOpen(boolean bl) {
		if (this.field_6412) {
			BlockState blockState = this.mob.world.getBlockState(this.doorPos);
			if (blockState.getBlock() instanceof DoorBlock) {
				((DoorBlock)blockState.getBlock()).setOpen(this.mob.world, this.doorPos, bl);
			}
		}
	}

	@Override
	public boolean canStart() {
		if (!this.mob.horizontalCollision) {
			return false;
		} else {
			MobNavigation mobNavigation = (MobNavigation)this.mob.getNavigation();
			Path path = mobNavigation.getCurrentPath();
			if (path != null && !path.isFinished() && mobNavigation.canEnterOpenDoors()) {
				for (int i = 0; i < Math.min(path.getCurrentNodeIndex() + 2, path.getLength()); i++) {
					PathNode pathNode = path.getNode(i);
					this.doorPos = new BlockPos(pathNode.x, pathNode.y + 1, pathNode.z);
					if (!(this.mob.squaredDistanceTo((double)this.doorPos.getX(), this.mob.y, (double)this.doorPos.getZ()) > 2.25)) {
						this.field_6412 = getDoor(this.mob.world, this.doorPos);
						if (this.field_6412) {
							return true;
						}
					}
				}

				this.doorPos = new BlockPos(this.mob).up();
				this.field_6412 = getDoor(this.mob.world, this.doorPos);
				return this.field_6412;
			} else {
				return false;
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		return !this.shouldStop;
	}

	@Override
	public void start() {
		this.shouldStop = false;
		this.field_6410 = (float)((double)((float)this.doorPos.getX() + 0.5F) - this.mob.x);
		this.field_6409 = (float)((double)((float)this.doorPos.getZ() + 0.5F) - this.mob.z);
	}

	@Override
	public void tick() {
		float f = (float)((double)((float)this.doorPos.getX() + 0.5F) - this.mob.x);
		float g = (float)((double)((float)this.doorPos.getZ() + 0.5F) - this.mob.z);
		float h = this.field_6410 * f + this.field_6409 * g;
		if (h < 0.0F) {
			this.shouldStop = true;
		}
	}

	public static boolean getDoor(World world, BlockPos blockPos) {
		BlockState blockState = world.getBlockState(blockPos);
		return blockState.getBlock() instanceof DoorBlock && blockState.getMaterial() == Material.WOOD;
	}
}
