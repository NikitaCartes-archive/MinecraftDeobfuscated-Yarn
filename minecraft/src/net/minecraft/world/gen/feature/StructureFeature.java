package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.StructureHolder;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class StructureFeature<C extends FeatureConfig> extends Feature<C> {
	private static final Logger LOGGER = LogManager.getLogger();

	public StructureFeature(Function<Dynamic<?>, ? extends C> function) {
		super(function);
	}

	@Override
	public ConfiguredFeature<C, ? extends StructureFeature<C>> configure(C config) {
		return new ConfiguredFeature<>(this, config);
	}

	@Override
	public boolean generate(IWorld world, ChunkGenerator<? extends ChunkGeneratorConfig> generator, Random random, BlockPos pos, C config) {
		if (!world.getLevelProperties().hasStructures()) {
			return false;
		} else {
			int i = pos.getX() >> 4;
			int j = pos.getZ() >> 4;
			int k = i << 4;
			int l = j << 4;
			boolean bl = false;
			LongIterator var11 = world.getChunk(i, j).getStructureReferences(this.getName()).iterator();

			while (var11.hasNext()) {
				Long long_ = (Long)var11.next();
				ChunkPos chunkPos = new ChunkPos(long_);
				StructureStart structureStart = world.getChunk(chunkPos.x, chunkPos.z).getStructureStart(this.getName());
				if (structureStart != null && structureStart != StructureStart.DEFAULT) {
					structureStart.generateStructure(world, generator, random, new BlockBox(k, l, k + 15, l + 15), new ChunkPos(i, j));
					bl = true;
				}
			}

			return bl;
		}
	}

	protected StructureStart isInsideStructure(IWorld world, BlockPos pos, boolean exact) {
		for (StructureStart structureStart : this.getStructureStarts(world, pos.getX() >> 4, pos.getZ() >> 4)) {
			if (structureStart.hasChildren() && structureStart.getBoundingBox().contains(pos)) {
				if (!exact) {
					return structureStart;
				}

				for (StructurePiece structurePiece : structureStart.getChildren()) {
					if (structurePiece.getBoundingBox().contains(pos)) {
						return structureStart;
					}
				}
			}
		}

		return StructureStart.DEFAULT;
	}

	public boolean isApproximatelyInsideStructure(IWorld world, BlockPos pos) {
		return this.isInsideStructure(world, pos, false).hasChildren();
	}

	public boolean isInsideStructure(IWorld world, BlockPos pos) {
		return this.isInsideStructure(world, pos, true).hasChildren();
	}

	@Nullable
	public BlockPos locateStructure(
		World world, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, BlockPos blockPos, int i, boolean skipExistingChunks
	) {
		if (!chunkGenerator.getBiomeSource().hasStructureFeature(this)) {
			return null;
		} else {
			int j = blockPos.getX() >> 4;
			int k = blockPos.getZ() >> 4;
			int l = 0;

			for (ChunkRandom chunkRandom = new ChunkRandom(); l <= i; l++) {
				for (int m = -l; m <= l; m++) {
					boolean bl = m == -l || m == l;

					for (int n = -l; n <= l; n++) {
						boolean bl2 = n == -l || n == l;
						if (bl || bl2) {
							ChunkPos chunkPos = this.getStart(chunkGenerator, chunkRandom, j, k, m, n);
							StructureStart structureStart = world.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.STRUCTURE_STARTS).getStructureStart(this.getName());
							if (structureStart != null && structureStart.hasChildren()) {
								if (skipExistingChunks && structureStart.isInExistingChunk()) {
									structureStart.incrementReferences();
									return structureStart.getPos();
								}

								if (!skipExistingChunks) {
									return structureStart.getPos();
								}
							}

							if (l == 0) {
								break;
							}
						}
					}

					if (l == 0) {
						break;
					}
				}
			}

			return null;
		}
	}

	private List<StructureStart> getStructureStarts(IWorld world, int chunkX, int chunkZ) {
		List<StructureStart> list = Lists.<StructureStart>newArrayList();
		Chunk chunk = world.getChunk(chunkX, chunkZ, ChunkStatus.STRUCTURE_REFERENCES);
		LongIterator longIterator = chunk.getStructureReferences(this.getName()).iterator();

		while (longIterator.hasNext()) {
			long l = longIterator.nextLong();
			StructureHolder structureHolder = world.getChunk(ChunkPos.getPackedX(l), ChunkPos.getPackedZ(l), ChunkStatus.STRUCTURE_STARTS);
			StructureStart structureStart = structureHolder.getStructureStart(this.getName());
			if (structureStart != null) {
				list.add(structureStart);
			}
		}

		return list;
	}

	protected ChunkPos getStart(ChunkGenerator<?> chunkGenerator, Random random, int i, int j, int k, int l) {
		return new ChunkPos(i + k, j + l);
	}

	public abstract boolean shouldStartAt(BiomeAccess biomeAccess, ChunkGenerator<?> chunkGenerator, Random random, int chunkX, int chunkZ, Biome biome);

	public abstract StructureFeature.StructureStartFactory getStructureStartFactory();

	public abstract String getName();

	public abstract int getRadius();

	public interface StructureStartFactory {
		StructureStart create(StructureFeature<?> feature, int x, int z, BlockBox box, int i, long l);
	}
}
