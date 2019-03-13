package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class FossilFeature extends Feature<DefaultFeatureConfig> {
	private static final Identifier field_13624 = new Identifier("fossil/spine_1");
	private static final Identifier field_13615 = new Identifier("fossil/spine_2");
	private static final Identifier field_13627 = new Identifier("fossil/spine_3");
	private static final Identifier field_13619 = new Identifier("fossil/spine_4");
	private static final Identifier field_13620 = new Identifier("fossil/spine_1_coal");
	private static final Identifier field_13618 = new Identifier("fossil/spine_2_coal");
	private static final Identifier field_13625 = new Identifier("fossil/spine_3_coal");
	private static final Identifier field_13616 = new Identifier("fossil/spine_4_coal");
	private static final Identifier field_13611 = new Identifier("fossil/skull_1");
	private static final Identifier field_13621 = new Identifier("fossil/skull_2");
	private static final Identifier field_13612 = new Identifier("fossil/skull_3");
	private static final Identifier field_13622 = new Identifier("fossil/skull_4");
	private static final Identifier field_13614 = new Identifier("fossil/skull_1_coal");
	private static final Identifier field_13613 = new Identifier("fossil/skull_2_coal");
	private static final Identifier field_13623 = new Identifier("fossil/skull_3_coal");
	private static final Identifier field_13610 = new Identifier("fossil/skull_4_coal");
	private static final Identifier[] field_13617 = new Identifier[]{
		field_13624, field_13615, field_13627, field_13619, field_13611, field_13621, field_13612, field_13622
	};
	private static final Identifier[] field_13626 = new Identifier[]{
		field_13620, field_13618, field_13625, field_13616, field_13614, field_13613, field_13623, field_13610
	};

	public FossilFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	public boolean method_13236(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		Random random2 = iWorld.getRandom();
		Rotation[] rotations = Rotation.values();
		Rotation rotation = rotations[random2.nextInt(rotations.length)];
		int i = random2.nextInt(field_13617.length);
		StructureManager structureManager = ((ServerWorld)iWorld.getWorld()).getSaveHandler().getStructureManager();
		Structure structure = structureManager.method_15091(field_13617[i]);
		Structure structure2 = structureManager.method_15091(field_13626[i]);
		ChunkPos chunkPos = new ChunkPos(blockPos);
		MutableIntBoundingBox mutableIntBoundingBox = new MutableIntBoundingBox(
			chunkPos.getStartX(), 0, chunkPos.getStartZ(), chunkPos.getEndX(), 256, chunkPos.getEndZ()
		);
		StructurePlacementData structurePlacementData = new StructurePlacementData()
			.setRotation(rotation)
			.setBoundingBox(mutableIntBoundingBox)
			.setRandom(random2)
			.method_16184(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
		BlockPos blockPos2 = structure.method_15166(rotation);
		int j = random2.nextInt(16 - blockPos2.getX());
		int k = random2.nextInt(16 - blockPos2.getZ());
		int l = 256;

		for (int m = 0; m < blockPos2.getX(); m++) {
			for (int n = 0; n < blockPos2.getX(); n++) {
				l = Math.min(l, iWorld.method_8589(Heightmap.Type.OCEAN_FLOOR_WG, blockPos.getX() + m + j, blockPos.getZ() + n + k));
			}
		}

		int m = Math.max(l - 15 - random2.nextInt(10), 10);
		BlockPos blockPos3 = structure.method_15167(blockPos.add(j, m, k), Mirror.NONE, rotation);
		BlockRotStructureProcessor blockRotStructureProcessor = new BlockRotStructureProcessor(0.9F);
		structurePlacementData.clearProcessors().method_16184(blockRotStructureProcessor);
		structure.method_15172(iWorld, blockPos3, structurePlacementData, 4);
		structurePlacementData.method_16664(blockRotStructureProcessor);
		BlockRotStructureProcessor blockRotStructureProcessor2 = new BlockRotStructureProcessor(0.1F);
		structurePlacementData.clearProcessors().method_16184(blockRotStructureProcessor2);
		structure2.method_15172(iWorld, blockPos3, structurePlacementData, 4);
		return true;
	}
}
