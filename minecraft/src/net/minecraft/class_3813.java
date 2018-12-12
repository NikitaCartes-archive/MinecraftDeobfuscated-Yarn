package net.minecraft;

import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sortme.StructurePiece;
import net.minecraft.sortme.structures.StructureManager;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.NewVillageFeatureConfig;
import net.minecraft.world.gen.features.village.DesertVillageData;
import net.minecraft.world.gen.features.village.PlainsVillageData;
import net.minecraft.world.gen.features.village.SavannaVillageData;
import net.minecraft.world.gen.features.village.SnowyVillageData;
import net.minecraft.world.gen.features.village.TaigaVillageData;

public class class_3813 {
	public static void method_16753(
		ChunkGenerator<?> chunkGenerator,
		StructureManager structureManager,
		BlockPos blockPos,
		List<class_3443> list,
		ChunkRandom chunkRandom,
		NewVillageFeatureConfig newVillageFeatureConfig
	) {
		PlainsVillageData.method_16754();
		SnowyVillageData.method_16845();
		SavannaVillageData.method_16844();
		DesertVillageData.method_17037();
		TaigaVillageData.method_17038();
		class_3778.method_16605(
			newVillageFeatureConfig.startPool, newVillageFeatureConfig.size, class_3813.class_3814::new, chunkGenerator, structureManager, blockPos, list, chunkRandom
		);
	}

	public static class class_3814 extends class_3790 {
		public class_3814(StructureManager structureManager, class_3784 arg, BlockPos blockPos, int i, Rotation rotation) {
			super(StructurePiece.field_16954, structureManager, arg, blockPos, i, rotation);
		}

		public class_3814(StructureManager structureManager, CompoundTag compoundTag) {
			super(structureManager, compoundTag, StructurePiece.field_16954);
		}
	}
}
