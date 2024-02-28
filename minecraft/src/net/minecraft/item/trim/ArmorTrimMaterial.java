package net.minecraft.item.trim;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.dynamic.Codecs;

public record ArmorTrimMaterial(
	String assetName, RegistryEntry<Item> ingredient, float itemModelIndex, Map<RegistryEntry<ArmorMaterial>, String> overrideArmorMaterials, Text description
) {
	public static final Codec<ArmorTrimMaterial> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.IDENTIFIER_PATH.fieldOf("asset_name").forGetter(ArmorTrimMaterial::assetName),
					RegistryFixedCodec.of(RegistryKeys.ITEM).fieldOf("ingredient").forGetter(ArmorTrimMaterial::ingredient),
					Codec.FLOAT.fieldOf("item_model_index").forGetter(ArmorTrimMaterial::itemModelIndex),
					Codec.unboundedMap(ArmorMaterial.CODEC, Codec.STRING)
						.optionalFieldOf("override_armor_materials", Map.of())
						.forGetter(ArmorTrimMaterial::overrideArmorMaterials),
					TextCodecs.CODEC.fieldOf("description").forGetter(ArmorTrimMaterial::description)
				)
				.apply(instance, ArmorTrimMaterial::new)
	);
	public static final PacketCodec<RegistryByteBuf, ArmorTrimMaterial> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.STRING,
		ArmorTrimMaterial::assetName,
		PacketCodecs.registryEntry(RegistryKeys.ITEM),
		ArmorTrimMaterial::ingredient,
		PacketCodecs.FLOAT,
		ArmorTrimMaterial::itemModelIndex,
		PacketCodecs.map(Object2ObjectOpenHashMap::new, PacketCodecs.registryEntry(RegistryKeys.ARMOR_MATERIAL), PacketCodecs.STRING),
		ArmorTrimMaterial::overrideArmorMaterials,
		TextCodecs.REGISTRY_PACKET_CODEC,
		ArmorTrimMaterial::description,
		ArmorTrimMaterial::new
	);
	public static final Codec<RegistryEntry<ArmorTrimMaterial>> ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.TRIM_MATERIAL, CODEC);
	public static final PacketCodec<RegistryByteBuf, RegistryEntry<ArmorTrimMaterial>> ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(
		RegistryKeys.TRIM_MATERIAL, PACKET_CODEC
	);

	public static ArmorTrimMaterial of(
		String assetName, Item ingredient, float itemModelIndex, Text description, Map<RegistryEntry<ArmorMaterial>, String> overrideArmorMaterials
	) {
		return new ArmorTrimMaterial(assetName, Registries.ITEM.getEntry(ingredient), itemModelIndex, overrideArmorMaterials, description);
	}
}
