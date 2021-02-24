package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.MarginedStructureStart;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.HeightLimitView;
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
		return (feature, chunkPos, blockBox, i, l) -> new JigsawFeature.Start(this, chunkPos, blockBox, i, l);
	}

	public static class Start extends MarginedStructureStart<StructurePoolFeatureConfig> {
		private final JigsawFeature jigsawFeature;

		public Start(JigsawFeature feature, ChunkPos chunkPos, BlockBox blockBox, int i, long l) {
			super(feature, chunkPos, blockBox, i, l);
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
				this.jigsawFeature.surface,
				heightLimitView
			);
			this.setBoundingBoxFromChildren();
		}
	}
}
