package net.minecraft.world.chunk.light;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;

public class LightingProvider implements LightingView {
	@Nullable
	private final ChunkLightProvider<?, ?> blockLightProvider;
	@Nullable
	private final ChunkLightProvider<?, ?> skyLightProvider;

	public LightingProvider(ChunkProvider chunkProvider, boolean hasBlockLight, boolean hasSkyLight) {
		this.blockLightProvider = hasBlockLight ? new ChunkBlockLightProvider(chunkProvider) : null;
		this.skyLightProvider = hasSkyLight ? new ChunkSkyLightProvider(chunkProvider) : null;
	}

	public void checkBlock(BlockPos pos) {
		if (this.blockLightProvider != null) {
			this.blockLightProvider.checkBlock(pos);
		}

		if (this.skyLightProvider != null) {
			this.skyLightProvider.checkBlock(pos);
		}
	}

	public void addLightSource(BlockPos pos, int level) {
		if (this.blockLightProvider != null) {
			this.blockLightProvider.method_15514(pos, level);
		}
	}

	public boolean hasUpdates() {
		return this.skyLightProvider != null && this.skyLightProvider.hasUpdates() ? true : this.blockLightProvider != null && this.blockLightProvider.hasUpdates();
	}

	public int doLightUpdates(int maxUpdateCount, boolean doSkylight, boolean skipEdgeLightPropagation) {
		if (this.blockLightProvider != null && this.skyLightProvider != null) {
			int i = maxUpdateCount / 2;
			int j = this.blockLightProvider.doLightUpdates(i, doSkylight, skipEdgeLightPropagation);
			int k = maxUpdateCount - i + j;
			int l = this.skyLightProvider.doLightUpdates(k, doSkylight, skipEdgeLightPropagation);
			return j == 0 && l > 0 ? this.blockLightProvider.doLightUpdates(l, doSkylight, skipEdgeLightPropagation) : l;
		} else if (this.blockLightProvider != null) {
			return this.blockLightProvider.doLightUpdates(maxUpdateCount, doSkylight, skipEdgeLightPropagation);
		} else {
			return this.skyLightProvider != null ? this.skyLightProvider.doLightUpdates(maxUpdateCount, doSkylight, skipEdgeLightPropagation) : maxUpdateCount;
		}
	}

	@Override
	public void updateSectionStatus(ChunkSectionPos pos, boolean status) {
		if (this.blockLightProvider != null) {
			this.blockLightProvider.updateSectionStatus(pos, status);
		}

		if (this.skyLightProvider != null) {
			this.skyLightProvider.updateSectionStatus(pos, status);
		}
	}

	public void setLightEnabled(ChunkPos pos, boolean lightEnabled) {
		if (this.blockLightProvider != null) {
			this.blockLightProvider.method_15512(pos, lightEnabled);
		}

		if (this.skyLightProvider != null) {
			this.skyLightProvider.method_15512(pos, lightEnabled);
		}
	}

	public ChunkLightingView get(LightType lightType) {
		if (lightType == LightType.BLOCK) {
			return (ChunkLightingView)(this.blockLightProvider == null ? ChunkLightingView.Empty.INSTANCE : this.blockLightProvider);
		} else {
			return (ChunkLightingView)(this.skyLightProvider == null ? ChunkLightingView.Empty.INSTANCE : this.skyLightProvider);
		}
	}

	@Environment(EnvType.CLIENT)
	public String method_15564(LightType lightType, ChunkSectionPos chunkSectionPos) {
		if (lightType == LightType.BLOCK) {
			if (this.blockLightProvider != null) {
				return this.blockLightProvider.method_15520(chunkSectionPos.asLong());
			}
		} else if (this.skyLightProvider != null) {
			return this.skyLightProvider.method_15520(chunkSectionPos.asLong());
		}

		return "n/a";
	}

	public void queueData(LightType lightType, ChunkSectionPos chunkSectionPos, @Nullable ChunkNibbleArray chunkNibbleArray) {
		if (lightType == LightType.BLOCK) {
			if (this.blockLightProvider != null) {
				this.blockLightProvider.setLightArray(chunkSectionPos.asLong(), chunkNibbleArray);
			}
		} else if (this.skyLightProvider != null) {
			this.skyLightProvider.setLightArray(chunkSectionPos.asLong(), chunkNibbleArray);
		}
	}

	public void method_20601(ChunkPos chunkPos, boolean bl) {
		if (this.blockLightProvider != null) {
			this.blockLightProvider.method_20599(chunkPos, bl);
		}

		if (this.skyLightProvider != null) {
			this.skyLightProvider.method_20599(chunkPos, bl);
		}
	}
}
