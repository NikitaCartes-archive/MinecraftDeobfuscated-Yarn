package net.minecraft.data.server.tag;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.minecraft.data.DataOutput;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;

public abstract class EnchantmentTagProvider extends TagProvider<Enchantment> {
	public EnchantmentTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		super(output, RegistryKeys.ENCHANTMENT, registryLookupFuture);
	}

	protected void createTooltipOrderTag(RegistryWrapper.WrapperLookup registryLookup, RegistryKey<Enchantment>... enchantments) {
		this.getOrCreateTagBuilder(EnchantmentTags.TOOLTIP_ORDER).add(enchantments);
		Set<RegistryKey<Enchantment>> set = Set.of(enchantments);
		List<String> list = (List<String>)registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT)
			.streamEntries()
			.filter(entry -> !set.contains(entry.getKey().get()))
			.map(RegistryEntry::getIdAsString)
			.collect(Collectors.toList());
		if (!list.isEmpty()) {
			throw new IllegalStateException("Not all enchantments were registered for tooltip ordering. Missing: " + String.join(", ", list));
		}
	}
}
