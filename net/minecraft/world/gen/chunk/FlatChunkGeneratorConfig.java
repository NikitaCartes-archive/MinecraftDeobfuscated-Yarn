/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import net.minecraft.world.gen.feature.SingleStateFeatureConfig;
import net.minecraft.world.gen.feature.VillageFeatureConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class FlatChunkGeneratorConfig
extends ChunkGeneratorConfig {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ConfiguredFeature<?, ?> MINESHAFT = Feature.MINESHAFT.configure(new MineshaftFeatureConfig(0.004, MineshaftFeature.Type.NORMAL)).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT));
    private static final ConfiguredFeature<?, ?> VILLAGE = Feature.VILLAGE.configure(new VillageFeatureConfig("village/plains/town_centers", 6)).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT));
    private static final ConfiguredFeature<?, ?> STRONGHOLD = Feature.STRONGHOLD.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT));
    private static final ConfiguredFeature<?, ?> SWAMP_HUT = Feature.SWAMP_HUT.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT));
    private static final ConfiguredFeature<?, ?> DESERT_PYRAMID = Feature.DESERT_PYRAMID.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT));
    private static final ConfiguredFeature<?, ?> JUNGLE_TEMPLE = Feature.JUNGLE_TEMPLE.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT));
    private static final ConfiguredFeature<?, ?> IGLOO = Feature.IGLOO.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT));
    private static final ConfiguredFeature<?, ?> SHIPWRECK = Feature.SHIPWRECK.configure(new ShipwreckFeatureConfig(false)).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT));
    private static final ConfiguredFeature<?, ?> OCEAN_MONUMENT = Feature.OCEAN_MONUMENT.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT));
    private static final ConfiguredFeature<?, ?> WATER_LAKE = Feature.LAKE.configure(new SingleStateFeatureConfig(Blocks.WATER.getDefaultState())).createDecoratedFeature(Decorator.WATER_LAKE.configure(new ChanceDecoratorConfig(4)));
    private static final ConfiguredFeature<?, ?> LAVA_LAKE = Feature.LAKE.configure(new SingleStateFeatureConfig(Blocks.LAVA.getDefaultState())).createDecoratedFeature(Decorator.LAVA_LAKE.configure(new ChanceDecoratorConfig(80)));
    private static final ConfiguredFeature<?, ?> END_CITY = Feature.END_CITY.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT));
    private static final ConfiguredFeature<?, ?> WOODLAND_MANSION = Feature.WOODLAND_MANSION.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT));
    private static final ConfiguredFeature<?, ?> NETHER_BRIDGE = Feature.NETHER_BRIDGE.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT));
    private static final ConfiguredFeature<?, ?> OCEAN_RUIN = Feature.OCEAN_RUIN.configure(new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.COLD, 0.3f, 0.1f)).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT));
    private static final ConfiguredFeature<?, ?> PILLAGER_OUTPOST = Feature.PILLAGER_OUTPOST.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT));
    public static final Map<ConfiguredFeature<?, ?>, GenerationStep.Feature> FEATURE_TO_GENERATION_STEP = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(MINESHAFT, GenerationStep.Feature.UNDERGROUND_STRUCTURES);
        hashMap.put(VILLAGE, GenerationStep.Feature.SURFACE_STRUCTURES);
        hashMap.put(STRONGHOLD, GenerationStep.Feature.UNDERGROUND_STRUCTURES);
        hashMap.put(SWAMP_HUT, GenerationStep.Feature.SURFACE_STRUCTURES);
        hashMap.put(DESERT_PYRAMID, GenerationStep.Feature.SURFACE_STRUCTURES);
        hashMap.put(JUNGLE_TEMPLE, GenerationStep.Feature.SURFACE_STRUCTURES);
        hashMap.put(IGLOO, GenerationStep.Feature.SURFACE_STRUCTURES);
        hashMap.put(SHIPWRECK, GenerationStep.Feature.SURFACE_STRUCTURES);
        hashMap.put(OCEAN_RUIN, GenerationStep.Feature.SURFACE_STRUCTURES);
        hashMap.put(WATER_LAKE, GenerationStep.Feature.LOCAL_MODIFICATIONS);
        hashMap.put(LAVA_LAKE, GenerationStep.Feature.LOCAL_MODIFICATIONS);
        hashMap.put(END_CITY, GenerationStep.Feature.SURFACE_STRUCTURES);
        hashMap.put(WOODLAND_MANSION, GenerationStep.Feature.SURFACE_STRUCTURES);
        hashMap.put(NETHER_BRIDGE, GenerationStep.Feature.UNDERGROUND_STRUCTURES);
        hashMap.put(OCEAN_MONUMENT, GenerationStep.Feature.SURFACE_STRUCTURES);
        hashMap.put(PILLAGER_OUTPOST, GenerationStep.Feature.SURFACE_STRUCTURES);
    });
    public static final Map<String, ConfiguredFeature<?, ?>[]> STRUCTURE_TO_FEATURES = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put("mineshaft", new ConfiguredFeature[]{MINESHAFT});
        hashMap.put("village", new ConfiguredFeature[]{VILLAGE});
        hashMap.put("stronghold", new ConfiguredFeature[]{STRONGHOLD});
        hashMap.put("biome_1", new ConfiguredFeature[]{SWAMP_HUT, DESERT_PYRAMID, JUNGLE_TEMPLE, IGLOO, OCEAN_RUIN, SHIPWRECK});
        hashMap.put("oceanmonument", new ConfiguredFeature[]{OCEAN_MONUMENT});
        hashMap.put("lake", new ConfiguredFeature[]{WATER_LAKE});
        hashMap.put("lava_lake", new ConfiguredFeature[]{LAVA_LAKE});
        hashMap.put("endcity", new ConfiguredFeature[]{END_CITY});
        hashMap.put("mansion", new ConfiguredFeature[]{WOODLAND_MANSION});
        hashMap.put("fortress", new ConfiguredFeature[]{NETHER_BRIDGE});
        hashMap.put("pillager_outpost", new ConfiguredFeature[]{PILLAGER_OUTPOST});
    });
    public static final Map<ConfiguredFeature<?, ?>, FeatureConfig> FEATURE_TO_FEATURE_CONFIG = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(MINESHAFT, new MineshaftFeatureConfig(0.004, MineshaftFeature.Type.NORMAL));
        hashMap.put(VILLAGE, new VillageFeatureConfig("village/plains/town_centers", 6));
        hashMap.put(STRONGHOLD, FeatureConfig.DEFAULT);
        hashMap.put(SWAMP_HUT, FeatureConfig.DEFAULT);
        hashMap.put(DESERT_PYRAMID, FeatureConfig.DEFAULT);
        hashMap.put(JUNGLE_TEMPLE, FeatureConfig.DEFAULT);
        hashMap.put(IGLOO, FeatureConfig.DEFAULT);
        hashMap.put(OCEAN_RUIN, new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.COLD, 0.3f, 0.9f));
        hashMap.put(SHIPWRECK, new ShipwreckFeatureConfig(false));
        hashMap.put(OCEAN_MONUMENT, FeatureConfig.DEFAULT);
        hashMap.put(END_CITY, FeatureConfig.DEFAULT);
        hashMap.put(WOODLAND_MANSION, FeatureConfig.DEFAULT);
        hashMap.put(NETHER_BRIDGE, FeatureConfig.DEFAULT);
        hashMap.put(PILLAGER_OUTPOST, FeatureConfig.DEFAULT);
    });
    private final List<FlatChunkGeneratorLayer> layers = Lists.newArrayList();
    private final Map<String, Map<String, String>> structures = Maps.newHashMap();
    private Biome biome;
    private final BlockState[] layerBlocks = new BlockState[256];
    private boolean hasNoTerrain;
    private int groundHeight;

    @Nullable
    public static Block parseBlock(String string) {
        try {
            Identifier identifier = new Identifier(string);
            return Registry.BLOCK.getOrEmpty(identifier).orElse(null);
        } catch (IllegalArgumentException illegalArgumentException) {
            LOGGER.warn("Invalid blockstate: {}", (Object)string, (Object)illegalArgumentException);
            return null;
        }
    }

    public Biome getBiome() {
        return this.biome;
    }

    public void setBiome(Biome biome) {
        this.biome = biome;
    }

    public Map<String, Map<String, String>> getStructures() {
        return this.structures;
    }

    public List<FlatChunkGeneratorLayer> getLayers() {
        return this.layers;
    }

    public void updateLayerBlocks() {
        int i = 0;
        for (FlatChunkGeneratorLayer flatChunkGeneratorLayer : this.layers) {
            flatChunkGeneratorLayer.setStartY(i);
            i += flatChunkGeneratorLayer.getThickness();
        }
        this.groundHeight = 0;
        this.hasNoTerrain = true;
        i = 0;
        for (FlatChunkGeneratorLayer flatChunkGeneratorLayer : this.layers) {
            for (int j = flatChunkGeneratorLayer.getStartY(); j < flatChunkGeneratorLayer.getStartY() + flatChunkGeneratorLayer.getThickness(); ++j) {
                BlockState blockState = flatChunkGeneratorLayer.getBlockState();
                if (blockState.getBlock() == Blocks.AIR) continue;
                this.hasNoTerrain = false;
                this.layerBlocks[j] = blockState;
            }
            if (flatChunkGeneratorLayer.getBlockState().getBlock() == Blocks.AIR) {
                i += flatChunkGeneratorLayer.getThickness();
                continue;
            }
            this.groundHeight += flatChunkGeneratorLayer.getThickness() + i;
            i = 0;
        }
    }

    public String toString() {
        int i;
        StringBuilder stringBuilder = new StringBuilder();
        for (i = 0; i < this.layers.size(); ++i) {
            if (i > 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(this.layers.get(i));
        }
        stringBuilder.append(";");
        stringBuilder.append(Registry.BIOME.getId(this.biome));
        stringBuilder.append(";");
        if (!this.structures.isEmpty()) {
            i = 0;
            for (Map.Entry<String, Map<String, String>> entry : this.structures.entrySet()) {
                if (i++ > 0) {
                    stringBuilder.append(",");
                }
                stringBuilder.append(entry.getKey().toLowerCase(Locale.ROOT));
                Map<String, String> map = entry.getValue();
                if (map.isEmpty()) continue;
                stringBuilder.append("(");
                int j = 0;
                for (Map.Entry<String, String> entry2 : map.entrySet()) {
                    if (j++ > 0) {
                        stringBuilder.append(" ");
                    }
                    stringBuilder.append(entry2.getKey());
                    stringBuilder.append("=");
                    stringBuilder.append(entry2.getValue());
                }
                stringBuilder.append(")");
            }
        }
        return stringBuilder.toString();
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    private static FlatChunkGeneratorLayer parseLayerString(String string, int i) {
        Block block;
        int j;
        String[] strings = string.split("\\*", 2);
        if (strings.length == 2) {
            try {
                j = Math.max(Integer.parseInt(strings[0]), 0);
            } catch (NumberFormatException numberFormatException) {
                LOGGER.error("Error while parsing flat world string => {}", (Object)numberFormatException.getMessage());
                return null;
            }
        } else {
            j = 1;
        }
        int k = Math.min(i + j, 256);
        int l = k - i;
        try {
            block = FlatChunkGeneratorConfig.parseBlock(strings[strings.length - 1]);
        } catch (Exception exception) {
            LOGGER.error("Error while parsing flat world string => {}", (Object)exception.getMessage());
            return null;
        }
        if (block == null) {
            LOGGER.error("Error while parsing flat world string => Unknown block, {}", (Object)strings[strings.length - 1]);
            return null;
        }
        FlatChunkGeneratorLayer flatChunkGeneratorLayer = new FlatChunkGeneratorLayer(l, block);
        flatChunkGeneratorLayer.setStartY(i);
        return flatChunkGeneratorLayer;
    }

    @Environment(value=EnvType.CLIENT)
    private static List<FlatChunkGeneratorLayer> parseLayersString(String string) {
        ArrayList<FlatChunkGeneratorLayer> list = Lists.newArrayList();
        String[] strings = string.split(",");
        int i = 0;
        for (String string2 : strings) {
            FlatChunkGeneratorLayer flatChunkGeneratorLayer = FlatChunkGeneratorConfig.parseLayerString(string2, i);
            if (flatChunkGeneratorLayer == null) {
                return Collections.emptyList();
            }
            list.add(flatChunkGeneratorLayer);
            i += flatChunkGeneratorLayer.getThickness();
        }
        return list;
    }

    @Environment(value=EnvType.CLIENT)
    public <T> Dynamic<T> toDynamic(DynamicOps<T> dynamicOps) {
        Object object = dynamicOps.createList(this.layers.stream().map(flatChunkGeneratorLayer -> dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("height"), dynamicOps.createInt(flatChunkGeneratorLayer.getThickness()), dynamicOps.createString("block"), dynamicOps.createString(Registry.BLOCK.getId(flatChunkGeneratorLayer.getBlockState().getBlock()).toString())))));
        Object object2 = dynamicOps.createMap(this.structures.entrySet().stream().map(entry2 -> Pair.of(dynamicOps.createString(((String)entry2.getKey()).toLowerCase(Locale.ROOT)), dynamicOps.createMap(((Map)entry2.getValue()).entrySet().stream().map(entry -> Pair.of(dynamicOps.createString((String)entry.getKey()), dynamicOps.createString((String)entry.getValue()))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
        return new Dynamic<Object>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("layers"), object, dynamicOps.createString("biome"), dynamicOps.createString(Registry.BIOME.getId(this.biome).toString()), dynamicOps.createString("structures"), object2)));
    }

    public static FlatChunkGeneratorConfig fromDynamic(Dynamic<?> dynamic2) {
        FlatChunkGeneratorConfig flatChunkGeneratorConfig = ChunkGeneratorType.FLAT.createSettings();
        List<Pair> list = dynamic2.get("layers").asList(dynamic -> Pair.of(dynamic.get("height").asInt(1), FlatChunkGeneratorConfig.parseBlock(dynamic.get("block").asString(""))));
        if (list.stream().anyMatch(pair -> pair.getSecond() == null)) {
            return FlatChunkGeneratorConfig.getDefaultConfig();
        }
        List list2 = list.stream().map(pair -> new FlatChunkGeneratorLayer((Integer)pair.getFirst(), (Block)pair.getSecond())).collect(Collectors.toList());
        if (list2.isEmpty()) {
            return FlatChunkGeneratorConfig.getDefaultConfig();
        }
        flatChunkGeneratorConfig.getLayers().addAll(list2);
        flatChunkGeneratorConfig.updateLayerBlocks();
        flatChunkGeneratorConfig.setBiome(Registry.BIOME.get(new Identifier(dynamic2.get("biome").asString(""))));
        dynamic2.get("structures").flatMap(Dynamic::getMapValues).ifPresent(map -> map.keySet().forEach(dynamic -> dynamic.asString().map(string -> flatChunkGeneratorConfig.getStructures().put((String)string, Maps.newHashMap()))));
        return flatChunkGeneratorConfig;
    }

    @Environment(value=EnvType.CLIENT)
    public static FlatChunkGeneratorConfig fromString(String string) {
        Iterator<String> iterator = Splitter.on(';').split(string).iterator();
        if (!iterator.hasNext()) {
            return FlatChunkGeneratorConfig.getDefaultConfig();
        }
        FlatChunkGeneratorConfig flatChunkGeneratorConfig = ChunkGeneratorType.FLAT.createSettings();
        List<FlatChunkGeneratorLayer> list = FlatChunkGeneratorConfig.parseLayersString(iterator.next());
        if (list.isEmpty()) {
            return FlatChunkGeneratorConfig.getDefaultConfig();
        }
        flatChunkGeneratorConfig.getLayers().addAll(list);
        flatChunkGeneratorConfig.updateLayerBlocks();
        Biome biome = Biomes.PLAINS;
        if (iterator.hasNext()) {
            try {
                Identifier identifier = new Identifier(iterator.next());
                biome = Registry.BIOME.getOrEmpty(identifier).orElseThrow(() -> new IllegalArgumentException("Invalid Biome: " + identifier));
            } catch (Exception exception) {
                LOGGER.error("Error while parsing flat world string => {}", (Object)exception.getMessage());
            }
        }
        flatChunkGeneratorConfig.setBiome(biome);
        if (iterator.hasNext()) {
            String[] strings;
            for (String string2 : strings = iterator.next().toLowerCase(Locale.ROOT).split(",")) {
                String[] strings3;
                String[] strings2 = string2.split("\\(", 2);
                if (strings2[0].isEmpty()) continue;
                flatChunkGeneratorConfig.addStructure(strings2[0]);
                if (strings2.length <= 1 || !strings2[1].endsWith(")") || strings2[1].length() <= 1) continue;
                for (String string3 : strings3 = strings2[1].substring(0, strings2[1].length() - 1).split(" ")) {
                    String[] strings4 = string3.split("=", 2);
                    if (strings4.length != 2) continue;
                    flatChunkGeneratorConfig.setStructureOption(strings2[0], strings4[0], strings4[1]);
                }
            }
        } else {
            flatChunkGeneratorConfig.getStructures().put("village", Maps.newHashMap());
        }
        return flatChunkGeneratorConfig;
    }

    @Environment(value=EnvType.CLIENT)
    private void addStructure(String string) {
        HashMap map = Maps.newHashMap();
        this.structures.put(string, map);
    }

    @Environment(value=EnvType.CLIENT)
    private void setStructureOption(String string, String string2, String string3) {
        this.structures.get(string).put(string2, string3);
        if ("village".equals(string) && "distance".equals(string2)) {
            this.villageDistance = MathHelper.parseInt(string3, this.villageDistance, 9);
        }
        if ("biome_1".equals(string) && "distance".equals(string2)) {
            this.templeDistance = MathHelper.parseInt(string3, this.templeDistance, 9);
        }
        if ("stronghold".equals(string)) {
            if ("distance".equals(string2)) {
                this.strongholdDistance = MathHelper.parseInt(string3, this.strongholdDistance, 1);
            } else if ("count".equals(string2)) {
                this.strongholdCount = MathHelper.parseInt(string3, this.strongholdCount, 1);
            } else if ("spread".equals(string2)) {
                this.strongholdSpread = MathHelper.parseInt(string3, this.strongholdSpread, 1);
            }
        }
        if ("oceanmonument".equals(string)) {
            if ("separation".equals(string2)) {
                this.oceanMonumentSeparation = MathHelper.parseInt(string3, this.oceanMonumentSeparation, 1);
            } else if ("spacing".equals(string2)) {
                this.oceanMonumentSpacing = MathHelper.parseInt(string3, this.oceanMonumentSpacing, 1);
            }
        }
        if ("endcity".equals(string) && "distance".equals(string2)) {
            this.endCityDistance = MathHelper.parseInt(string3, this.endCityDistance, 1);
        }
        if ("mansion".equals(string) && "distance".equals(string2)) {
            this.mansionDistance = MathHelper.parseInt(string3, this.mansionDistance, 1);
        }
    }

    public static FlatChunkGeneratorConfig getDefaultConfig() {
        FlatChunkGeneratorConfig flatChunkGeneratorConfig = ChunkGeneratorType.FLAT.createSettings();
        flatChunkGeneratorConfig.setBiome(Biomes.PLAINS);
        flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(1, Blocks.BEDROCK));
        flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(2, Blocks.DIRT));
        flatChunkGeneratorConfig.getLayers().add(new FlatChunkGeneratorLayer(1, Blocks.GRASS_BLOCK));
        flatChunkGeneratorConfig.updateLayerBlocks();
        flatChunkGeneratorConfig.getStructures().put("village", Maps.newHashMap());
        return flatChunkGeneratorConfig;
    }

    public boolean hasNoTerrain() {
        return this.hasNoTerrain;
    }

    public BlockState[] getLayerBlocks() {
        return this.layerBlocks;
    }

    public void removeLayerBlock(int i) {
        this.layerBlocks[i] = null;
    }
}

