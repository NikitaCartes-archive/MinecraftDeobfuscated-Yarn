package net.minecraft.entity.ai.goal;

import java.util.Random;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillageProperties;
import net.minecraft.world.gen.Heightmap;

public class MoveToVillageCenterGoal extends Goal {
	private final MobEntityWithAi owner;
	private final int searchRange;
	private final int reciprocalChance;
	private BlockPos villageCenter = BlockPos.ORIGIN;

	public MoveToVillageCenterGoal(MobEntityWithAi mobEntityWithAi, int i, int j) {
		this.owner = mobEntityWithAi;
		this.searchRange = i;
		this.reciprocalChance = j;
		this.setControlBits(1);
	}

	@Override
	public boolean canStart() {
		if (this.owner.hasPassengers()) {
			return false;
		} else if (this.owner.world.isDaylight()) {
			return false;
		} else if (this.owner.getRand().nextInt(this.reciprocalChance) != 0) {
			return false;
		} else if (this.owner.world.getVillageManager() == null) {
			return false;
		} else {
			VillageProperties villageProperties = this.owner.world.getVillageManager().getNearestVillage(new BlockPos(this.owner), this.searchRange);
			if (villageProperties != null && this.owner.squaredDistanceTo(villageProperties.getCenter()) > (double)villageProperties.getRadius()) {
				this.villageCenter = villageProperties.getCenter();
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		return !this.owner.getNavigation().isIdle() && this.owner.getNavigation().getTargetPos().equals(this.villageCenter);
	}

	@Override
	public void tick() {
		EntityNavigation entityNavigation = this.owner.getNavigation();
		if (entityNavigation.isIdle()) {
			double d = this.owner.squaredDistanceTo(this.villageCenter);
			if (d >= 100.0) {
				Vec3d vec3d = new Vec3d(this.villageCenter);
				Vec3d vec3d2 = new Vec3d(this.owner.x, this.owner.y, this.owner.z);
				Vec3d vec3d3 = vec3d2.subtract(vec3d);
				vec3d = vec3d3.multiply(0.4).add(vec3d);
				Vec3d vec3d4 = vec3d.subtract(vec3d2).normalize().multiply(10.0).add(vec3d2);
				BlockPos blockPos = new BlockPos((int)vec3d4.x, (int)vec3d4.y, (int)vec3d4.z);
				blockPos = this.owner.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockPos);
				if (!entityNavigation.startMovingTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 1.0)) {
					this.findOtherWaypoint();
				}
			}
		}
	}

	private void findOtherWaypoint() {
		Random random = this.owner.getRand();
		BlockPos blockPos = this.owner
			.world
			.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(this.owner).add(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
		this.owner.getNavigation().startMovingTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 1.0);
	}
}
