package net.minecraft.world.gen.structure;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface StructureType<S extends Structure> {
	StructureType<BuriedTreasureStructure> BURIED_TREASURE = register("buried_treasure", BuriedTreasureStructure.CODEC);
	StructureType<DesertPyramidStructure> DESERT_PYRAMID = register("desert_pyramid", DesertPyramidStructure.CODEC);
	StructureType<EndCityStructure> END_CITY = register("end_city", EndCityStructure.CODEC);
	StructureType<NetherFortressStructure> FORTRESS = register("fortress", NetherFortressStructure.CODEC);
	StructureType<IglooStructure> IGLOO = register("igloo", IglooStructure.CODEC);
	StructureType<JigsawStructure> JIGSAW = register("jigsaw", JigsawStructure.CODEC);
	StructureType<JungleTempleStructure> JUNGLE_TEMPLE = register("jungle_temple", JungleTempleStructure.CODEC);
	StructureType<MineshaftStructure> MINESHAFT = register("mineshaft", MineshaftStructure.CODEC);
	StructureType<NetherFossilStructure> NETHER_FOSSIL = register("nether_fossil", NetherFossilStructure.CODEC);
	StructureType<OceanMonumentStructure> OCEAN_MONUMENT = register("ocean_monument", OceanMonumentStructure.CODEC);
	StructureType<OceanRuinStructure> OCEAN_RUIN = register("ocean_ruin", OceanRuinStructure.CODEC);
	StructureType<RuinedPortalStructure> RUINED_PORTAL = register("ruined_portal", RuinedPortalStructure.CODEC);
	StructureType<ShipwreckStructure> SHIPWRECK = register("shipwreck", ShipwreckStructure.CODEC);
	StructureType<StrongholdStructure> STRONGHOLD = register("stronghold", StrongholdStructure.CODEC);
	StructureType<SwampHutStructure> SWAMP_HUT = register("swamp_hut", SwampHutStructure.CODEC);
	StructureType<WoodlandMansionStructure> WOODLAND_MANSION = register("woodland_mansion", WoodlandMansionStructure.CODEC);

	MapCodec<S> codec();

	private static <S extends Structure> StructureType<S> register(String id, MapCodec<S> codec) {
		return Registry.register(Registries.STRUCTURE_TYPE, id, () -> codec);
	}
}
