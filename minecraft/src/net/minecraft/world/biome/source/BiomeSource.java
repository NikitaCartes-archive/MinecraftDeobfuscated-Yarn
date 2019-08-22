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

	public Biome getBiome(BlockPos blockPos) {
		return this.getBiome(blockPos.getX(), blockPos.getZ());
	}

	public abstract Biome getBiome(int i, int j);

	public Biome getBiomeForNoiseGen(int i, int j) {
		return this.getBiome(i << 2, j << 2);
	}

	public Biome[] sampleBiomes(int i, int j, int k, int l) {
		return this.sampleBiomes(i, j, k, l, true);
	}

	public abstract Biome[] sampleBiomes(int i, int j, int k, int l, boolean bl);

	public abstract Set<Biome> getBiomesInArea(int i, int j, int k);

	@Nullable
	public abstract BlockPos locateBiome(int i, int j, int k, List<Biome> list, Random random);

	public float method_8757(int i, int j) {
		return 0.0F;
	}

	public abstract boolean hasStructureFeature(StructureFeature<?> structureFeature);

	public abstract Set<BlockState> getTopMaterials();
}
