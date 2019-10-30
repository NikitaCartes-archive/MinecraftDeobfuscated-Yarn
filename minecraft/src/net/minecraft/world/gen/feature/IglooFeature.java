package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.structure.IglooGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class IglooFeature extends AbstractTempleFeature<DefaultFeatureConfig> {
	public IglooFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
		super(configFactory);
	}

	@Override
	public String getName() {
		return "Igloo";
	}

	@Override
	public int getRadius() {
		return 3;
	}

	@Override
	public StructureFeature.StructureStartFactory getStructureStartFactory() {
		return IglooFeature.Start::new;
	}

	@Override
	protected int getSeedModifier() {
		return 14357618;
	}

	public static class Start extends StructureStart {
		public Start(StructureFeature<?> structureFeature, int chunkX, int chunkZ, BlockBox blockBox, int i, long l) {
			super(structureFeature, chunkX, chunkZ, blockBox, i, l);
		}

		@Override
		public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int x, int z, Biome biome) {
			DefaultFeatureConfig defaultFeatureConfig = chunkGenerator.getStructureConfig(biome, Feature.IGLOO);
			int i = x * 16;
			int j = z * 16;
			BlockPos blockPos = new BlockPos(i, 90, j);
			BlockRotation blockRotation = BlockRotation.values()[this.random.nextInt(BlockRotation.values().length)];
			IglooGenerator.addPieces(structureManager, blockPos, blockRotation, this.children, this.random, defaultFeatureConfig);
			this.setBoundingBoxFromChildren();
		}
	}
}
