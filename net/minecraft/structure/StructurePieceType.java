/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure;

import java.util.Locale;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.BuriedTreasureGenerator;
import net.minecraft.structure.DesertTempleGenerator;
import net.minecraft.structure.EndCityGenerator;
import net.minecraft.structure.IglooGenerator;
import net.minecraft.structure.JungleTempleGenerator;
import net.minecraft.structure.MineshaftGenerator;
import net.minecraft.structure.NetherFortressGenerator;
import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.structure.OceanRuinGenerator;
import net.minecraft.structure.PillagerOutpostGenerator;
import net.minecraft.structure.ShipwreckGenerator;
import net.minecraft.structure.StrongholdGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.SwampHutGenerator;
import net.minecraft.structure.VillageGenerator;
import net.minecraft.structure.WoodlandMansionGenerator;
import net.minecraft.util.registry.Registry;

public interface StructurePieceType {
    public static final StructurePieceType MINESHAFT_CORRIDOR = StructurePieceType.register(MineshaftGenerator.MineshaftCorridor::new, "MSCorridor");
    public static final StructurePieceType MINESHAFT_CROSSING = StructurePieceType.register(MineshaftGenerator.MineshaftCrossing::new, "MSCrossing");
    public static final StructurePieceType MINESHAFT_ROOM = StructurePieceType.register(MineshaftGenerator.MineshaftRoom::new, "MSRoom");
    public static final StructurePieceType MINESHAFT_STAIRS = StructurePieceType.register(MineshaftGenerator.MineshaftStairs::new, "MSStairs");
    public static final StructurePieceType PILLAGER_OUTPOST = StructurePieceType.register(PillagerOutpostGenerator.Piece::new, "PCP");
    public static final StructurePieceType VILLAGE = StructurePieceType.register(VillageGenerator.Piece::new, "NVi");
    public static final StructurePieceType NETHER_FORTRESS_BRIDGE_CROSSING = StructurePieceType.register(NetherFortressGenerator.BridgeCrossing::new, "NeBCr");
    public static final StructurePieceType NETHER_FORTRESS_BRIDGE_END = StructurePieceType.register(NetherFortressGenerator.BridgeEnd::new, "NeBEF");
    public static final StructurePieceType NETHER_FORTRESS_BRIDGE = StructurePieceType.register(NetherFortressGenerator.Bridge::new, "NeBS");
    public static final StructurePieceType NETHER_FORTRESS_CORRIDOR_STAIRS = StructurePieceType.register(NetherFortressGenerator.CorridorStairs::new, "NeCCS");
    public static final StructurePieceType NETHER_FORTRESS_CORRIDOR_BALCONY = StructurePieceType.register(NetherFortressGenerator.CorridorBalcony::new, "NeCTB");
    public static final StructurePieceType NETHER_FORTRESS_CORRIDOR_EXIT = StructurePieceType.register(NetherFortressGenerator.CorridorExit::new, "NeCE");
    public static final StructurePieceType NETHER_FORTRESS_CORRIDOR_CROSSING = StructurePieceType.register(NetherFortressGenerator.CorridorCrossing::new, "NeSCSC");
    public static final StructurePieceType NETHER_FORTRESS_CORRIDOR_LEFT_TURN = StructurePieceType.register(NetherFortressGenerator.CorridorLeftTurn::new, "NeSCLT");
    public static final StructurePieceType NETHER_FORTRESS_SMALL_CORRIDOR = StructurePieceType.register(NetherFortressGenerator.SmallCorridor::new, "NeSC");
    public static final StructurePieceType NETHER_FORTRESS_CORRIDOR_RIGHT_TURN = StructurePieceType.register(NetherFortressGenerator.CorridorRightTurn::new, "NeSCRT");
    public static final StructurePieceType NETHER_FORTRESS_CORRIDOR_NETHER_WARTS_ROOM = StructurePieceType.register(NetherFortressGenerator.CorridorNetherWartsRoom::new, "NeCSR");
    public static final StructurePieceType NETHER_FORTRESS_BRIDGE_PLATFORM = StructurePieceType.register(NetherFortressGenerator.BridgePlatform::new, "NeMT");
    public static final StructurePieceType NETHER_FORTRESS_BRIDGE_SMALL_CROSSING = StructurePieceType.register(NetherFortressGenerator.BridgeSmallCrossing::new, "NeRC");
    public static final StructurePieceType NETHER_FORTRESS_BRIDGE_STAIRS = StructurePieceType.register(NetherFortressGenerator.BridgeStairs::new, "NeSR");
    public static final StructurePieceType NETHER_FORTRESS_START = StructurePieceType.register(NetherFortressGenerator.Start::new, "NeStart");
    public static final StructurePieceType STRONGHOLD_CHEST_CORRIDOR = StructurePieceType.register(StrongholdGenerator.ChestCorridor::new, "SHCC");
    public static final StructurePieceType STRONGHOLD_SMALL_CORRIDOR = StructurePieceType.register(StrongholdGenerator.SmallCorridor::new, "SHFC");
    public static final StructurePieceType STRONGHOLD_FIVE_WAY_CROSSING = StructurePieceType.register(StrongholdGenerator.FiveWayCrossing::new, "SH5C");
    public static final StructurePieceType STRONGHOLD_LEFT_TURN = StructurePieceType.register(StrongholdGenerator.LeftTurn::new, "SHLT");
    public static final StructurePieceType STRONGHOLD_LIBRARY = StructurePieceType.register(StrongholdGenerator.Library::new, "SHLi");
    public static final StructurePieceType STRONGHOLD_PORTAL_ROOM = StructurePieceType.register(StrongholdGenerator.PortalRoom::new, "SHPR");
    public static final StructurePieceType STRONGHOLD_PRISON_HALL = StructurePieceType.register(StrongholdGenerator.PrisonHall::new, "SHPH");
    public static final StructurePieceType STRONGHOLD_RIGHT_TURN = StructurePieceType.register(StrongholdGenerator.RightTurn::new, "SHRT");
    public static final StructurePieceType STRONGHOLD_SQUARE_ROOM = StructurePieceType.register(StrongholdGenerator.SquareRoom::new, "SHRC");
    public static final StructurePieceType STRONGHOLD_SPIRAL_STAIRCASE = StructurePieceType.register(StrongholdGenerator.SpiralStaircase::new, "SHSD");
    public static final StructurePieceType STRONGHOLD_START = StructurePieceType.register(StrongholdGenerator.Start::new, "SHStart");
    public static final StructurePieceType STRONGHOLD_CORRIDOR = StructurePieceType.register(StrongholdGenerator.Corridor::new, "SHS");
    public static final StructurePieceType STRONGHOLD_STAIRS = StructurePieceType.register(StrongholdGenerator.Stairs::new, "SHSSD");
    public static final StructurePieceType JUNGLE_TEMPLE = StructurePieceType.register(JungleTempleGenerator::new, "TeJP");
    public static final StructurePieceType OCEAN_TEMPLE = StructurePieceType.register(OceanRuinGenerator.Piece::new, "ORP");
    public static final StructurePieceType IGLOO = StructurePieceType.register(IglooGenerator.Piece::new, "Iglu");
    public static final StructurePieceType SWAMP_HUT = StructurePieceType.register(SwampHutGenerator::new, "TeSH");
    public static final StructurePieceType DESERT_TEMPLE = StructurePieceType.register(DesertTempleGenerator::new, "TeDP");
    public static final StructurePieceType OCEAN_MONUMENT_BASE = StructurePieceType.register(OceanMonumentGenerator.Base::new, "OMB");
    public static final StructurePieceType OCEAN_MONUMENT_CORE_ROOM = StructurePieceType.register(OceanMonumentGenerator.CoreRoom::new, "OMCR");
    public static final StructurePieceType OCEAN_MONUMENT_DOUBLE_X_ROOM = StructurePieceType.register(OceanMonumentGenerator.DoubleXRoom::new, "OMDXR");
    public static final StructurePieceType OCEAN_MONUMENT_DOUBLE_X_Y_ROOM = StructurePieceType.register(OceanMonumentGenerator.DoubleXYRoom::new, "OMDXYR");
    public static final StructurePieceType OCEAN_MONUMENT_DOUBLE_Y_ROOM = StructurePieceType.register(OceanMonumentGenerator.DoubleYRoom::new, "OMDYR");
    public static final StructurePieceType OCEAN_MONUMENT_DOUBLE_Y_Z_ROOM = StructurePieceType.register(OceanMonumentGenerator.DoubleYZRoom::new, "OMDYZR");
    public static final StructurePieceType OCEAN_MONUMENT_DOUBLE_Z_ROOM = StructurePieceType.register(OceanMonumentGenerator.DoubleZRoom::new, "OMDZR");
    public static final StructurePieceType OCEAN_MONUMENT_ENTRY_ROOM = StructurePieceType.register(OceanMonumentGenerator.Entry::new, "OMEntry");
    public static final StructurePieceType OCEAN_MONUMENT_PENTHOUSE = StructurePieceType.register(OceanMonumentGenerator.Penthouse::new, "OMPenthouse");
    public static final StructurePieceType OCEAN_MONUMENT_SIMPLE_ROOM = StructurePieceType.register(OceanMonumentGenerator.SimpleRoom::new, "OMSimple");
    public static final StructurePieceType OCEAN_MONUMENT_SIMPLE_TOP_ROOM = StructurePieceType.register(OceanMonumentGenerator.SimpleRoomTop::new, "OMSimpleT");
    public static final StructurePieceType OCEAN_MONUMENT_WING_ROOM = StructurePieceType.register(OceanMonumentGenerator.WingRoom::new, "OMWR");
    public static final StructurePieceType END_CITY = StructurePieceType.register(EndCityGenerator.Piece::new, "ECP");
    public static final StructurePieceType WOODLAND_MANSION = StructurePieceType.register(WoodlandMansionGenerator.Piece::new, "WMP");
    public static final StructurePieceType BURIED_TREASURE = StructurePieceType.register(BuriedTreasureGenerator.Piece::new, "BTP");
    public static final StructurePieceType SHIPWRECK = StructurePieceType.register(ShipwreckGenerator.Piece::new, "Shipwreck");

    public StructurePiece load(StructureManager var1, CompoundTag var2);

    public static StructurePieceType register(StructurePieceType pieceType, String id) {
        return Registry.register(Registry.STRUCTURE_PIECE, id.toLowerCase(Locale.ROOT), pieceType);
    }
}

