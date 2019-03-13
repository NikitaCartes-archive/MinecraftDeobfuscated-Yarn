package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.IronGolemEntity;

public class TrackIronGolemTargetGoal extends TrackTargetGoal {
	private final IronGolemEntity field_6629;
	private LivingEntity target;

	public TrackIronGolemTargetGoal(IronGolemEntity ironGolemEntity) {
		super(ironGolemEntity, false, true);
		this.field_6629 = ironGolemEntity;
		this.setControlBits(EnumSet.of(Goal.class_4134.field_18408));
	}

	@Override
	public boolean canStart() {
		return false;
	}

	@Override
	public void start() {
		this.field_6629.setTarget(this.target);
		super.start();
	}
}
