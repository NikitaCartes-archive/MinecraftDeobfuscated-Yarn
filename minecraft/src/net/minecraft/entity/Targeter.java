package net.minecraft.entity;

import javax.annotation.Nullable;

public interface Targeter {
	@Nullable
	LivingEntity getTarget();
}
