package net.minecraft.structure;

import java.util.Locale;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface StructurePieceType {
	StructurePieceType MINESHAFT_CORRIDOR = register(MineshaftGenerator.MineshaftCorridor::new, "MSCorridor");
	StructurePieceType MINESHAFT_CROSSING = register(MineshaftGenerator.MineshaftCrossing::new, "MSCrossing");
	StructurePieceType MINESHAFT_ROOM = register(MineshaftGenerator.MineshaftRoom::new, "MSRoom");
	StructurePieceType MINESHAFT_STAIRS = register(MineshaftGenerator.MineshaftStairs::new, "MSStairs");
	StructurePieceType NETHER_FORTRESS_BRIDGE_CROSSING = register(NetherFortressGenerator.BridgeCrossing::new, "NeBCr");
	StructurePieceType NETHER_FORTRESS_BRIDGE_END = register(NetherFortressGenerator.BridgeEnd::new, "NeBEF");
	StructurePieceType NETHER_FORTRESS_BRIDGE = register(NetherFortressGenerator.Bridge::new, "NeBS");
	StructurePieceType NETHER_FORTRESS_CORRIDOR_STAIRS = register(NetherFortressGenerator.CorridorStairs::new, "NeCCS");
	StructurePieceType NETHER_FORTRESS_CORRIDOR_BALCONY = register(NetherFortressGenerator.CorridorBalcony::new, "NeCTB");
	StructurePieceType NETHER_FORTRESS_CORRIDOR_EXIT = register(NetherFortressGenerator.CorridorExit::new, "NeCE");
	StructurePieceType NETHER_FORTRESS_CORRIDOR_CROSSING = register(NetherFortressGenerator.CorridorCrossing::new, "NeSCSC");
	StructurePieceType NETHER_FORTRESS_CORRIDOR_LEFT_TURN = register(NetherFortressGenerator.CorridorLeftTurn::new, "NeSCLT");
	StructurePieceType NETHER_FORTRESS_SMALL_CORRIDOR = register(NetherFortressGenerator.SmallCorridor::new, "NeSC");
	StructurePieceType NETHER_FORTRESS_CORRIDOR_RIGHT_TURN = register(NetherFortressGenerator.CorridorRightTurn::new, "NeSCRT");
	StructurePieceType NETHER_FORTRESS_CORRIDOR_NETHER_WARTS_ROOM = register(NetherFortressGenerator.CorridorNetherWartsRoom::new, "NeCSR");
	StructurePieceType NETHER_FORTRESS_BRIDGE_PLATFORM = register(NetherFortressGenerator.BridgePlatform::new, "NeMT");
	StructurePieceType NETHER_FORTRESS_BRIDGE_SMALL_CROSSING = register(NetherFortressGenerator.BridgeSmallCrossing::new, "NeRC");
	StructurePieceType NETHER_FORTRESS_BRIDGE_STAIRS = register(NetherFortressGenerator.BridgeStairs::new, "NeSR");
	StructurePieceType NETHER_FORTRESS_START = register(NetherFortressGenerator.Start::new, "NeStart");
	StructurePieceType STRONGHOLD_CHEST_CORRIDOR = register(StrongholdGenerator.ChestCorridor::new, "SHCC");
	StructurePieceType STRONGHOLD_SMALL_CORRIDOR = register(StrongholdGenerator.SmallCorridor::new, "SHFC");
	StructurePieceType STRONGHOLD_FIVE_WAY_CROSSING = register(StrongholdGenerator.FiveWayCrossing::new, "SH5C");
	StructurePieceType STRONGHOLD_LEFT_TURN = register(StrongholdGenerator.LeftTurn::new, "SHLT");
	StructurePieceType STRONGHOLD_LIBRARY = register(StrongholdGenerator.Library::new, "SHLi");
	StructurePieceType STRONGHOLD_PORTAL_ROOM = register(StrongholdGenerator.PortalRoom::new, "SHPR");
	StructurePieceType STRONGHOLD_PRISON_HALL = register(StrongholdGenerator.PrisonHall::new, "SHPH");
	StructurePieceType STRONGHOLD_RIGHT_TURN = register(StrongholdGenerator.RightTurn::new, "SHRT");
	StructurePieceType STRONGHOLD_SQUARE_ROOM = register(StrongholdGenerator.SquareRoom::new, "SHRC");
	StructurePieceType STRONGHOLD_SPIRAL_STAIRCASE = register(StrongholdGenerator.SpiralStaircase::new, "SHSD");
	StructurePieceType STRONGHOLD_START = register(StrongholdGenerator.Start::new, "SHStart");
	StructurePieceType STRONGHOLD_CORRIDOR = register(StrongholdGenerator.Corridor::new, "SHS");
	StructurePieceType STRONGHOLD_STAIRS = register(StrongholdGenerator.Stairs::new, "SHSSD");
	StructurePieceType JUNGLE_TEMPLE = register(JungleTempleGenerator::new, "TeJP");
	StructurePieceType OCEAN_TEMPLE = register(OceanRuinGenerator.Piece::fromNbt, "ORP");
	StructurePieceType IGLOO = register(IglooGenerator.Piece::new, "Iglu");
	StructurePieceType RUINED_PORTAL = register(RuinedPortalStructurePiece::new, "RUPO");
	StructurePieceType SWAMP_HUT = register(SwampHutGenerator::new, "TeSH");
	StructurePieceType DESERT_TEMPLE = register(DesertTempleGenerator::new, "TeDP");
	StructurePieceType OCEAN_MONUMENT_BASE = register(OceanMonumentGenerator.Base::new, "OMB");
	StructurePieceType OCEAN_MONUMENT_CORE_ROOM = register(OceanMonumentGenerator.CoreRoom::new, "OMCR");
	StructurePieceType OCEAN_MONUMENT_DOUBLE_X_ROOM = register(OceanMonumentGenerator.DoubleXRoom::new, "OMDXR");
	StructurePieceType OCEAN_MONUMENT_DOUBLE_X_Y_ROOM = register(OceanMonumentGenerator.DoubleXYRoom::new, "OMDXYR");
	StructurePieceType OCEAN_MONUMENT_DOUBLE_Y_ROOM = register(OceanMonumentGenerator.DoubleYRoom::new, "OMDYR");
	StructurePieceType OCEAN_MONUMENT_DOUBLE_Y_Z_ROOM = register(OceanMonumentGenerator.DoubleYZRoom::new, "OMDYZR");
	StructurePieceType OCEAN_MONUMENT_DOUBLE_Z_ROOM = register(OceanMonumentGenerator.DoubleZRoom::new, "OMDZR");
	StructurePieceType OCEAN_MONUMENT_ENTRY_ROOM = register(OceanMonumentGenerator.Entry::new, "OMEntry");
	StructurePieceType OCEAN_MONUMENT_PENTHOUSE = register(OceanMonumentGenerator.Penthouse::new, "OMPenthouse");
	StructurePieceType OCEAN_MONUMENT_SIMPLE_ROOM = register(OceanMonumentGenerator.SimpleRoom::new, "OMSimple");
	StructurePieceType OCEAN_MONUMENT_SIMPLE_TOP_ROOM = register(OceanMonumentGenerator.SimpleRoomTop::new, "OMSimpleT");
	StructurePieceType OCEAN_MONUMENT_WING_ROOM = register(OceanMonumentGenerator.WingRoom::new, "OMWR");
	StructurePieceType END_CITY = register(EndCityGenerator.Piece::new, "ECP");
	StructurePieceType WOODLAND_MANSION = register(WoodlandMansionGenerator.Piece::new, "WMP");
	StructurePieceType BURIED_TREASURE = register(BuriedTreasureGenerator.Piece::new, "BTP");
	StructurePieceType SHIPWRECK = register(ShipwreckGenerator.Piece::new, "Shipwreck");
	StructurePieceType NETHER_FOSSIL = register(NetherFossilGenerator.Piece::new, "NeFos");
	StructurePieceType JIGSAW = register(PoolStructurePiece::new, "jigsaw");

	StructurePiece load(StructureContext context, NbtCompound nbt);

	private static StructurePieceType register(StructurePieceType type, String id) {
		return Registry.register(Registries.STRUCTURE_PIECE, id.toLowerCase(Locale.ROOT), type);
	}

	private static StructurePieceType register(StructurePieceType.Simple type, String id) {
		return register((StructurePieceType)type, id);
	}

	private static StructurePieceType register(StructurePieceType.ManagerAware type, String id) {
		return register((StructurePieceType)type, id);
	}

	public interface ManagerAware extends StructurePieceType {
		StructurePiece load(StructureTemplateManager structureTemplateManager, NbtCompound nbt);

		@Override
		default StructurePiece load(StructureContext structureContext, NbtCompound nbtCompound) {
			return this.load(structureContext.structureTemplateManager(), nbtCompound);
		}
	}

	public interface Simple extends StructurePieceType {
		StructurePiece load(NbtCompound nbt);

		@Override
		default StructurePiece load(StructureContext structureContext, NbtCompound nbtCompound) {
			return this.load(nbtCompound);
		}
	}
}
