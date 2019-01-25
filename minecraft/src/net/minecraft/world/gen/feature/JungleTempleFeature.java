package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.generator.JungleTempleGenerator;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class JungleTempleFeature extends AbstractTempleFeature<DefaultFeatureConfig> {
	public JungleTempleFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	public String getName() {
		return "Jungle_Pyramid";
	}

	@Override
	public int method_14021() {
		return 3;
	}

	@Override
	public StructureFeature.StructureStartFactory getStructureStartFactory() {
		return JungleTempleFeature.class_3077::new;
	}

	@Override
	protected int method_13774() {
		return 14357619;
	}

	public static class class_3077 extends StructureStart {
		public class_3077(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
			super(structureFeature, i, j, biome, mutableIntBoundingBox, k, l);
		}

		@Override
		public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome) {
			JungleTempleGenerator jungleTempleGenerator = new JungleTempleGenerator(this.random, i * 16, j * 16);
			this.children.add(jungleTempleGenerator);
			this.setBoundingBoxFromChildren();
		}
	}
}
