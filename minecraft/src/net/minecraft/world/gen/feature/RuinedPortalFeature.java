package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.block.BlockState;
import net.minecraft.structure.RuinedPortalStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;

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

	public RuinedPortalFeature(Codec<RuinedPortalFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public StructureFeature.StructureStartFactory<RuinedPortalFeatureConfig> getStructureStartFactory() {
		return RuinedPortalFeature.Start::new;
	}

	private static boolean method_27209(BlockPos blockPos, Biome biome) {
		return biome.getTemperature(blockPos) < 0.15F;
	}

	private static int method_27211(
		Random random, ChunkGenerator chunkGenerator, RuinedPortalStructurePiece.VerticalPlacement verticalPlacement, boolean bl, int i, int j, BlockBox blockBox
	) {
		int k;
		if (verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.field_24034) {
			if (bl) {
				k = choose(random, 32, 100);
			} else if (random.nextFloat() < 0.5F) {
				k = choose(random, 27, 29);
			} else {
				k = choose(random, 29, 100);
			}
		} else if (verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.field_24032) {
			int l = i - j;
			k = choosePlacementHeight(random, 70, l);
		} else if (verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.field_24033) {
			int l = i - j;
			k = choosePlacementHeight(random, 15, l);
		} else if (verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.field_24030) {
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
		Heightmap.Type type = verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.field_24031 ? Heightmap.Type.field_13195 : Heightmap.Type.field_13194;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		int m;
		for (m = k; m > 15; m--) {
			int n = 0;
			mutable.set(0, m, 0);

			for (BlockView blockView : list2) {
				BlockState blockState = blockView.getBlockState(mutable);
				if (blockState != null && type.getBlockPredicate().test(blockState)) {
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

	public static class Start extends StructureStart<RuinedPortalFeatureConfig> {
		protected Start(StructureFeature<RuinedPortalFeatureConfig> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
			super(structureFeature, i, j, blockBox, k, l);
		}

		public void method_28646(
			DynamicRegistryManager dynamicRegistryManager,
			ChunkGenerator chunkGenerator,
			StructureManager structureManager,
			int i,
			int j,
			Biome biome,
			RuinedPortalFeatureConfig ruinedPortalFeatureConfig
		) {
			RuinedPortalStructurePiece.Properties properties = new RuinedPortalStructurePiece.Properties();
			RuinedPortalStructurePiece.VerticalPlacement verticalPlacement;
			if (ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.field_24001) {
				verticalPlacement = RuinedPortalStructurePiece.VerticalPlacement.field_24030;
				properties.airPocket = false;
				properties.mossiness = 0.0F;
			} else if (ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.field_24002) {
				verticalPlacement = RuinedPortalStructurePiece.VerticalPlacement.field_24029;
				properties.airPocket = this.random.nextFloat() < 0.5F;
				properties.mossiness = 0.8F;
				properties.overgrown = true;
				properties.vines = true;
			} else if (ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.field_24003) {
				verticalPlacement = RuinedPortalStructurePiece.VerticalPlacement.field_24031;
				properties.airPocket = false;
				properties.mossiness = 0.5F;
				properties.vines = true;
			} else if (ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.field_24004) {
				boolean bl = this.random.nextFloat() < 0.5F;
				verticalPlacement = bl ? RuinedPortalStructurePiece.VerticalPlacement.field_24032 : RuinedPortalStructurePiece.VerticalPlacement.field_24029;
				properties.airPocket = bl || this.random.nextFloat() < 0.5F;
			} else if (ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.field_24005) {
				verticalPlacement = RuinedPortalStructurePiece.VerticalPlacement.field_24031;
				properties.airPocket = false;
				properties.mossiness = 0.8F;
			} else if (ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.field_24006) {
				verticalPlacement = RuinedPortalStructurePiece.VerticalPlacement.field_24034;
				properties.airPocket = this.random.nextFloat() < 0.5F;
				properties.mossiness = 0.0F;
				properties.replaceWithBlackstone = true;
			} else {
				boolean bl = this.random.nextFloat() < 0.5F;
				verticalPlacement = bl ? RuinedPortalStructurePiece.VerticalPlacement.field_24033 : RuinedPortalStructurePiece.VerticalPlacement.field_24029;
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
			BlockMirror blockMirror = this.random.nextFloat() < 0.5F ? BlockMirror.field_11302 : BlockMirror.field_11301;
			BlockPos blockPos = new BlockPos(structure.getSize().getX() / 2, 0, structure.getSize().getZ() / 2);
			BlockPos blockPos2 = new ChunkPos(i, j).getCenterBlockPos();
			BlockBox blockBox = structure.method_27267(blockPos2, blockRotation, blockPos, blockMirror);
			Vec3i vec3i = blockBox.getCenter();
			int k = vec3i.getX();
			int l = vec3i.getZ();
			int m = chunkGenerator.getHeight(k, l, RuinedPortalStructurePiece.getHeightmapType(verticalPlacement)) - 1;
			int n = RuinedPortalFeature.method_27211(this.random, chunkGenerator, verticalPlacement, properties.airPocket, m, blockBox.getBlockCountY(), blockBox);
			BlockPos blockPos3 = new BlockPos(blockPos2.getX(), n, blockPos2.getZ());
			if (ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.field_24004
				|| ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.field_24005
				|| ruinedPortalFeatureConfig.portalType == RuinedPortalFeature.Type.field_24000) {
				properties.cold = RuinedPortalFeature.method_27209(blockPos3, biome);
			}

			this.children.add(new RuinedPortalStructurePiece(blockPos3, verticalPlacement, properties, identifier, structure, blockRotation, blockMirror, blockPos));
			this.setBoundingBoxFromChildren();
		}
	}

	public static enum Type implements StringIdentifiable {
		field_24000("standard"),
		field_24001("desert"),
		field_24002("jungle"),
		field_24003("swamp"),
		field_24004("mountain"),
		field_24005("ocean"),
		field_24006("nether");

		public static final Codec<RuinedPortalFeature.Type> field_24840 = StringIdentifiable.createCodec(
			RuinedPortalFeature.Type::values, RuinedPortalFeature.Type::byName
		);
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
