package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.block.BlockState;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.structure.RuinedPortalStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureType;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.random.ChunkRandom;

public class RuinedPortalFeature extends StructureFeature {
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
	private static final int field_31511 = 15;
	private final List<RuinedPortalFeature.class_7155> field_37813;
	public static final Codec<RuinedPortalFeature> CODEC = RecordCodecBuilder.create(
		instance -> method_41608(instance)
				.and(
					Codecs.nonEmptyList(RuinedPortalFeature.class_7155.field_37814.listOf())
						.fieldOf("setups")
						.forGetter(ruinedPortalFeature -> ruinedPortalFeature.field_37813)
				)
				.apply(instance, RuinedPortalFeature::new)
	);

	public RuinedPortalFeature(
		RegistryEntryList<Biome> registryEntryList,
		Map<SpawnGroup, StructureSpawns> map,
		GenerationStep.Feature feature,
		boolean bl,
		List<RuinedPortalFeature.class_7155> list
	) {
		super(registryEntryList, map, feature, bl);
		this.field_37813 = list;
	}

	public RuinedPortalFeature(
		RegistryEntryList<Biome> registryEntryList,
		Map<SpawnGroup, StructureSpawns> map,
		GenerationStep.Feature feature,
		boolean bl,
		RuinedPortalFeature.class_7155 arg
	) {
		this(registryEntryList, map, feature, bl, List.of(arg));
	}

	@Override
	public Optional<StructureFeature.class_7150> method_38676(StructureFeature.class_7149 arg) {
		RuinedPortalStructurePiece.Properties properties = new RuinedPortalStructurePiece.Properties();
		ChunkRandom chunkRandom = arg.random();
		RuinedPortalFeature.class_7155 lv = null;
		if (this.field_37813.size() > 1) {
			float f = 0.0F;

			for (RuinedPortalFeature.class_7155 lv2 : this.field_37813) {
				f += lv2.weight();
			}

			float g = chunkRandom.nextFloat();

			for (RuinedPortalFeature.class_7155 lv3 : this.field_37813) {
				g -= lv3.weight() / f;
				if (g < 0.0F) {
					lv = lv3;
					break;
				}
			}
		} else {
			lv = (RuinedPortalFeature.class_7155)this.field_37813.get(0);
		}

		if (lv == null) {
			throw new IllegalStateException();
		} else {
			RuinedPortalFeature.class_7155 lv4 = lv;
			properties.airPocket = method_41682(chunkRandom, lv4.airPocketProbability());
			properties.mossiness = lv4.mossiness();
			properties.overgrown = lv4.overgrown();
			properties.vines = lv4.vines();
			properties.replaceWithBlackstone = lv4.replaceWithBlackstone();
			Identifier identifier;
			if (chunkRandom.nextFloat() < 0.05F) {
				identifier = new Identifier(RARE_PORTAL_STRUCTURE_IDS[chunkRandom.nextInt(RARE_PORTAL_STRUCTURE_IDS.length)]);
			} else {
				identifier = new Identifier(COMMON_PORTAL_STRUCTURE_IDS[chunkRandom.nextInt(COMMON_PORTAL_STRUCTURE_IDS.length)]);
			}

			Structure structure = arg.structureTemplateManager().getStructureOrBlank(identifier);
			BlockRotation blockRotation = Util.getRandom(BlockRotation.values(), chunkRandom);
			BlockMirror blockMirror = chunkRandom.nextFloat() < 0.5F ? BlockMirror.NONE : BlockMirror.FRONT_BACK;
			BlockPos blockPos = new BlockPos(structure.getSize().getX() / 2, 0, structure.getSize().getZ() / 2);
			ChunkGenerator chunkGenerator = arg.chunkGenerator();
			HeightLimitView heightLimitView = arg.heightAccessor();
			NoiseConfig noiseConfig = arg.randomState();
			BlockPos blockPos2 = arg.chunkPos().getStartPos();
			BlockBox blockBox = structure.calculateBoundingBox(blockPos2, blockRotation, blockPos, blockMirror);
			BlockPos blockPos3 = blockBox.getCenter();
			int i = chunkGenerator.getHeight(
					blockPos3.getX(), blockPos3.getZ(), RuinedPortalStructurePiece.getHeightmapType(lv4.placement()), heightLimitView, noiseConfig
				)
				- 1;
			int j = getFloorHeight(
				chunkRandom, chunkGenerator, lv4.placement(), properties.airPocket, i, blockBox.getBlockCountY(), blockBox, heightLimitView, noiseConfig
			);
			BlockPos blockPos4 = new BlockPos(blockPos2.getX(), j, blockPos2.getZ());
			return Optional.of(
				new StructureFeature.class_7150(
					blockPos4,
					structurePiecesCollector -> {
						if (lv4.canBeCold()) {
							properties.cold = isColdAt(
								blockPos4,
								arg.chunkGenerator()
									.getBiomeSource()
									.getBiome(
										BiomeCoords.fromBlock(blockPos4.getX()),
										BiomeCoords.fromBlock(blockPos4.getY()),
										BiomeCoords.fromBlock(blockPos4.getZ()),
										noiseConfig.getMultiNoiseSampler()
									)
							);
						}

						structurePiecesCollector.addPiece(
							new RuinedPortalStructurePiece(
								arg.structureTemplateManager(), blockPos4, lv4.placement(), properties, identifier, structure, blockRotation, blockMirror, blockPos
							)
						);
					}
				)
			);
		}
	}

	private static boolean method_41682(ChunkRandom chunkRandom, float f) {
		if (f == 0.0F) {
			return false;
		} else {
			return f == 1.0F ? true : chunkRandom.nextFloat() < f;
		}
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
		HeightLimitView world,
		NoiseConfig noiseConfig
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
			.map(blockPos -> chunkGenerator.getColumnSample(blockPos.getX(), blockPos.getZ(), world, noiseConfig))
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

	@Override
	public StructureType<?> getType() {
		return StructureType.RUINED_PORTAL;
	}

	public static record class_7155(
		RuinedPortalStructurePiece.VerticalPlacement placement,
		float airPocketProbability,
		float mossiness,
		boolean overgrown,
		boolean vines,
		boolean canBeCold,
		boolean replaceWithBlackstone,
		float weight
	) {
		public static final Codec<RuinedPortalFeature.class_7155> field_37814 = RecordCodecBuilder.create(
			instance -> instance.group(
						RuinedPortalStructurePiece.VerticalPlacement.field_37811.fieldOf("placement").forGetter(RuinedPortalFeature.class_7155::placement),
						Codec.floatRange(0.0F, 1.0F).fieldOf("air_pocket_probability").forGetter(RuinedPortalFeature.class_7155::airPocketProbability),
						Codec.floatRange(0.0F, 1.0F).fieldOf("mossiness").forGetter(RuinedPortalFeature.class_7155::mossiness),
						Codec.BOOL.fieldOf("overgrown").forGetter(RuinedPortalFeature.class_7155::overgrown),
						Codec.BOOL.fieldOf("vines").forGetter(RuinedPortalFeature.class_7155::vines),
						Codec.BOOL.fieldOf("can_be_cold").forGetter(RuinedPortalFeature.class_7155::canBeCold),
						Codec.BOOL.fieldOf("replace_with_blackstone").forGetter(RuinedPortalFeature.class_7155::replaceWithBlackstone),
						Codecs.POSITIVE_FLOAT.fieldOf("weight").forGetter(RuinedPortalFeature.class_7155::weight)
					)
					.apply(instance, RuinedPortalFeature.class_7155::new)
		);
	}
}
