package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.predicate.NumberRange;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.dynamic.Codecs;

public record EnchantmentPredicate(Optional<RegistryEntry<Enchantment>> enchantment, NumberRange.IntRange levels) {
	public static final Codec<EnchantmentPredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.createStrictOptionalFieldCodec(Registries.ENCHANTMENT.createEntryCodec(), "enchantment").forGetter(EnchantmentPredicate::enchantment),
					Codecs.createStrictOptionalFieldCodec(NumberRange.IntRange.CODEC, "levels", NumberRange.IntRange.ANY).forGetter(EnchantmentPredicate::levels)
				)
				.apply(instance, EnchantmentPredicate::new)
	);

	public EnchantmentPredicate(Enchantment enchantment, NumberRange.IntRange levels) {
		this(Optional.of(enchantment.getRegistryEntry()), levels);
	}

	public boolean test(Map<Enchantment, Integer> enchantments) {
		if (this.enchantment.isPresent()) {
			Enchantment enchantment = (Enchantment)((RegistryEntry)this.enchantment.get()).value();
			if (!enchantments.containsKey(enchantment)) {
				return false;
			}

			int i = (Integer)enchantments.get(enchantment);
			if (this.levels != NumberRange.IntRange.ANY && !this.levels.test(i)) {
				return false;
			}
		} else if (this.levels != NumberRange.IntRange.ANY) {
			for (Integer integer : enchantments.values()) {
				if (this.levels.test(integer)) {
					return true;
				}
			}

			return false;
		}

		return true;
	}
}
