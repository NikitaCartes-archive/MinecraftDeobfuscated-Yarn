package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.function.Predicate;
import net.minecraft.structure.JungleTempleGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class JungleTempleFeature extends StructureFeature<DefaultFeatureConfig> {
	public JungleTempleFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public StructureFeature.StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
		return JungleTempleFeature.Start::new;
	}

	public static class Start extends StructureStart<DefaultFeatureConfig> {
		public Start(StructureFeature<DefaultFeatureConfig> structureFeature, ChunkPos chunkPos, int i, long l) {
			super(structureFeature, chunkPos, i, l);
		}

		public void init(
			DynamicRegistryManager dynamicRegistryManager,
			ChunkGenerator chunkGenerator,
			StructureManager structureManager,
			ChunkPos chunkPos,
			DefaultFeatureConfig defaultFeatureConfig,
			HeightLimitView heightLimitView,
			Predicate<Biome> predicate
		) {
			if (StructureFeature.checkBiome(chunkGenerator, heightLimitView, predicate, Heightmap.Type.WORLD_SURFACE_WG, chunkPos.getCenterX(), chunkPos.getCenterZ())) {
				if (StructureFeature.getLowestCornerInGroundHeight(chunkGenerator, 12, 15, chunkPos, heightLimitView) >= chunkGenerator.getSeaLevel()) {
					JungleTempleGenerator jungleTempleGenerator = new JungleTempleGenerator(this.random, chunkPos.getStartX(), chunkPos.getStartZ());
					this.addPiece(jungleTempleGenerator);
				}
			}
		}
	}
}
