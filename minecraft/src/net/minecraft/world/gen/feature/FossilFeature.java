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
	private static final Identifier SPINE_1 = new Identifier("fossil/spine_1");
	private static final Identifier SPINE_2 = new Identifier("fossil/spine_2");
	private static final Identifier SPINE_3 = new Identifier("fossil/spine_3");
	private static final Identifier SPINE_4 = new Identifier("fossil/spine_4");
	private static final Identifier SPINE_1_COAL = new Identifier("fossil/spine_1_coal");
	private static final Identifier SPINE_2_COAL = new Identifier("fossil/spine_2_coal");
	private static final Identifier SPINE_3_COAL = new Identifier("fossil/spine_3_coal");
	private static final Identifier SPINE_4_COAL = new Identifier("fossil/spine_4_coal");
	private static final Identifier SKULL_1 = new Identifier("fossil/skull_1");
	private static final Identifier SKULL_2 = new Identifier("fossil/skull_2");
	private static final Identifier SKULL_3 = new Identifier("fossil/skull_3");
	private static final Identifier SKULL_4 = new Identifier("fossil/skull_4");
	private static final Identifier SKULL_1_COAL = new Identifier("fossil/skull_1_coal");
	private static final Identifier SKULL_2_COAL = new Identifier("fossil/skull_2_coal");
	private static final Identifier SKULL_3_COAL = new Identifier("fossil/skull_3_coal");
	private static final Identifier SKULL_4_COAL = new Identifier("fossil/skull_4_coal");
	private static final Identifier[] FOSSILS = new Identifier[]{SPINE_1, SPINE_2, SPINE_3, SPINE_4, SKULL_1, SKULL_2, SKULL_3, SKULL_4};
	private static final Identifier[] COAL_FOSSILS = new Identifier[]{
		SPINE_1_COAL, SPINE_2_COAL, SPINE_3_COAL, SPINE_4_COAL, SKULL_1_COAL, SKULL_2_COAL, SKULL_3_COAL, SKULL_4_COAL
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
		int i = random2.nextInt(FOSSILS.length);
		StructureManager structureManager = ((ServerWorld)iWorld.getWorld()).getSaveHandler().getStructureManager();
		Structure structure = structureManager.getStructureOrBlank(FOSSILS[i]);
		Structure structure2 = structureManager.getStructureOrBlank(COAL_FOSSILS[i]);
		ChunkPos chunkPos = new ChunkPos(blockPos);
		MutableIntBoundingBox mutableIntBoundingBox = new MutableIntBoundingBox(
			chunkPos.getStartX(), 0, chunkPos.getStartZ(), chunkPos.getEndX(), 256, chunkPos.getEndZ()
		);
		StructurePlacementData structurePlacementData = new StructurePlacementData()
			.setRotation(rotation)
			.setBoundingBox(mutableIntBoundingBox)
			.setRandom(random2)
			.addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
		BlockPos blockPos2 = structure.method_15166(rotation);
		int j = random2.nextInt(16 - blockPos2.getX());
		int k = random2.nextInt(16 - blockPos2.getZ());
		int l = 256;

		for (int m = 0; m < blockPos2.getX(); m++) {
			for (int n = 0; n < blockPos2.getX(); n++) {
				l = Math.min(l, iWorld.getTop(Heightmap.Type.OCEAN_FLOOR_WG, blockPos.getX() + m + j, blockPos.getZ() + n + k));
			}
		}

		int m = Math.max(l - 15 - random2.nextInt(10), 10);
		BlockPos blockPos3 = structure.method_15167(blockPos.add(j, m, k), Mirror.NONE, rotation);
		BlockRotStructureProcessor blockRotStructureProcessor = new BlockRotStructureProcessor(0.9F);
		structurePlacementData.clearProcessors().addProcessor(blockRotStructureProcessor);
		structure.method_15172(iWorld, blockPos3, structurePlacementData, 4);
		structurePlacementData.removeProcessor(blockRotStructureProcessor);
		BlockRotStructureProcessor blockRotStructureProcessor2 = new BlockRotStructureProcessor(0.1F);
		structurePlacementData.clearProcessors().addProcessor(blockRotStructureProcessor2);
		structure2.method_15172(iWorld, blockPos3, structurePlacementData, 4);
		return true;
	}
}
