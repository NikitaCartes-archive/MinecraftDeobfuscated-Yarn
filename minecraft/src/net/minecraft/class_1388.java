package net.minecraft;

import java.util.List;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;

public class class_1388 extends Goal {
	private final VillagerEntity field_6607;
	private IronGolemEntity field_6604;
	private int field_6605;
	private boolean field_6606;

	public class_1388(VillagerEntity villagerEntity) {
		this.field_6607 = villagerEntity;
		this.setControlBits(3);
	}

	@Override
	public boolean canStart() {
		if (this.field_6607.getBreedingAge() >= 0) {
			return false;
		} else if (!this.field_6607.world.isDaylight()) {
			return false;
		} else {
			List<IronGolemEntity> list = this.field_6607.world.getVisibleEntities(IronGolemEntity.class, this.field_6607.getBoundingBox().expand(6.0, 2.0, 6.0));
			if (list.isEmpty()) {
				return false;
			} else {
				for (IronGolemEntity ironGolemEntity : list) {
					if (ironGolemEntity.method_6502() > 0) {
						this.field_6604 = ironGolemEntity;
						break;
					}
				}

				return this.field_6604 != null;
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.field_6604.method_6502() > 0;
	}

	@Override
	public void start() {
		this.field_6605 = this.field_6607.getRand().nextInt(320);
		this.field_6606 = false;
		this.field_6604.getNavigation().method_6340();
	}

	@Override
	public void onRemove() {
		this.field_6604 = null;
		this.field_6607.getNavigation().method_6340();
	}

	@Override
	public void tick() {
		this.field_6607.getLookControl().lookAt(this.field_6604, 30.0F, 30.0F);
		if (this.field_6604.method_6502() == this.field_6605) {
			this.field_6607.getNavigation().method_6335(this.field_6604, 0.5);
			this.field_6606 = true;
		}

		if (this.field_6606 && this.field_6607.squaredDistanceTo(this.field_6604) < 4.0) {
			this.field_6604.method_6497(false);
			this.field_6607.getNavigation().method_6340();
		}
	}
}
