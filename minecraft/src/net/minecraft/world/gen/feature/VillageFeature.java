package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.VillageGenerator;
import net.minecraft.structure.VillageStructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class VillageFeature extends StructureFeature<StructurePoolFeatureConfig> {
	public VillageFeature(Function<Dynamic<?>, ? extends StructurePoolFeatureConfig> function) {
		super(function);
	}

	@Override
	protected int getSpacing(ChunkGeneratorConfig chunkGeneratorConfig) {
		return chunkGeneratorConfig.getVillageSpacing();
	}

	@Override
	protected int getSeparation(ChunkGeneratorConfig chunkGeneratorConfig) {
		return chunkGeneratorConfig.getVillageSeparation();
	}

	@Override
	protected int getSeedModifier(ChunkGeneratorConfig chunkGeneratorConfig) {
		return 10387312;
	}

	@Override
	public StructureFeature.StructureStartFactory getStructureStartFactory() {
		return VillageFeature.Start::new;
	}

	@Override
	public String getName() {
		return "Village";
	}

	@Override
	public int getRadius() {
		return 8;
	}

	public static class Start extends VillageStructureStart {
		public Start(StructureFeature<?> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
			super(structureFeature, i, j, blockBox, k, l);
		}

		@Override
		public void init(ChunkGenerator chunkGenerator, StructureManager structureManager, int x, int z, Biome biome) {
			StructurePoolFeatureConfig structurePoolFeatureConfig = chunkGenerator.getStructureConfig(biome, Feature.VILLAGE);
			BlockPos blockPos = new BlockPos(x * 16, 0, z * 16);
			VillageGenerator.addPieces(chunkGenerator, structureManager, blockPos, this.children, this.random, structurePoolFeatureConfig);
			this.setBoundingBoxFromChildren();
		}
	}
}
