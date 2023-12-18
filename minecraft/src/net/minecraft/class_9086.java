package net.minecraft;

import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.Registerable;

public class class_9086 {
	public static void method_55843(Registerable<DamageType> registerable) {
		registerable.register(DamageTypes.field_47737, new DamageType("mob", 0.1F));
	}
}
