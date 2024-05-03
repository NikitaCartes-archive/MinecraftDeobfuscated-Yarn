package net.minecraft.enchantment.effect;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;

public record DamageImmunityEnchantmentEffectType() {
	public static final DamageImmunityEnchantmentEffectType INSTANCE = new DamageImmunityEnchantmentEffectType();
	public static final Codec<DamageImmunityEnchantmentEffectType> CODEC = Codec.unit((Supplier<DamageImmunityEnchantmentEffectType>)(() -> INSTANCE));
}
