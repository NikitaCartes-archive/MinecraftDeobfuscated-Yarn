package net.minecraft.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public interface ExtendedBlockView extends BlockView {
	Biome method_8310(BlockPos blockPos);

	int method_8314(LightType lightType, BlockPos blockPos);

	default boolean method_8311(BlockPos blockPos) {
		return this.method_8314(LightType.SKY, blockPos) >= this.getMaxLightLevel();
	}

	@Environment(EnvType.CLIENT)
	default int method_8312(LightType lightType, BlockPos blockPos) {
		if (this.method_8320(blockPos).method_11593(this, blockPos)) {
			int i = this.method_8314(lightType, blockPos.up());
			int j = this.method_8314(lightType, blockPos.east());
			int k = this.method_8314(lightType, blockPos.west());
			int l = this.method_8314(lightType, blockPos.south());
			int m = this.method_8314(lightType, blockPos.north());
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
			return this.method_8314(lightType, blockPos);
		}
	}

	@Environment(EnvType.CLIENT)
	default int method_8313(BlockPos blockPos, int i) {
		int j = this.method_8312(LightType.SKY, blockPos);
		int k = this.method_8312(LightType.BLOCK, blockPos);
		if (k < i) {
			k = i;
		}

		return j << 20 | k << 4;
	}
}
