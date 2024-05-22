package net.minecraft.enchantment.effect;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;

public record DamageImmunityEnchantmentEffect() {
	public static final DamageImmunityEnchantmentEffect INSTANCE = new DamageImmunityEnchantmentEffect();
	public static final Codec<DamageImmunityEnchantmentEffect> CODEC = Codec.unit((Supplier<DamageImmunityEnchantmentEffect>)(() -> INSTANCE));
}
