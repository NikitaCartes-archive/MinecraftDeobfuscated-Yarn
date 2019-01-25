package net.minecraft.structure;

import java.util.Locale;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.generator.BuriedTreasureGenerator;
import net.minecraft.structure.generator.DesertTempleGenerator;
import net.minecraft.structure.generator.EndCityGenerator;
import net.minecraft.structure.generator.IglooGenerator;
import net.minecraft.structure.generator.JungleTempleGenerator;
import net.minecraft.structure.generator.MineshaftGenerator;
import net.minecraft.structure.generator.NetherFortressGenerator;
import net.minecraft.structure.generator.OceanMonumentGenerator;
import net.minecraft.structure.generator.OceanTempleGenerator;
import net.minecraft.structure.generator.PillagerOutpostGenerator;
import net.minecraft.structure.generator.ShipwreckGenerator;
import net.minecraft.structure.generator.StrongholdGenerator;
import net.minecraft.structure.generator.SwampHutGenerator;
import net.minecraft.structure.generator.WoodlandMansionGenerator;
import net.minecraft.structure.generator.village.VillageGenerator;
import net.minecraft.util.registry.Registry;

public interface StructurePieceType {
	StructurePieceType MINESHAFT_CORRIDOR = register(MineshaftGenerator.MineshaftCorridor::new, "MSCorridor");
	StructurePieceType MINESHAFT_CROSSING = register(MineshaftGenerator.MineshaftCrossing::new, "MSCrossing");
	StructurePieceType MINESHAFT_ROOM = register(MineshaftGenerator.MineshaftRoom::new, "MSRoom");
	StructurePieceType MINESHAFT_STAIRS = register(MineshaftGenerator.MineshaftStairs::new, "MSStairs");
	StructurePieceType PILLAGER_OUTPOST = register(PillagerOutpostGenerator.Piece::new, "PCP");
	StructurePieceType VILLAGE = register(VillageGenerator.Piece::new, "NVi");
	StructurePieceType field_16926 = register(NetherFortressGenerator.class_3391::new, "NeBCr");
	StructurePieceType field_16903 = register(NetherFortressGenerator.class_3392::new, "NeBEF");
	StructurePieceType field_16917 = register(NetherFortressGenerator.class_3393::new, "NeBS");
	StructurePieceType field_16930 = register(NetherFortressGenerator.class_3394::new, "NeCCS");
	StructurePieceType field_16943 = register(NetherFortressGenerator.class_3395::new, "NeCTB");
	StructurePieceType field_16952 = register(NetherFortressGenerator.class_3396::new, "NeCE");
	StructurePieceType field_16929 = register(NetherFortressGenerator.class_3397::new, "NeSCSC");
	StructurePieceType field_16962 = register(NetherFortressGenerator.class_3398::new, "NeSCLT");
	StructurePieceType field_16921 = register(NetherFortressGenerator.class_3399::new, "NeSC");
	StructurePieceType field_16945 = register(NetherFortressGenerator.class_3400::new, "NeSCRT");
	StructurePieceType field_16961 = register(NetherFortressGenerator.class_3401::new, "NeCSR");
	StructurePieceType field_16931 = register(NetherFortressGenerator.class_3402::new, "NeMT");
	StructurePieceType field_16908 = register(NetherFortressGenerator.class_3405::new, "NeRC");
	StructurePieceType field_16967 = register(NetherFortressGenerator.class_3406::new, "NeSR");
	StructurePieceType NETHER_FORTRESS_START = register(NetherFortressGenerator.Start::new, "NeStart");
	StructurePieceType field_16955 = register(StrongholdGenerator.Door::new, "SHCC");
	StructurePieceType field_16965 = register(StrongholdGenerator.class_3423::new, "SHFC");
	StructurePieceType STRONGHOLD_FIVE_WAY_CROSSING = register(StrongholdGenerator.FiveWayCrossing::new, "SH5C");
	StructurePieceType field_16906 = register(StrongholdGenerator.class_3425::new, "SHLT");
	StructurePieceType field_16959 = register(StrongholdGenerator.class_3426::new, "SHLi");
	StructurePieceType field_16939 = register(StrongholdGenerator.class_3428::new, "SHPR");
	StructurePieceType field_16948 = register(StrongholdGenerator.class_3429::new, "SHPH");
	StructurePieceType field_16958 = register(StrongholdGenerator.class_3430::new, "SHRT");
	StructurePieceType field_16941 = register(StrongholdGenerator.class_3431::new, "SHRC");
	StructurePieceType field_16904 = register(StrongholdGenerator.class_3433::new, "SHSD");
	StructurePieceType STRONGHOLD_START = register(StrongholdGenerator.Start::new, "SHStart");
	StructurePieceType field_16934 = register(StrongholdGenerator.class_3435::new, "SHS");
	StructurePieceType field_16949 = register(StrongholdGenerator.class_3436::new, "SHSSD");
	StructurePieceType JUNGLE_TEMPLE = register(JungleTempleGenerator::new, "TeJP");
	StructurePieceType OCEAN_TEMPLE = register(OceanTempleGenerator.Piece::new, "ORP");
	StructurePieceType IGLOO = register(IglooGenerator.Piece::new, "Iglu");
	StructurePieceType SWAMP_HUT = register(SwampHutGenerator::new, "TeSH");
	StructurePieceType DESERT_TEMPLE = register(DesertTempleGenerator::new, "TeDP");
	StructurePieceType field_16922 = register(OceanMonumentGenerator.class_3374::new, "OMB");
	StructurePieceType field_16911 = register(OceanMonumentGenerator.class_3376::new, "OMCR");
	StructurePieceType field_16963 = register(OceanMonumentGenerator.class_3377::new, "OMDXR");
	StructurePieceType field_16927 = register(OceanMonumentGenerator.class_3378::new, "OMDXYR");
	StructurePieceType field_16946 = register(OceanMonumentGenerator.class_3379::new, "OMDYR");
	StructurePieceType field_16970 = register(OceanMonumentGenerator.class_3380::new, "OMDYZR");
	StructurePieceType field_16925 = register(OceanMonumentGenerator.class_3381::new, "OMDZR");
	StructurePieceType OCEAN_MONUMENT_ENTRY = register(OceanMonumentGenerator.Entry::new, "OMEntry");
	StructurePieceType OCEAN_MONUMENT_PENTHOUSE = register(OceanMonumentGenerator.Penthouse::new, "OMPenthouse");
	StructurePieceType OCEAN_MONUMENT_SIMPLE_ROOM = register(OceanMonumentGenerator.SimpleRoom::new, "OMSimple");
	StructurePieceType OCEAN_MONUMENT_SIMPLE_ROOM_TOP = register(OceanMonumentGenerator.SimpleRoomTop::new, "OMSimpleT");
	StructurePieceType field_16957 = register(OceanMonumentGenerator.class_3387::new, "OMWR");
	StructurePieceType END_CITY = register(EndCityGenerator.Piece::new, "ECP");
	StructurePieceType WOODLAND_MANSION = register(WoodlandMansionGenerator.Piece::new, "WMP");
	StructurePieceType BURIED_TREASURE = register(BuriedTreasureGenerator.Piece::new, "BTP");
	StructurePieceType SHIPWRECK = register(ShipwreckGenerator.Piece::new, "Shipwreck");

	StructurePiece load(StructureManager structureManager, CompoundTag compoundTag);

	static StructurePieceType register(StructurePieceType structurePieceType, String string) {
		return Registry.register(Registry.STRUCTURE_PIECE, string.toLowerCase(Locale.ROOT), structurePieceType);
	}
}
