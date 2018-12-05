package net.minecraft;

import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sortme.StructurePiece;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.config.feature.NewVillageFeatureConfig;

public class class_3813 {
	public static void method_16753(
		ChunkGenerator<?> chunkGenerator, class_3485 arg, BlockPos blockPos, List<class_3443> list, class_2919 arg2, NewVillageFeatureConfig newVillageFeatureConfig
	) {
		class_3815.method_16754();
		class_3836.method_16845();
		class_3834.method_16844();
		class_3778.method_16605(
			newVillageFeatureConfig.startPool, newVillageFeatureConfig.size, class_3813.class_3814::new, chunkGenerator, arg, blockPos, list, arg2
		);
	}

	public static class class_3814 extends class_3790 {
		public class_3814(class_3485 arg, class_3784 arg2, BlockPos blockPos, int i, Rotation rotation) {
			super(StructurePiece.field_16954, arg, arg2, blockPos, i, rotation);
		}

		public class_3814(class_3485 arg, CompoundTag compoundTag) {
			super(arg, compoundTag, StructurePiece.field_16954);
		}
	}
}
