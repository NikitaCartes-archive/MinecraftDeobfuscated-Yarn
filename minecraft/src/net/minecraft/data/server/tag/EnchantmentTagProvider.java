package net.minecraft.data.server.tag;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.minecraft.data.DataOutput;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.resource.featuretoggle.FeatureSet;

public abstract class EnchantmentTagProvider extends ValueLookupTagProvider<Enchantment> {
	private final FeatureSet enabledFeatures;

	public EnchantmentTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture, FeatureSet enabledFeatures) {
		super(output, RegistryKeys.ENCHANTMENT, registryLookupFuture, enchantment -> enchantment.getRegistryEntry().registryKey());
		this.enabledFeatures = enabledFeatures;
	}

	protected void createTooltipOrderTag(RegistryWrapper.WrapperLookup registryLookup, Enchantment... enchantments) {
		this.getOrCreateTagBuilder(EnchantmentTags.TOOLTIP_ORDER).add(enchantments);
		Set<Enchantment> set = Set.of(enchantments);
		List<String> list = (List<String>)registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT)
			.streamEntries()
			.filter(entry -> ((Enchantment)entry.value()).getRequiredFeatures().isSubsetOf(this.enabledFeatures))
			.filter(entry -> !set.contains(entry.value()))
			.map(RegistryEntry::getIdAsString)
			.collect(Collectors.toList());
		if (!list.isEmpty()) {
			throw new IllegalStateException("Not all enchantments were registered for tooltip ordering. Missing: " + String.join(", ", list));
		}
	}
}
