package net.minecraft.item.trim;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public record ArmorTrimPattern(Identifier assetId, RegistryEntry<Item> templateItem, Text description) {
	public static final Codec<ArmorTrimPattern> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Identifier.CODEC.fieldOf("asset_id").forGetter(ArmorTrimPattern::assetId),
					RegistryFixedCodec.of(RegistryKeys.ITEM).fieldOf("template_item").forGetter(ArmorTrimPattern::templateItem),
					Codecs.TEXT.fieldOf("description").forGetter(ArmorTrimPattern::description)
				)
				.apply(instance, ArmorTrimPattern::new)
	);
	public static final Codec<RegistryEntry<ArmorTrimPattern>> ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.TRIM_PATTERN, CODEC);

	public Text getDescription(RegistryEntry<ArmorTrimMaterial> material) {
		return this.description.copy().fillStyle(material.value().description().getStyle());
	}
}
