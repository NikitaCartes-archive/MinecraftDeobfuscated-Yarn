package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

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

	public FossilFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
		Random random = context.getRandom();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		BlockPos blockPos = context.getPos();
		BlockRotation blockRotation = BlockRotation.random(random);
		int i = random.nextInt(FOSSILS.length);
		StructureManager structureManager = structureWorldAccess.toServerWorld().getServer().getStructureManager();
		Structure structure = structureManager.getStructureOrBlank(FOSSILS[i]);
		Structure structure2 = structureManager.getStructureOrBlank(COAL_FOSSILS[i]);
		ChunkPos chunkPos = new ChunkPos(blockPos);
		BlockBox blockBox = new BlockBox(
			chunkPos.getStartX(), structureWorldAccess.getBottomY(), chunkPos.getStartZ(), chunkPos.getEndX(), structureWorldAccess.getTopY(), chunkPos.getEndZ()
		);
		StructurePlacementData structurePlacementData = new StructurePlacementData()
			.setRotation(blockRotation)
			.setBoundingBox(blockBox)
			.setRandom(random)
			.addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
		BlockPos blockPos2 = structure.getRotatedSize(blockRotation);
		int j = random.nextInt(16 - blockPos2.getX());
		int k = random.nextInt(16 - blockPos2.getZ());
		int l = structureWorldAccess.getTopY();

		for (int m = 0; m < blockPos2.getX(); m++) {
			for (int n = 0; n < blockPos2.getZ(); n++) {
				l = Math.min(l, structureWorldAccess.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, blockPos.getX() + m + j, blockPos.getZ() + n + k));
			}
		}

		int m = Math.max(l - 15 - random.nextInt(10), structureWorldAccess.getBottomY() + 10);
		BlockPos blockPos3 = structure.offsetByTransformedSize(blockPos.add(j, m, k), BlockMirror.NONE, blockRotation);
		BlockRotStructureProcessor blockRotStructureProcessor = new BlockRotStructureProcessor(0.9F);
		structurePlacementData.clearProcessors().addProcessor(blockRotStructureProcessor);
		structure.place(structureWorldAccess, blockPos3, blockPos3, structurePlacementData, random, 4);
		structurePlacementData.removeProcessor(blockRotStructureProcessor);
		BlockRotStructureProcessor blockRotStructureProcessor2 = new BlockRotStructureProcessor(0.1F);
		structurePlacementData.clearProcessors().addProcessor(blockRotStructureProcessor2);
		structure2.place(structureWorldAccess, blockPos3, blockPos3, structurePlacementData, random, 4);
		return true;
	}
}
