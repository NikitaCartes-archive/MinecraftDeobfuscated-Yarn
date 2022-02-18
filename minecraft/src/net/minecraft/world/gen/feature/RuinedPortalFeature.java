package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.block.BlockState;
import net.minecraft.structure.RuinedPortalStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

public class RuinedPortalFeature extends StructureFeature<RuinedPortalFeatureConfig> {
	private static final String[] COMMON_PORTAL_STRUCTURE_IDS = new String[]{
		"ruined_portal/portal_1",
		"ruined_portal/portal_2",
		"ruined_portal/portal_3",
		"ruined_portal/portal_4",
		"ruined_portal/portal_5",
		"ruined_portal/portal_6",
		"ruined_portal/portal_7",
		"ruined_portal/portal_8",
		"ruined_portal/portal_9",
		"ruined_portal/portal_10"
	};
	private static final String[] RARE_PORTAL_STRUCTURE_IDS = new String[]{
		"ruined_portal/giant_portal_1", "ruined_portal/giant_portal_2", "ruined_portal/giant_portal_3"
	};
	private static final float field_31512 = 0.05F;
	private static final float field_31513 = 0.5F;
	private static final float field_31514 = 0.5F;
	private static final float field_31508 = 0.8F;
	private static final float field_31509 = 0.8F;
	private static final float field_31510 = 0.5F;
	private static final int field_31511 = 15;

	public RuinedPortalFeature(Codec<RuinedPortalFeatureConfig> configCodec) {
		super(configCodec, RuinedPortalFeature::addPieces);
	}

	private static Optional<StructurePiecesGenerator<RuinedPortalFeatureConfig>> addPieces(StructureGeneratorFactory.Context<RuinedPortalFeatureConfig> context) {
		RuinedPortalStructurePiece.Properties properties = new RuinedPortalStructurePiece.Properties();
		RuinedPortalFeatureConfig ruinedPortalFeatureConfig = context.config();
		ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
		chunkRandom.setCarverSeed(context.seed(), context.chunkPos().x, context.chunkPos().z);
		RuinedPortalStructurePiece.VerticalPlacement verticalPlacement;
		if (ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.DESERT) {
			verticalPlacement = RuinedPortalStructurePiece.VerticalPlacement.PARTLY_BURIED;
			properties.airPocket = false;
			properties.mossiness = 0.0F;
		} else if (ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.JUNGLE) {
			verticalPlacement = RuinedPortalStructurePiece.VerticalPlacement.ON_LAND_SURFACE;
			properties.airPocket = chunkRandom.nextFloat() < 0.5F;
			properties.mossiness = 0.8F;
			properties.overgrown = true;
			properties.vines = true;
		} else if (ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.SWAMP) {
			verticalPlacement = RuinedPortalStructurePiece.VerticalPlacement.ON_OCEAN_FLOOR;
			properties.airPocket = false;
			properties.mossiness = 0.5F;
			properties.vines = true;
		} else if (ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.MOUNTAIN) {
			boolean bl = chunkRandom.nextFloat() < 0.5F;
			verticalPlacement = bl ? RuinedPortalStructurePiece.VerticalPlacement.IN_MOUNTAIN : RuinedPortalStructurePiece.VerticalPlacement.ON_LAND_SURFACE;
			properties.airPocket = bl || chunkRandom.nextFloat() < 0.5F;
		} else if (ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.OCEAN) {
			verticalPlacement = RuinedPortalStructurePiece.VerticalPlacement.ON_OCEAN_FLOOR;
			properties.airPocket = false;
			properties.mossiness = 0.8F;
		} else if (ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.NETHER) {
			verticalPlacement = RuinedPortalStructurePiece.VerticalPlacement.IN_NETHER;
			properties.airPocket = chunkRandom.nextFloat() < 0.5F;
			properties.mossiness = 0.0F;
			properties.replaceWithBlackstone = true;
		} else {
			boolean bl = chunkRandom.nextFloat() < 0.5F;
			verticalPlacement = bl ? RuinedPortalStructurePiece.VerticalPlacement.UNDERGROUND : RuinedPortalStructurePiece.VerticalPlacement.ON_LAND_SURFACE;
			properties.airPocket = bl || chunkRandom.nextFloat() < 0.5F;
		}

		Identifier identifier;
		if (chunkRandom.nextFloat() < 0.05F) {
			identifier = new Identifier(RARE_PORTAL_STRUCTURE_IDS[chunkRandom.nextInt(RARE_PORTAL_STRUCTURE_IDS.length)]);
		} else {
			identifier = new Identifier(COMMON_PORTAL_STRUCTURE_IDS[chunkRandom.nextInt(COMMON_PORTAL_STRUCTURE_IDS.length)]);
		}

		Structure structure = context.structureManager().getStructureOrBlank(identifier);
		BlockRotation blockRotation = Util.getRandom(BlockRotation.values(), chunkRandom);
		BlockMirror blockMirror = chunkRandom.nextFloat() < 0.5F ? BlockMirror.NONE : BlockMirror.FRONT_BACK;
		BlockPos blockPos = new BlockPos(structure.getSize().getX() / 2, 0, structure.getSize().getZ() / 2);
		BlockPos blockPos2 = context.chunkPos().getStartPos();
		BlockBox blockBox = structure.calculateBoundingBox(blockPos2, blockRotation, blockPos, blockMirror);
		BlockPos blockPos3 = blockBox.getCenter();
		int i = context.chunkGenerator()
				.getHeight(blockPos3.getX(), blockPos3.getZ(), RuinedPortalStructurePiece.getHeightmapType(verticalPlacement), context.world())
			- 1;
		int j = getFloorHeight(
			chunkRandom, context.chunkGenerator(), verticalPlacement, properties.airPocket, i, blockBox.getBlockCountY(), blockBox, context.world()
		);
		BlockPos blockPos4 = new BlockPos(blockPos2.getX(), j, blockPos2.getZ());
		return !context.validBiome()
				.test(
					context.chunkGenerator()
						.getBiomeForNoiseGen(BiomeCoords.fromBlock(blockPos4.getX()), BiomeCoords.fromBlock(blockPos4.getY()), BiomeCoords.fromBlock(blockPos4.getZ()))
				)
			? Optional.empty()
			: Optional.of(
				(StructurePiecesGenerator<>)(collector, contextx) -> {
					if (ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.MOUNTAIN
						|| ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.OCEAN
						|| ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.STANDARD) {
						properties.cold = isColdAt(
							blockPos4,
							context.chunkGenerator()
								.getBiomeForNoiseGen(BiomeCoords.fromBlock(blockPos4.getX()), BiomeCoords.fromBlock(blockPos4.getY()), BiomeCoords.fromBlock(blockPos4.getZ()))
						);
					}

					collector.addPiece(
						new RuinedPortalStructurePiece(
							contextx.structureManager(), blockPos4, verticalPlacement, properties, identifier, structure, blockRotation, blockMirror, blockPos
						)
					);
				}
			);
	}

