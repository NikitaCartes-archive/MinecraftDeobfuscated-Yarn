package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.NetherFortressGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class NetherFortressFeature extends StructureFeature<DefaultFeatureConfig> {
	private static final List<Biome.SpawnEntry> MONSTER_SPAWNS = Lists.<Biome.SpawnEntry>newArrayList(
		new Biome.SpawnEntry(EntityType.BLAZE, 10, 2, 3),
		new Biome.SpawnEntry(EntityType.ZOMBIFIED_PIGLIN, 5, 4, 4),
		new Biome.SpawnEntry(EntityType.WITHER_SKELETON, 8, 5, 5),
		new Biome.SpawnEntry(EntityType.SKELETON, 2, 5, 5),
		new Biome.SpawnEntry(EntityType.MAGMA_CUBE, 3, 4, 4)
	);

	public NetherFortressFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	protected boolean shouldStartAt(
		ChunkGenerator chunkGenerator,
		BiomeSource biomeSource,
		long l,
		ChunkRandom chunkRandom,
		int i,
		int j,
		Biome biome,
		ChunkPos chunkPos,
		DefaultFeatureConfig defaultFeatureConfig
	) {
		return chunkRandom.nextInt(6) < 2;
	}

	@Override
	public StructureFeature.StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
		return NetherFortressFeature.Start::new;
	}

	@Override
	public List<Biome.SpawnEntry> getMonsterSpawns() {
		return MONSTER_SPAWNS;
	}

	public static class Start extends StructureStart<DefaultFeatureConfig> {
		public Start(StructureFeature<DefaultFeatureConfig> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
			super(structureFeature, i, j, blockBox, k, l);
		}

		public void init(ChunkGenerator chunkGenerator, StructureManager structureManager, int i, int j, Biome biome, DefaultFeatureConfig defaultFeatureConfig) {
			NetherFortressGenerator.Start start = new NetherFortressGenerator.Start(this.random, (i << 4) + 2, (j << 4) + 2);
			this.children.add(start);
			start.placeJigsaw(start, this.children, this.random);
			List<StructurePiece> list = start.field_14505;

			while (!list.isEmpty()) {
				int k = this.random.nextInt(list.size());
				StructurePiece structurePiece = (StructurePiece)list.remove(k);
				structurePiece.placeJigsaw(start, this.children, this.random);
			}

			this.setBoundingBoxFromChildren();
			this.method_14976(this.random, 48, 70);
		}
	}
}
