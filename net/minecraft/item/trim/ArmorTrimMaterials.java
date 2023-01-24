/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item.trim;

import java.util.Optional;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.trim.ArmorTrimMaterial;
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
    public static final RegistryKey<ArmorTrimMaterial> QUARTZ = ArmorTrimMaterials.of("quartz");
    public static final RegistryKey<ArmorTrimMaterial> IRON = ArmorTrimMaterials.of("iron");
    public static final RegistryKey<ArmorTrimMaterial> NETHERITE = ArmorTrimMaterials.of("netherite");
    public static final RegistryKey<ArmorTrimMaterial> REDSTONE = ArmorTrimMaterials.of("redstone");
    public static final RegistryKey<ArmorTrimMaterial> COPPER = ArmorTrimMaterials.of("copper");
    public static final RegistryKey<ArmorTrimMaterial> GOLD = ArmorTrimMaterials.of("gold");
    public static final RegistryKey<ArmorTrimMaterial> EMERALD = ArmorTrimMaterials.of("emerald");
    public static final RegistryKey<ArmorTrimMaterial> DIAMOND = ArmorTrimMaterials.of("diamond");
    public static final RegistryKey<ArmorTrimMaterial> LAPIS = ArmorTrimMaterials.of("lapis");
    public static final RegistryKey<ArmorTrimMaterial> AMETHYST = ArmorTrimMaterials.of("amethyst");

    public static void bootstrap(Registerable<ArmorTrimMaterial> registry) {
    }

    public static void oneTwentyBootstrap(Registerable<ArmorTrimMaterial> registry) {
        ArmorTrimMaterials.register(registry, QUARTZ, Items.QUARTZ, Style.EMPTY.withColor(14931140), 0.1f, Optional.empty());
        ArmorTrimMaterials.register(registry, IRON, Items.IRON_INGOT, Style.EMPTY.withColor(0xECECEC), 0.2f, Optional.of(ArmorMaterials.IRON));
        ArmorTrimMaterials.register(registry, NETHERITE, Items.NETHERITE_INGOT, Style.EMPTY.withColor(6445145), 0.3f, Optional.of(ArmorMaterials.NETHERITE));
        ArmorTrimMaterials.register(registry, REDSTONE, Items.REDSTONE, Style.EMPTY.withColor(9901575), 0.4f, Optional.empty());
        ArmorTrimMaterials.register(registry, COPPER, Items.COPPER_INGOT, Style.EMPTY.withColor(11823181), 0.5f, Optional.empty());
        ArmorTrimMaterials.register(registry, GOLD, Items.GOLD_INGOT, Style.EMPTY.withColor(14594349), 0.6f, Optional.of(ArmorMaterials.GOLD));
        ArmorTrimMaterials.register(registry, EMERALD, Items.EMERALD, Style.EMPTY.withColor(1155126), 0.7f, Optional.empty());
        ArmorTrimMaterials.register(registry, DIAMOND, Items.DIAMOND, Style.EMPTY.withColor(7269586), 0.8f, Optional.of(ArmorMaterials.DIAMOND));
        ArmorTrimMaterials.register(registry, LAPIS, Items.LAPIS_LAZULI, Style.EMPTY.withColor(4288151), 0.9f, Optional.empty());
        ArmorTrimMaterials.register(registry, AMETHYST, Items.AMETHYST_SHARD, Style.EMPTY.withColor(10116294), 1.0f, Optional.empty());
    }

    public static Optional<RegistryEntry.Reference<ArmorTrimMaterial>> get(DynamicRegistryManager registryManager, ItemStack stack) {
        return registryManager.get(RegistryKeys.TRIM_MATERIAL).streamEntries().filter(recipe -> stack.itemMatches(((ArmorTrimMaterial)recipe.value()).ingredient())).findFirst();
    }

    private static void register(Registerable<ArmorTrimMaterial> registry, RegistryKey<ArmorTrimMaterial> key, Item ingredient, Style style, float itemModelIndex, Optional<ArmorMaterials> incompatibleArmorMaterial) {
        ArmorTrimMaterial armorTrimMaterial = ArmorTrimMaterial.of(key.getValue().getPath(), ingredient, itemModelIndex, incompatibleArmorMaterial, Text.translatable(Util.createTranslationKey("trim_material", key.getValue())).fillStyle(style));
        registry.register(key, armorTrimMaterial);
    }

    private static RegistryKey<ArmorTrimMaterial> of(String id) {
        return RegistryKey.of(RegistryKeys.TRIM_MATERIAL, new Identifier(id));
    }
}

