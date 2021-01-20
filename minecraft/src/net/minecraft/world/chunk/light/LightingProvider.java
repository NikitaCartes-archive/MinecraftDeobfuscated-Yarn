package net.minecraft.world.chunk.light;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;

public class LightingProvider implements LightingView {
	protected final HeightLimitView world;
	@Nullable
	private final ChunkLightProvider<?, ?> blockLightProvider;
	@Nullable
	private final ChunkLightProvider<?, ?> skyLightProvider;

	public LightingProvider(ChunkProvider chunkProvider, boolean hasBlockLight, boolean hasSkyLight) {
		this.world = chunkProvider.getWorld();
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
			this.blockLightProvider.addLightSource(pos, level);
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
	public void setSectionStatus(ChunkSectionPos pos, boolean notReady) {
		if (this.blockLightProvider != null) {
			this.blockLightProvider.setSectionStatus(pos, notReady);
		}

		if (this.skyLightProvider != null) {
			this.skyLightProvider.setSectionStatus(pos, notReady);
		}
	}

	public void setColumnEnabled(ChunkPos pos, boolean lightEnabled) {
		if (this.blockLightProvider != null) {
			this.blockLightProvider.setColumnEnabled(pos, lightEnabled);
		}

		if (this.skyLightProvider != null) {
			this.skyLightProvider.setColumnEnabled(pos, lightEnabled);
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
	public String displaySectionLevel(LightType lightType, ChunkSectionPos chunkSectionPos) {
		if (lightType == LightType.BLOCK) {
			if (this.blockLightProvider != null) {
				return this.blockLightProvider.displaySectionLevel(chunkSectionPos.asLong());
			}
		} else if (this.skyLightProvider != null) {
			return this.skyLightProvider.displaySectionLevel(chunkSectionPos.asLong());
		}

		return "n/a";
	}

	public void enqueueSectionData(LightType lightType, ChunkSectionPos pos, @Nullable ChunkNibbleArray nibbles, boolean bl) {
		if (lightType == LightType.BLOCK) {
			if (this.blockLightProvider != null) {
				this.blockLightProvider.enqueueSectionData(pos.asLong(), nibbles, bl);
			}
		} else if (this.skyLightProvider != null) {
			this.skyLightProvider.enqueueSectionData(pos.asLong(), nibbles, bl);
		}
	}

	public void setRetainData(ChunkPos pos, boolean retainData) {
		if (this.blockLightProvider != null) {
			this.blockLightProvider.setRetainColumn(pos, retainData);
		}

		if (this.skyLightProvider != null) {
			this.skyLightProvider.setRetainColumn(pos, retainData);
		}
	}

	public int getLight(BlockPos pos, int ambientDarkness) {
		int i = this.skyLightProvider == null ? 0 : this.skyLightProvider.getLightLevel(pos) - ambientDarkness;
		int j = this.blockLightProvider == null ? 0 : this.blockLightProvider.getLightLevel(pos);
		return Math.max(j, i);
	}

	public int method_31928() {
		return this.world.getSections() + 2;
	}

	public int method_31929() {
		return this.world.getMinimumSection() - 1;
	}

	public int method_31930() {
		return this.method_31929() + this.method_31928();
	}
}
