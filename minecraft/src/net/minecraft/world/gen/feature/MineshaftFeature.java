package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.MineshaftGenerator;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

public class MineshaftFeature extends StructureFeature<MineshaftFeatureConfig> {
	public MineshaftFeature(Codec<MineshaftFeatureConfig> configCodec) {
		super(configCodec, StructureGeneratorFactory.simple(MineshaftFeature::method_28638, MineshaftFeature::addPieces));
	}

	private static boolean method_28638(StructureGeneratorFactory.Context<MineshaftFeatureConfig> context) {
		ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
		chunkRandom.setCarverSeed(context.seed(), context.chunkPos().x, context.chunkPos().z);
		double d = (double)context.config().probability;
		return chunkRandom.nextDouble() >= d
			? false
			: context.validBiome()
				.test(
					context.chunkGenerator()
						.getBiomeForNoiseGen(
							BiomeCoords.fromBlock(context.chunkPos().getCenterX()), BiomeCoords.fromBlock(50), BiomeCoords.fromBlock(context.chunkPos().getCenterZ())
						)
				);
	}

	private static void addPieces(StructurePiecesCollector collector, StructurePiecesGenerator.Context<MineshaftFeatureConfig> context) {
		MineshaftGenerator.MineshaftRoom mineshaftRoom = new MineshaftGenerator.MineshaftRoom(
			0, context.random(), context.chunkPos().getOffsetX(2), context.chunkPos().getOffsetZ(2), context.config().type
		);
		collector.addPiece(mineshaftRoom);
		mineshaftRoom.fillOpenings(mineshaftRoom, collector, context.random());
		int i = context.chunkGenerator().getSeaLevel();
		if (context.config().type == MineshaftFeature.Type.MESA) {
			BlockPos blockPos = collector.getBoundingBox().getCenter();
			int j = context.chunkGenerator().getHeight(blockPos.getX(), blockPos.getZ(), Heightmap.Type.WORLD_SURFACE_WG, context.world());
			int k = j <= i ? i : MathHelper.nextBetween(context.random(), i, j);
			int l = k - blockPos.getY();
			collector.shift(l);
		} else {
			collector.shiftInto(i, context.chunkGenerator().getMinimumY(), context.random(), 10);
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
