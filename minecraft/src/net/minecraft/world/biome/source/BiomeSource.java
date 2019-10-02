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
import net.minecraft.world.biome.BiomeAccess;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class BiomeSource implements BiomeAccess.Storage {
	private static final List<Biome> SPAWN_BIOMES = Lists.<Biome>newArrayList(
		Biomes.FOREST, Biomes.PLAINS, Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.WOODED_HILLS, Biomes.JUNGLE, Biomes.JUNGLE_HILLS
	);
	protected final Map<StructureFeature<?>, Boolean> structureFeatures = Maps.<StructureFeature<?>, Boolean>newHashMap();
	protected final Set<BlockState> topMaterials = Sets.<BlockState>newHashSet();
	protected final Set<Biome> biomes;

	protected BiomeSource(Set<Biome> set) {
		this.biomes = set;
	}

	public List<Biome> getSpawnBiomes() {
		return SPAWN_BIOMES;
	}

	public Set<Biome> getBiomesInArea(int i, int j, int k, int l) {
		int m = i - l >> 2;
		int n = j - l >> 2;
		int o = k - l >> 2;
		int p = i + l >> 2;
		int q = j + l >> 2;
		int r = k + l >> 2;
		int s = p - m + 1;
		int t = q - n + 1;
		int u = r - o + 1;
		Set<Biome> set = Sets.<Biome>newHashSet();

		for (int v = 0; v < u; v++) {
			for (int w = 0; w < s; w++) {
				for (int x = 0; x < t; x++) {
					int y = m + w;
					int z = n + x;
					int aa = o + v;
					set.add(this.getStoredBiome(y, z, aa));
				}
			}
		}

		return set;
	}

	@Nullable
	public BlockPos locateBiome(int i, int j, int k, int l, List<Biome> list, Random random) {
		int m = i - l >> 2;
		int n = k - l >> 2;
		int o = i + l >> 2;
		int p = k + l >> 2;
		int q = o - m + 1;
		int r = p - n + 1;
		int s = j >> 2;
		BlockPos blockPos = null;
		int t = 0;

		for (int u = 0; u < r; u++) {
			for (int v = 0; v < q; v++) {
				int w = m + v;
				int x = n + u;
				if (list.contains(this.getStoredBiome(w, s, x))) {
					if (blockPos == null || random.nextInt(t + 1) == 0) {
						blockPos = new BlockPos(w << 2, j, x << 2);
					}

					t++;
				}
			}
		}

		return blockPos;
	}

	public float getNoiseRange(int i, int j) {
		return 0.0F;
	}

	public boolean hasStructureFeature(StructureFeature<?> structureFeature) {
		return (Boolean)this.structureFeatures
			.computeIfAbsent(structureFeature, structureFeaturex -> this.biomes.stream().anyMatch(biome -> biome.hasStructureFeature(structureFeaturex)));
	}

	public Set<BlockState> getTopMaterials() {
		if (this.topMaterials.isEmpty()) {
			for (Biome biome : this.biomes) {
				this.topMaterials.add(biome.getSurfaceConfig().getTopMaterial());
			}
		}

		return this.topMaterials;
	}
}
