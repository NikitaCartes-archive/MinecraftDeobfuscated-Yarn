package net.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.mob.MobEntity;

public class class_1358 extends LookAtEntityGoal {
	public class_1358(MobEntity mobEntity, Class<? extends Entity> class_, float f, float g) {
		super(mobEntity, class_, f, g);
		this.setControlBits(3);
	}
}
