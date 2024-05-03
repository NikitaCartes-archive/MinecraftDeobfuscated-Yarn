package net.minecraft.enchantment;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.collection.Weighted;

/**
 * Represents an entry in an enchantments array, i.e. a pair between
 * enchantment and level. This is also a weighted entry and is available
 * for the weighted picker to pick from a list.
 * 
 * <p>This is usually used where multiple enchantment to level mappings can
 * exist, such as in enchanting logic. In other cases, vanilla prefers
 * {@code Map<Enchantment, Integer>} over {@code List<EnchantmentLevelEntry>}.
 * 
 * <p>This class is immutable. It does not override hashCode or equals.
 */
public class EnchantmentLevelEntry extends Weighted.Absent {
	public final RegistryEntry<Enchantment> enchantment;
	public final int level;

	public EnchantmentLevelEntry(RegistryEntry<Enchantment> enchantment, int level) {
		super(enchantment.value().getWeight());
		this.enchantment = enchantment;
		this.level = level;
	}
}
