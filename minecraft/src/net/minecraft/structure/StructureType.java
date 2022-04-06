package net.minecraft.structure;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.structure.BuriedTreasureStructure;
import net.minecraft.world.gen.structure.DesertPyramidStructure;
import net.minecraft.world.gen.structure.EndCityStructure;
import net.minecraft.world.gen.structure.IglooStructure;
import net.minecraft.world.gen.structure.JigsawStructure;
import net.minecraft.world.gen.structure.JungleTempleStructure;
import net.minecraft.world.gen.structure.MineshaftStructure;
import net.minecraft.world.gen.structure.NetherFortressStructure;
import net.minecraft.world.gen.structure.NetherFossilStructure;
import net.minecraft.world.gen.structure.OceanMonumentStructure;
import net.minecraft.world.gen.structure.OceanRuinStructure;
import net.minecraft.world.gen.structure.RuinedPortalStructure;
import net.minecraft.world.gen.structure.ShipwreckStructure;
import net.minecraft.world.gen.structure.StrongholdStructure;
import net.minecraft.world.gen.structure.SwampHutStructure;
import net.minecraft.world.gen.structure.WoodlandMansionStructure;

public interface StructureType<S extends net.minecraft.world.gen.structure.StructureType> {
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

	Codec<S> codec();

	private static <S extends net.minecraft.world.gen.structure.StructureType> StructureType<S> register(String id, Codec<S> codec) {
		return Registry.register(Registry.STRUCTURE_TYPE, id, () -> codec);
	}
}
