package net.minecraft.entity.damage;

import net.minecraft.registry.Registerable;

public class OneTwentyOneDamageTypes {
	public static void bootstrap(Registerable<DamageType> damageTypeRegisterable) {
		damageTypeRegisterable.register(DamageTypes.WIND_CHARGE, new DamageType("mob", 0.1F));
	}
}
