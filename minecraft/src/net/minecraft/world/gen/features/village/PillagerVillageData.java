package net.minecraft.world.gen.features.village;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.class_3443;
import net.minecraft.class_3777;
import net.minecraft.class_3778;
import net.minecraft.class_3781;
import net.minecraft.class_3782;
import net.minecraft.class_3784;
import net.minecraft.class_3785;
import net.minecraft.class_3790;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sortme.StructurePiece;
import net.minecraft.sortme.structures.StructureManager;
import net.minecraft.sortme.structures.processor.BlockRotStructureProcessor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class PillagerVillageData {
	public static void method_16650(
		ChunkGenerator<?> chunkGenerator, StructureManager structureManager, BlockPos blockPos, List<class_3443> list, ChunkRandom chunkRandom
	) {
		class_3778.method_16605(
			new Identifier("pillager_outpost/base_plates"), 7, PillagerVillageData.class_3792::new, chunkGenerator, structureManager, blockPos, list, chunkRandom
		);
	}

	static {
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("pillager_outpost/base_plates"),
					new Identifier("empty"),
					ImmutableList.of(Pair.of(new class_3781("pillager_outpost/base_plate"), 1)),
					class_3785.Projection.RIGID
				)
			);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("pillager_outpost/towers"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(
							new class_3782(
								ImmutableList.of(
									new class_3781("pillager_outpost/watchtower"),
									new class_3781("pillager_outpost/watchtower_overgrown", ImmutableList.of(new BlockRotStructureProcessor(0.05F)))
								)
							),
							1
						)
					),
					class_3785.Projection.RIGID
				)
			);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("pillager_outpost/feature_plates"),
					new Identifier("empty"),
					ImmutableList.of(Pair.of(new class_3781("pillager_outpost/feature_plate"), 1)),
					class_3785.Projection.TERRAIN_MATCHING
				)
			);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new Identifier("pillager_outpost/features"),
					new Identifier("empty"),
					ImmutableList.of(
						Pair.of(new class_3781("pillager_outpost/feature_cage1"), 1),
						Pair.of(new class_3781("pillager_outpost/feature_cage2"), 1),
						Pair.of(new class_3781("pillager_outpost/feature_logs"), 1),
						Pair.of(new class_3781("pillager_outpost/feature_tent1"), 1),
						Pair.of(new class_3781("pillager_outpost/feature_tent2"), 1),
						Pair.of(new class_3781("pillager_outpost/feature_targets"), 1),
						Pair.of(class_3777.field_16663, 6)
					),
					class_3785.Projection.RIGID
				)
			);
	}

	public static class class_3792 extends class_3790 {
		public class_3792(StructureManager structureManager, class_3784 arg, BlockPos blockPos, int i, Rotation rotation) {
			super(StructurePiece.field_16950, structureManager, arg, blockPos, i, rotation);
		}

		public class_3792(StructureManager structureManager, CompoundTag compoundTag) {
			super(structureManager, compoundTag, StructurePiece.field_16950);
		}
	}
}
