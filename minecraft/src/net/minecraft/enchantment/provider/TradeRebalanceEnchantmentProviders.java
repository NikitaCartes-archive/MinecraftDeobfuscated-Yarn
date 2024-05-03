package net.minecraft.enchantment.provider;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.intprovider.ConstantIntProvider;

public interface TradeRebalanceEnchantmentProviders {
	RegistryKey<EnchantmentProvider> DESERT_ARMORER_BOOTS_4 = EnchantmentProviders.of("trades/desert_armorer_boots_4");
	RegistryKey<EnchantmentProvider> DESERT_ARMORER_LEGGINGS_4 = EnchantmentProviders.of("trades/desert_armorer_leggings_4");
	RegistryKey<EnchantmentProvider> DESERT_ARMORER_CHESTPLATE_4 = EnchantmentProviders.of("trades/desert_armorer_chestplate_4");
	RegistryKey<EnchantmentProvider> DESERT_ARMORER_HELMET_4 = EnchantmentProviders.of("trades/desert_armorer_helmet_4");
	RegistryKey<EnchantmentProvider> DESERT_ARMORER_LEGGINGS_5 = EnchantmentProviders.of("trades/desert_armorer_leggings_5");
	RegistryKey<EnchantmentProvider> DESERT_ARMORER_CHESTPLATE_5 = EnchantmentProviders.of("trades/desert_armorer_chestplate_5");
	RegistryKey<EnchantmentProvider> PLAINS_ARMORER_BOOTS_4 = EnchantmentProviders.of("trades/plains_armorer_boots_4");
	RegistryKey<EnchantmentProvider> PLAINS_ARMORER_LEGGINGS_4 = EnchantmentProviders.of("trades/plains_armorer_leggings_4");
	RegistryKey<EnchantmentProvider> PLAINS_ARMORER_CHESTPLATE_4 = EnchantmentProviders.of("trades/plains_armorer_chestplate_4");
	RegistryKey<EnchantmentProvider> PLAINS_ARMORER_HELMET_4 = EnchantmentProviders.of("trades/plains_armorer_helmet_4");
	RegistryKey<EnchantmentProvider> PLAINS_ARMORER_BOOTS_5 = EnchantmentProviders.of("trades/plains_armorer_boots_5");
	RegistryKey<EnchantmentProvider> PLAINS_ARMORER_LEGGINGS_5 = EnchantmentProviders.of("trades/plains_armorer_leggings_5");
	RegistryKey<EnchantmentProvider> SAVANNA_ARMORER_BOOTS_4 = EnchantmentProviders.of("trades/savanna_armorer_boots_4");
	RegistryKey<EnchantmentProvider> SAVANNA_ARMORER_LEGGINGS_4 = EnchantmentProviders.of("trades/savanna_armorer_leggings_4");
	RegistryKey<EnchantmentProvider> SAVANNA_ARMORER_CHESTPLATE_4 = EnchantmentProviders.of("trades/savanna_armorer_chestplate_4");
	RegistryKey<EnchantmentProvider> SAVANNA_ARMORER_HELMET_4 = EnchantmentProviders.of("trades/savanna_armorer_helmet_4");
	RegistryKey<EnchantmentProvider> SAVANNA_ARMORER_CHESTPLATE_5 = EnchantmentProviders.of("trades/savanna_armorer_chestplate_5");
	RegistryKey<EnchantmentProvider> SAVANNA_ARMORER_HELMET_5 = EnchantmentProviders.of("trades/savanna_armorer_helmet_5");
	RegistryKey<EnchantmentProvider> SNOW_ARMORER_BOOTS_4 = EnchantmentProviders.of("trades/snow_armorer_boots_4");
	RegistryKey<EnchantmentProvider> SNOW_ARMORER_HELMET_4 = EnchantmentProviders.of("trades/snow_armorer_helmet_4");
	RegistryKey<EnchantmentProvider> SNOW_ARMORER_BOOTS_5 = EnchantmentProviders.of("trades/snow_armorer_boots_5");
	RegistryKey<EnchantmentProvider> SNOW_ARMORER_HELMET_5 = EnchantmentProviders.of("trades/snow_armorer_helmet_5");
	RegistryKey<EnchantmentProvider> JUNGLE_ARMORER_BOOTS_4 = EnchantmentProviders.of("trades/jungle_armorer_boots_4");
	RegistryKey<EnchantmentProvider> JUNGLE_ARMORER_LEGGINGS_4 = EnchantmentProviders.of("trades/jungle_armorer_leggings_4");
	RegistryKey<EnchantmentProvider> JUNGLE_ARMORER_CHESTPLATE_4 = EnchantmentProviders.of("trades/jungle_armorer_chestplate_4");
	RegistryKey<EnchantmentProvider> JUNGLE_ARMORER_HELMET_4 = EnchantmentProviders.of("trades/jungle_armorer_helmet_4");
	RegistryKey<EnchantmentProvider> JUNGLE_ARMORER_BOOTS_5 = EnchantmentProviders.of("trades/jungle_armorer_boots_5");
	RegistryKey<EnchantmentProvider> JUNGLE_ARMORER_HELMET_5 = EnchantmentProviders.of("trades/jungle_armorer_helmet_5");
	RegistryKey<EnchantmentProvider> SWAMP_ARMORER_BOOTS_4 = EnchantmentProviders.of("trades/swamp_armorer_boots_4");
	RegistryKey<EnchantmentProvider> SWAMP_ARMORER_LEGGINGS_4 = EnchantmentProviders.of("trades/swamp_armorer_leggings_4");
	RegistryKey<EnchantmentProvider> SWAMP_ARMORER_CHESTPLATE_4 = EnchantmentProviders.of("trades/swamp_armorer_chestplate_4");
	RegistryKey<EnchantmentProvider> SWAMP_ARMORER_HELMET_4 = EnchantmentProviders.of("trades/swamp_armorer_helmet_4");
	RegistryKey<EnchantmentProvider> SWAMP_ARMORER_BOOTS_5 = EnchantmentProviders.of("trades/swamp_armorer_boots_5");
	RegistryKey<EnchantmentProvider> SWAMP_ARMORER_HELMET_5 = EnchantmentProviders.of("trades/swamp_armorer_helmet_5");
	RegistryKey<EnchantmentProvider> TAIGA_ARMORER_LEGGINGS_5 = EnchantmentProviders.of("trades/taiga_armorer_leggings_5");
	RegistryKey<EnchantmentProvider> TAIGA_ARMORER_CHESTPLATE_5 = EnchantmentProviders.of("trades/taiga_armorer_chestplate_5");

