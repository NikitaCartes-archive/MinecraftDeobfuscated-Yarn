package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.structure.IglooGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class IglooFeature extends AbstractTempleFeature<DefaultFeatureConfig> {
	public IglooFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
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
		public Start(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
			super(structureFeature, i, j, biome, mutableIntBoundingBox, k, l);
		}

		@Override
		public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome) {
			DefaultFeatureConfig defaultFeatureConfig = chunkGenerator.getStructureConfig(biome, Feature.IGLOO);
			int k = i * 16;
			int l = j * 16;
			BlockPos blockPos = new BlockPos(k, 90, l);
			Rotation rotation = Rotation.values()[this.random.nextInt(Rotation.values().length)];
			IglooGenerator.method_14705(structureManager, blockPos, rotation, this.children, this.random, defaultFeatureConfig);
			this.setBoundingBoxFromChildren();
		}
	}
}
