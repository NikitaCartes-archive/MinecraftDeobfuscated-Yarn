package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.structure.DesertTempleGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class DesertPyramidFeature extends AbstractTempleFeature<DefaultFeatureConfig> {
	public DesertPyramidFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	public String getName() {
		return "Desert_Pyramid";
	}

	@Override
	public int getRadius() {
		return 3;
	}

	@Override
	public StructureFeature.StructureStartFactory getStructureStartFactory() {
		return DesertPyramidFeature.Start::new;
	}

	@Override
	protected int getSeedModifier() {
		return 14357617;
	}

	public static class Start extends StructureStart {
		public Start(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
			super(structureFeature, i, j, biome, mutableIntBoundingBox, k, l);
		}

		@Override
		public void method_16655(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome) {
			DesertTempleGenerator desertTempleGenerator = new DesertTempleGenerator(this.random, i * 16, j * 16);
			this.children.add(desertTempleGenerator);
			this.setBoundingBoxFromChildren();
		}
	}
}
