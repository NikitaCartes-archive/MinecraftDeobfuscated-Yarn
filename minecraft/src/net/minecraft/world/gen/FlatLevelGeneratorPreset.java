package net.minecraft.world.gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryFixedCodec;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;

public record FlatLevelGeneratorPreset(RegistryEntry<Item> displayItem, FlatChunkGeneratorConfig settings) {
	public static final Codec<FlatLevelGeneratorPreset> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					RegistryFixedCodec.of(Registry.ITEM_KEY).fieldOf("display").forGetter(preset -> preset.displayItem),
					FlatChunkGeneratorConfig.CODEC.fieldOf("settings").forGetter(preset -> preset.settings)
				)
				.apply(instance, FlatLevelGeneratorPreset::new)
	);
	public static final Codec<RegistryEntry<FlatLevelGeneratorPreset>> ENTRY_CODEC = RegistryElementCodec.of(Registry.FLAT_LEVEL_GENERATOR_PRESET_KEY, CODEC);
}
