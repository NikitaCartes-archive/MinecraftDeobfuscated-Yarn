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
	private final Biome field_9486;

	public FixedBiomeSource(FixedBiomeSourceConfig fixedBiomeSourceConfig) {
		this.field_9486 = fixedBiomeSourceConfig.method_8781();
	}

	@Override
	public Biome method_16359(int i, int j) {
		return this.field_9486;
	}

	@Override
	public Biome[] method_8760(int i, int j, int k, int l, boolean bl) {
		Biome[] biomes = new Biome[k * l];
		Arrays.fill(biomes, 0, k * l, this.field_9486);
		return biomes;
	}

	@Nullable
	@Override
	public BlockPos method_8762(int i, int j, int k, List<Biome> list, Random random) {
		return list.contains(this.field_9486) ? new BlockPos(i - k + random.nextInt(k * 2 + 1), 0, j - k + random.nextInt(k * 2 + 1)) : null;
	}

	@Override
	public boolean hasStructureFeature(StructureFeature<?> structureFeature) {
		return (Boolean)this.STRUCTURE_FEATURES.computeIfAbsent(structureFeature, this.field_9486::hasStructureFeature);
	}

	@Override
	public Set<BlockState> getTopMaterials() {
		if (this.topMaterials.isEmpty()) {
			this.topMaterials.add(this.field_9486.getSurfaceConfig().getTopMaterial());
		}

		return this.topMaterials;
	}

	@Override
	public Set<Biome> method_8763(int i, int j, int k) {
		return Sets.<Biome>newHashSet(this.field_9486);
	}
}
