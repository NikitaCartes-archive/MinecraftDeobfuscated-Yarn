package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.function.Function;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.NetherFortressGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class NetherFortressFeature extends StructureFeature<DefaultFeatureConfig> {
	private static final List<Biome.SpawnEntry> MONSTER_SPAWNS = Lists.<Biome.SpawnEntry>newArrayList(
		new Biome.SpawnEntry(EntityType.BLAZE, 10, 2, 3),
		new Biome.SpawnEntry(EntityType.ZOMBIFIED_PIGLIN, 5, 4, 4),
		new Biome.SpawnEntry(EntityType.WITHER_SKELETON, 8, 5, 5),
		new Biome.SpawnEntry(EntityType.SKELETON, 2, 5, 5),
		new Biome.SpawnEntry(EntityType.MAGMA_CUBE, 3, 4, 4)
	);

	public NetherFortressFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	protected int getSpacing(ChunkGeneratorConfig chunkGeneratorConfig) {
		return chunkGeneratorConfig.getNetherStructureSpacing();
	}

	@Override
	protected int getSeparation(ChunkGeneratorConfig chunkGeneratorConfig) {
		return chunkGeneratorConfig.getNetherStructureSeparation();
	}

	@Override
	protected int getSeedModifier(ChunkGeneratorConfig chunkGeneratorConfig) {
		return chunkGeneratorConfig.getNetherStructureSeedModifier();
	}

	@Override
	protected boolean shouldStartAt(
		BiomeAccess biomeAccess, ChunkGenerator chunkGenerator, long l, ChunkRandom chunkRandom, int i, int j, Biome biome, ChunkPos chunkPos
	) {
		return chunkRandom.nextInt(6) < 2;
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
		public Start(StructureFeature<?> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
			super(structureFeature, i, j, blockBox, k, l);
		}

		@Override
		public void init(ChunkGenerator chunkGenerator, StructureManager structureManager, int x, int z, Biome biome) {
			NetherFortressGenerator.Start start = new NetherFortressGenerator.Start(this.random, (x << 4) + 2, (z << 4) + 2);
			this.children.add(start);
			start.placeJigsaw(start, this.children, this.random);
			List<StructurePiece> list = start.field_14505;

			while (!list.isEmpty()) {
				int i = this.random.nextInt(list.size());
				StructurePiece structurePiece = (StructurePiece)list.remove(i);
				structurePiece.placeJigsaw(start, this.children, this.random);
			}

			this.setBoundingBoxFromChildren();
			this.method_14976(this.random, 48, 70);
		}
	}
}
