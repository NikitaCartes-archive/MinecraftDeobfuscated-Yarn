package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.class_3145;
import net.minecraft.class_3449;
import net.minecraft.class_3485;
import net.minecraft.sortme.structures.JungleTempleGenerator;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.config.feature.DefaultFeatureConfig;

public class JungleTempleFeature extends class_3145<DefaultFeatureConfig> {
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
	public StructureFeature.class_3774 method_14016() {
		return JungleTempleFeature.class_3077::new;
	}

	@Override
	protected int method_13774() {
		return 14357619;
	}

	public static class class_3077 extends class_3449 {
		public class_3077(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
			super(structureFeature, i, j, biome, mutableIntBoundingBox, k, l);
		}

		@Override
		public void method_16655(ChunkGenerator<?> chunkGenerator, class_3485 arg, int i, int j, Biome biome) {
			JungleTempleGenerator jungleTempleGenerator = new JungleTempleGenerator(this.field_16715, i * 16, j * 16);
			this.children.add(jungleTempleGenerator);
			this.method_14969();
		}
	}
}
