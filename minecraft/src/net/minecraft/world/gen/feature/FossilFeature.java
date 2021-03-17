package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.fabricmc.yarn.constants.SetBlockStateFlags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
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
			chunkPos.getStartX(), structureWorldAccess.getBottomY(), chunkPos.getStartZ(), chunkPos.getEndX(), structureWorldAccess.getTopY(), chunkPos.getEndZ()
		);
		StructurePlacementData structurePlacementData = new StructurePlacementData().setRotation(blockRotation).setBoundingBox(blockBox).setRandom(random);
		Vec3i vec3i = structure.getRotatedSize(blockRotation);
		int j = random.nextInt(16 - vec3i.getX());
		int k = random.nextInt(16 - vec3i.getZ());
		int l = structureWorldAccess.getTopY();

		for (int m = 0; m < vec3i.getX(); m++) {
			for (int n = 0; n < vec3i.getZ(); n++) {
				l = Math.min(l, structureWorldAccess.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, blockPos.getX() + m + j, blockPos.getZ() + n + k));
			}
		}

		int m = MathHelper.clamp(blockPos.getY(), structureWorldAccess.getBottomY(), l - 15);
		BlockPos blockPos2 = structure.offsetByTransformedSize(blockPos.add(j, 0, k).withY(m), BlockMirror.NONE, blockRotation);
		if (getEmptyCorners(structureWorldAccess, structure.calculateBoundingBox(structurePlacementData, blockPos2)) > fossilFeatureConfig.maxEmptyCorners) {
			return false;
		} else {
			structurePlacementData.clearProcessors();
			((StructureProcessorList)fossilFeatureConfig.fossilProcessors.get())
				.getList()
				.forEach(structureProcessor -> structurePlacementData.addProcessor(structureProcessor));
			structure.place(structureWorldAccess, blockPos2, blockPos2, structurePlacementData, random, SetBlockStateFlags.NO_REDRAW);
			structurePlacementData.clearProcessors();
			((StructureProcessorList)fossilFeatureConfig.overlayProcessors.get())
				.getList()
				.forEach(structureProcessor -> structurePlacementData.addProcessor(structureProcessor));
			structure2.place(structureWorldAccess, blockPos2, blockPos2, structurePlacementData, random, SetBlockStateFlags.NO_REDRAW);
			return true;
		}
	}

	private static int getEmptyCorners(StructureWorldAccess world, BlockBox box) {
		MutableInt mutableInt = new MutableInt(0);
		box.forEachVertex(blockPos -> {
			BlockState blockState = world.getBlockState(blockPos);
			if (blockState.isAir() || blockState.isOf(Blocks.LAVA) || blockState.isOf(Blocks.WATER)) {
				mutableInt.add(1);
			}
		});
		return mutableInt.getValue();
	}
}
