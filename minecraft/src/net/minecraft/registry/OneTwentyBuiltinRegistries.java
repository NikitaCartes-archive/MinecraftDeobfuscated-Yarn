package net.minecraft.registry;

import net.minecraft.item.trim.ArmorTrimMaterials;
import net.minecraft.item.trim.ArmorTrimPatterns;

public class OneTwentyBuiltinRegistries {
	private static final RegistryBuilder REGISTRY_BUILDER = new RegistryBuilder()
		.addRegistry(RegistryKeys.TRIM_MATERIAL, ArmorTrimMaterials::oneTwentyBootstrap)
		.addRegistry(RegistryKeys.TRIM_PATTERN, ArmorTrimPatterns::oneTwentyBootstrap);

	public static RegistryWrapper.WrapperLookup createWrapperLookup() {
		DynamicRegistryManager.Immutable immutable = DynamicRegistryManager.of(Registries.REGISTRIES);
		return REGISTRY_BUILDER.createWrapperLookup(immutable);
	}
}