	private static boolean isColdAt(BlockPos pos, RegistryEntry<Biome> biome) {
		return biome.value().isCold(pos);
	}

	private static int getFloorHeight(
		Random random,
		ChunkGenerator chunkGenerator,
		RuinedPortalStructurePiece.VerticalPlacement verticalPlacement,
		boolean airPocket,
		int height,
		int blockCountY,
		BlockBox box,
		HeightLimitView world
	) {
		int i = world.getBottomY() + 15;
		int j;
		if (verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.IN_NETHER) {
			if (airPocket) {
				j = MathHelper.nextBetween(random, 32, 100);
			} else if (random.nextFloat() < 0.5F) {
				j = MathHelper.nextBetween(random, 27, 29);
			} else {
				j = MathHelper.nextBetween(random, 29, 100);
			}
		} else if (verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.IN_MOUNTAIN) {
			int k = height - blockCountY;
			j = choosePlacementHeight(random, 70, k);
		} else if (verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.UNDERGROUND) {
			int k = height - blockCountY;
			j = choosePlacementHeight(random, i, k);
		} else if (verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.PARTLY_BURIED) {
			j = height - blockCountY + MathHelper.nextBetween(random, 2, 8);
		} else {
			j = height;
		}

		List<BlockPos> list = ImmutableList.of(
			new BlockPos(box.getMinX(), 0, box.getMinZ()),
			new BlockPos(box.getMaxX(), 0, box.getMinZ()),
			new BlockPos(box.getMinX(), 0, box.getMaxZ()),
			new BlockPos(box.getMaxX(), 0, box.getMaxZ())
		);
		List<VerticalBlockSample> list2 = (List<VerticalBlockSample>)list.stream()
			.map(pos -> chunkGenerator.getColumnSample(pos.getX(), pos.getZ(), world))
			.collect(Collectors.toList());
		Heightmap.Type type = verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.ON_OCEAN_FLOOR
			? Heightmap.Type.OCEAN_FLOOR_WG
			: Heightmap.Type.WORLD_SURFACE_WG;

		int l;
		for (l = j; l > i; l--) {
			int m = 0;

			for (VerticalBlockSample verticalBlockSample : list2) {
				BlockState blockState = verticalBlockSample.getState(l);
				if (type.getBlockPredicate().test(blockState)) {
					if (++m == 3) {
						return l;
					}
				}
			}
		}

		return l;
	}

	private static int choosePlacementHeight(Random random, int min, int max) {
		return min < max ? MathHelper.nextBetween(random, min, max) : max;
	}

	public static enum Type implements StringIdentifiable {
		STANDARD("standard"),
		DESERT("desert"),
		JUNGLE("jungle"),
		SWAMP("swamp"),
		MOUNTAIN("mountain"),
		OCEAN("ocean"),
		NETHER("nether");

		public static final Codec<RuinedPortalFeature.Type> CODEC = StringIdentifiable.createCodec(RuinedPortalFeature.Type::values, RuinedPortalFeature.Type::byName);
		private static final Map<String, RuinedPortalFeature.Type> BY_NAME = (Map<String, RuinedPortalFeature.Type>)Arrays.stream(values())
			.collect(Collectors.toMap(RuinedPortalFeature.Type::getName, type -> type));
		private final String name;

		private Type(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public static RuinedPortalFeature.Type byName(String name) {
			return (RuinedPortalFeature.Type)BY_NAME.get(name);
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}
