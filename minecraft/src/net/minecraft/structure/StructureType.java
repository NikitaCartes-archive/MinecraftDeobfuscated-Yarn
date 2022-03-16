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
	StructureType<BuriedTreasureFeature> BURIED_TREASURE = register("buried_treasure", BuriedTreasureFeature.CODEC);
	StructureType<DesertPyramidFeature> DESERT_PYRAMID = register("desert_pyramid", DesertPyramidFeature.CODEC);
	StructureType<EndCityFeature> END_CITY = register("end_city", EndCityFeature.CODEC);
	StructureType<NetherFortressFeature> FORTRESS = register("fortress", NetherFortressFeature.CODEC);
	StructureType<IglooFeature> IGLOO = register("igloo", IglooFeature.CODEC);
	StructureType<JigsawFeature> JIGSAW = register("jigsaw", JigsawFeature.CODEC);
	StructureType<JungleTempleFeature> JUNGLE_TEMPLE = register("jungle_temple", JungleTempleFeature.CODEC);
	StructureType<MineshaftFeature> MINESHAFT = register("mineshaft", MineshaftFeature.CODEC);
	StructureType<NetherFossilFeature> NETHER_FOSSIL = register("nether_fossil", NetherFossilFeature.CODEC);
	StructureType<OceanMonumentFeature> OCEAN_MONUMENT = register("ocean_monument", OceanMonumentFeature.CODEC);
	StructureType<OceanRuinFeature> OCEAN_RUIN = register("ocean_ruin", OceanRuinFeature.CODEC);
	StructureType<RuinedPortalFeature> RUINED_PORTAL = register("ruined_portal", RuinedPortalFeature.CODEC);
	StructureType<ShipwreckFeature> SHIPWRECK = register("shipwreck", ShipwreckFeature.CODEC);
	StructureType<StrongholdFeature> STRONGHOLD = register("stronghold", StrongholdFeature.CODEC);
	StructureType<SwampHutFeature> SWAMP_HUT = register("swamp_hut", SwampHutFeature.CODEC);
	StructureType<WoodlandMansionFeature> WOODLAND_MANSION = register("woodland_mansion", WoodlandMansionFeature.CODEC);

	Codec<S> codec();

	private static <S extends StructureFeature> StructureType<S> register(String id, Codec<S> codec) {
		return Registry.register(Registry.STRUCTURE_TYPE, id, () -> codec);
	}
}
