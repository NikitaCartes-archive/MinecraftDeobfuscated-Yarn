package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.MarginedStructureStart;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class JigsawFeature extends StructureFeature<StructurePoolFeatureConfig> {
	final int structureStartY;
	final boolean modifyBoundingBox;
	final boolean surface;

	public JigsawFeature(Codec<StructurePoolFeatureConfig> codec, int structureStartY, boolean modifyBoundingBox, boolean surface) {
		super(codec);
		this.structureStartY = structureStartY;
		this.modifyBoundingBox = modifyBoundingBox;
		this.surface = surface;
	}

	@Override
	public StructureFeature.StructureStartFactory<StructurePoolFeatureConfig> getStructureStartFactory() {
		return (feature, pos, references, seed) -> new JigsawFeature.Start(this, pos, references, seed);
	}

	public static class Start extends MarginedStructureStart<StructurePoolFeatureConfig> {
		private final JigsawFeature jigsawFeature;

		public Start(JigsawFeature feature, ChunkPos pos, int references, long seed) {
			super(feature, pos, references, seed);
			this.jigsawFeature = feature;
		}

		public void init(
			DynamicRegistryManager dynamicRegistryManager,
			ChunkGenerator chunkGenerator,
			StructureManager structureManager,
			ChunkPos chunkPos,
			Biome biome,
			StructurePoolFeatureConfig structurePoolFeatureConfig,
			HeightLimitView heightLimitView
		) {
			BlockPos blockPos = new BlockPos(chunkPos.getStartX(), this.jigsawFeature.structureStartY, chunkPos.getStartZ());
			StructurePools.initDefaultPools();
			StructurePoolBasedGenerator.generate(
				dynamicRegistryManager,
				structurePoolFeatureConfig,
				PoolStructurePiece::new,
				chunkGenerator,
				structureManager,
				blockPos,
				this,
				this.random,
				this.jigsawFeature.modifyBoundingBox,
				this.jigsawFeature.surface,
				heightLimitView
			);
		}
	}
}
