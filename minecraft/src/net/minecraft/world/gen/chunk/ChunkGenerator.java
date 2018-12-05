package net.minecraft.world.gen.chunk;

import java.util.BitSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.class_2919;
import net.minecraft.class_3233;
import net.minecraft.class_3449;
import net.minecraft.class_3485;
import net.minecraft.entity.EntityCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.config.feature.FeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class ChunkGenerator<C extends ChunkGeneratorSettings> {
	protected final IWorld world;
	protected final long seed;
	protected final BiomeSource biomeSource;
	protected final C settings;

	public ChunkGenerator(IWorld iWorld, BiomeSource biomeSource, C chunkGeneratorSettings) {
		this.world = iWorld;
		this.seed = iWorld.getSeed();
		this.biomeSource = biomeSource;
		this.settings = chunkGeneratorSettings;
	}

	public void populateBiomes(Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		int i = chunkPos.x;
		int j = chunkPos.z;
		Biome[] biomes = this.biomeSource.method_8756(i * 16, j * 16, 16, 16);
		chunk.setBiomes(biomes);
	}

	protected Biome getDecorationBiome(Chunk chunk) {
		return chunk.getBiome(BlockPos.ORIGIN);
	}

	protected Biome method_16554(class_3233 arg, int i, int j) {
		return arg.getChunk(i + 1, j + 1).getBiomeArray()[0];
	}

	public void carve(Chunk chunk, GenerationStep.Carver carver) {
		class_2919 lv = new class_2919();
		int i = 8;
		ChunkPos chunkPos = chunk.getPos();
		int j = chunkPos.x;
		int k = chunkPos.z;
		BitSet bitSet = chunk.method_12025(carver);

		for (int l = j - 8; l <= j + 8; l++) {
			for (int m = k - 8; m <= k + 8; m++) {
				List<ConfiguredCarver<?>> list = this.getDecorationBiome(chunk).getCarversForStep(carver);
				ListIterator<ConfiguredCarver<?>> listIterator = list.listIterator();

				while (listIterator.hasNext()) {
					int n = listIterator.nextIndex();
					ConfiguredCarver<?> configuredCarver = (ConfiguredCarver<?>)listIterator.next();
					lv.method_12663(this.seed + (long)n, l, m);
					if (configuredCarver.method_12669(lv, l, m)) {
						configuredCarver.method_12668(chunk, lv, this.method_16398(), l, m, j, k, bitSet);
					}
				}
			}
		}
	}

	@Nullable
	public BlockPos locateStructure(World world, String string, BlockPos blockPos, int i, boolean bl) {
		StructureFeature<?> structureFeature = (StructureFeature<?>)Feature.STRUCTURES.get(string.toLowerCase(Locale.ROOT));
		return structureFeature != null ? structureFeature.locateStructure(world, this, blockPos, i, bl) : null;
	}

	public void generateFeatures(class_3233 arg) {
		int i = arg.method_14336();
		int j = arg.method_14339();
		int k = i * 16;
		int l = j * 16;
		BlockPos blockPos = new BlockPos(k, 0, l);
		Biome biome = this.method_16554(arg, i, j);
		class_2919 lv = new class_2919();
		long m = lv.method_12661(arg.getSeed(), k, l);

		for (GenerationStep.Feature feature : GenerationStep.Feature.values()) {
			biome.generateFeatureStep(feature, this, arg, m, lv, blockPos);
		}
	}

	public abstract void buildSurface(Chunk chunk);

	public void populateEntities(class_3233 arg) {
	}

	public C getSettings() {
		return this.settings;
	}

	public abstract int method_12100();

	public void method_12099(World world, boolean bl, boolean bl2) {
	}

	public boolean hasStructure(Biome biome, StructureFeature<? extends FeatureConfig> structureFeature) {
		return biome.hasStructureFeature(structureFeature);
	}

	@Nullable
	public <C extends FeatureConfig> C getStructureConfig(Biome biome, StructureFeature<C> structureFeature) {
		return biome.getStructureFeatureConfig(structureFeature);
	}

	public BiomeSource getBiomeSource() {
		return this.biomeSource;
	}

	public long getSeed() {
		return this.seed;
	}

	public int method_12104() {
		return 256;
	}

	public List<Biome.SpawnEntry> getEntitySpawnList(EntityCategory entityCategory, BlockPos blockPos) {
		return this.world.getBiome(blockPos).getEntitySpawnList(entityCategory);
	}

	public void method_16129(Chunk chunk, ChunkGenerator<?> chunkGenerator, class_3485 arg) {
		for (StructureFeature<?> structureFeature : Feature.STRUCTURES.values()) {
			if (chunkGenerator.getBiomeSource().hasStructureFeature(structureFeature)) {
				class_2919 lv = new class_2919();
				ChunkPos chunkPos = chunk.getPos();
				class_3449 lv2 = class_3449.field_16713;
				if (structureFeature.method_14026(chunkGenerator, lv, chunkPos.x, chunkPos.z)) {
					Biome biome = this.getBiomeSource().method_8758(new BlockPos(chunkPos.getXStart() + 9, 0, chunkPos.getZStart() + 9));
					class_3449 lv3 = structureFeature.method_14016()
						.create(structureFeature, chunkPos.x, chunkPos.z, biome, MutableIntBoundingBox.maxSize(), 0, chunkGenerator.getSeed());
					lv3.method_16655(this, arg, chunkPos.x, chunkPos.z, biome);
					lv2 = lv3.hasChildren() ? lv3 : class_3449.field_16713;
				}

				chunk.method_12184(structureFeature.getName(), lv2);
			}
		}
	}

	public void method_16130(IWorld iWorld, Chunk chunk) {
		int i = 8;
		int j = chunk.getPos().x;
		int k = chunk.getPos().z;
		int l = j << 4;
		int m = k << 4;

		for (int n = j - 8; n <= j + 8; n++) {
			for (int o = k - 8; o <= k + 8; o++) {
				long p = ChunkPos.toLong(n, o);

				for (Entry<String, class_3449> entry : iWorld.getChunk(n, o).method_12016().entrySet()) {
					class_3449 lv = (class_3449)entry.getValue();
					if (lv != class_3449.field_16713 && lv.method_14968().intersectsXZ(l, m, l + 15, m + 15)) {
						chunk.method_12182((String)entry.getKey(), p);
					}
				}
			}
		}
	}

	public abstract void populateNoise(IWorld iWorld, Chunk chunk);

	public int method_16398() {
		return 63;
	}

	public abstract int produceHeight(int i, int j, Heightmap.Type type);
}
