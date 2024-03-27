package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Optional;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.predicate.NumberRange;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

public record EnchantmentPredicate(Optional<RegistryEntry<Enchantment>> enchantment, NumberRange.IntRange levels) {
	public static final Codec<EnchantmentPredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Registries.ENCHANTMENT.getEntryCodec().optionalFieldOf("enchantment").forGetter(EnchantmentPredicate::enchantment),
					NumberRange.IntRange.CODEC.optionalFieldOf("levels", NumberRange.IntRange.ANY).forGetter(EnchantmentPredicate::levels)
				)
				.apply(instance, EnchantmentPredicate::new)
	);

	public EnchantmentPredicate(Enchantment enchantment, NumberRange.IntRange levels) {
		this(Optional.of(enchantment.getRegistryEntry()), levels);
	}

	public boolean test(ItemEnchantmentsComponent enchantmentsComponent) {
		if (this.enchantment.isPresent()) {
			Enchantment enchantment = (Enchantment)((RegistryEntry)this.enchantment.get()).value();
			int i = enchantmentsComponent.getLevel(enchantment);
			if (i == 0) {
				return false;
			}

			if (this.levels != NumberRange.IntRange.ANY && !this.levels.test(i)) {
				return false;
			}
		} else if (this.levels != NumberRange.IntRange.ANY) {
			for (Entry<RegistryEntry<Enchantment>> entry : enchantmentsComponent.getEnchantmentsMap()) {
				if (this.levels.test(entry.getIntValue())) {
					return true;
				}
			}

			return false;
		}

		return true;
	}
}
