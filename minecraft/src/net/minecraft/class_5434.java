package net.minecraft;

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
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

public class class_5434 extends StructureFeature<StructurePoolFeatureConfig> {
	private final int field_25835;
	private final boolean field_25836;
	private final boolean field_25837;

	public class_5434(Codec<StructurePoolFeatureConfig> codec, int i, boolean bl, boolean bl2) {
		super(codec);
		this.field_25835 = i;
		this.field_25836 = bl;
		this.field_25837 = bl2;
	}

	@Override
	public StructureFeature.StructureStartFactory<StructurePoolFeatureConfig> getStructureStartFactory() {
		return (structureFeature, i, j, blockBox, k, l) -> new class_5434.class_5435(this, i, j, blockBox, k, l);
	}

	public static class class_5435 extends MarginedStructureStart<StructurePoolFeatureConfig> {
		private final class_5434 field_25838;

		public class_5435(class_5434 arg, int i, int j, BlockBox blockBox, int k, long l) {
			super(arg, i, j, blockBox, k, l);
			this.field_25838 = arg;
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
			BlockPos blockPos = new BlockPos(i * 16, this.field_25838.field_25835, j * 16);
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
				this.field_25838.field_25836,
				this.field_25838.field_25837
			);
			this.setBoundingBoxFromChildren();
		}
	}
}
