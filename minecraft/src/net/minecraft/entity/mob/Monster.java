package net.minecraft.entity.mob;

import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.sortme.Living;

public interface Monster extends Living {
	Predicate<Entity> field_7270 = entity -> entity instanceof Monster;
	Predicate<Entity> field_7271 = entity -> entity instanceof Monster && !entity.isInvisible();
}
