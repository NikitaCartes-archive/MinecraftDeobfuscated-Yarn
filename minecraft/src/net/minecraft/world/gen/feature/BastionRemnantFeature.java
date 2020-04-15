package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.structure.BastionRemnantGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.VillageStructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class BastionRemnantFeature extends StructureFeature<BastionRemnantFeatureConfig> {
	public BastionRemnantFeature(Function<Dynamic<?>, ? extends BastionRemnantFeatureConfig> function) {
		super(function);
	}

	@Override
	protected int getSpacing(DimensionType dimensionType, ChunkGeneratorConfig chunkGeneratorConfig) {
		return chunkGeneratorConfig.getNetherStructureSpacing();
	}

	@Override
	protected int getSeparation(DimensionType dimensionType, ChunkGeneratorConfig chunkGenerationConfig) {
		return chunkGenerationConfig.getNetherStructureSeparation();
	}

	@Override
	protected int getSeedModifier(ChunkGeneratorConfig chunkGeneratorConfig) {
		return chunkGeneratorConfig.getNetherStructureSeedModifier();
	}

	@Override
	protected boolean shouldStartAt(
		BiomeAccess biomeAccess, ChunkGenerator<?> chunkGenerator, ChunkRandom chunkRandom, int chunkX, int chunkZ, Biome biome, ChunkPos chunkPos
	) {
		return chunkRandom.nextInt(6) >= 2;
	}

	@Override
	public StructureFeature.StructureStartFactory getStructureStartFactory() {
		return BastionRemnantFeature.Start::new;
	}

	@Override
	public String getName() {
		return "Bastion_Remnant";
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
		public void init(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int x, int z, Biome biome) {
			BastionRemnantFeatureConfig bastionRemnantFeatureConfig = chunkGenerator.getStructureConfig(biome, Feature.BASTION_REMNANT);
			BlockPos blockPos = new BlockPos(x * 16, 33, z * 16);
			BastionRemnantGenerator.addPieces(chunkGenerator, structureManager, blockPos, this.children, this.random, bastionRemnantFeatureConfig);
			this.setBoundingBoxFromChildren();
		}
	}
}
