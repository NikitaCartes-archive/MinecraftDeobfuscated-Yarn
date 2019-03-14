package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.IronGolemEntity;

public class TrackIronGolemTargetGoal extends TrackTargetGoal {
	private final IronGolemEntity ironGolem;
	private LivingEntity target;

	public TrackIronGolemTargetGoal(IronGolemEntity ironGolemEntity) {
		super(ironGolemEntity, false, true);
		this.ironGolem = ironGolemEntity;
		this.setControlBits(EnumSet.of(Goal.ControlBit.field_18408));
	}

	@Override
	public boolean canStart() {
		return false;
	}

	@Override
	public void start() {
		this.ironGolem.setTarget(this.target);
		super.start();
	}
}
