package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.generator.NetherFortressGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class NetherFortressFeature extends StructureFeature<DefaultFeatureConfig> {
	private static final List<Biome.SpawnEntry> MONSTER_SPAWNS = Lists.<Biome.SpawnEntry>newArrayList(
		new Biome.SpawnEntry(EntityType.BLAZE, 10, 2, 3),
		new Biome.SpawnEntry(EntityType.ZOMBIE_PIGMAN, 5, 4, 4),
		new Biome.SpawnEntry(EntityType.WITHER_SKELETON, 8, 5, 5),
		new Biome.SpawnEntry(EntityType.SKELETON, 2, 5, 5),
		new Biome.SpawnEntry(EntityType.MAGMA_CUBE, 3, 4, 4)
	);

	public NetherFortressFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	public boolean shouldStartAt(ChunkGenerator<?> chunkGenerator, Random random, int i, int j) {
		int k = i >> 4;
		int l = j >> 4;
		random.setSeed((long)(k ^ l << 4) ^ chunkGenerator.getSeed());
		random.nextInt();
		if (random.nextInt(3) != 0) {
			return false;
		} else if (i != (k << 4) + 4 + random.nextInt(8)) {
			return false;
		} else if (j != (l << 4) + 4 + random.nextInt(8)) {
			return false;
		} else {
			Biome biome = chunkGenerator.getBiomeSource().getBiome(new BlockPos((i << 4) + 9, 0, (j << 4) + 9));
			return chunkGenerator.hasStructure(biome, Feature.NETHER_BRIDGE);
		}
	}

	@Override
	public StructureFeature.StructureStartFactory getStructureStartFactory() {
		return NetherFortressFeature.Start::new;
	}

	@Override
	public String getName() {
		return "Fortress";
	}

	@Override
	public int getRadius() {
		return 8;
	}

	@Override
	public List<Biome.SpawnEntry> getMonsterSpawns() {
		return MONSTER_SPAWNS;
	}

	public static class Start extends StructureStart {
		public Start(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
			super(structureFeature, i, j, biome, mutableIntBoundingBox, k, l);
		}

		@Override
		public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome) {
			NetherFortressGenerator.Start start = new NetherFortressGenerator.Start(this.random, (i << 4) + 2, (j << 4) + 2);
			this.children.add(start);
			start.method_14918(start, this.children, this.random);
			List<StructurePiece> list = start.field_14505;

			while (!list.isEmpty()) {
				int k = this.random.nextInt(list.size());
				StructurePiece structurePiece = (StructurePiece)list.remove(k);
				structurePiece.method_14918(start, this.children, this.random);
			}

			this.setBoundingBoxFromChildren();
			this.method_14976(this.random, 48, 70);
		}
	}
}
