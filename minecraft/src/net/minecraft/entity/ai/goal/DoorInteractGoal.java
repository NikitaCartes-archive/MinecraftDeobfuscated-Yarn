package net.minecraft.entity.ai.goal;

import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.Material;
import net.minecraft.entity.ai.pathing.EntityMobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class DoorInteractGoal extends Goal {
	protected MobEntity owner;
	protected BlockPos field_6414 = BlockPos.ORIGIN;
	protected boolean field_6412;
	private boolean shouldStop;
	private float field_6410;
	private float field_6409;

	public DoorInteractGoal(MobEntity mobEntity) {
		this.owner = mobEntity;
		if (!(mobEntity.method_5942() instanceof EntityMobNavigation)) {
			throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
		}
	}

	protected boolean method_6256() {
		if (!this.field_6412) {
			return false;
		} else {
			BlockState blockState = this.owner.field_6002.method_8320(this.field_6414);
			if (!(blockState.getBlock() instanceof DoorBlock)) {
				this.field_6412 = false;
				return false;
			} else {
				return (Boolean)blockState.method_11654(DoorBlock.field_10945);
			}
		}
	}

	@Override
	public boolean canStart() {
		if (!this.owner.horizontalCollision) {
			return false;
		} else {
			EntityMobNavigation entityMobNavigation = (EntityMobNavigation)this.owner.method_5942();
			Path path = entityMobNavigation.method_6345();
			if (path != null && !path.isFinished() && entityMobNavigation.canEnterOpenDoors()) {
				for (int i = 0; i < Math.min(path.getCurrentNodeIndex() + 2, path.getLength()); i++) {
					PathNode pathNode = path.getNode(i);
					this.field_6414 = new BlockPos(pathNode.x, pathNode.y + 1, pathNode.z);
					if (!(this.owner.squaredDistanceTo((double)this.field_6414.getX(), this.owner.y, (double)this.field_6414.getZ()) > 2.25)) {
						this.field_6412 = method_6254(this.owner.field_6002, this.field_6414);
						if (this.field_6412) {
							return true;
						}
					}
				}

				this.field_6414 = new BlockPos(this.owner).up();
				this.field_6412 = method_6254(this.owner.field_6002, this.field_6414);
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
		this.field_6410 = (float)((double)((float)this.field_6414.getX() + 0.5F) - this.owner.x);
		this.field_6409 = (float)((double)((float)this.field_6414.getZ() + 0.5F) - this.owner.z);
	}

	@Override
	public void tick() {
		float f = (float)((double)((float)this.field_6414.getX() + 0.5F) - this.owner.x);
		float g = (float)((double)((float)this.field_6414.getZ() + 0.5F) - this.owner.z);
		float h = this.field_6410 * f + this.field_6409 * g;
		if (h < 0.0F) {
			this.shouldStop = true;
		}
	}

	public static boolean method_6254(World world, BlockPos blockPos) {
		BlockState blockState = world.method_8320(blockPos);
		return blockState.getBlock() instanceof DoorBlock && blockState.method_11620() == Material.WOOD;
	}
}
