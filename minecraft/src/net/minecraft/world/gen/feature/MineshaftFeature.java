package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.structure.MineshaftGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
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
	public boolean shouldStartAt(ChunkGenerator<?> chunkGenerator, Random random, int i, int j) {
		((ChunkRandom)random).setStructureSeed(chunkGenerator.getSeed(), i, j);
		Biome biome = chunkGenerator.getBiomeSource().getBiome(new BlockPos((i << 4) + 9, 0, (j << 4) + 9));
		if (chunkGenerator.hasStructure(biome, Feature.MINESHAFT)) {
			MineshaftFeatureConfig mineshaftFeatureConfig = chunkGenerator.getStructureConfig(biome, Feature.MINESHAFT);
			double d = mineshaftFeatureConfig.probability;
			return random.nextDouble() < d;
		} else {
			return false;
		}
	}

	@Override
	public StructureFeature.StructureStartFactory getStructureStartFactory() {
		return MineshaftFeature.Start::new;
	}

	@Override
	public String getName() {
		return "Mineshaft";
	}

	@Override
	public int getRadius() {
		return 8;
	}

	public static class Start extends StructureStart {
		public Start(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
			super(structureFeature, i, j, biome, mutableIntBoundingBox, k, l);
		}

		@Override
		public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome) {
			MineshaftFeatureConfig mineshaftFeatureConfig = chunkGenerator.getStructureConfig(biome, Feature.MINESHAFT);
			MineshaftGenerator.MineshaftRoom mineshaftRoom = new MineshaftGenerator.MineshaftRoom(
				0, this.random, (i << 4) + 2, (j << 4) + 2, mineshaftFeatureConfig.type
			);
			this.children.add(mineshaftRoom);
			mineshaftRoom.method_14918(mineshaftRoom, this.children, this.random);
			this.setBoundingBoxFromChildren();
			if (mineshaftFeatureConfig.type == MineshaftFeature.Type.field_13691) {
				int k = -5;
				int l = chunkGenerator.getSeaLevel() - this.boundingBox.maxY + this.boundingBox.getBlockCountY() / 2 - -5;
				this.boundingBox.translate(0, l, 0);

				for (StructurePiece structurePiece : this.children) {
					structurePiece.translate(0, l, 0);
				}
			} else {
				this.method_14978(chunkGenerator.getSeaLevel(), this.random, 10);
			}
		}
	}

	public static enum Type {
		field_13692("normal"),
		field_13691("mesa");

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
			return i >= 0 && i < values().length ? values()[i] : field_13692;
		}
	}
}
