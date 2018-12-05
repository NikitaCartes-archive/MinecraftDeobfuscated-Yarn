package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.class_3145;
import net.minecraft.class_3449;
import net.minecraft.class_3485;
import net.minecraft.sortme.structures.OceanTempleGenerator;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.config.feature.OceanRuinFeatureConfig;

public class OceanRuinFeature extends class_3145<OceanRuinFeatureConfig> {
	public OceanRuinFeature(Function<Dynamic<?>, ? extends OceanRuinFeatureConfig> function) {
		super(function);
	}

	@Override
	public String getName() {
		return "Ocean_Ruin";
	}

	@Override
	public int method_14021() {
		return 3;
	}

	@Override
	protected int method_13773(ChunkGenerator<?> chunkGenerator) {
		return chunkGenerator.getSettings().method_12564();
	}

	@Override
	protected int method_13775(ChunkGenerator<?> chunkGenerator) {
		return chunkGenerator.getSettings().method_12555();
	}

	@Override
	public StructureFeature.class_3774 method_14016() {
		return OceanRuinFeature.class_3412::new;
	}

	@Override
	protected int method_13774() {
		return 14357621;
	}

	public static enum BiomeTemperature {
		WARM("warm"),
		COLD("cold");

		private static final Map<String, OceanRuinFeature.BiomeTemperature> nameMap = (Map<String, OceanRuinFeature.BiomeTemperature>)Arrays.stream(values())
			.collect(Collectors.toMap(OceanRuinFeature.BiomeTemperature::getName, biomeTemperature -> biomeTemperature));
		private final String name;

		private BiomeTemperature(String string2) {
			this.name = string2;
		}

		public String getName() {
			return this.name;
		}

		public static OceanRuinFeature.BiomeTemperature byName(String string) {
			return (OceanRuinFeature.BiomeTemperature)nameMap.get(string);
		}
	}

	public static class class_3412 extends class_3449 {
		public class_3412(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
			super(structureFeature, i, j, biome, mutableIntBoundingBox, k, l);
		}

		@Override
		public void method_16655(ChunkGenerator<?> chunkGenerator, class_3485 arg, int i, int j, Biome biome) {
			OceanRuinFeatureConfig oceanRuinFeatureConfig = chunkGenerator.getStructureConfig(biome, Feature.OCEAN_RUIN);
			int k = i * 16;
			int l = j * 16;
			BlockPos blockPos = new BlockPos(k, 90, l);
			Rotation rotation = Rotation.values()[this.field_16715.nextInt(Rotation.values().length)];
			OceanTempleGenerator.method_14827(arg, blockPos, rotation, this.children, this.field_16715, oceanRuinFeatureConfig);
			this.method_14969();
		}
	}
}
