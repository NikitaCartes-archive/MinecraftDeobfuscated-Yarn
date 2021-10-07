package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.class_6622;
import net.minecraft.class_6624;
import net.minecraft.class_6626;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.random.RandomSeed;

public class OceanMonumentFeature extends StructureFeature<DefaultFeatureConfig> {
	public static final Pool<SpawnSettings.SpawnEntry> MONSTER_SPAWNS = Pool.of(new SpawnSettings.SpawnEntry(EntityType.GUARDIAN, 1, 2, 4));

	public OceanMonumentFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec, OceanMonumentFeature::method_38682);
	}

	@Override
	protected boolean isUniformDistribution() {
		return false;
	}

	protected boolean shouldStartAt(
		ChunkGenerator chunkGenerator,
		BiomeSource biomeSource,
		long l,
		ChunkRandom chunkRandom,
		ChunkPos chunkPos,
		ChunkPos chunkPos2,
		DefaultFeatureConfig defaultFeatureConfig,
		HeightLimitView heightLimitView
	) {
		int i = chunkPos.getOffsetX(9);
		int j = chunkPos.getOffsetZ(9);

		for (Biome biome : biomeSource.getBiomesInArea(i, chunkGenerator.getSeaLevel(), j, 29, chunkGenerator.getMultiNoiseSampler())) {
			if (biome.getCategory() != Biome.Category.OCEAN && biome.getCategory() != Biome.Category.RIVER) {
				return false;
			}
		}

		return true;
	}

	private static StructurePiece method_38681(ChunkPos chunkPos, ChunkRandom chunkRandom) {
		int i = chunkPos.getStartX() - 29;
		int j = chunkPos.getStartZ() - 29;
		Direction direction = Direction.Type.HORIZONTAL.random(chunkRandom);
		return new OceanMonumentGenerator.Base(chunkRandom, i, j, direction);
	}

	private static void method_38682(class_6626 arg, DefaultFeatureConfig defaultFeatureConfig, class_6622.class_6623 arg2) {
		method_38683(arg, arg2);
	}

	private static void method_38683(class_6626 arg, class_6622.class_6623 arg2) {
		if (arg2.method_38707(Heightmap.Type.OCEAN_FLOOR_WG)) {
			arg.addPiece(method_38681(arg2.chunkPos(), arg2.random()));
		}
	}

	public static class_6624 method_38680(ChunkPos chunkPos, long l, class_6624 arg) {
		if (arg.method_38708()) {
			return arg;
		} else {
			ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(RandomSeed.getSeed()));
			chunkRandom.setCarverSeed(l, chunkPos.x, chunkPos.z);
			StructurePiece structurePiece = method_38681(chunkPos, chunkRandom);
			class_6626 lv = new class_6626();
			lv.addPiece(structurePiece);
			return lv.method_38714();
		}
	}
}
