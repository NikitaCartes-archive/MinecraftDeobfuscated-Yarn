package net.minecraft.world.chunk.light;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;

public class LightingProvider implements LightingView {
	public static final int field_31713 = 15;
	public static final int field_31714 = 1;
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

	@Override
	public void checkBlock(BlockPos pos) {
		if (this.blockLightProvider != null) {
			this.blockLightProvider.checkBlock(pos);
		}

		if (this.skyLightProvider != null) {
			this.skyLightProvider.checkBlock(pos);
		}
	}

	@Override
	public void addLightSource(BlockPos pos, int level) {
		if (this.blockLightProvider != null) {
			this.blockLightProvider.addLightSource(pos, level);
		}
	}

	@Override
	public boolean hasUpdates() {
		return this.skyLightProvider != null && this.skyLightProvider.hasUpdates() ? true : this.blockLightProvider != null && this.blockLightProvider.hasUpdates();
	}

	@Override
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
	public void setSectionStatus(ChunkSectionPos pos, boolean notReady) {
		if (this.blockLightProvider != null) {
			this.blockLightProvider.setSectionStatus(pos, notReady);
		}

		if (this.skyLightProvider != null) {
			this.skyLightProvider.setSectionStatus(pos, notReady);
		}
	}

	@Override
	public void setColumnEnabled(ChunkPos chunkPos, boolean bl) {
		if (this.blockLightProvider != null) {
			this.blockLightProvider.setColumnEnabled(chunkPos, bl);
		}

		if (this.skyLightProvider != null) {
			this.skyLightProvider.setColumnEnabled(chunkPos, bl);
		}
	}

	public ChunkLightingView get(LightType lightType) {
		if (lightType == LightType.BLOCK) {
			return (ChunkLightingView)(this.blockLightProvider == null ? ChunkLightingView.Empty.INSTANCE : this.blockLightProvider);
		} else {
			return (ChunkLightingView)(this.skyLightProvider == null ? ChunkLightingView.Empty.INSTANCE : this.skyLightProvider);
		}
	}

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

	public int getHeight() {
		return this.world.countVerticalSections() + 2;
	}

	public int getBottomY() {
		return this.world.getBottomSectionCoord() - 1;
	}

	public int getTopY() {
		return this.getBottomY() + this.getHeight();
	}
}
