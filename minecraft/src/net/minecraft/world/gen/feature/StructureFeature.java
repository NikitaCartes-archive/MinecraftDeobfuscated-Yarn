package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
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
	public boolean generate(
		IWorld world, StructureAccessor accessor, ChunkGenerator<? extends ChunkGeneratorConfig> generator, Random random, BlockPos pos, C config
	) {
		if (!world.getLevelProperties().hasStructures()) {
			return false;
		} else {
			int i = pos.getX() >> 4;
			int j = pos.getZ() >> 4;
			int k = i << 4;
			int l = j << 4;
			return accessor.getStructuresWithChildren(ChunkSectionPos.from(pos), this, world).map(structureStart -> {
				structureStart.generateStructure(world, accessor, generator, random, new BlockBox(k, l, k + 15, l + 15), new ChunkPos(i, j));
				return null;
			}).count() != 0L;
		}
	}

	protected StructureStart isInsideStructure(IWorld iWorld, StructureAccessor structureAccessor, BlockPos blockPos, boolean bl) {
		return (StructureStart)structureAccessor.getStructuresWithChildren(ChunkSectionPos.from(blockPos), this, iWorld)
			.filter(structureStart -> structureStart.getBoundingBox().contains(blockPos))
			.filter(structureStart -> !bl || structureStart.getChildren().stream().anyMatch(structurePiece -> structurePiece.getBoundingBox().contains(blockPos)))
			.findFirst()
			.orElse(StructureStart.DEFAULT);
	}

	public boolean isApproximatelyInsideStructure(IWorld world, StructureAccessor structureAccessor, BlockPos blockPos) {
		return this.isInsideStructure(world, structureAccessor, blockPos, false).hasChildren();
	}

	public boolean isInsideStructure(IWorld world, StructureAccessor structureAccessor, BlockPos blockPos) {
		return this.isInsideStructure(world, structureAccessor, blockPos, true).hasChildren();
	}

	@Nullable
	public BlockPos locateStructure(
		ServerWorld serverWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, BlockPos blockPos, int i, boolean skipExistingChunks
	) {
		if (!chunkGenerator.getBiomeSource().hasStructureFeature(this)) {
			return null;
		} else {
			StructureAccessor structureAccessor = serverWorld.getStructureAccessor();
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
							Chunk chunk = serverWorld.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.STRUCTURE_STARTS);
							StructureStart structureStart = structureAccessor.getStructureStart(ChunkSectionPos.from(chunk.getPos(), 0), this, chunk);
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
