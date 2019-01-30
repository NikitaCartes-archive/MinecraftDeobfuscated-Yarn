package net.minecraft.entity.ai.goal;

import java.util.List;
import net.minecraft.class_1414;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.Vec3d;

public class VillagerStareGoal extends Goal {
	private final VillagerEntity owner;
	private LivingEntity target;
	private final double speed;
	private int timeLeft;

	public VillagerStareGoal(VillagerEntity villagerEntity, double d) {
		this.owner = villagerEntity;
		this.speed = d;
		this.setControlBits(1);
	}

	@Override
	public boolean canStart() {
		if (this.owner.getBreedingAge() >= 0) {
			return false;
		} else if (this.owner.getRand().nextInt(400) != 0) {
			return false;
		} else {
			List<VillagerEntity> list = this.owner.world.getVisibleEntities(VillagerEntity.class, this.owner.getBoundingBox().expand(6.0, 3.0, 6.0));
			double d = Double.MAX_VALUE;

			for (VillagerEntity villagerEntity : list) {
				if (villagerEntity != this.owner && !villagerEntity.isStaring() && villagerEntity.getBreedingAge() < 0) {
					double e = villagerEntity.squaredDistanceTo(this.owner);
					if (!(e > d)) {
						d = e;
						this.target = villagerEntity;
					}
				}
			}

			if (this.target == null) {
				Vec3d vec3d = class_1414.method_6375(this.owner, 16, 3);
				if (vec3d == null) {
					return false;
				}
			}

			return true;
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.timeLeft > 0;
	}

	@Override
	public void start() {
		if (this.target != null) {
			this.owner.setStaring(true);
		}

		this.timeLeft = 1000;
	}

	@Override
	public void onRemove() {
		this.owner.setStaring(false);
		this.target = null;
	}

	@Override
	public void tick() {
		this.timeLeft--;
		if (this.target != null) {
			if (this.owner.squaredDistanceTo(this.target) > 4.0) {
				this.owner.getNavigation().startMovingTo(this.target, this.speed);
			}
		} else if (this.owner.getNavigation().isIdle()) {
			Vec3d vec3d = class_1414.method_6375(this.owner, 16, 3);
			if (vec3d == null) {
				return;
			}

			this.owner.getNavigation().startMovingTo(vec3d.x, vec3d.y, vec3d.z, this.speed);
		}
	}
}
