package net.minecraft.item.trim;

import java.util.Map;
import java.util.Optional;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class ArmorTrimMaterials {
	public static final RegistryKey<ArmorTrimMaterial> QUARTZ = of("quartz");
	public static final RegistryKey<ArmorTrimMaterial> IRON = of("iron");
	public static final RegistryKey<ArmorTrimMaterial> NETHERITE = of("netherite");
	public static final RegistryKey<ArmorTrimMaterial> REDSTONE = of("redstone");
	public static final RegistryKey<ArmorTrimMaterial> COPPER = of("copper");
	public static final RegistryKey<ArmorTrimMaterial> GOLD = of("gold");
	public static final RegistryKey<ArmorTrimMaterial> EMERALD = of("emerald");
	public static final RegistryKey<ArmorTrimMaterial> DIAMOND = of("diamond");
	public static final RegistryKey<ArmorTrimMaterial> LAPIS = of("lapis");
	public static final RegistryKey<ArmorTrimMaterial> AMETHYST = of("amethyst");

	public static void bootstrap(Registerable<ArmorTrimMaterial> registry) {
		register(registry, QUARTZ, Items.QUARTZ, Style.EMPTY.withColor(14931140), 0.1F);
		register(registry, IRON, Items.IRON_INGOT, Style.EMPTY.withColor(15527148), 0.2F, Map.of(ArmorMaterials.IRON, "iron_darker"));
		register(registry, NETHERITE, Items.NETHERITE_INGOT, Style.EMPTY.withColor(6445145), 0.3F, Map.of(ArmorMaterials.NETHERITE, "netherite_darker"));
		register(registry, REDSTONE, Items.REDSTONE, Style.EMPTY.withColor(9901575), 0.4F);
		register(registry, COPPER, Items.COPPER_INGOT, Style.EMPTY.withColor(11823181), 0.5F);
		register(registry, GOLD, Items.GOLD_INGOT, Style.EMPTY.withColor(14594349), 0.6F, Map.of(ArmorMaterials.GOLD, "gold_darker"));
		register(registry, EMERALD, Items.EMERALD, Style.EMPTY.withColor(1155126), 0.7F);
		register(registry, DIAMOND, Items.DIAMOND, Style.EMPTY.withColor(7269586), 0.8F, Map.of(ArmorMaterials.DIAMOND, "diamond_darker"));
		register(registry, LAPIS, Items.LAPIS_LAZULI, Style.EMPTY.withColor(4288151), 0.9F);
		register(registry, AMETHYST, Items.AMETHYST_SHARD, Style.EMPTY.withColor(10116294), 1.0F);
	}

	public static Optional<RegistryEntry.Reference<ArmorTrimMaterial>> get(DynamicRegistryManager registryManager, ItemStack stack) {
		return registryManager.get(RegistryKeys.TRIM_MATERIAL)
			.streamEntries()
			.filter(recipe -> stack.itemMatches(((ArmorTrimMaterial)recipe.value()).ingredient()))
			.findFirst();
	}

	private static void register(Registerable<ArmorTrimMaterial> registry, RegistryKey<ArmorTrimMaterial> key, Item ingredient, Style style, float itemModelIndex) {
		register(registry, key, ingredient, style, itemModelIndex, Map.of());
	}

	private static void register(
		Registerable<ArmorTrimMaterial> registry,
		RegistryKey<ArmorTrimMaterial> key,
		Item ingredient,
		Style style,
		float itemModelIndex,
		Map<ArmorMaterials, String> overrideArmorMaterials
	) {
		ArmorTrimMaterial armorTrimMaterial = ArmorTrimMaterial.of(
			key.getValue().getPath(),
			ingredient,
			itemModelIndex,
			Text.translatable(Util.createTranslationKey("trim_material", key.getValue())).fillStyle(style),
			overrideArmorMaterials
		);
		registry.register(key, armorTrimMaterial);
	}

	private static RegistryKey<ArmorTrimMaterial> of(String id) {
		return RegistryKey.of(RegistryKeys.TRIM_MATERIAL, new Identifier(id));
	}
}
