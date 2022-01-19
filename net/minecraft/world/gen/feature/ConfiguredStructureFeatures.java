/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.function.BiConsumer;
import net.minecraft.structure.BastionRemnantGenerator;
import net.minecraft.structure.DesertVillageData;
import net.minecraft.structure.PillagerOutpostGenerator;
import net.minecraft.structure.PlainsVillageData;
import net.minecraft.structure.SavannaVillageData;
import net.minecraft.structure.SnowyVillageData;
import net.minecraft.structure.TaigaVillageData;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.RangeFeatureConfig;
import net.minecraft.world.gen.feature.RuinedPortalFeature;
import net.minecraft.world.gen.feature.RuinedPortalFeatureConfig;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

public class ConfiguredStructureFeatures {
    private static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> PILLAGER_OUTPOST = ConfiguredStructureFeatures.register("pillager_outpost", StructureFeature.PILLAGER_OUTPOST.configure(new StructurePoolFeatureConfig(() -> PillagerOutpostGenerator.STRUCTURE_POOLS, 7)));
    private static final ConfiguredStructureFeature<MineshaftFeatureConfig, ? extends StructureFeature<MineshaftFeatureConfig>> MINESHAFT = ConfiguredStructureFeatures.register("mineshaft", StructureFeature.MINESHAFT.configure(new MineshaftFeatureConfig(0.004f, MineshaftFeature.Type.NORMAL)));
    private static final ConfiguredStructureFeature<MineshaftFeatureConfig, ? extends StructureFeature<MineshaftFeatureConfig>> MINESHAFT_MESA = ConfiguredStructureFeatures.register("mineshaft_mesa", StructureFeature.MINESHAFT.configure(new MineshaftFeatureConfig(0.004f, MineshaftFeature.Type.MESA)));
    private static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> MANSION = ConfiguredStructureFeatures.register("mansion", StructureFeature.MANSION.configure(DefaultFeatureConfig.INSTANCE));
    private static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> JUNGLE_PYRAMID = ConfiguredStructureFeatures.register("jungle_pyramid", StructureFeature.JUNGLE_PYRAMID.configure(DefaultFeatureConfig.INSTANCE));
    private static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> DESERT_PYRAMID = ConfiguredStructureFeatures.register("desert_pyramid", StructureFeature.DESERT_PYRAMID.configure(DefaultFeatureConfig.INSTANCE));
    private static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> IGLOO = ConfiguredStructureFeatures.register("igloo", StructureFeature.IGLOO.configure(DefaultFeatureConfig.INSTANCE));
    private static final ConfiguredStructureFeature<ShipwreckFeatureConfig, ? extends StructureFeature<ShipwreckFeatureConfig>> SHIPWRECK = ConfiguredStructureFeatures.register("shipwreck", StructureFeature.SHIPWRECK.configure(new ShipwreckFeatureConfig(false)));
    private static final ConfiguredStructureFeature<ShipwreckFeatureConfig, ? extends StructureFeature<ShipwreckFeatureConfig>> SHIPWRECK_BEACHED = ConfiguredStructureFeatures.register("shipwreck_beached", StructureFeature.SHIPWRECK.configure(new ShipwreckFeatureConfig(true)));
    private static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> SWAMP_HUT = ConfiguredStructureFeatures.register("swamp_hut", StructureFeature.SWAMP_HUT.configure(DefaultFeatureConfig.INSTANCE));
    public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> STRONGHOLD = ConfiguredStructureFeatures.register("stronghold", StructureFeature.STRONGHOLD.configure(DefaultFeatureConfig.INSTANCE));
    private static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> MONUMENT = ConfiguredStructureFeatures.register("monument", StructureFeature.MONUMENT.configure(DefaultFeatureConfig.INSTANCE));
    private static final ConfiguredStructureFeature<OceanRuinFeatureConfig, ? extends StructureFeature<OceanRuinFeatureConfig>> OCEAN_RUIN_COLD = ConfiguredStructureFeatures.register("ocean_ruin_cold", StructureFeature.OCEAN_RUIN.configure(new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.COLD, 0.3f, 0.9f)));
    private static final ConfiguredStructureFeature<OceanRuinFeatureConfig, ? extends StructureFeature<OceanRuinFeatureConfig>> OCEAN_RUIN_WARM = ConfiguredStructureFeatures.register("ocean_ruin_warm", StructureFeature.OCEAN_RUIN.configure(new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.WARM, 0.3f, 0.9f)));
    private static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> FORTRESS = ConfiguredStructureFeatures.register("fortress", StructureFeature.FORTRESS.configure(DefaultFeatureConfig.INSTANCE));
    private static final ConfiguredStructureFeature<RangeFeatureConfig, ? extends StructureFeature<RangeFeatureConfig>> NETHER_FOSSIL = ConfiguredStructureFeatures.register("nether_fossil", StructureFeature.NETHER_FOSSIL.configure(new RangeFeatureConfig(UniformHeightProvider.create(YOffset.fixed(32), YOffset.belowTop(2)))));
    private static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> END_CITY = ConfiguredStructureFeatures.register("end_city", StructureFeature.END_CITY.configure(DefaultFeatureConfig.INSTANCE));
    private static final ConfiguredStructureFeature<ProbabilityConfig, ? extends StructureFeature<ProbabilityConfig>> BURIED_TREASURE = ConfiguredStructureFeatures.register("buried_treasure", StructureFeature.BURIED_TREASURE.configure(new ProbabilityConfig(0.01f)));
    private static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> BASTION_REMNANT = ConfiguredStructureFeatures.register("bastion_remnant", StructureFeature.BASTION_REMNANT.configure(new StructurePoolFeatureConfig(() -> BastionRemnantGenerator.STRUCTURE_POOLS, 6)));
    private static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> VILLAGE_PLAINS = ConfiguredStructureFeatures.register("village_plains", StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(() -> PlainsVillageData.STRUCTURE_POOLS, 6)));
    private static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> VILLAGE_DESERT = ConfiguredStructureFeatures.register("village_desert", StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(() -> DesertVillageData.STRUCTURE_POOLS, 6)));
    private static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> VILLAGE_SAVANNA = ConfiguredStructureFeatures.register("village_savanna", StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(() -> SavannaVillageData.STRUCTURE_POOLS, 6)));
    private static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> VILLAGE_SNOWY = ConfiguredStructureFeatures.register("village_snowy", StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(() -> SnowyVillageData.STRUCTURE_POOLS, 6)));
    private static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> VILLAGE_TAIGA = ConfiguredStructureFeatures.register("village_taiga", StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(() -> TaigaVillageData.STRUCTURE_POOLS, 6)));
    private static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> RUINED_PORTAL = ConfiguredStructureFeatures.register("ruined_portal", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.STANDARD)));
    private static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> RUINED_PORTAL_DESERT = ConfiguredStructureFeatures.register("ruined_portal_desert", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.DESERT)));
    private static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> RUINED_PORTAL_JUNGLE = ConfiguredStructureFeatures.register("ruined_portal_jungle", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.JUNGLE)));
    private static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> RUINED_PORTAL_SWAMP = ConfiguredStructureFeatures.register("ruined_portal_swamp", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.SWAMP)));
    private static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> RUINED_PORTAL_MOUNTAIN = ConfiguredStructureFeatures.register("ruined_portal_mountain", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.MOUNTAIN)));
    private static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> RUINED_PORTAL_OCEAN = ConfiguredStructureFeatures.register("ruined_portal_ocean", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.OCEAN)));
    private static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> RUINED_PORTAL_NETHER = ConfiguredStructureFeatures.register("ruined_portal_nether", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.NETHER)));

    public static ConfiguredStructureFeature<?, ?> getDefault() {
        return MINESHAFT;
    }

    private static <FC extends FeatureConfig, F extends StructureFeature<FC>> ConfiguredStructureFeature<FC, F> register(String id, ConfiguredStructureFeature<FC, F> configuredStructureFeature) {
        return BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, id, configuredStructureFeature);
    }

    private static void register(BiConsumer<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>> registrar, ConfiguredStructureFeature<?, ?> feature, Set<RegistryKey<Biome>> biomes) {
        biomes.forEach(biome -> registrar.accept(feature, (RegistryKey<Biome>)biome));
    }

    private static void register(BiConsumer<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>> registrar, ConfiguredStructureFeature<?, ?> feature, RegistryKey<Biome> biome) {
        registrar.accept(feature, biome);
    }

    public static void registerAll(BiConsumer<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>> registrar) {
        ImmutableCollection set = ((ImmutableSet.Builder)((ImmutableSet.Builder)((ImmutableSet.Builder)((ImmutableSet.Builder)ImmutableSet.builder().add(BiomeKeys.DEEP_FROZEN_OCEAN)).add(BiomeKeys.DEEP_COLD_OCEAN)).add(BiomeKeys.DEEP_OCEAN)).add(BiomeKeys.DEEP_LUKEWARM_OCEAN)).build();
        ImmutableCollection set2 = ((ImmutableSet.Builder)((ImmutableSet.Builder)((ImmutableSet.Builder)((ImmutableSet.Builder)((ImmutableSet.Builder)((ImmutableSet.Builder)ImmutableSet.builder().add(BiomeKeys.FROZEN_OCEAN)).add(BiomeKeys.OCEAN)).add(BiomeKeys.COLD_OCEAN)).add(BiomeKeys.LUKEWARM_OCEAN)).add(BiomeKeys.WARM_OCEAN)).addAll((Iterable)set)).build();
        ImmutableCollection set3 = ((ImmutableSet.Builder)((ImmutableSet.Builder)ImmutableSet.builder().add(BiomeKeys.BEACH)).add(BiomeKeys.SNOWY_BEACH)).build();
        ImmutableCollection set4 = ((ImmutableSet.Builder)((ImmutableSet.Builder)ImmutableSet.builder().add(BiomeKeys.RIVER)).add(BiomeKeys.FROZEN_RIVER)).build();
        ImmutableCollection set5 = ((ImmutableSet.Builder)((ImmutableSet.Builder)((ImmutableSet.Builder)((ImmutableSet.Builder)((ImmutableSet.Builder)ImmutableSet.builder().add(BiomeKeys.MEADOW)).add(BiomeKeys.FROZEN_PEAKS)).add(BiomeKeys.JAGGED_PEAKS)).add(BiomeKeys.STONY_PEAKS)).add(BiomeKeys.SNOWY_SLOPES)).build();
        ImmutableCollection set6 = ((ImmutableSet.Builder)((ImmutableSet.Builder)((ImmutableSet.Builder)ImmutableSet.builder().add(BiomeKeys.BADLANDS)).add(BiomeKeys.ERODED_BADLANDS)).add(BiomeKeys.WOODED_BADLANDS)).build();
        ImmutableCollection set7 = ((ImmutableSet.Builder)((ImmutableSet.Builder)((ImmutableSet.Builder)ImmutableSet.builder().add(BiomeKeys.WINDSWEPT_HILLS)).add(BiomeKeys.WINDSWEPT_FOREST)).add(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS)).build();
        ImmutableCollection set8 = ((ImmutableSet.Builder)((ImmutableSet.Builder)((ImmutableSet.Builder)((ImmutableSet.Builder)ImmutableSet.builder().add(BiomeKeys.TAIGA)).add(BiomeKeys.SNOWY_TAIGA)).add(BiomeKeys.OLD_GROWTH_PINE_TAIGA)).add(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA)).build();
        ImmutableCollection set9 = ((ImmutableSet.Builder)((ImmutableSet.Builder)((ImmutableSet.Builder)ImmutableSet.builder().add(BiomeKeys.BAMBOO_JUNGLE)).add(BiomeKeys.JUNGLE)).add(BiomeKeys.SPARSE_JUNGLE)).build();
        ImmutableCollection set10 = ((ImmutableSet.Builder)((ImmutableSet.Builder)((ImmutableSet.Builder)((ImmutableSet.Builder)((ImmutableSet.Builder)((ImmutableSet.Builder)ImmutableSet.builder().add(BiomeKeys.FOREST)).add(BiomeKeys.FLOWER_FOREST)).add(BiomeKeys.BIRCH_FOREST)).add(BiomeKeys.OLD_GROWTH_BIRCH_FOREST)).add(BiomeKeys.DARK_FOREST)).add(BiomeKeys.GROVE)).build();
        ImmutableCollection set11 = ((ImmutableSet.Builder)((ImmutableSet.Builder)((ImmutableSet.Builder)((ImmutableSet.Builder)((ImmutableSet.Builder)ImmutableSet.builder().add(BiomeKeys.NETHER_WASTES)).add(BiomeKeys.BASALT_DELTAS)).add(BiomeKeys.SOUL_SAND_VALLEY)).add(BiomeKeys.CRIMSON_FOREST)).add(BiomeKeys.WARPED_FOREST)).build();
        ConfiguredStructureFeatures.register(registrar, BURIED_TREASURE, (Set<RegistryKey<Biome>>)((Object)set3));
        ConfiguredStructureFeatures.register(registrar, DESERT_PYRAMID, BiomeKeys.DESERT);
        ConfiguredStructureFeatures.register(registrar, IGLOO, BiomeKeys.SNOWY_TAIGA);
        ConfiguredStructureFeatures.register(registrar, IGLOO, BiomeKeys.SNOWY_PLAINS);
        ConfiguredStructureFeatures.register(registrar, IGLOO, BiomeKeys.SNOWY_SLOPES);
        ConfiguredStructureFeatures.register(registrar, JUNGLE_PYRAMID, BiomeKeys.BAMBOO_JUNGLE);
        ConfiguredStructureFeatures.register(registrar, JUNGLE_PYRAMID, BiomeKeys.JUNGLE);
        ConfiguredStructureFeatures.register(registrar, MINESHAFT, (Set<RegistryKey<Biome>>)((Object)set2));
        ConfiguredStructureFeatures.register(registrar, MINESHAFT, (Set<RegistryKey<Biome>>)((Object)set4));
        ConfiguredStructureFeatures.register(registrar, MINESHAFT, (Set<RegistryKey<Biome>>)((Object)set3));
        ConfiguredStructureFeatures.register(registrar, MINESHAFT, BiomeKeys.STONY_SHORE);
        ConfiguredStructureFeatures.register(registrar, MINESHAFT, (Set<RegistryKey<Biome>>)((Object)set5));
        ConfiguredStructureFeatures.register(registrar, MINESHAFT, (Set<RegistryKey<Biome>>)((Object)set7));
        ConfiguredStructureFeatures.register(registrar, MINESHAFT, (Set<RegistryKey<Biome>>)((Object)set8));
        ConfiguredStructureFeatures.register(registrar, MINESHAFT, (Set<RegistryKey<Biome>>)((Object)set9));
        ConfiguredStructureFeatures.register(registrar, MINESHAFT, (Set<RegistryKey<Biome>>)((Object)set10));
        ConfiguredStructureFeatures.register(registrar, MINESHAFT, BiomeKeys.MUSHROOM_FIELDS);
        ConfiguredStructureFeatures.register(registrar, MINESHAFT, BiomeKeys.ICE_SPIKES);
        ConfiguredStructureFeatures.register(registrar, MINESHAFT, BiomeKeys.WINDSWEPT_SAVANNA);
        ConfiguredStructureFeatures.register(registrar, MINESHAFT, BiomeKeys.DESERT);
        ConfiguredStructureFeatures.register(registrar, MINESHAFT, BiomeKeys.SAVANNA);
        ConfiguredStructureFeatures.register(registrar, MINESHAFT, BiomeKeys.SNOWY_PLAINS);
        ConfiguredStructureFeatures.register(registrar, MINESHAFT, BiomeKeys.PLAINS);
        ConfiguredStructureFeatures.register(registrar, MINESHAFT, BiomeKeys.SUNFLOWER_PLAINS);
        ConfiguredStructureFeatures.register(registrar, MINESHAFT, BiomeKeys.SWAMP);
        ConfiguredStructureFeatures.register(registrar, MINESHAFT, BiomeKeys.SAVANNA_PLATEAU);
        ConfiguredStructureFeatures.register(registrar, MINESHAFT, BiomeKeys.DRIPSTONE_CAVES);
        ConfiguredStructureFeatures.register(registrar, MINESHAFT, BiomeKeys.LUSH_CAVES);
        ConfiguredStructureFeatures.register(registrar, MINESHAFT_MESA, (Set<RegistryKey<Biome>>)((Object)set6));
        ConfiguredStructureFeatures.register(registrar, MONUMENT, (Set<RegistryKey<Biome>>)((Object)set));
        ConfiguredStructureFeatures.register(registrar, OCEAN_RUIN_COLD, BiomeKeys.FROZEN_OCEAN);
        ConfiguredStructureFeatures.register(registrar, OCEAN_RUIN_COLD, BiomeKeys.COLD_OCEAN);
        ConfiguredStructureFeatures.register(registrar, OCEAN_RUIN_COLD, BiomeKeys.OCEAN);
        ConfiguredStructureFeatures.register(registrar, OCEAN_RUIN_COLD, BiomeKeys.DEEP_FROZEN_OCEAN);
        ConfiguredStructureFeatures.register(registrar, OCEAN_RUIN_COLD, BiomeKeys.DEEP_COLD_OCEAN);
        ConfiguredStructureFeatures.register(registrar, OCEAN_RUIN_COLD, BiomeKeys.DEEP_OCEAN);
        ConfiguredStructureFeatures.register(registrar, OCEAN_RUIN_WARM, BiomeKeys.LUKEWARM_OCEAN);
        ConfiguredStructureFeatures.register(registrar, OCEAN_RUIN_WARM, BiomeKeys.WARM_OCEAN);
        ConfiguredStructureFeatures.register(registrar, OCEAN_RUIN_WARM, BiomeKeys.DEEP_LUKEWARM_OCEAN);
        ConfiguredStructureFeatures.register(registrar, PILLAGER_OUTPOST, BiomeKeys.DESERT);
        ConfiguredStructureFeatures.register(registrar, PILLAGER_OUTPOST, BiomeKeys.PLAINS);
        ConfiguredStructureFeatures.register(registrar, PILLAGER_OUTPOST, BiomeKeys.SAVANNA);
        ConfiguredStructureFeatures.register(registrar, PILLAGER_OUTPOST, BiomeKeys.SNOWY_PLAINS);
        ConfiguredStructureFeatures.register(registrar, PILLAGER_OUTPOST, BiomeKeys.TAIGA);
        ConfiguredStructureFeatures.register(registrar, PILLAGER_OUTPOST, (Set<RegistryKey<Biome>>)((Object)set5));
        ConfiguredStructureFeatures.register(registrar, PILLAGER_OUTPOST, BiomeKeys.GROVE);
        ConfiguredStructureFeatures.register(registrar, RUINED_PORTAL_DESERT, BiomeKeys.DESERT);
        ConfiguredStructureFeatures.register(registrar, RUINED_PORTAL_JUNGLE, (Set<RegistryKey<Biome>>)((Object)set9));
        ConfiguredStructureFeatures.register(registrar, RUINED_PORTAL_OCEAN, (Set<RegistryKey<Biome>>)((Object)set2));
        ConfiguredStructureFeatures.register(registrar, RUINED_PORTAL_SWAMP, BiomeKeys.SWAMP);
        ConfiguredStructureFeatures.register(registrar, RUINED_PORTAL_MOUNTAIN, (Set<RegistryKey<Biome>>)((Object)set6));
        ConfiguredStructureFeatures.register(registrar, RUINED_PORTAL_MOUNTAIN, (Set<RegistryKey<Biome>>)((Object)set7));
        ConfiguredStructureFeatures.register(registrar, RUINED_PORTAL_MOUNTAIN, BiomeKeys.SAVANNA_PLATEAU);
        ConfiguredStructureFeatures.register(registrar, RUINED_PORTAL_MOUNTAIN, BiomeKeys.WINDSWEPT_SAVANNA);
        ConfiguredStructureFeatures.register(registrar, RUINED_PORTAL_MOUNTAIN, BiomeKeys.STONY_SHORE);
        ConfiguredStructureFeatures.register(registrar, RUINED_PORTAL_MOUNTAIN, (Set<RegistryKey<Biome>>)((Object)set5));
        ConfiguredStructureFeatures.register(registrar, RUINED_PORTAL, BiomeKeys.MUSHROOM_FIELDS);
        ConfiguredStructureFeatures.register(registrar, RUINED_PORTAL, BiomeKeys.ICE_SPIKES);
        ConfiguredStructureFeatures.register(registrar, RUINED_PORTAL, (Set<RegistryKey<Biome>>)((Object)set3));
        ConfiguredStructureFeatures.register(registrar, RUINED_PORTAL, (Set<RegistryKey<Biome>>)((Object)set4));
        ConfiguredStructureFeatures.register(registrar, RUINED_PORTAL, (Set<RegistryKey<Biome>>)((Object)set8));
        ConfiguredStructureFeatures.register(registrar, RUINED_PORTAL, (Set<RegistryKey<Biome>>)((Object)set10));
        ConfiguredStructureFeatures.register(registrar, RUINED_PORTAL, BiomeKeys.DRIPSTONE_CAVES);
        ConfiguredStructureFeatures.register(registrar, RUINED_PORTAL, BiomeKeys.LUSH_CAVES);
        ConfiguredStructureFeatures.register(registrar, RUINED_PORTAL, BiomeKeys.SAVANNA);
        ConfiguredStructureFeatures.register(registrar, RUINED_PORTAL, BiomeKeys.SNOWY_PLAINS);
        ConfiguredStructureFeatures.register(registrar, RUINED_PORTAL, BiomeKeys.PLAINS);
        ConfiguredStructureFeatures.register(registrar, RUINED_PORTAL, BiomeKeys.SUNFLOWER_PLAINS);
        ConfiguredStructureFeatures.register(registrar, SHIPWRECK_BEACHED, (Set<RegistryKey<Biome>>)((Object)set3));
        ConfiguredStructureFeatures.register(registrar, SHIPWRECK, (Set<RegistryKey<Biome>>)((Object)set2));
        ConfiguredStructureFeatures.register(registrar, SWAMP_HUT, BiomeKeys.SWAMP);
        ConfiguredStructureFeatures.register(registrar, VILLAGE_DESERT, BiomeKeys.DESERT);
        ConfiguredStructureFeatures.register(registrar, VILLAGE_PLAINS, BiomeKeys.PLAINS);
        ConfiguredStructureFeatures.register(registrar, VILLAGE_PLAINS, BiomeKeys.MEADOW);
        ConfiguredStructureFeatures.register(registrar, VILLAGE_SAVANNA, BiomeKeys.SAVANNA);
        ConfiguredStructureFeatures.register(registrar, VILLAGE_SNOWY, BiomeKeys.SNOWY_PLAINS);
        ConfiguredStructureFeatures.register(registrar, VILLAGE_TAIGA, BiomeKeys.TAIGA);
        ConfiguredStructureFeatures.register(registrar, MANSION, BiomeKeys.DARK_FOREST);
        ConfiguredStructureFeatures.register(registrar, FORTRESS, (Set<RegistryKey<Biome>>)((Object)set11));
        ConfiguredStructureFeatures.register(registrar, NETHER_FOSSIL, BiomeKeys.SOUL_SAND_VALLEY);
        ConfiguredStructureFeatures.register(registrar, BASTION_REMNANT, BiomeKeys.CRIMSON_FOREST);
        ConfiguredStructureFeatures.register(registrar, BASTION_REMNANT, BiomeKeys.NETHER_WASTES);
        ConfiguredStructureFeatures.register(registrar, BASTION_REMNANT, BiomeKeys.SOUL_SAND_VALLEY);
        ConfiguredStructureFeatures.register(registrar, BASTION_REMNANT, BiomeKeys.WARPED_FOREST);
        ConfiguredStructureFeatures.register(registrar, RUINED_PORTAL_NETHER, (Set<RegistryKey<Biome>>)((Object)set11));
        ConfiguredStructureFeatures.register(registrar, END_CITY, BiomeKeys.END_HIGHLANDS);
        ConfiguredStructureFeatures.register(registrar, END_CITY, BiomeKeys.END_MIDLANDS);
    }
}

