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

	public LightingProvider(ChunkProvider chunkProvider, boolean bl, boolean bl2) {
		this.blockLightProvider = bl ? new ChunkBlockLightProvider(chunkProvider) : null;
		this.skyLightProvider = bl2 ? new ChunkSkyLightProvider(chunkProvider) : null;
	}

	public void enqueueLightUpdate(BlockPos blockPos) {
		if (this.blockLightProvider != null) {
			this.blockLightProvider.queueLightCheck(blockPos);
		}

		if (this.skyLightProvider != null) {
			this.skyLightProvider.queueLightCheck(blockPos);
		}
	}

	public void method_15560(BlockPos blockPos, int i) {
		if (this.blockLightProvider != null) {
			this.blockLightProvider.method_15514(blockPos, i);
		}
	}

	public boolean hasUpdates() {
		return this.skyLightProvider != null && this.skyLightProvider.hasUpdates() ? true : this.blockLightProvider != null && this.blockLightProvider.hasUpdates();
	}

	public int doLightUpdates(int i, boolean bl, boolean bl2) {
		if (this.blockLightProvider != null && this.skyLightProvider != null) {
			int j = i / 2;
			int k = this.blockLightProvider.doLightUpdates(j, bl, bl2);
			int l = i - j + k;
			int m = this.skyLightProvider.doLightUpdates(l, bl, bl2);
			return k == 0 && m > 0 ? this.blockLightProvider.doLightUpdates(m, bl, bl2) : m;
		} else if (this.blockLightProvider != null) {
			return this.blockLightProvider.doLightUpdates(i, bl, bl2);
		} else {
			return this.skyLightProvider != null ? this.skyLightProvider.doLightUpdates(i, bl, bl2) : i;
		}
	}

	@Override
	public void updateSectionStatus(ChunkSectionPos chunkSectionPos, boolean bl) {
		if (this.blockLightProvider != null) {
			this.blockLightProvider.updateSectionStatus(chunkSectionPos, bl);
		}

		if (this.skyLightProvider != null) {
			this.skyLightProvider.updateSectionStatus(chunkSectionPos, bl);
		}
	}

	public void suppressLight(ChunkPos chunkPos, boolean bl) {
		if (this.blockLightProvider != null) {
			this.blockLightProvider.method_15512(chunkPos, bl);
		}

		if (this.skyLightProvider != null) {
			this.skyLightProvider.method_15512(chunkPos, bl);
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
				this.blockLightProvider.setSection(chunkSectionPos.asLong(), chunkNibbleArray);
			}
		} else if (this.skyLightProvider != null) {
			this.skyLightProvider.setSection(chunkSectionPos.asLong(), chunkNibbleArray);
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
