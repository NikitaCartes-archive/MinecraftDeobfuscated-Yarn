package net.minecraft.world.biome.source;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.StructureFeature;

public class FixedBiomeSource extends BiomeSource {
	private final Biome biome;

	public FixedBiomeSource(FixedBiomeSourceConfig fixedBiomeSourceConfig) {
		this.biome = fixedBiomeSourceConfig.getBiome();
	}

	@Override
	public Biome getBiome(int i, int j) {
		return this.biome;
	}

	@Override
	public Biome[] sampleBiomes(int i, int j, int k, int l, boolean bl) {
		Biome[] biomes = new Biome[k * l];
		Arrays.fill(biomes, 0, k * l, this.biome);
		return biomes;
	}

	@Nullable
	@Override
	public BlockPos locateBiome(int i, int j, int k, List<Biome> list, Random random) {
		return list.contains(this.biome) ? new BlockPos(i - k + random.nextInt(k * 2 + 1), 0, j - k + random.nextInt(k * 2 + 1)) : null;
	}

	@Override
	public boolean method_8754(StructureFeature<?> structureFeature) {
		return (Boolean)this.structureFeatures.computeIfAbsent(structureFeature, this.biome::method_8684);
	}

	@Override
	public Set<BlockState> getTopMaterials() {
		if (this.topMaterials.isEmpty()) {
			this.topMaterials.add(this.biome.method_8722().getTopMaterial());
		}

		return this.topMaterials;
	}

	@Override
	public Set<Biome> getBiomesInArea(int i, int j, int k) {
		return Sets.<Biome>newHashSet(this.biome);
	}
}
