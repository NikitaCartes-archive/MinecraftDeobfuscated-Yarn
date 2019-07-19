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
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class MineshaftFeature extends StructureFeature<MineshaftFeatureConfig> {
	public MineshaftFeature(Function<Dynamic<?>, ? extends MineshaftFeatureConfig> configFactory) {
		super(configFactory);
	}

	@Override
	public boolean shouldStartAt(ChunkGenerator<?> chunkGenerator, Random random, int chunkX, int chunkZ) {
		((ChunkRandom)random).setStructureSeed(chunkGenerator.getSeed(), chunkX, chunkZ);
		Biome biome = chunkGenerator.getBiomeSource().getBiome(new BlockPos((chunkX << 4) + 9, 0, (chunkZ << 4) + 9));
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
		public Start(StructureFeature<?> structureFeature, int chunkX, int chunkZ, Biome biome, BlockBox blockBox, int i, long l) {
			super(structureFeature, chunkX, chunkZ, biome, blockBox, i, l);
		}

		@Override
		public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int x, int z, Biome biome) {
			MineshaftFeatureConfig mineshaftFeatureConfig = chunkGenerator.getStructureConfig(biome, Feature.MINESHAFT);
			MineshaftGenerator.MineshaftRoom mineshaftRoom = new MineshaftGenerator.MineshaftRoom(
				0, this.random, (x << 4) + 2, (z << 4) + 2, mineshaftFeatureConfig.type
			);
			this.children.add(mineshaftRoom);
			mineshaftRoom.method_14918(mineshaftRoom, this.children, this.random);
			this.setBoundingBoxFromChildren();
			if (mineshaftFeatureConfig.type == MineshaftFeature.Type.MESA) {
				int i = -5;
				int j = chunkGenerator.getSeaLevel() - this.boundingBox.maxY + this.boundingBox.getBlockCountY() / 2 - -5;
				this.boundingBox.offset(0, j, 0);

				for (StructurePiece structurePiece : this.children) {
					structurePiece.translate(0, j, 0);
				}
			} else {
				this.method_14978(chunkGenerator.getSeaLevel(), this.random, 10);
			}
		}
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

		public static MineshaftFeature.Type byName(String nam) {
			return (MineshaftFeature.Type)nameMap.get(nam);
		}

		public static MineshaftFeature.Type byIndex(int index) {
			return index >= 0 && index < values().length ? values()[index] : NORMAL;
		}
	}
}
