package net.minecraft;

import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.Vec3d;

public class class_1377 extends Goal {
	private final VillagerEntity field_6560;
	private LivingEntity field_6559;
	private final double field_6557;
	private int field_6558;

	public class_1377(VillagerEntity villagerEntity, double d) {
		this.field_6560 = villagerEntity;
		this.field_6557 = d;
		this.setControlBits(1);
	}

	@Override
	public boolean canStart() {
		if (this.field_6560.getBreedingAge() >= 0) {
			return false;
		} else if (this.field_6560.getRand().nextInt(400) != 0) {
			return false;
		} else {
			List<VillagerEntity> list = this.field_6560.world.getVisibleEntities(VillagerEntity.class, this.field_6560.getBoundingBox().expand(6.0, 3.0, 6.0));
			double d = Double.MAX_VALUE;

			for (VillagerEntity villagerEntity : list) {
				if (villagerEntity != this.field_6560 && !villagerEntity.method_7236() && villagerEntity.getBreedingAge() < 0) {
					double e = villagerEntity.squaredDistanceTo(this.field_6560);
					if (!(e > d)) {
						d = e;
						this.field_6559 = villagerEntity;
					}
				}
			}

			if (this.field_6559 == null) {
				Vec3d vec3d = class_1414.method_6375(this.field_6560, 16, 3);
				if (vec3d == null) {
					return false;
				}
			}

			return true;
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.field_6558 > 0;
	}

	@Override
	public void start() {
		if (this.field_6559 != null) {
			this.field_6560.method_7220(true);
		}

		this.field_6558 = 1000;
	}

	@Override
	public void onRemove() {
		this.field_6560.method_7220(false);
		this.field_6559 = null;
	}

	@Override
	public void tick() {
		this.field_6558--;
		if (this.field_6559 != null) {
			if (this.field_6560.squaredDistanceTo(this.field_6559) > 4.0) {
				this.field_6560.getNavigation().method_6335(this.field_6559, this.field_6557);
			}
		} else if (this.field_6560.getNavigation().method_6357()) {
			Vec3d vec3d = class_1414.method_6375(this.field_6560, 16, 3);
			if (vec3d == null) {
				return;
			}

			this.field_6560.getNavigation().method_6337(vec3d.x, vec3d.y, vec3d.z, this.field_6557);
		}
	}
}
