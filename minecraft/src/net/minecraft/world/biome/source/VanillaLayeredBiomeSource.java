package net.minecraft.world.biome.source;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.BiomeLayerSampler;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.level.LevelProperties;

public class VanillaLayeredBiomeSource extends BiomeSource {
	private final BiomeLayerSampler noiseLayer;
	private final BiomeLayerSampler biomeLayer;
	private final Biome[] biomes = new Biome[]{
		Biomes.field_9423,
		Biomes.field_9451,
		Biomes.field_9424,
		Biomes.field_9472,
		Biomes.field_9409,
		Biomes.field_9420,
		Biomes.field_9471,
		Biomes.field_9438,
		Biomes.field_9435,
		Biomes.field_9463,
		Biomes.field_9452,
		Biomes.field_9444,
		Biomes.field_9462,
		Biomes.field_9407,
		Biomes.field_9434,
		Biomes.field_9466,
		Biomes.field_9459,
		Biomes.field_9428,
		Biomes.field_9464,
		Biomes.field_9417,
		Biomes.field_9432,
		Biomes.field_9474,
		Biomes.field_9446,
		Biomes.field_9419,
		Biomes.field_9478,
		Biomes.field_9412,
		Biomes.field_9421,
		Biomes.field_9475,
		Biomes.field_9454,
		Biomes.field_9425,
		Biomes.field_9477,
		Biomes.field_9429,
		Biomes.field_9460,
		Biomes.field_9449,
		Biomes.field_9430,
		Biomes.field_9415,
		Biomes.field_9410,
		Biomes.field_9433,
		Biomes.field_9408,
		Biomes.field_9441,
		Biomes.field_9467,
		Biomes.field_9448,
		Biomes.field_9439,
		Biomes.field_9470,
		Biomes.field_9418,
		Biomes.field_9455,
		Biomes.field_9427,
		Biomes.field_9476,
		Biomes.field_9414,
		Biomes.field_9422,
		Biomes.field_9479,
		Biomes.field_9453,
		Biomes.field_9426,
		Biomes.field_9405,
		Biomes.field_9431,
		Biomes.field_9458,
		Biomes.field_9450,
		Biomes.field_9437,
		Biomes.field_9416,
		Biomes.field_9404,
		Biomes.field_9436,
		Biomes.field_9456,
		Biomes.field_9445,
		Biomes.field_9443,
		Biomes.field_9413,
		Biomes.field_9406
	};

	public VanillaLayeredBiomeSource(VanillaLayeredBiomeSourceConfig vanillaLayeredBiomeSourceConfig) {
		LevelProperties levelProperties = vanillaLayeredBiomeSourceConfig.getLevelProperties();
		OverworldChunkGeneratorConfig overworldChunkGeneratorConfig = vanillaLayeredBiomeSourceConfig.getGeneratorSettings();
		BiomeLayerSampler[] biomeLayerSamplers = BiomeLayers.build(levelProperties.getSeed(), levelProperties.getGeneratorType(), overworldChunkGeneratorConfig);
		this.noiseLayer = biomeLayerSamplers[0];
		this.biomeLayer = biomeLayerSamplers[1];
	}

	@Override
	public Biome getBiome(int i, int j) {
		return this.biomeLayer.sample(i, j);
	}

	@Override
	public Biome getBiomeForNoiseGen(int i, int j) {
		return this.noiseLayer.sample(i, j);
	}

	@Override
	public Biome[] sampleBiomes(int i, int j, int k, int l, boolean bl) {
		return this.biomeLayer.sample(i, j, k, l);
	}

	@Override
	public Set<Biome> getBiomesInArea(int i, int j, int k) {
		int l = i - k >> 2;
		int m = j - k >> 2;
		int n = i + k >> 2;
		int o = j + k >> 2;
		int p = n - l + 1;
		int q = o - m + 1;
		Set<Biome> set = Sets.<Biome>newHashSet();
		Collections.addAll(set, this.noiseLayer.sample(l, m, p, q));
		return set;
	}

	@Nullable
	@Override
	public BlockPos locateBiome(int i, int j, int k, List<Biome> list, Random random) {
		int l = i - k >> 2;
		int m = j - k >> 2;
		int n = i + k >> 2;
		int o = j + k >> 2;
		int p = n - l + 1;
		int q = o - m + 1;
		Biome[] biomes = this.noiseLayer.sample(l, m, p, q);
		BlockPos blockPos = null;
		int r = 0;

		for (int s = 0; s < p * q; s++) {
			int t = l + s % p << 2;
			int u = m + s / p << 2;
			if (list.contains(biomes[s])) {
				if (blockPos == null || random.nextInt(r + 1) == 0) {
					blockPos = new BlockPos(t, 0, u);
				}

				r++;
			}
		}

		return blockPos;
	}

	@Override
	public boolean hasStructureFeature(StructureFeature<?> structureFeature) {
		return (Boolean)this.structureFeatures.computeIfAbsent(structureFeature, structureFeaturex -> {
			for (Biome biome : this.biomes) {
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
			for (Biome biome : this.biomes) {
				this.topMaterials.add(biome.getSurfaceConfig().getTopMaterial());
			}
		}

		return this.topMaterials;
	}
}
