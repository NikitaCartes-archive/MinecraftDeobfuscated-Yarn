package net.minecraft;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.mob.MobEntity;

public class class_1358 extends LookAtEntityGoal {
	public class_1358(MobEntity mobEntity, Class<? extends LivingEntity> class_, float f, float g) {
		super(mobEntity, class_, f, g);
		this.setControlBits(3);
	}
}
