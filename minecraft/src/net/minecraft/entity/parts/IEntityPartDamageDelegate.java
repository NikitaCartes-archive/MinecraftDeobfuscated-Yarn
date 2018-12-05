package net.minecraft.entity.parts;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.World;

public interface IEntityPartDamageDelegate {
	World getPartDamageWorld();

	boolean damage(EntityPart entityPart, DamageSource damageSource, float f);

	EntityType<?> getType();
}
