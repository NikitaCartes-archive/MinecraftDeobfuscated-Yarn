package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.structure.MineshaftGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class MineshaftFeature extends StructureFeature<MineshaftFeatureConfig> {
	public MineshaftFeature(Function<Dynamic<?>, ? extends MineshaftFeatureConfig> function) {
		super(function);
	}

	@Override
	protected boolean shouldStartAt(
		BiomeAccess biomeAccess, ChunkGenerator<?> chunkGenerator, ChunkRandom chunkRandom, int chunkX, int chunkZ, Biome biome, ChunkPos chunkPos
	) {
		chunkRandom.setCarverSeed(chunkGenerator.getSeed(), chunkX, chunkZ);
		MineshaftFeatureConfig mineshaftFeatureConfig = chunkGenerator.getStructureConfig(biome, this);
		double d = mineshaftFeatureConfig.probability;
		return chunkRandom.nextDouble() < d;
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
		public Start(StructureFeature<?> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
			super(structureFeature, i, j, blockBox, k, l);
		}

		@Override
		public void init(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int x, int z, Biome biome) {
			MineshaftFeatureConfig mineshaftFeatureConfig = chunkGenerator.getStructureConfig(biome, Feature.MINESHAFT);
			MineshaftGenerator.MineshaftRoom mineshaftRoom = new MineshaftGenerator.MineshaftRoom(
				0, this.random, (x << 4) + 2, (z << 4) + 2, mineshaftFeatureConfig.type
			);
			this.children.add(mineshaftRoom);
			mineshaftRoom.placeJigsaw(mineshaftRoom, this.children, this.random);
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
