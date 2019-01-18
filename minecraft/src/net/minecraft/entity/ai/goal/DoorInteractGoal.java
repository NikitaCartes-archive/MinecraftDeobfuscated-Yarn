package net.minecraft.entity.ai.goal;

import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.Material;
import net.minecraft.entity.ai.pathing.EntityMobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;

public abstract class DoorInteractGoal extends Goal {
	protected MobEntity owner;
	protected BlockPos doorPos = BlockPos.ORIGIN;
	protected boolean field_6412;
	private boolean shouldStop;
	private float field_6410;
	private float field_6409;

	public DoorInteractGoal(MobEntity mobEntity) {
		this.owner = mobEntity;
		if (!(mobEntity.getNavigation() instanceof EntityMobNavigation)) {
			throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
		}
	}

	protected boolean method_6256() {
		if (!this.field_6412) {
			return false;
		} else {
			BlockState blockState = this.owner.world.getBlockState(this.doorPos);
			if (!(blockState.getBlock() instanceof DoorBlock)) {
				this.field_6412 = false;
				return false;
			} else {
				return (Boolean)blockState.get(DoorBlock.OPEN);
			}
		}
	}

	protected void method_6255(boolean bl) {
		if (this.field_6412) {
			BlockState blockState = this.owner.world.getBlockState(this.doorPos);
			if (blockState.getBlock() instanceof DoorBlock) {
				((DoorBlock)blockState.getBlock()).onMobOpenedOrClosed(this.owner.world, this.doorPos, bl);
			}
		}
	}

	@Override
	public boolean canStart() {
		if (!this.owner.horizontalCollision) {
			return false;
		} else {
			EntityMobNavigation entityMobNavigation = (EntityMobNavigation)this.owner.getNavigation();
			Path path = entityMobNavigation.method_6345();
			if (path != null && !path.isFinished() && entityMobNavigation.canEnterOpenDoors()) {
				for (int i = 0; i < Math.min(path.getCurrentNodeIndex() + 2, path.getPathLength()); i++) {
					PathNode pathNode = path.getNode(i);
					this.doorPos = new BlockPos(pathNode.x, pathNode.y + 1, pathNode.z);
					if (!(this.owner.squaredDistanceTo((double)this.doorPos.getX(), this.owner.y, (double)this.doorPos.getZ()) > 2.25)) {
						this.field_6412 = this.getDoor(this.doorPos);
						if (this.field_6412) {
							return true;
						}
					}
				}

				this.doorPos = new BlockPos(this.owner).up();
				this.field_6412 = this.getDoor(this.doorPos);
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
		this.field_6410 = (float)((double)((float)this.doorPos.getX() + 0.5F) - this.owner.x);
		this.field_6409 = (float)((double)((float)this.doorPos.getZ() + 0.5F) - this.owner.z);
	}

	@Override
	public void tick() {
		float f = (float)((double)((float)this.doorPos.getX() + 0.5F) - this.owner.x);
		float g = (float)((double)((float)this.doorPos.getZ() + 0.5F) - this.owner.z);
		float h = this.field_6410 * f + this.field_6409 * g;
		if (h < 0.0F) {
			this.shouldStop = true;
		}
	}

	private boolean getDoor(BlockPos blockPos) {
		BlockState blockState = this.owner.world.getBlockState(blockPos);
		return blockState.getBlock() instanceof DoorBlock && blockState.getMaterial() == Material.WOOD;
	}
}
