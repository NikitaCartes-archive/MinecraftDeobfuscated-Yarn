package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;
import org.apache.commons.lang3.mutable.MutableInt;

public class FossilFeature extends Feature<FossilFeatureConfig> {
	public FossilFeature(Codec<FossilFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<FossilFeatureConfig> context) {
		Random random = context.getRandom();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		BlockPos blockPos = context.getOrigin();
		BlockRotation blockRotation = BlockRotation.random(random);
		FossilFeatureConfig fossilFeatureConfig = context.getConfig();
		int i = random.nextInt(fossilFeatureConfig.fossilStructures.size());
		StructureManager structureManager = structureWorldAccess.toServerWorld().getServer().getStructureManager();
		Structure structure = structureManager.getStructureOrBlank((Identifier)fossilFeatureConfig.fossilStructures.get(i));
		Structure structure2 = structureManager.getStructureOrBlank((Identifier)fossilFeatureConfig.overlayStructures.get(i));
		ChunkPos chunkPos = new ChunkPos(blockPos);
		BlockBox blockBox = new BlockBox(
			chunkPos.getStartX() - 16,
			structureWorldAccess.getBottomY(),
			chunkPos.getStartZ() - 16,
			chunkPos.getEndX() + 16,
			structureWorldAccess.getTopY(),
			chunkPos.getEndZ() + 16
		);
		StructurePlacementData structurePlacementData = new StructurePlacementData().setRotation(blockRotation).setBoundingBox(blockBox).setRandom(random);
		Vec3i vec3i = structure.getRotatedSize(blockRotation);
		BlockPos blockPos2 = blockPos.add(-vec3i.getX() / 2, 0, -vec3i.getZ() / 2);
		int j = blockPos.getY();

		for (int k = 0; k < vec3i.getX(); k++) {
			for (int l = 0; l < vec3i.getZ(); l++) {
				j = Math.min(j, structureWorldAccess.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, blockPos2.getX() + k, blockPos2.getZ() + l));
			}
		}

		int k = Math.max(j - 15 - random.nextInt(10), structureWorldAccess.getBottomY() + 10);
		BlockPos blockPos3 = structure.offsetByTransformedSize(blockPos2.withY(k), BlockMirror.NONE, blockRotation);
		if (getEmptyCorners(structureWorldAccess, structure.calculateBoundingBox(structurePlacementData, blockPos3)) > fossilFeatureConfig.maxEmptyCorners) {
			return false;
		} else {
			structurePlacementData.clearProcessors();
			fossilFeatureConfig.fossilProcessors.value().getList().forEach(structurePlacementData::addProcessor);
			structure.place(structureWorldAccess, blockPos3, blockPos3, structurePlacementData, random, 4);
			structurePlacementData.clearProcessors();
			fossilFeatureConfig.overlayProcessors.value().getList().forEach(structurePlacementData::addProcessor);
			structure2.place(structureWorldAccess, blockPos3, blockPos3, structurePlacementData, random, 4);
			return true;
		}
	}

	private static int getEmptyCorners(StructureWorldAccess world, BlockBox box) {
		MutableInt mutableInt = new MutableInt(0);
		box.forEachVertex(pos -> {
			BlockState blockState = world.getBlockState(pos);
			if (blockState.isAir() || blockState.isOf(Blocks.LAVA) || blockState.isOf(Blocks.WATER)) {
				mutableInt.add(1);
			}
		});
		return mutableInt.getValue();
	}
}
