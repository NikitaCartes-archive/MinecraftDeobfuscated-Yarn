package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.class_6622;
import net.minecraft.class_6626;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.MineshaftGenerator;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.random.ChunkRandom;

public class MineshaftFeature extends StructureFeature<MineshaftFeatureConfig> {
	public MineshaftFeature(Codec<MineshaftFeatureConfig> codec) {
		super(codec, MineshaftFeature::method_38678);
	}

	protected boolean shouldStartAt(
		ChunkGenerator chunkGenerator,
		BiomeSource biomeSource,
		long l,
		ChunkRandom chunkRandom,
		ChunkPos chunkPos,
		ChunkPos chunkPos2,
		MineshaftFeatureConfig mineshaftFeatureConfig,
		HeightLimitView heightLimitView
	) {
		chunkRandom.setCarverSeed(l, chunkPos.x, chunkPos.z);
		double d = (double)mineshaftFeatureConfig.probability;
		return chunkRandom.nextDouble() < d;
	}

	private static void method_38678(class_6626 arg, MineshaftFeatureConfig mineshaftFeatureConfig, class_6622.class_6623 arg2) {
		if (arg2.validBiome()
			.test(
				arg2.chunkGenerator()
					.getBiomeForNoiseGen(BiomeCoords.fromBlock(arg2.chunkPos().getCenterX()), BiomeCoords.fromBlock(50), BiomeCoords.fromBlock(arg2.chunkPos().getCenterZ()))
			)) {
			MineshaftGenerator.MineshaftRoom mineshaftRoom = new MineshaftGenerator.MineshaftRoom(
				0, arg2.random(), arg2.chunkPos().getOffsetX(2), arg2.chunkPos().getOffsetZ(2), mineshaftFeatureConfig.type
			);
			arg.addPiece(mineshaftRoom);
			mineshaftRoom.fillOpenings(mineshaftRoom, arg, arg2.random());
			int i = arg2.chunkGenerator().getSeaLevel();
			if (mineshaftFeatureConfig.type == MineshaftFeature.Type.MESA) {
				BlockPos blockPos = arg.method_38721().getCenter();
				int j = arg2.chunkGenerator().getHeight(blockPos.getX(), blockPos.getZ(), Heightmap.Type.WORLD_SURFACE_WG, arg2.heightAccessor());
				int k = j <= i ? i : MathHelper.nextBetween(arg2.random(), i, j);
				int l = k - blockPos.getY();
				arg.method_38715(l);
			} else {
				arg.method_38716(i, arg2.chunkGenerator().getMinimumY(), arg2.random(), 10);
			}
		}
	}

	public static enum Type implements StringIdentifiable {
		NORMAL("normal", Blocks.OAK_LOG, Blocks.OAK_PLANKS, Blocks.OAK_FENCE),
		MESA("mesa", Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_FENCE);

		public static final Codec<MineshaftFeature.Type> CODEC = StringIdentifiable.createCodec(MineshaftFeature.Type::values, MineshaftFeature.Type::byName);
		private static final Map<String, MineshaftFeature.Type> BY_NAME = (Map<String, MineshaftFeature.Type>)Arrays.stream(values())
			.collect(Collectors.toMap(MineshaftFeature.Type::getName, type -> type));
		private final String name;
		private final BlockState log;
		private final BlockState planks;
		private final BlockState fence;

		private Type(String name, Block log, Block planks, Block fence) {
			this.name = name;
			this.log = log.getDefaultState();
			this.planks = planks.getDefaultState();
			this.fence = fence.getDefaultState();
		}

		public String getName() {
			return this.name;
		}

		private static MineshaftFeature.Type byName(String name) {
			return (MineshaftFeature.Type)BY_NAME.get(name);
		}

		public static MineshaftFeature.Type byIndex(int index) {
			return index >= 0 && index < values().length ? values()[index] : NORMAL;
		}

		public BlockState getLog() {
			return this.log;
		}

		public BlockState getPlanks() {
			return this.planks;
		}

		public BlockState getFence() {
			return this.fence;
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}
