package net.minecraft.item.trim;

import java.util.Optional;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class ArmorTrimPatterns {
	public static final RegistryKey<ArmorTrimPattern> SENTRY = of("sentry");
	public static final RegistryKey<ArmorTrimPattern> DUNE = of("dune");
	public static final RegistryKey<ArmorTrimPattern> COAST = of("coast");
	public static final RegistryKey<ArmorTrimPattern> WILD = of("wild");
	public static final RegistryKey<ArmorTrimPattern> WARD = of("ward");
	public static final RegistryKey<ArmorTrimPattern> EYE = of("eye");
	public static final RegistryKey<ArmorTrimPattern> VEX = of("vex");
	public static final RegistryKey<ArmorTrimPattern> TIDE = of("tide");
	public static final RegistryKey<ArmorTrimPattern> SNOUT = of("snout");
	public static final RegistryKey<ArmorTrimPattern> RIB = of("rib");
	public static final RegistryKey<ArmorTrimPattern> SPIRE = of("spire");

	public static void bootstrap(Registerable<ArmorTrimPattern> registry) {
	}

	public static void oneTwentyBootstrap(Registerable<ArmorTrimPattern> registry) {
		register(registry, Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, SENTRY);
		register(registry, Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, DUNE);
		register(registry, Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, COAST);
		register(registry, Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, WILD);
		register(registry, Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, WARD);
		register(registry, Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, EYE);
		register(registry, Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, VEX);
		register(registry, Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, TIDE);
		register(registry, Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, SNOUT);
		register(registry, Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, RIB);
		register(registry, Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, SPIRE);
	}

	public static Optional<RegistryEntry.Reference<ArmorTrimPattern>> get(DynamicRegistryManager registryManager, ItemStack stack) {
		return registryManager.get(RegistryKeys.TRIM_PATTERN)
			.streamEntries()
			.filter(pattern -> stack.itemMatches(((ArmorTrimPattern)pattern.value()).templateItem()))
			.findFirst();
	}

	private static void register(Registerable<ArmorTrimPattern> registry, Item template, RegistryKey<ArmorTrimPattern> key) {
		ArmorTrimPattern armorTrimPattern = new ArmorTrimPattern(
			key.getValue(), Registries.ITEM.getEntry(template), Text.translatable(Util.createTranslationKey("trim_pattern", key.getValue()))
		);
		registry.register(key, armorTrimPattern);
	}

	private static RegistryKey<ArmorTrimPattern> of(String id) {
		return RegistryKey.of(RegistryKeys.TRIM_PATTERN, new Identifier(id));
	}
}
