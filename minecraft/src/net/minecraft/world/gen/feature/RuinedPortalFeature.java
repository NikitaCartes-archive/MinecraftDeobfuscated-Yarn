package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Dynamic;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class RuinedPortalFeature extends AbstractTempleFeature<RuinedPortalFeatureConfig> {
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

	public RuinedPortalFeature(Function<Dynamic<?>, ? extends RuinedPortalFeatureConfig> function) {
		super(function);
	}

	@Override
	public String getName() {
		return "Ruined_Portal";
	}

	@Override
	public int getRadius() {
		return 3;
	}

	@Override
	protected int getSpacing(ChunkGeneratorConfig chunkGeneratorConfig) {
		return chunkGeneratorConfig.getRuinedPortalSpacing();
	}

	@Override
	protected int getSeparation(ChunkGeneratorConfig chunkGeneratorConfig) {
		return chunkGeneratorConfig.getRuinedPortalSeparation();
	}

	@Override
	public StructureFeature.StructureStartFactory getStructureStartFactory() {
		return RuinedPortalFeature.Start::new;
	}

	@Override
	protected int getSeedModifier(ChunkGeneratorConfig chunkGeneratorConfig) {
		return 34222645;
	}

	private static boolean method_27209(BlockPos blockPos, Biome biome) {
		return biome.getTemperature(blockPos) < 0.15F;
	}

	private static int method_27211(
		Random random, ChunkGenerator chunkGenerator, RuinedPortalFeaturePiece.VerticalPlacement verticalPlacement, boolean bl, int i, int j, BlockBox blockBox
	) {
		int k;
		if (verticalPlacement == RuinedPortalFeaturePiece.VerticalPlacement.IN_NETHER) {
			if (bl) {
				k = choose(random, 32, 100);
			} else if (random.nextFloat() < 0.5F) {
				k = choose(random, 27, 29);
			} else {
				k = choose(random, 29, 100);
			}
		} else if (verticalPlacement == RuinedPortalFeaturePiece.VerticalPlacement.IN_MOUNTAIN) {
			int l = i - j;
			k = choosePlacementHeight(random, 70, l);
		} else if (verticalPlacement == RuinedPortalFeaturePiece.VerticalPlacement.UNDERGROUND) {
			int l = i - j;
			k = choosePlacementHeight(random, 15, l);
		} else if (verticalPlacement == RuinedPortalFeaturePiece.VerticalPlacement.PARTLY_BURIED) {
			k = i - j + choose(random, 2, 8);
		} else {
			k = i;
		}

		List<BlockPos> list = ImmutableList.of(
			new BlockPos(blockBox.minX, 0, blockBox.minZ),
			new BlockPos(blockBox.maxX, 0, blockBox.minZ),
			new BlockPos(blockBox.minX, 0, blockBox.maxZ),
			new BlockPos(blockBox.maxX, 0, blockBox.maxZ)
		);
		List<BlockView> list2 = (List<BlockView>)list.stream()
			.map(blockPos -> chunkGenerator.getColumnSample(blockPos.getX(), blockPos.getZ()))
			.collect(Collectors.toList());
		Heightmap.Type type = verticalPlacement == RuinedPortalFeaturePiece.VerticalPlacement.ON_OCEAN_FLOOR
			? Heightmap.Type.OCEAN_FLOOR_WG
			: Heightmap.Type.WORLD_SURFACE_WG;

		int m;
		for (m = k; m > 15; m--) {
			int n = 0;

			for (BlockView blockView : list2) {
				if (type.getBlockPredicate().test(blockView.getBlockState(new BlockPos(0, m, 0)))) {
					if (++n == 3) {
						return m;
					}
				}
			}
		}

		return m;
	}

	private static int choose(Random random, int min, int max) {
		return random.nextInt(max - min + 1) + min;
	}

	private static int choosePlacementHeight(Random random, int min, int max) {
		return min < max ? choose(random, min, max) : max;
	}

	public static class Start extends StructureStart {
		protected Start(StructureFeature<?> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
			super(structureFeature, i, j, blockBox, k, l);
		}

		@Override
		public void init(ChunkGenerator chunkGenerator, StructureManager structureManager, int x, int z, Biome biome) {
			RuinedPortalFeatureConfig ruinedPortalFeatureConfig = chunkGenerator.getStructureConfig(biome, Feature.RUINED_PORTAL);
			if (ruinedPortalFeatureConfig != null) {
				RuinedPortalFeaturePiece.Properties properties = new RuinedPortalFeaturePiece.Properties();
				RuinedPortalFeaturePiece.VerticalPlacement verticalPlacement;
				if (ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.DESERT) {
					verticalPlacement = RuinedPortalFeaturePiece.VerticalPlacement.PARTLY_BURIED;
					properties.airPocket = false;
					properties.mossiness = 0.0F;
				} else if (ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.JUNGLE) {
					verticalPlacement = RuinedPortalFeaturePiece.VerticalPlacement.ON_LAND_SURFACE;
					properties.airPocket = this.random.nextFloat() < 0.5F;
					properties.mossiness = 0.8F;
					properties.overgrown = true;
					properties.vines = true;
				} else if (ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.SWAMP) {
					verticalPlacement = RuinedPortalFeaturePiece.VerticalPlacement.ON_OCEAN_FLOOR;
					properties.airPocket = false;
					properties.mossiness = 0.5F;
					properties.vines = true;
				} else if (ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.MOUNTAIN) {
					boolean bl = this.random.nextFloat() < 0.5F;
					verticalPlacement = bl ? RuinedPortalFeaturePiece.VerticalPlacement.IN_MOUNTAIN : RuinedPortalFeaturePiece.VerticalPlacement.ON_LAND_SURFACE;
					properties.airPocket = bl || this.random.nextFloat() < 0.5F;
				} else if (ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.OCEAN) {
					verticalPlacement = RuinedPortalFeaturePiece.VerticalPlacement.ON_OCEAN_FLOOR;
					properties.airPocket = false;
					properties.mossiness = 0.8F;
				} else if (ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.NETHER) {
					verticalPlacement = RuinedPortalFeaturePiece.VerticalPlacement.IN_NETHER;
					properties.airPocket = this.random.nextFloat() < 0.5F;
					properties.mossiness = 0.0F;
					properties.replaceWithBlackstone = true;
				} else {
					boolean bl = this.random.nextFloat() < 0.5F;
					verticalPlacement = bl ? RuinedPortalFeaturePiece.VerticalPlacement.UNDERGROUND : RuinedPortalFeaturePiece.VerticalPlacement.ON_LAND_SURFACE;
					properties.airPocket = bl || this.random.nextFloat() < 0.5F;
				}

				Identifier identifier;
				if (this.random.nextFloat() < 0.05F) {
					identifier = new Identifier(RuinedPortalFeature.RARE_PORTAL_STRUCTURE_IDS[this.random.nextInt(RuinedPortalFeature.RARE_PORTAL_STRUCTURE_IDS.length)]);
				} else {
					identifier = new Identifier(RuinedPortalFeature.COMMON_PORTAL_STRUCTURE_IDS[this.random.nextInt(RuinedPortalFeature.COMMON_PORTAL_STRUCTURE_IDS.length)]);
				}

				Structure structure = structureManager.getStructureOrBlank(identifier);
				BlockRotation blockRotation = Util.getRandom(BlockRotation.values(), this.random);
				BlockMirror blockMirror = this.random.nextFloat() < 0.5F ? BlockMirror.NONE : BlockMirror.FRONT_BACK;
				BlockPos blockPos = new BlockPos(structure.getSize().getX() / 2, 0, structure.getSize().getZ() / 2);
				BlockPos blockPos2 = new ChunkPos(x, z).getCenterBlockPos();
				BlockBox blockBox = structure.method_27267(blockPos2, blockRotation, blockPos, blockMirror);
				Vec3i vec3i = blockBox.getCenter();
				int i = vec3i.getX();
				int j = vec3i.getZ();
				int k = chunkGenerator.getHeight(i, j, RuinedPortalFeaturePiece.getHeightmapType(verticalPlacement)) - 1;
				int l = RuinedPortalFeature.method_27211(this.random, chunkGenerator, verticalPlacement, properties.airPocket, k, blockBox.getBlockCountY(), blockBox);
				BlockPos blockPos3 = new BlockPos(blockPos2.getX(), l, blockPos2.getZ());
				if (ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.MOUNTAIN
					|| ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.OCEAN
					|| ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.STANDARD) {
					properties.cold = RuinedPortalFeature.method_27209(blockPos3, biome);
				}

				this.children.add(new RuinedPortalFeaturePiece(blockPos3, verticalPlacement, properties, identifier, structure, blockRotation, blockMirror, blockPos));
				this.setBoundingBoxFromChildren();
			}
		}
	}

	public static enum Type {
		STANDARD("standard"),
		DESERT("desert"),
		JUNGLE("jungle"),
		SWAMP("swamp"),
		MOUNTAIN("mountain"),
		OCEAN("ocean"),
		NETHER("nether");

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
	}
}
