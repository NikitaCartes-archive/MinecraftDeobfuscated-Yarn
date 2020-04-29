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
import net.minecraft.world.dimension.DimensionType;
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
		if (!accessor.method_27834()) {
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
		if (!chunkGenerator.hasStructure(this)) {
			return null;
		} else {
			StructureAccessor structureAccessor = serverWorld.getStructureAccessor();
			int j = this.getSpacing(chunkGenerator.getDimensionType(), chunkGenerator.getConfig());
			int k = blockPos.getX() >> 4;
			int l = blockPos.getZ() >> 4;
			int m = 0;

			for (ChunkRandom chunkRandom = new ChunkRandom(); m <= i; m++) {
				for (int n = -m; n <= m; n++) {
					boolean bl = n == -m || n == m;

					for (int o = -m; o <= m; o++) {
						boolean bl2 = o == -m || o == m;
						if (bl || bl2) {
							int p = k + j * n;
							int q = l + j * o;
							ChunkPos chunkPos = this.method_27218(chunkGenerator, chunkRandom, p, q);
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

							if (m == 0) {
								break;
							}
						}
					}

					if (m == 0) {
						break;
					}
				}
			}

			return null;
		}
	}

	protected int getSpacing(DimensionType dimensionType, ChunkGeneratorConfig chunkGeneratorConfig) {
		return 1;
	}

	protected int getSeparation(DimensionType dimensionType, ChunkGeneratorConfig chunkGenerationConfig) {
		return 0;
	}

	protected int getSeedModifier(ChunkGeneratorConfig chunkGeneratorConfig) {
		return 0;
	}

	protected boolean method_27219() {
		return true;
	}

	public final ChunkPos method_27218(ChunkGenerator<?> chunkGenerator, ChunkRandom chunkRandom, int i, int j) {
		ChunkGeneratorConfig chunkGeneratorConfig = chunkGenerator.getConfig();
		DimensionType dimensionType = chunkGenerator.getDimensionType();
		int k = this.getSpacing(dimensionType, chunkGeneratorConfig);
		int l = this.getSeparation(dimensionType, chunkGeneratorConfig);
		int m = Math.floorDiv(i, k);
		int n = Math.floorDiv(j, k);
		chunkRandom.setRegionSeed(chunkGenerator.getSeed(), m, n, this.getSeedModifier(chunkGeneratorConfig));
		int o;
		int p;
		if (this.method_27219()) {
			o = chunkRandom.nextInt(k - l);
			p = chunkRandom.nextInt(k - l);
		} else {
			o = (chunkRandom.nextInt(k - l) + chunkRandom.nextInt(k - l)) / 2;
			p = (chunkRandom.nextInt(k - l) + chunkRandom.nextInt(k - l)) / 2;
		}

		return new ChunkPos(m * k + o, n * k + p);
	}

	public boolean method_27217(BiomeAccess biomeAccess, ChunkGenerator<?> chunkGenerator, ChunkRandom chunkRandom, int i, int j, Biome biome) {
		ChunkPos chunkPos = this.method_27218(chunkGenerator, chunkRandom, i, j);
		return i == chunkPos.x
			&& j == chunkPos.z
			&& chunkGenerator.hasStructure(biome, this)
			&& this.shouldStartAt(biomeAccess, chunkGenerator, chunkRandom, i, j, biome, chunkPos);
	}

	protected boolean shouldStartAt(
		BiomeAccess biomeAccess, ChunkGenerator<?> chunkGenerator, ChunkRandom chunkRandom, int chunkX, int chunkZ, Biome biome, ChunkPos chunkPos
	) {
		return true;
	}

	public abstract StructureFeature.StructureStartFactory getStructureStartFactory();

	public abstract String getName();

	public abstract int getRadius();

	public interface StructureStartFactory {
		StructureStart create(StructureFeature<?> feature, int x, int z, BlockBox box, int i, long l);
	}
}
