package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.class_3443;
import net.minecraft.sortme.structures.MineshaftGenerator;
import net.minecraft.sortme.structures.StructureManager;
import net.minecraft.sortme.structures.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class MineshaftFeature extends StructureFeature<MineshaftFeatureConfig> {
	public MineshaftFeature(Function<Dynamic<?>, ? extends MineshaftFeatureConfig> function) {
		super(function);
	}

	@Override
	public boolean method_14026(ChunkGenerator<?> chunkGenerator, Random random, int i, int j) {
		((ChunkRandom)random).setStructureSeed(chunkGenerator.getSeed(), i, j);
		Biome biome = chunkGenerator.getBiomeSource().getBiome(new BlockPos((i << 4) + 9, 0, (j << 4) + 9));
		if (chunkGenerator.hasStructure(biome, Feature.MINESHAFT)) {
			MineshaftFeatureConfig mineshaftFeatureConfig = chunkGenerator.method_12105(biome, Feature.MINESHAFT);
			double d = mineshaftFeatureConfig.probability;
			return random.nextDouble() < d;
		} else {
			return false;
		}
	}

	@Override
	public StructureFeature.class_3774 method_14016() {
		return MineshaftFeature.class_3099::new;
	}

	@Override
	public String getName() {
		return "Mineshaft";
	}

	@Override
	public int method_14021() {
		return 8;
	}

	public static enum Type {
		NORMAL("normal"),
		MESA("mesa");

		private static final Map<String, MineshaftFeature.Type> nameMap = (Map<String, MineshaftFeature.Type>)Arrays.stream(values())
			.collect(Collectors.toMap(MineshaftFeature.Type::getName, type -> type));
		private final String name;

		private Type(String string2) {
			this.name = string2;
		}

		public String getName() {
			return this.name;
		}

		public static MineshaftFeature.Type byName(String string) {
			return (MineshaftFeature.Type)nameMap.get(string);
		}

		public static MineshaftFeature.Type byIndex(int i) {
			return i >= 0 && i < values().length ? values()[i] : NORMAL;
		}
	}

	public static class class_3099 extends StructureStart {
		public class_3099(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
			super(structureFeature, i, j, biome, mutableIntBoundingBox, k, l);
		}

		@Override
		public void method_16655(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome) {
			MineshaftFeatureConfig mineshaftFeatureConfig = chunkGenerator.method_12105(biome, Feature.MINESHAFT);
			MineshaftGenerator.MineshaftRoom mineshaftRoom = new MineshaftGenerator.MineshaftRoom(
				0, this.field_16715, (i << 4) + 2, (j << 4) + 2, mineshaftFeatureConfig.type
			);
			this.children.add(mineshaftRoom);
			mineshaftRoom.method_14918(mineshaftRoom, this.children, this.field_16715);
			this.method_14969();
			if (mineshaftFeatureConfig.type == MineshaftFeature.Type.MESA) {
				int k = -5;
				int l = chunkGenerator.method_16398() - this.boundingBox.maxY + this.boundingBox.getBlockCountY() / 2 - -5;
				this.boundingBox.translate(0, l, 0);

				for (class_3443 lv : this.children) {
					lv.translate(0, l, 0);
				}
			} else {
				this.method_14978(chunkGenerator.method_16398(), this.field_16715, 10);
			}
		}
	}
}
