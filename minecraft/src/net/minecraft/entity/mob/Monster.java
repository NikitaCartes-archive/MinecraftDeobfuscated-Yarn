package net.minecraft.entity.mob;

import java.util.function.Predicate;
import net.minecraft.entity.Entity;

public interface Monster {
	Predicate<Entity> field_7270 = entity -> entity instanceof Monster;
	Predicate<Entity> field_7271 = entity -> entity instanceof Monster && !entity.isInvisible();
}
