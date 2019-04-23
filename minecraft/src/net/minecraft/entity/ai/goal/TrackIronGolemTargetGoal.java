package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.IronGolemEntity;

public class TrackIronGolemTargetGoal extends TrackTargetGoal {
	private final IronGolemEntity golem;
	private LivingEntity target;

	public TrackIronGolemTargetGoal(IronGolemEntity ironGolemEntity) {
		super(ironGolemEntity, false, true);
		this.golem = ironGolemEntity;
		this.setControls(EnumSet.of(Goal.Control.field_18408));
	}

	@Override
	public boolean canStart() {
		return false;
	}

	@Override
	public void start() {
		this.golem.setTarget(this.target);
		super.start();
	}
}
