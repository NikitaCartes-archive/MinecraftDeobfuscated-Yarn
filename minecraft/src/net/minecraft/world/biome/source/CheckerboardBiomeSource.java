package net.minecraft.world.biome.source;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.StructureFeature;

public class CheckerboardBiomeSource extends BiomeSource {
	private final Biome[] field_9481;
	private final int field_9480;

	public CheckerboardBiomeSource(CheckerboardBiomeSourceConfig checkerboardBiomeSourceConfig) {
		this.field_9481 = checkerboardBiomeSourceConfig.method_8779();
		this.field_9480 = checkerboardBiomeSourceConfig.method_8778() + 4;
	}

	@Override
	public Biome getBiome(int i, int j) {
		return this.field_9481[Math.abs(((i >> this.field_9480) + (j >> this.field_9480)) % this.field_9481.length)];
	}

	@Override
	public Biome[] sampleBiomes(int i, int j, int k, int l, boolean bl) {
		Biome[] biomes = new Biome[k * l];

		for (int m = 0; m < l; m++) {
			for (int n = 0; n < k; n++) {
				int o = Math.abs(((i + m >> this.field_9480) + (j + n >> this.field_9480)) % this.field_9481.length);
				Biome biome = this.field_9481[o];
				biomes[m * k + n] = biome;
			}
		}

		return biomes;
	}

	@Nullable
	@Override
	public BlockPos locateBiome(int i, int j, int k, List<Biome> list, Random random) {
		return null;
	}

	@Override
	public boolean hasStructureFeature(StructureFeature<?> structureFeature) {
		return (Boolean)this.structureFeatures.computeIfAbsent(structureFeature, structureFeaturex -> {
			for (Biome biome : this.field_9481) {
				if (biome.hasStructureFeature(structureFeaturex)) {
					return true;
				}
			}

			return false;
		});
	}

	@Override
	public Set<BlockState> getTopMaterials() {
		if (this.topMaterials.isEmpty()) {
			for (Biome biome : this.field_9481) {
				this.topMaterials.add(biome.method_8722().getTopMaterial());
			}
		}

		return this.topMaterials;
	}

	@Override
	public Set<Biome> getBiomesInArea(int i, int j, int k) {
		return Sets.<Biome>newHashSet(this.field_9481);
	}
}
