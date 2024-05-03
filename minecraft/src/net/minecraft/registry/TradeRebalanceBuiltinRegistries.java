package net.minecraft.registry;

import java.util.concurrent.CompletableFuture;
import net.minecraft.enchantment.provider.TradeRebalanceEnchantmentProviders;

public class TradeRebalanceBuiltinRegistries {
	private static final RegistryBuilder REGISTRY_BUILDER = new RegistryBuilder()
		.addRegistry(RegistryKeys.ENCHANTMENT_PROVIDER, TradeRebalanceEnchantmentProviders::bootstrap);

	public static CompletableFuture<RegistryBuilder.FullPatchesRegistriesPair> validate(CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		return ExperimentalRegistriesValidator.validate(registryLookupFuture, REGISTRY_BUILDER);
	}
}
