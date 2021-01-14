package net.minecraft.entity.ai;

import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.mob.MobEntity;

public class NavigationConditions {
	public static boolean hasMobNavigation(MobEntity entity) {
		return entity.getNavigation() instanceof MobNavigation;
	}
}
