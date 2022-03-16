/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.BuriedTreasureFeature;
import net.minecraft.world.gen.feature.DesertPyramidFeature;
import net.minecraft.world.gen.feature.EndCityFeature;
import net.minecraft.world.gen.feature.IglooFeature;
import net.minecraft.world.gen.feature.JigsawFeature;
import net.minecraft.world.gen.feature.JungleTempleFeature;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.NetherFortressFeature;
import net.minecraft.world.gen.feature.NetherFossilFeature;
import net.minecraft.world.gen.feature.OceanMonumentFeature;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.RuinedPortalFeature;
import net.minecraft.world.gen.feature.ShipwreckFeature;
import net.minecraft.world.gen.feature.StrongholdFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.SwampHutFeature;
import net.minecraft.world.gen.feature.WoodlandMansionFeature;

public interface StructureType<S extends StructureFeature> {
    public static final StructureType<BuriedTreasureFeature> BURIED_TREASURE = StructureType.register("buried_treasure", BuriedTreasureFeature.CODEC);
    public static final StructureType<DesertPyramidFeature> DESERT_PYRAMID = StructureType.register("desert_pyramid", DesertPyramidFeature.CODEC);
    public static final StructureType<EndCityFeature> END_CITY = StructureType.register("end_city", EndCityFeature.CODEC);
    public static final StructureType<NetherFortressFeature> FORTRESS = StructureType.register("fortress", NetherFortressFeature.CODEC);
    public static final StructureType<IglooFeature> IGLOO = StructureType.register("igloo", IglooFeature.CODEC);
    public static final StructureType<JigsawFeature> JIGSAW = StructureType.register("jigsaw", JigsawFeature.CODEC);
    public static final StructureType<JungleTempleFeature> JUNGLE_TEMPLE = StructureType.register("jungle_temple", JungleTempleFeature.CODEC);
    public static final StructureType<MineshaftFeature> MINESHAFT = StructureType.register("mineshaft", MineshaftFeature.CODEC);
    public static final StructureType<NetherFossilFeature> NETHER_FOSSIL = StructureType.register("nether_fossil", NetherFossilFeature.CODEC);
    public static final StructureType<OceanMonumentFeature> OCEAN_MONUMENT = StructureType.register("ocean_monument", OceanMonumentFeature.CODEC);
    public static final StructureType<OceanRuinFeature> OCEAN_RUIN = StructureType.register("ocean_ruin", OceanRuinFeature.CODEC);
    public static final StructureType<RuinedPortalFeature> RUINED_PORTAL = StructureType.register("ruined_portal", RuinedPortalFeature.CODEC);
    public static final StructureType<ShipwreckFeature> SHIPWRECK = StructureType.register("shipwreck", ShipwreckFeature.CODEC);
    public static final StructureType<StrongholdFeature> STRONGHOLD = StructureType.register("stronghold", StrongholdFeature.CODEC);
    public static final StructureType<SwampHutFeature> SWAMP_HUT = StructureType.register("swamp_hut", SwampHutFeature.CODEC);
    public static final StructureType<WoodlandMansionFeature> WOODLAND_MANSION = StructureType.register("woodland_mansion", WoodlandMansionFeature.CODEC);

    public Codec<S> codec();

    private static <S extends StructureFeature> StructureType<S> register(String id, Codec<S> codec) {
        return Registry.register(Registry.STRUCTURE_TYPE, id, () -> codec);
    }
}

