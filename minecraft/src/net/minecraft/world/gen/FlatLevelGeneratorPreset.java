package net.minecraft.world.gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;

public record FlatLevelGeneratorPreset(RegistryEntry<Item> displayItem, FlatChunkGeneratorConfig settings) {
	public static final Codec<FlatLevelGeneratorPreset> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					RegistryFixedCodec.of(RegistryKeys.ITEM).fieldOf("display").forGetter(preset -> preset.displayItem),
					FlatChunkGeneratorConfig.CODEC.fieldOf("settings").forGetter(preset -> preset.settings)
				)
				.apply(instance, FlatLevelGeneratorPreset::new)
	);
	public static final Codec<RegistryEntry<FlatLevelGeneratorPreset>> ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.FLAT_LEVEL_GENERATOR_PRESET, CODEC);
}
