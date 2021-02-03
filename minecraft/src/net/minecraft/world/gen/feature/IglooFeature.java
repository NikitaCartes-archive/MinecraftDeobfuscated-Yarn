package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.IglooGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class IglooFeature extends StructureFeature<DefaultFeatureConfig> {
	public IglooFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public StructureFeature.StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
		return IglooFeature.Start::new;
	}

	public static class Start extends StructureStart<DefaultFeatureConfig> {
		public Start(StructureFeature<DefaultFeatureConfig> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
			super(structureFeature, i, j, blockBox, k, l);
		}

		public void init(
			DynamicRegistryManager dynamicRegistryManager,
			ChunkGenerator chunkGenerator,
			StructureManager structureManager,
			int i,
			int j,
			Biome biome,
			DefaultFeatureConfig defaultFeatureConfig,
			HeightLimitView heightLimitView
		) {
			int k = ChunkSectionPos.getBlockCoord(i);
			int l = ChunkSectionPos.getBlockCoord(j);
			BlockPos blockPos = new BlockPos(k, 90, l);
			BlockRotation blockRotation = BlockRotation.random(this.random);
			IglooGenerator.addPieces(structureManager, blockPos, blockRotation, this.children, this.random);
			this.setBoundingBoxFromChildren();
		}
	}
}
