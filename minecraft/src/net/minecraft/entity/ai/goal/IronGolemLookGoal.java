package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;

public class IronGolemLookGoal extends Goal {
	private static final TargetPredicate CLOSE_VILLAGER_PREDICATE = new TargetPredicate().setBaseMaxDistance(6.0).includeTeammates().includeInvulnerable();
	private final IronGolemEntity golem;
	private VillagerEntity targetVillager;
	private int lookCountdown;

	public IronGolemLookGoal(IronGolemEntity ironGolemEntity) {
		this.golem = ironGolemEntity;
		this.setControls(EnumSet.of(Goal.Control.field_18405, Goal.Control.field_18406));
	}

	@Override
	public boolean canStart() {
		if (!this.golem.world.isDaylight()) {
			return false;
		} else if (this.golem.getRand().nextInt(8000) != 0) {
			return false;
		} else {
			this.targetVillager = this.golem
				.world
				.method_21726(
					VillagerEntity.class, CLOSE_VILLAGER_PREDICATE, this.golem, this.golem.x, this.golem.y, this.golem.z, this.golem.getBoundingBox().expand(6.0, 2.0, 6.0)
				);
			return this.targetVillager != null;
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.lookCountdown > 0;
	}

	@Override
	public void start() {
		this.lookCountdown = 400;
		this.golem.method_6497(true);
	}

	@Override
	public void stop() {
		this.golem.method_6497(false);
		this.targetVillager = null;
	}

	@Override
	public void tick() {
		this.golem.getLookControl().lookAt(this.targetVillager, 30.0F, 30.0F);
		this.lookCountdown--;
	}
}
