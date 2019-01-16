package net.minecraft.sortme;

import java.util.Locale;
import net.minecraft.class_3366;
import net.minecraft.class_3443;
import net.minecraft.class_3789;
import net.minecraft.class_3813;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sortme.structures.DesertTempleGenerator;
import net.minecraft.sortme.structures.EndCityGenerator;
import net.minecraft.sortme.structures.IglooGenerator;
import net.minecraft.sortme.structures.JungleTempleGenerator;
import net.minecraft.sortme.structures.MansionGenerator;
import net.minecraft.sortme.structures.MineshaftGenerator;
import net.minecraft.sortme.structures.NetherFortressGenerator;
import net.minecraft.sortme.structures.OceanTempleGenerator;
import net.minecraft.sortme.structures.ShipwreckGenerator;
import net.minecraft.sortme.structures.StrongholdGenerator;
import net.minecraft.sortme.structures.StructureManager;
import net.minecraft.sortme.structures.SwampHutGenerator;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.features.village.PillagerVillageData;

public interface StructurePiece {
	StructurePiece field_16969 = setPieceId(MineshaftGenerator.MineshaftCorridor::new, "MSCorridor");
	StructurePiece field_16919 = setPieceId(MineshaftGenerator.MineshaftCrossing::new, "MSCrossing");
	StructurePiece field_16915 = setPieceId(MineshaftGenerator.MineshaftRoom::new, "MSRoom");
	StructurePiece field_16968 = setPieceId(MineshaftGenerator.MineshaftStairs::new, "MSStairs");
	StructurePiece field_16950 = setPieceId(PillagerVillageData.class_3792::new, "PCP");
	StructurePiece field_16954 = setPieceId(class_3813.class_3814::new, "NVi");
	StructurePiece field_16926 = setPieceId(NetherFortressGenerator.class_3391::new, "NeBCr");
	StructurePiece field_16903 = setPieceId(NetherFortressGenerator.class_3392::new, "NeBEF");
	StructurePiece field_16917 = setPieceId(NetherFortressGenerator.class_3393::new, "NeBS");
	StructurePiece field_16930 = setPieceId(NetherFortressGenerator.class_3394::new, "NeCCS");
	StructurePiece field_16943 = setPieceId(NetherFortressGenerator.class_3395::new, "NeCTB");
	StructurePiece field_16952 = setPieceId(NetherFortressGenerator.class_3396::new, "NeCE");
	StructurePiece field_16929 = setPieceId(NetherFortressGenerator.class_3397::new, "NeSCSC");
	StructurePiece field_16962 = setPieceId(NetherFortressGenerator.class_3398::new, "NeSCLT");
	StructurePiece field_16921 = setPieceId(NetherFortressGenerator.class_3399::new, "NeSC");
	StructurePiece field_16945 = setPieceId(NetherFortressGenerator.class_3400::new, "NeSCRT");
	StructurePiece field_16961 = setPieceId(NetherFortressGenerator.class_3401::new, "NeCSR");
	StructurePiece field_16931 = setPieceId(NetherFortressGenerator.class_3402::new, "NeMT");
	StructurePiece field_16908 = setPieceId(NetherFortressGenerator.class_3405::new, "NeRC");
	StructurePiece field_16967 = setPieceId(NetherFortressGenerator.class_3406::new, "NeSR");
	StructurePiece field_16924 = setPieceId(NetherFortressGenerator.class_3407::new, "NeStart");
	StructurePiece field_16955 = setPieceId(StrongholdGenerator.class_3422::new, "SHCC");
	StructurePiece field_16965 = setPieceId(StrongholdGenerator.class_3423::new, "SHFC");
	StructurePiece field_16937 = setPieceId(StrongholdGenerator.class_3424::new, "SH5C");
	StructurePiece field_16906 = setPieceId(StrongholdGenerator.class_3425::new, "SHLT");
	StructurePiece field_16959 = setPieceId(StrongholdGenerator.class_3426::new, "SHLi");
	StructurePiece field_16939 = setPieceId(StrongholdGenerator.class_3428::new, "SHPR");
	StructurePiece field_16948 = setPieceId(StrongholdGenerator.class_3429::new, "SHPH");
	StructurePiece field_16958 = setPieceId(StrongholdGenerator.class_3430::new, "SHRT");
	StructurePiece field_16941 = setPieceId(StrongholdGenerator.class_3431::new, "SHRC");
	StructurePiece field_16904 = setPieceId(StrongholdGenerator.class_3433::new, "SHSD");
	StructurePiece field_16914 = setPieceId(StrongholdGenerator.class_3434::new, "SHStart");
	StructurePiece field_16934 = setPieceId(StrongholdGenerator.class_3435::new, "SHS");
	StructurePiece field_16949 = setPieceId(StrongholdGenerator.class_3436::new, "SHSSD");
	StructurePiece field_16953 = setPieceId(JungleTempleGenerator::new, "TeJP");
	StructurePiece field_16932 = setPieceId(OceanTempleGenerator.class_3410::new, "ORP");
	StructurePiece field_16909 = setPieceId(IglooGenerator.class_3352::new, "Iglu");
	StructurePiece field_16918 = setPieceId(SwampHutGenerator::new, "TeSH");
	StructurePiece field_16933 = setPieceId(DesertTempleGenerator::new, "TeDP");
	StructurePiece field_16922 = setPieceId(class_3366.class_3374::new, "OMB");
	StructurePiece field_16911 = setPieceId(class_3366.class_3376::new, "OMCR");
	StructurePiece field_16963 = setPieceId(class_3366.class_3377::new, "OMDXR");
	StructurePiece field_16927 = setPieceId(class_3366.class_3378::new, "OMDXYR");
	StructurePiece field_16946 = setPieceId(class_3366.class_3379::new, "OMDYR");
	StructurePiece field_16970 = setPieceId(class_3366.class_3380::new, "OMDYZR");
	StructurePiece field_16925 = setPieceId(class_3366.class_3381::new, "OMDZR");
	StructurePiece field_16905 = setPieceId(class_3366.class_3382::new, "OMEntry");
	StructurePiece field_16966 = setPieceId(class_3366.class_3383::new, "OMPenthouse");
	StructurePiece field_16928 = setPieceId(class_3366.class_3385::new, "OMSimple");
	StructurePiece field_16944 = setPieceId(class_3366.class_3386::new, "OMSimpleT");
	StructurePiece field_16957 = setPieceId(class_3366.class_3387::new, "OMWR");
	StructurePiece field_16936 = setPieceId(EndCityGenerator.class_3343::new, "ECP");
	StructurePiece field_16907 = setPieceId(MansionGenerator.class_3480::new, "WMP");
	StructurePiece field_16960 = setPieceId(class_3789.class_3339::new, "BTP");
	StructurePiece field_16935 = setPieceId(ShipwreckGenerator.class_3416::new, "Shipwreck");

	class_3443 load(StructureManager structureManager, CompoundTag compoundTag);

	static StructurePiece setPieceId(StructurePiece structurePiece, String string) {
		return Registry.register(Registry.STRUCTURE_PIECE, string.toLowerCase(Locale.ROOT), structurePiece);
	}
}
