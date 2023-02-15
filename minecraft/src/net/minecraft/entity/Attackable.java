package net.minecraft.entity;

import javax.annotation.Nullable;

public interface Attackable {
	@Nullable
	LivingEntity getLastAttacker();
}
