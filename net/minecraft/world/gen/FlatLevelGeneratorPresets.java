/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.StructureSetKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;

public class FlatLevelGeneratorPresets {
    public static final RegistryKey<FlatLevelGeneratorPreset> CLASSIC_FLAT = FlatLevelGeneratorPresets.of("classic_flat");
    public static final RegistryKey<FlatLevelGeneratorPreset> TUNNELERS_DREAM = FlatLevelGeneratorPresets.of("tunnelers_dream");
    public static final RegistryKey<FlatLevelGeneratorPreset> WATER_WORLD = FlatLevelGeneratorPresets.of("water_world");
    public static final RegistryKey<FlatLevelGeneratorPreset> OVERWORLD = FlatLevelGeneratorPresets.of("overworld");
    public static final RegistryKey<FlatLevelGeneratorPreset> SNOWY_KINGDOM = FlatLevelGeneratorPresets.of("snowy_kingdom");
    public static final RegistryKey<FlatLevelGeneratorPreset> BOTTOMLESS_PIT = FlatLevelGeneratorPresets.of("bottomless_pit");
    public static final RegistryKey<FlatLevelGeneratorPreset> DESERT = FlatLevelGeneratorPresets.of("desert");
    public static final RegistryKey<FlatLevelGeneratorPreset> REDSTONE_READY = FlatLevelGeneratorPresets.of("redstone_ready");
    public static final RegistryKey<FlatLevelGeneratorPreset> THE_VOID = FlatLevelGeneratorPresets.of("the_void");

    public static RegistryEntry<FlatLevelGeneratorPreset> initAndGetDefault() {
        return new Registrar().initAndGetDefault();
    }

    private static RegistryKey<FlatLevelGeneratorPreset> of(String id) {
        return RegistryKey.of(Registry.FLAT_LEVEL_GENERATOR_PRESET_KEY, new Identifier(id));
    }

    static class Registrar {
        private final Registry<FlatLevelGeneratorPreset> flatLevelGeneratorPresetRegistry = BuiltinRegistries.FLAT_LEVEL_GENERATOR_PRESET;
        private final Registry<Biome> biomeRegistry = BuiltinRegistries.BIOME;
        private final Registry<StructureSet> structureSetRegistry = BuiltinRegistries.STRUCTURE_SET;

        Registrar() {
        }

        private RegistryEntry<FlatLevelGeneratorPreset> createAndRegister(RegistryKey<FlatLevelGeneratorPreset> registryKey, ItemConvertible icon, RegistryKey<Biome> biome, Set<RegistryKey<StructureSet>> structureSets, boolean hasFeatures, boolean hasLakes, FlatChunkGeneratorLayer ... layers) {
            RegistryEntryList.Direct direct = RegistryEntryList.of(structureSets.stream().flatMap(structureSet -> this.structureSetRegistry.getEntry((RegistryKey<StructureSet>)structureSet).stream()).collect(Collectors.toList()));
            FlatChunkGeneratorConfig flatChunkGeneratorConfig = new FlatChunkGeneratorConfig(Optional.of(direct), this.biomeRegistry);
            if (hasFeatures) {
                flatChunkGeneratorConfig.enableFeatures();
            }
            if (hasLakes) {
                flatChunkGeneratorConfig.enableLakes();
            }
            for (int i = layers.length - 1; i >= 0; --i) {
                flatChunkGeneratorConfig.getLayers().add(layers[i]);
            }
            flatChunkGeneratorConfig.setBiome(this.biomeRegistry.getOrCreateEntry(biome));
            return BuiltinRegistries.add(this.flatLevelGeneratorPresetRegistry, registryKey, new FlatLevelGeneratorPreset(icon.asItem().getRegistryEntry(), flatChunkGeneratorConfig));
        }

        public RegistryEntry<FlatLevelGeneratorPreset> initAndGetDefault() {
            this.createAndRegister(CLASSIC_FLAT, Blocks.GRASS_BLOCK, BiomeKeys.PLAINS, ImmutableSet.of(StructureSetKeys.VILLAGES), false, false, new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK), new FlatChunkGeneratorLayer(2, Blocks.DIRT), new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
            this.createAndRegister(TUNNELERS_DREAM, Blocks.STONE, BiomeKeys.WINDSWEPT_HILLS, ImmutableSet.of(StructureSetKeys.MINESHAFTS, StructureSetKeys.STRONGHOLDS), true, false, new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK), new FlatChunkGeneratorLayer(5, Blocks.DIRT), new FlatChunkGeneratorLayer(230, Blocks.STONE), new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
            this.createAndRegister(WATER_WORLD, Items.WATER_BUCKET, BiomeKeys.DEEP_OCEAN, ImmutableSet.of(StructureSetKeys.OCEAN_RUINS, StructureSetKeys.SHIPWRECKS, StructureSetKeys.OCEAN_MONUMENTS), false, false, new FlatChunkGeneratorLayer(90, Blocks.WATER), new FlatChunkGeneratorLayer(5, Blocks.GRAVEL), new FlatChunkGeneratorLayer(5, Blocks.DIRT), new FlatChunkGeneratorLayer(5, Blocks.STONE), new FlatChunkGeneratorLayer(64, Blocks.DEEPSLATE), new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
            this.createAndRegister(OVERWORLD, Blocks.GRASS, BiomeKeys.PLAINS, ImmutableSet.of(StructureSetKeys.VILLAGES, StructureSetKeys.MINESHAFTS, StructureSetKeys.PILLAGER_OUTPOSTS, StructureSetKeys.RUINED_PORTALS, StructureSetKeys.STRONGHOLDS), true, true, new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK), new FlatChunkGeneratorLayer(3, Blocks.DIRT), new FlatChunkGeneratorLayer(59, Blocks.STONE), new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
            this.createAndRegister(SNOWY_KINGDOM, Blocks.SNOW, BiomeKeys.SNOWY_PLAINS, ImmutableSet.of(StructureSetKeys.VILLAGES, StructureSetKeys.IGLOOS), false, false, new FlatChunkGeneratorLayer(1, Blocks.SNOW), new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK), new FlatChunkGeneratorLayer(3, Blocks.DIRT), new FlatChunkGeneratorLayer(59, Blocks.STONE), new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
            this.createAndRegister(BOTTOMLESS_PIT, Items.FEATHER, BiomeKeys.PLAINS, ImmutableSet.of(StructureSetKeys.VILLAGES), false, false, new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK), new FlatChunkGeneratorLayer(3, Blocks.DIRT), new FlatChunkGeneratorLayer(2, Blocks.COBBLESTONE));
            this.createAndRegister(DESERT, Blocks.SAND, BiomeKeys.DESERT, ImmutableSet.of(StructureSetKeys.VILLAGES, StructureSetKeys.DESERT_PYRAMIDS, StructureSetKeys.MINESHAFTS, StructureSetKeys.STRONGHOLDS), true, false, new FlatChunkGeneratorLayer(8, Blocks.SAND), new FlatChunkGeneratorLayer(52, Blocks.SANDSTONE), new FlatChunkGeneratorLayer(3, Blocks.STONE), new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
            this.createAndRegister(REDSTONE_READY, Items.REDSTONE, BiomeKeys.DESERT, ImmutableSet.of(), false, false, new FlatChunkGeneratorLayer(116, Blocks.SANDSTONE), new FlatChunkGeneratorLayer(3, Blocks.STONE), new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
            return this.createAndRegister(THE_VOID, Blocks.BARRIER, BiomeKeys.THE_VOID, ImmutableSet.of(), true, false, new FlatChunkGeneratorLayer(1, Blocks.AIR));
        }
    }
}