	static void bootstrap(Registerable<EnchantmentProvider> registry) {
		RegistryEntryLookup<Enchantment> registryEntryLookup = registry.getRegistryLookup(RegistryKeys.ENCHANTMENT);
		registry.register(DESERT_ARMORER_BOOTS_4, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.THORNS), ConstantIntProvider.create(1)));
		registry.register(
			DESERT_ARMORER_LEGGINGS_4, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.THORNS), ConstantIntProvider.create(1))
		);
		registry.register(
			DESERT_ARMORER_CHESTPLATE_4, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.THORNS), ConstantIntProvider.create(1))
		);
		registry.register(DESERT_ARMORER_HELMET_4, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.THORNS), ConstantIntProvider.create(1)));
		registry.register(
			DESERT_ARMORER_LEGGINGS_5, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.THORNS), ConstantIntProvider.create(1))
		);
		registry.register(
			DESERT_ARMORER_CHESTPLATE_5, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.THORNS), ConstantIntProvider.create(1))
		);
		registry.register(
			PLAINS_ARMORER_BOOTS_4, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.PROTECTION), ConstantIntProvider.create(1))
		);
		registry.register(
			PLAINS_ARMORER_LEGGINGS_4, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.PROTECTION), ConstantIntProvider.create(1))
		);
		registry.register(
			PLAINS_ARMORER_CHESTPLATE_4, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.PROTECTION), ConstantIntProvider.create(1))
		);
		registry.register(
			PLAINS_ARMORER_HELMET_4, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.PROTECTION), ConstantIntProvider.create(1))
		);
		registry.register(
			PLAINS_ARMORER_BOOTS_5, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.PROTECTION), ConstantIntProvider.create(1))
		);
		registry.register(
			PLAINS_ARMORER_LEGGINGS_5, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.PROTECTION), ConstantIntProvider.create(1))
		);
		registry.register(
			SAVANNA_ARMORER_BOOTS_4, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.BINDING_CURSE), ConstantIntProvider.create(1))
		);
		registry.register(
			SAVANNA_ARMORER_LEGGINGS_4, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.BINDING_CURSE), ConstantIntProvider.create(1))
		);
		registry.register(
			SAVANNA_ARMORER_CHESTPLATE_4, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.BINDING_CURSE), ConstantIntProvider.create(1))
		);
		registry.register(
			SAVANNA_ARMORER_HELMET_4, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.BINDING_CURSE), ConstantIntProvider.create(1))
		);
		registry.register(
			SAVANNA_ARMORER_CHESTPLATE_5, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.BINDING_CURSE), ConstantIntProvider.create(1))
		);
		registry.register(
			SAVANNA_ARMORER_HELMET_5, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.BINDING_CURSE), ConstantIntProvider.create(1))
		);
		registry.register(
			SNOW_ARMORER_BOOTS_4, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.FROST_WALKER), ConstantIntProvider.create(1))
		);
		registry.register(
			SNOW_ARMORER_HELMET_4, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.AQUA_AFFINITY), ConstantIntProvider.create(1))
		);
		registry.register(
			SNOW_ARMORER_BOOTS_5, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.FROST_WALKER), ConstantIntProvider.create(1))
		);
		registry.register(
			SNOW_ARMORER_HELMET_5, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.AQUA_AFFINITY), ConstantIntProvider.create(1))
		);
		registry.register(
			JUNGLE_ARMORER_BOOTS_4, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.UNBREAKING), ConstantIntProvider.create(1))
		);
		registry.register(
			JUNGLE_ARMORER_LEGGINGS_4, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.UNBREAKING), ConstantIntProvider.create(1))
		);
		registry.register(
			JUNGLE_ARMORER_CHESTPLATE_4, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.UNBREAKING), ConstantIntProvider.create(1))
		);
		registry.register(
			JUNGLE_ARMORER_HELMET_4, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.UNBREAKING), ConstantIntProvider.create(1))
		);
		registry.register(
			JUNGLE_ARMORER_BOOTS_5, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.FEATHER_FALLING), ConstantIntProvider.create(1))
		);
		registry.register(
			JUNGLE_ARMORER_HELMET_5, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.PROJECTILE_PROTECTION), ConstantIntProvider.create(1))
		);
		registry.register(SWAMP_ARMORER_BOOTS_4, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.MENDING), ConstantIntProvider.create(1)));
		registry.register(
			SWAMP_ARMORER_LEGGINGS_4, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.MENDING), ConstantIntProvider.create(1))
		);
		registry.register(
			SWAMP_ARMORER_CHESTPLATE_4, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.MENDING), ConstantIntProvider.create(1))
		);
		registry.register(SWAMP_ARMORER_HELMET_4, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.MENDING), ConstantIntProvider.create(1)));
		registry.register(
			SWAMP_ARMORER_BOOTS_5, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.DEPTH_STRIDER), ConstantIntProvider.create(1))
		);
		registry.register(
			SWAMP_ARMORER_HELMET_5, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.RESPIRATION), ConstantIntProvider.create(1))
		);
		registry.register(
			TAIGA_ARMORER_LEGGINGS_5, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.BLAST_PROTECTION), ConstantIntProvider.create(1))
		);
		registry.register(
			TAIGA_ARMORER_CHESTPLATE_5, new SingleEnchantmentProvider(registryEntryLookup.getOrThrow(Enchantments.BLAST_PROTECTION), ConstantIntProvider.create(1))
		);
	}
}
