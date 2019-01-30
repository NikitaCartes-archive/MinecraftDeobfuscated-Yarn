package net.minecraft.entity.ai.goal;

import java.util.List;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;

public class AcceptPoppyGoal extends Goal {
	private final VillagerEntity owner;
	private IronGolemEntity golem;
	private int field_6605;
	private boolean field_6606;

	public AcceptPoppyGoal(VillagerEntity villagerEntity) {
		this.owner = villagerEntity;
		this.setControlBits(3);
	}

	@Override
	public boolean canStart() {
		if (this.owner.getBreedingAge() >= 0) {
			return false;
		} else if (!this.owner.world.isDaylight()) {
			return false;
		} else {
			List<IronGolemEntity> list = this.owner.world.getVisibleEntities(IronGolemEntity.class, this.owner.getBoundingBox().expand(6.0, 2.0, 6.0));
			if (list.isEmpty()) {
				return false;
			} else {
				for (IronGolemEntity ironGolemEntity : list) {
					if (ironGolemEntity.method_6502() > 0) {
						this.golem = ironGolemEntity;
						break;
					}
				}

				return this.golem != null;
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.golem.method_6502() > 0;
	}

	@Override
	public void start() {
		this.field_6605 = this.owner.getRand().nextInt(320);
		this.field_6606 = false;
		this.golem.getNavigation().stop();
	}

	@Override
	public void onRemove() {
		this.golem = null;
		this.owner.getNavigation().stop();
	}

	@Override
	public void tick() {
		this.owner.getLookControl().lookAt(this.golem, 30.0F, 30.0F);
		if (this.golem.method_6502() == this.field_6605) {
			this.owner.getNavigation().startMovingTo(this.golem, 0.5);
			this.field_6606 = true;
		}

		if (this.field_6606 && this.owner.squaredDistanceTo(this.golem) < 4.0) {
			this.golem.method_6497(false);
			this.owner.getNavigation().stop();
		}
	}
}
