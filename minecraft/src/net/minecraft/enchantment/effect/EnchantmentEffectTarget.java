package net.minecraft.enchantment.effect;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum EnchantmentEffectTarget implements StringIdentifiable {
	ATTACKER("attacker"),
	DAMAGING_ENTITY("damaging_entity"),
	VICTIM("victim");

	public static final Codec<EnchantmentEffectTarget> CODEC = StringIdentifiable.createCodec(EnchantmentEffectTarget::values);
	private final String id;

	private EnchantmentEffectTarget(final String id) {
		this.id = id;
	}

	@Override
	public String asString() {
		return this.id;
	}
}
