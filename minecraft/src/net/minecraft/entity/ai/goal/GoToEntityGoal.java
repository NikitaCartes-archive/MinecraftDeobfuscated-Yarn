package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;

public class GoToEntityGoal extends LookAtEntityGoal {
	public GoToEntityGoal(MobEntity mobEntity, Class<? extends LivingEntity> class_, float f, float g) {
		super(mobEntity, class_, f, g);
		this.setControlBits(EnumSet.of(Goal.ControlBit.field_18406, Goal.ControlBit.field_18405));
	}
}
