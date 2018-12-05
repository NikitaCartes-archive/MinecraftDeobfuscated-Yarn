package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.class_3145;
import net.minecraft.class_3449;
import net.minecraft.class_3485;
import net.minecraft.sortme.structures.IglooGenerator;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.config.feature.DefaultFeatureConfig;

public class IglooFeature extends class_3145<DefaultFeatureConfig> {
	public IglooFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	public String getName() {
		return "Igloo";
	}

	@Override
	public int method_14021() {
		return 3;
	}

	@Override
	public StructureFeature.class_3774 method_14016() {
		return IglooFeature.class_3072::new;
	}

	@Override
	protected int method_13774() {
		return 14357618;
	}

	public static class class_3072 extends class_3449 {
		public class_3072(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
			super(structureFeature, i, j, biome, mutableIntBoundingBox, k, l);
		}

		@Override
		public void method_16655(ChunkGenerator<?> chunkGenerator, class_3485 arg, int i, int j, Biome biome) {
			DefaultFeatureConfig defaultFeatureConfig = chunkGenerator.getStructureConfig(biome, Feature.IGLOO);
			int k = i * 16;
			int l = j * 16;
			BlockPos blockPos = new BlockPos(k, 90, l);
			Rotation rotation = Rotation.values()[this.field_16715.nextInt(Rotation.values().length)];
			IglooGenerator.method_14705(arg, blockPos, rotation, this.children, this.field_16715, defaultFeatureConfig);
			this.method_14969();
		}
	}
}
