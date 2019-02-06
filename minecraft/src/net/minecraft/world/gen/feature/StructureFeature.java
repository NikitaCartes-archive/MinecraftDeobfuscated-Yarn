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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.BlockViewWithStructures;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class StructureFeature<C extends FeatureConfig> extends Feature<C> {
	private static final Logger LOGGER = LogManager.getLogger();

	public StructureFeature(Function<Dynamic<?>, ? extends C> function) {
		super(function, false);
	}

	@Override
	public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, C featureConfig) {
		if (!iWorld.getLevelProperties().hasStructures()) {
			return false;
		} else {
			int i = blockPos.getX() >> 4;
			int j = blockPos.getZ() >> 4;
			int k = i << 4;
			int l = j << 4;
			boolean bl = false;
			LongIterator var11 = iWorld.getChunk(i, j).getStructureReferences(this.getName()).iterator();

			while (var11.hasNext()) {
				Long long_ = (Long)var11.next();
				ChunkPos chunkPos = new ChunkPos(long_);
				StructureStart structureStart = iWorld.getChunk(chunkPos.x, chunkPos.z).getStructureStart(this.getName());
				if (structureStart != null && structureStart != StructureStart.DEFAULT) {
					structureStart.generateStructure(iWorld, random, new MutableIntBoundingBox(k, l, k + 15, l + 15), new ChunkPos(i, j));
					bl = true;
				}
			}

			return bl;
		}
	}

	protected StructureStart isInsideStructure(IWorld iWorld, BlockPos blockPos, boolean bl) {
		for (StructureStart structureStart : this.getStructureStarts(iWorld, blockPos.getX() >> 4, blockPos.getZ() >> 4)) {
			if (structureStart.hasChildren() && structureStart.getBoundingBox().contains(blockPos)) {
				if (!bl) {
					return structureStart;
				}

				for (StructurePiece structurePiece : structureStart.getChildren()) {
					if (structurePiece.getBoundingBox().contains(blockPos)) {
						return structureStart;
					}
				}
			}
		}

		return StructureStart.DEFAULT;
	}

	public boolean isApproximatelyInsideStructure(IWorld iWorld, BlockPos blockPos) {
		return this.isInsideStructure(iWorld, blockPos, false).hasChildren();
	}

	public boolean isInsideStructure(IWorld iWorld, BlockPos blockPos) {
		return this.isInsideStructure(iWorld, blockPos, true).hasChildren();
	}

	@Nullable
	public BlockPos locateStructure(World world, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, BlockPos blockPos, int i, boolean bl) {
		if (!chunkGenerator.getBiomeSource().hasStructureFeature(this)) {
			return null;
		} else {
			int j = blockPos.getX() >> 4;
			int k = blockPos.getZ() >> 4;
			int l = 0;

			for (ChunkRandom chunkRandom = new ChunkRandom(); l <= i; l++) {
				for (int m = -l; m <= l; m++) {
					boolean bl2 = m == -l || m == l;

					for (int n = -l; n <= l; n++) {
						boolean bl3 = n == -l || n == l;
						if (bl2 || bl3) {
							ChunkPos chunkPos = this.getStart(chunkGenerator, chunkRandom, j, k, m, n);
							StructureStart structureStart = world.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.STRUCTURE_STARTS).getStructureStart(this.getName());
							if (structureStart != null && structureStart.hasChildren()) {
								if (bl && structureStart.isInExistingChunk()) {
									structureStart.incrementReferences();
									return structureStart.getPos();
								}

								if (!bl) {
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

	private List<StructureStart> getStructureStarts(IWorld iWorld, int i, int j) {
		List<StructureStart> list = Lists.<StructureStart>newArrayList();
		Chunk chunk = iWorld.getChunk(i, j, ChunkStatus.EMPTY);
		LongIterator longIterator = chunk.getStructureReferences(this.getName()).iterator();

		while (longIterator.hasNext()) {
			long l = longIterator.nextLong();
			BlockViewWithStructures blockViewWithStructures = iWorld.getChunk(ChunkPos.getPackedX(l), ChunkPos.getPackedZ(l), ChunkStatus.EMPTY);
			StructureStart structureStart = blockViewWithStructures.getStructureStart(this.getName());
			if (structureStart != null) {
				list.add(structureStart);
			}
		}

		return list;
	}

	protected ChunkPos getStart(ChunkGenerator<?> chunkGenerator, Random random, int i, int j, int k, int l) {
		return new ChunkPos(i + k, j + l);
	}

	public abstract boolean shouldStartAt(ChunkGenerator<?> chunkGenerator, Random random, int i, int j);

	public abstract StructureFeature.StructureStartFactory getStructureStartFactory();

	public abstract String getName();

	public abstract int getRadius();

	public interface StructureStartFactory {
		StructureStart create(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l);
	}
}
