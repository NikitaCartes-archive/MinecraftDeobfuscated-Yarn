package net.minecraft.world.biome.source;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class BiomeSource {
	private static final List<Biome> SPAWN_BIOMES = Lists.<Biome>newArrayList(
		Biomes.FOREST, Biomes.PLAINS, Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.WOODED_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_HILLS
	);
	protected final Map<StructureFeature<?>, Boolean> structureFeatures = Maps.<StructureFeature<?>, Boolean>newHashMap();
	protected final Set<BlockState> topMaterials = Sets.<BlockState>newHashSet();

	protected BiomeSource() {
	}

	public List<Biome> getSpawnBiomes() {
		return SPAWN_BIOMES;
	}

	public Biome getBiome(BlockPos pos) {
		return this.getBiome(pos.getX(), pos.getZ());
	}

	public abstract Biome getBiome(int x, int z);

	public Biome getBiomeForNoiseGen(int x, int z) {
		return this.getBiome(x << 2, z << 2);
	}

	public Biome[] sampleBiomes(int x, int z, int width, int height) {
		return this.sampleBiomes(x, z, width, height, true);
	}

	public abstract Biome[] sampleBiomes(int x, int z, int width, int height, boolean bl);

	public abstract Set<Biome> getBiomesInArea(int x, int z, int radius);

	@Nullable
	public abstract BlockPos locateBiome(int x, int z, int radius, List<Biome> biomes, Random random);

	public float method_8757(int i, int j) {
		return 0.0F;
	}

	public abstract boolean hasStructureFeature(StructureFeature<?> feature);

	public abstract Set<BlockState> getTopMaterials();
}
