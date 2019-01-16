package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.sortme.structures.DesertTempleGenerator;
import net.minecraft.sortme.structures.StructureManager;
import net.minecraft.sortme.structures.StructureStart;
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
	public int method_14021() {
		return 3;
	}

	@Override
	public StructureFeature.class_3774 method_14016() {
		return DesertPyramidFeature.class_3007::new;
	}

	@Override
	protected int method_13774() {
		return 14357617;
	}

	public static class class_3007 extends StructureStart {
		public class_3007(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
			super(structureFeature, i, j, biome, mutableIntBoundingBox, k, l);
		}

		@Override
		public void method_16655(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome) {
			DesertTempleGenerator desertTempleGenerator = new DesertTempleGenerator(this.field_16715, i * 16, j * 16);
			this.children.add(desertTempleGenerator);
			this.method_14969();
		}
	}
}
