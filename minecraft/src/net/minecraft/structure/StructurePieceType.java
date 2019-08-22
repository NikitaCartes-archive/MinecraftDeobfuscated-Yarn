package net.minecraft.structure;

import java.util.Locale;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.registry.Registry;

public interface StructurePieceType {
	StructurePieceType MSCORRIDOR = register(MineshaftGenerator.MineshaftCorridor::new, "MSCorridor");
	StructurePieceType MSCROSSING = register(MineshaftGenerator.MineshaftCrossing::new, "MSCrossing");
	StructurePieceType MSROOM = register(MineshaftGenerator.MineshaftRoom::new, "MSRoom");
	StructurePieceType MSSTAIRS = register(MineshaftGenerator.MineshaftStairs::new, "MSStairs");
	StructurePieceType PCP = register(PillagerOutpostGenerator.Piece::new, "PCP");
	StructurePieceType NVI = register(VillageGenerator.Piece::new, "NVi");
	StructurePieceType NE_B_CR = register(NetherFortressGenerator.BridgeCrossing::new, "NeBCr");
	StructurePieceType NE_B_E_F = register(NetherFortressGenerator.BridgeEnd::new, "NeBEF");
	StructurePieceType NE_B_S = register(NetherFortressGenerator.Bridge::new, "NeBS");
	StructurePieceType NE_C_C_S = register(NetherFortressGenerator.CorridorStairs::new, "NeCCS");
	StructurePieceType NE_C_T_B = register(NetherFortressGenerator.CorridorBalcony::new, "NeCTB");
	StructurePieceType NE_C_E = register(NetherFortressGenerator.CorridorExit::new, "NeCE");
	StructurePieceType NE_S_C_S_C = register(NetherFortressGenerator.CorridorCrossing::new, "NeSCSC");
	StructurePieceType NE_S_C_L_T = register(NetherFortressGenerator.CorridorLeftTurn::new, "NeSCLT");
	StructurePieceType NE_S_C = register(NetherFortressGenerator.SmallCorridor::new, "NeSC");
	StructurePieceType NE_S_C_R_T = register(NetherFortressGenerator.CorridorRightTurn::new, "NeSCRT");
	StructurePieceType NE_C_S_R = register(NetherFortressGenerator.CorridorNetherWartsRoom::new, "NeCSR");
	StructurePieceType NE_M_T = register(NetherFortressGenerator.BridgePlatform::new, "NeMT");
	StructurePieceType NE_R_C = register(NetherFortressGenerator.BridgeSmallCrossing::new, "NeRC");
	StructurePieceType NE_S_R = register(NetherFortressGenerator.BridgeStairs::new, "NeSR");
	StructurePieceType NE_START = register(NetherFortressGenerator.Start::new, "NeStart");
	StructurePieceType SHCC = register(StrongholdGenerator.ChestCorridor::new, "SHCC");
	StructurePieceType SHFC = register(StrongholdGenerator.SmallCorridor::new, "SHFC");
	StructurePieceType SH5C = register(StrongholdGenerator.FiveWayCrossing::new, "SH5C");
	StructurePieceType SHLT = register(StrongholdGenerator.LeftTurn::new, "SHLT");
	StructurePieceType SHLI = register(StrongholdGenerator.Library::new, "SHLi");
	StructurePieceType SHPR = register(StrongholdGenerator.PortalRoom::new, "SHPR");
	StructurePieceType SHPH = register(StrongholdGenerator.PrisonHall::new, "SHPH");
	StructurePieceType SHRT = register(StrongholdGenerator.RightTurn::new, "SHRT");
	StructurePieceType SHRC = register(StrongholdGenerator.SquareRoom::new, "SHRC");
	StructurePieceType SHSD = register(StrongholdGenerator.SpiralStaircase::new, "SHSD");
	StructurePieceType SHSTART = register(StrongholdGenerator.Start::new, "SHStart");
	StructurePieceType SHS = register(StrongholdGenerator.Corridor::new, "SHS");
	StructurePieceType SHSSD = register(StrongholdGenerator.Stairs::new, "SHSSD");
	StructurePieceType TE_J_P = register(JungleTempleGenerator::new, "TeJP");
	StructurePieceType ORP = register(OceanRuinGenerator.Piece::new, "ORP");
	StructurePieceType IGLU = register(IglooGenerator.Piece::new, "Iglu");
	StructurePieceType TE_S_H = register(SwampHutGenerator::new, "TeSH");
	StructurePieceType TE_D_P = register(DesertTempleGenerator::new, "TeDP");
	StructurePieceType OMB = register(OceanMonumentGenerator.Base::new, "OMB");
	StructurePieceType OMCR = register(OceanMonumentGenerator.CoreRoom::new, "OMCR");
	StructurePieceType OMDXR = register(OceanMonumentGenerator.DoubleXRoom::new, "OMDXR");
	StructurePieceType OMDXYR = register(OceanMonumentGenerator.DoubleXYRoom::new, "OMDXYR");
	StructurePieceType OMDYR = register(OceanMonumentGenerator.DoubleYRoom::new, "OMDYR");
	StructurePieceType OMDYZR = register(OceanMonumentGenerator.DoubleYZRoom::new, "OMDYZR");
	StructurePieceType OMDZR = register(OceanMonumentGenerator.DoubleZRoom::new, "OMDZR");
	StructurePieceType OMENTRY = register(OceanMonumentGenerator.Entry::new, "OMEntry");
	StructurePieceType OMPENTHOUSE = register(OceanMonumentGenerator.Penthouse::new, "OMPenthouse");
	StructurePieceType OMSIMPLE = register(OceanMonumentGenerator.SimpleRoom::new, "OMSimple");
	StructurePieceType OMSIMPLE_T = register(OceanMonumentGenerator.SimpleRoomTop::new, "OMSimpleT");
	StructurePieceType OMWR = register(OceanMonumentGenerator.WingRoom::new, "OMWR");
	StructurePieceType ECP = register(EndCityGenerator.Piece::new, "ECP");
	StructurePieceType WMP = register(WoodlandMansionGenerator.Piece::new, "WMP");
	StructurePieceType BTP = register(BuriedTreasureGenerator.Piece::new, "BTP");
	StructurePieceType SHIPWRECK = register(ShipwreckGenerator.Piece::new, "Shipwreck");

	StructurePiece load(StructureManager structureManager, CompoundTag compoundTag);

	static StructurePieceType register(StructurePieceType structurePieceType, String string) {
		return Registry.register(Registry.STRUCTURE_PIECE, string.toLowerCase(Locale.ROOT), structurePieceType);
	}
}
