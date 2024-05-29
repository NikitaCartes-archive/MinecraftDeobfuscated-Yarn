package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Optional;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.predicate.NumberRange;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;

public record EnchantmentPredicate(Optional<RegistryEntryList<Enchantment>> enchantments, NumberRange.IntRange levels) {
	public static final Codec<EnchantmentPredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					RegistryCodecs.entryList(RegistryKeys.ENCHANTMENT).optionalFieldOf("enchantments").forGetter(EnchantmentPredicate::enchantments),
					NumberRange.IntRange.CODEC.optionalFieldOf("levels", NumberRange.IntRange.ANY).forGetter(EnchantmentPredicate::levels)
				)
				.apply(instance, EnchantmentPredicate::new)
	);

	public EnchantmentPredicate(RegistryEntry<Enchantment> enchantment, NumberRange.IntRange levels) {
		this(Optional.of(RegistryEntryList.of(enchantment)), levels);
	}

	public EnchantmentPredicate(RegistryEntryList<Enchantment> enchantments, NumberRange.IntRange levels) {
		this(Optional.of(enchantments), levels);
	}

	public boolean test(ItemEnchantmentsComponent enchantmentsComponent) {
		if (this.enchantments.isPresent()) {
			for (RegistryEntry<Enchantment> registryEntry : (RegistryEntryList)this.enchantments.get()) {
				if (this.testLevel(enchantmentsComponent, registryEntry)) {
					return true;
				}
			}

			return false;
		} else if (this.levels != NumberRange.IntRange.ANY) {
			for (Entry<RegistryEntry<Enchantment>> entry : enchantmentsComponent.getEnchantmentEntries()) {
				if (this.levels.test(entry.getIntValue())) {
					return true;
				}
			}

			return false;
		} else {
			return !enchantmentsComponent.isEmpty();
		}
	}

	private boolean testLevel(ItemEnchantmentsComponent enchantmentsComponent, RegistryEntry<Enchantment> enchantment) {
		int i = enchantmentsComponent.getLevel(enchantment);
		if (i == 0) {
			return false;
		} else {
			return this.levels == NumberRange.IntRange.ANY ? true : this.levels.test(i);
		}
	}
}
