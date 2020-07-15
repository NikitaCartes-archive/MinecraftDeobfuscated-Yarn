/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import net.minecraft.structure.BastionRemnantGenerator;
import net.minecraft.structure.DesertVillageData;
import net.minecraft.structure.PillagerOutpostGenerator;
import net.minecraft.structure.PlainsVillageData;
import net.minecraft.structure.SavannaVillageData;
import net.minecraft.structure.SnowyVillageData;
import net.minecraft.structure.TaigaVillageData;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.RuinedPortalFeature;
import net.minecraft.world.gen.feature.RuinedPortalFeatureConfig;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

public class ConfiguredStructureFeatures {
    public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> PILLAGER_OUTPOST = ConfiguredStructureFeatures.register("pillager_outpost", StructureFeature.PILLAGER_OUTPOST.configure(new StructurePoolFeatureConfig(() -> PillagerOutpostGenerator.field_26252, 7)));
    public static final ConfiguredStructureFeature<MineshaftFeatureConfig, ? extends StructureFeature<MineshaftFeatureConfig>> MINESHAFT = ConfiguredStructureFeatures.register("mineshaft", StructureFeature.MINESHAFT.configure(new MineshaftFeatureConfig(0.004f, MineshaftFeature.Type.NORMAL)));
    public static final ConfiguredStructureFeature<MineshaftFeatureConfig, ? extends StructureFeature<MineshaftFeatureConfig>> MINESHAFT_MESA = ConfiguredStructureFeatures.register("mineshaft_mesa", StructureFeature.MINESHAFT.configure(new MineshaftFeatureConfig(0.004f, MineshaftFeature.Type.MESA)));
    public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> MANSION = ConfiguredStructureFeatures.register("mansion", StructureFeature.MANSION.configure(DefaultFeatureConfig.INSTANCE));
    public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> JUNGLE_PYRAMID = ConfiguredStructureFeatures.register("jungle_pyramid", StructureFeature.JUNGLE_PYRAMID.configure(DefaultFeatureConfig.INSTANCE));
    public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> DESERT_PYRAMID = ConfiguredStructureFeatures.register("desert_pyramid", StructureFeature.DESERT_PYRAMID.configure(DefaultFeatureConfig.INSTANCE));
    public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> IGLOO = ConfiguredStructureFeatures.register("igloo", StructureFeature.IGLOO.configure(DefaultFeatureConfig.INSTANCE));
    public static final ConfiguredStructureFeature<ShipwreckFeatureConfig, ? extends StructureFeature<ShipwreckFeatureConfig>> SHIPWRECK = ConfiguredStructureFeatures.register("shipwreck", StructureFeature.SHIPWRECK.configure(new ShipwreckFeatureConfig(false)));
    public static final ConfiguredStructureFeature<ShipwreckFeatureConfig, ? extends StructureFeature<ShipwreckFeatureConfig>> SHIPWRECK_BEACHED = ConfiguredStructureFeatures.register("shipwreck_beached", StructureFeature.SHIPWRECK.configure(new ShipwreckFeatureConfig(true)));
    public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> SWAMP_HUT = ConfiguredStructureFeatures.register("swamp_hut", StructureFeature.SWAMP_HUT.configure(DefaultFeatureConfig.INSTANCE));
    public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> STRONGHOLD = ConfiguredStructureFeatures.register("stronghold", StructureFeature.STRONGHOLD.configure(DefaultFeatureConfig.INSTANCE));
    public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> MONUMENT = ConfiguredStructureFeatures.register("monument", StructureFeature.MONUMENT.configure(DefaultFeatureConfig.INSTANCE));
    public static final ConfiguredStructureFeature<OceanRuinFeatureConfig, ? extends StructureFeature<OceanRuinFeatureConfig>> OCEAN_RUIN_COLD = ConfiguredStructureFeatures.register("ocean_ruin_cold", StructureFeature.OCEAN_RUIN.configure(new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.COLD, 0.3f, 0.9f)));
    public static final ConfiguredStructureFeature<OceanRuinFeatureConfig, ? extends StructureFeature<OceanRuinFeatureConfig>> OCEAN_RUIN_WARM = ConfiguredStructureFeatures.register("ocean_ruin_warm", StructureFeature.OCEAN_RUIN.configure(new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.WARM, 0.3f, 0.9f)));
    public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> FORTRESS = ConfiguredStructureFeatures.register("fortress", StructureFeature.FORTRESS.configure(DefaultFeatureConfig.INSTANCE));
    public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> NETHER_FOSSIL = ConfiguredStructureFeatures.register("nether_fossil", StructureFeature.NETHER_FOSSIL.configure(DefaultFeatureConfig.INSTANCE));
    public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> END_CITY = ConfiguredStructureFeatures.register("end_city", StructureFeature.END_CITY.configure(DefaultFeatureConfig.INSTANCE));
    public static final ConfiguredStructureFeature<ProbabilityConfig, ? extends StructureFeature<ProbabilityConfig>> BURIED_TREASURE = ConfiguredStructureFeatures.register("buried_treasure", StructureFeature.BURIED_TREASURE.configure(new ProbabilityConfig(0.01f)));
    public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> BASTION_REMNANT = ConfiguredStructureFeatures.register("bastion_remnant", StructureFeature.BASTION_REMNANT.configure(new StructurePoolFeatureConfig(() -> BastionRemnantGenerator.field_25941, 6)));
    public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> VILLAGE_PLAINS = ConfiguredStructureFeatures.register("village_plains", StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(() -> PlainsVillageData.field_26253, 6)));
    public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> VILLAGE_DESERT = ConfiguredStructureFeatures.register("village_desert", StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(() -> DesertVillageData.field_25948, 6)));
    public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> VILLAGE_SAVANNA = ConfiguredStructureFeatures.register("village_savanna", StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(() -> SavannaVillageData.field_26285, 6)));
    public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> VILLAGE_SNOWY = ConfiguredStructureFeatures.register("village_snowy", StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(() -> SnowyVillageData.field_26286, 6)));
    public static final ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> VILLAGE_TAIGA = ConfiguredStructureFeatures.register("village_taiga", StructureFeature.VILLAGE.configure(new StructurePoolFeatureConfig(() -> TaigaVillageData.field_26341, 6)));
    public static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> RUINED_PORTAL = ConfiguredStructureFeatures.register("ruined_portal", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.STANDARD)));
    public static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> RUINED_PORTAL_DESERT = ConfiguredStructureFeatures.register("ruined_portal_desert", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.DESERT)));
    public static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> RUINED_PORTAL_JUNGLE = ConfiguredStructureFeatures.register("ruined_portal_jungle", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.JUNGLE)));
    public static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> RUINED_PORTAL_SWAMP = ConfiguredStructureFeatures.register("ruined_portal_swamp", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.SWAMP)));
    public static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> RUINED_PORTAL_MOUNTAIN = ConfiguredStructureFeatures.register("ruined_portal_mountain", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.MOUNTAIN)));
    public static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> RUINED_PORTAL_OCEAN = ConfiguredStructureFeatures.register("ruined_portal_ocean", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.OCEAN)));
    public static final ConfiguredStructureFeature<RuinedPortalFeatureConfig, ? extends StructureFeature<RuinedPortalFeatureConfig>> RUINED_PORTAL_NETHER = ConfiguredStructureFeatures.register("ruined_portal_nether", StructureFeature.RUINED_PORTAL.configure(new RuinedPortalFeatureConfig(RuinedPortalFeature.Type.NETHER)));

    private static <FC extends FeatureConfig, F extends StructureFeature<FC>> ConfiguredStructureFeature<FC, F> register(String id, ConfiguredStructureFeature<FC, F> configuredStructureFeature) {
        return BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, id, configuredStructureFeature);
    }
}

