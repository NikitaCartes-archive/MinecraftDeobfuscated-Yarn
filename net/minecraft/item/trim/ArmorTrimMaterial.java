/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item.trim;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.text.Text;
import net.minecraft.util.dynamic.Codecs;

public record ArmorTrimMaterial(String assetName, RegistryEntry<Item> ingredient, float itemModelIndex, Optional<ArmorMaterials> incompatibleArmorMaterial, Text description) {
    public static final Codec<ArmorTrimMaterial> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.STRING.fieldOf("asset_name")).forGetter(ArmorTrimMaterial::assetName), ((MapCodec)RegistryFixedCodec.of(RegistryKeys.ITEM).fieldOf("ingredient")).forGetter(ArmorTrimMaterial::ingredient), ((MapCodec)Codec.FLOAT.fieldOf("item_model_index")).forGetter(ArmorTrimMaterial::itemModelIndex), ArmorMaterials.CODEC.optionalFieldOf("incompatible_armor_material").forGetter(ArmorTrimMaterial::incompatibleArmorMaterial), ((MapCodec)Codecs.TEXT.fieldOf("description")).forGetter(ArmorTrimMaterial::description)).apply((Applicative<ArmorTrimMaterial, ?>)instance, ArmorTrimMaterial::new));
    public static final Codec<RegistryEntry<ArmorTrimMaterial>> ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.TRIM_MATERIAL, CODEC);

    public static ArmorTrimMaterial of(String assetName, Item ingredient, float itemModelIndex, Optional<ArmorMaterials> incompatibleArmorMaterial, Text description) {
        return new ArmorTrimMaterial(assetName, Registries.ITEM.getEntry(ingredient), itemModelIndex, incompatibleArmorMaterial, description);
    }
}

