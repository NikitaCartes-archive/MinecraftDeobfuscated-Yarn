package net.minecraft.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public interface ExtendedBlockView extends BlockView {
	Biome getBiome(BlockPos blockPos);

	int getLightLevel(LightType lightType, BlockPos blockPos);

	default boolean getSkyLightLevel(BlockPos blockPos) {
		return this.getLightLevel(LightType.field_9284, blockPos) >= this.getMaxLightLevel();
	}

	@Environment(EnvType.CLIENT)
	default int method_8312(LightType lightType, BlockPos blockPos) {
		if (this.getBlockState(blockPos).method_11593(this, blockPos)) {
			int i = this.getLightLevel(lightType, blockPos.up());
			int j = this.getLightLevel(lightType, blockPos.east());
			int k = this.getLightLevel(lightType, blockPos.west());
			int l = this.getLightLevel(lightType, blockPos.south());
			int m = this.getLightLevel(lightType, blockPos.north());
			if (j > i) {
				i = j;
			}

			if (k > i) {
				i = k;
			}

			if (l > i) {
				i = l;
			}

			if (m > i) {
				i = m;
			}

			return i;
		} else {
			return this.getLightLevel(lightType, blockPos);
		}
	}

	@Environment(EnvType.CLIENT)
	default int getLightmapIndex(BlockPos blockPos, int i) {
		int j = this.method_8312(LightType.field_9284, blockPos);
		int k = this.method_8312(LightType.field_9282, blockPos);
		if (k < i) {
			k = i;
		}

		return j << 20 | k << 4;
	}
}
