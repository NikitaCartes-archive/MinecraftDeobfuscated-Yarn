package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;

public class IronGolemLookGoal extends Goal {
	private static final TargetPredicate field_18089 = new TargetPredicate().setBaseMaxDistance(6.0).includeTeammates().includeInvulnerable();
	private final IronGolemEntity field_6542;
	private VillagerEntity field_6544;
	private int field_6543;

	public IronGolemLookGoal(IronGolemEntity ironGolemEntity) {
		this.field_6542 = ironGolemEntity;
		this.setControlBits(EnumSet.of(Goal.ControlBit.field_18405, Goal.ControlBit.field_18406));
	}

	@Override
	public boolean canStart() {
		if (!this.field_6542.world.isDaylight()) {
			return false;
		} else if (this.field_6542.getRand().nextInt(8000) != 0) {
			return false;
		} else {
			this.field_6544 = this.field_6542
				.world
				.method_18465(
					VillagerEntity.class,
					field_18089,
					this.field_6542,
					this.field_6542.x,
					this.field_6542.y,
					this.field_6542.z,
					this.field_6542.getBoundingBox().expand(6.0, 2.0, 6.0)
				);
			return this.field_6544 != null;
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.field_6543 > 0;
	}

	@Override
	public void start() {
		this.field_6543 = 400;
		this.field_6542.method_6497(true);
	}

	@Override
	public void onRemove() {
		this.field_6542.method_6497(false);
		this.field_6544 = null;
	}

	@Override
	public void tick() {
		this.field_6542.getLookControl().lookAt(this.field_6544, 30.0F, 30.0F);
		this.field_6543--;
	}
}
