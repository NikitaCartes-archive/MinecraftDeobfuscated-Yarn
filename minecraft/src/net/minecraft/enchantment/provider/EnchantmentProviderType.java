package net.minecraft.enchantment.provider;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registry;

public interface EnchantmentProviderType {
	static MapCodec<? extends EnchantmentProvider> registerAndGetDefault(Registry<MapCodec<? extends EnchantmentProvider>> registry) {
		Registry.register(registry, "by_cost", ByCostEnchantmentProvider.CODEC);
		Registry.register(registry, "by_cost_with_difficulty", ByCostWithDifficultyEnchantmentProvider.CODEC);
		return Registry.register(registry, "single", SingleEnchantmentProvider.CODEC);
	}
}
