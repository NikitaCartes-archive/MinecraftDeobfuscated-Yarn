package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.MarginedStructureStart;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class JigsawFeature extends StructureFeature<StructurePoolFeatureConfig> {
	private final int structureStartY;
	private final boolean field_25836;
	private final boolean surface;

	public JigsawFeature(Codec<StructurePoolFeatureConfig> codec, int structureStartY, boolean bl, boolean surface) {
		super(codec);
		this.structureStartY = structureStartY;
		this.field_25836 = bl;
		this.surface = surface;
	}

	@Override
	public StructureFeature.StructureStartFactory<StructurePoolFeatureConfig> getStructureStartFactory() {
		return (feature, chunkX, chunkZ, boundingBox, references, seed) -> new JigsawFeature.Start(this, chunkX, chunkZ, boundingBox, references, seed);
	}

	public static class Start extends MarginedStructureStart<StructurePoolFeatureConfig> {
		private final JigsawFeature jigsawFeature;

		public Start(JigsawFeature feature, int chunkX, int chunkZ, BlockBox boundingBox, int references, long seed) {
			super(feature, chunkX, chunkZ, boundingBox, references, seed);
			this.jigsawFeature = feature;
		}

		public void init(
			DynamicRegistryManager dynamicRegistryManager,
			ChunkGenerator chunkGenerator,
			StructureManager structureManager,
			int i,
			int j,
			Biome biome,
			StructurePoolFeatureConfig structurePoolFeatureConfig
		) {
			BlockPos blockPos = new BlockPos(i * 16, this.jigsawFeature.structureStartY, j * 16);
			StructurePools.initDefaultPools();
			StructurePoolBasedGenerator.method_30419(
				dynamicRegistryManager,
				structurePoolFeatureConfig,
				PoolStructurePiece::new,
				chunkGenerator,
				structureManager,
				blockPos,
				this.children,
				this.random,
				this.jigsawFeature.field_25836,
				this.jigsawFeature.surface
			);
			this.setBoundingBoxFromChildren();
		}
	}
}
