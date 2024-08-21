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
	public static final int field_31714 = 1;
	public static final LightingProvider DEFAULT = new LightingProvider();
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

	private LightingProvider() {
		this.world = HeightLimitView.create(0, 0);
		this.blockLightProvider = null;
		this.skyLightProvider = null;
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
	public boolean hasUpdates() {
		return this.skyLightProvider != null && this.skyLightProvider.hasUpdates() ? true : this.blockLightProvider != null && this.blockLightProvider.hasUpdates();
	}

	@Override
	public int doLightUpdates() {
		int i = 0;
		if (this.blockLightProvider != null) {
			i += this.blockLightProvider.doLightUpdates();
		}

		if (this.skyLightProvider != null) {
			i += this.skyLightProvider.doLightUpdates();
		}

		return i;
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
	public void setColumnEnabled(ChunkPos pos, boolean retainData) {
		if (this.blockLightProvider != null) {
			this.blockLightProvider.setColumnEnabled(pos, retainData);
		}

		if (this.skyLightProvider != null) {
			this.skyLightProvider.setColumnEnabled(pos, retainData);
		}
	}

	@Override
	public void propagateLight(ChunkPos chunkPos) {
		if (this.blockLightProvider != null) {
			this.blockLightProvider.propagateLight(chunkPos);
		}

		if (this.skyLightProvider != null) {
			this.skyLightProvider.propagateLight(chunkPos);
		}
	}

	public ChunkLightingView get(LightType lightType) {
		if (lightType == LightType.BLOCK) {
			return (ChunkLightingView)(this.blockLightProvider == null ? ChunkLightingView.Empty.INSTANCE : this.blockLightProvider);
		} else {
			return (ChunkLightingView)(this.skyLightProvider == null ? ChunkLightingView.Empty.INSTANCE : this.skyLightProvider);
		}
	}

	public String displaySectionLevel(LightType lightType, ChunkSectionPos pos) {
		if (lightType == LightType.BLOCK) {
			if (this.blockLightProvider != null) {
				return this.blockLightProvider.displaySectionLevel(pos.asLong());
			}
		} else if (this.skyLightProvider != null) {
			return this.skyLightProvider.displaySectionLevel(pos.asLong());
		}

		return "n/a";
	}

	public LightStorage.Status getStatus(LightType lightType, ChunkSectionPos pos) {
		if (lightType == LightType.BLOCK) {
			if (this.blockLightProvider != null) {
				return this.blockLightProvider.getStatus(pos.asLong());
			}
		} else if (this.skyLightProvider != null) {
			return this.skyLightProvider.getStatus(pos.asLong());
		}

		return LightStorage.Status.EMPTY;
	}

	public void enqueueSectionData(LightType lightType, ChunkSectionPos pos, @Nullable ChunkNibbleArray nibbles) {
		if (lightType == LightType.BLOCK) {
			if (this.blockLightProvider != null) {
				this.blockLightProvider.enqueueSectionData(pos.asLong(), nibbles);
			}
		} else if (this.skyLightProvider != null) {
			this.skyLightProvider.enqueueSectionData(pos.asLong(), nibbles);
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

	public boolean isLightingEnabled(long sectionPos) {
		return this.blockLightProvider == null
			|| this.blockLightProvider.lightStorage.isColumnEnabled(sectionPos)
				&& (this.skyLightProvider == null || this.skyLightProvider.lightStorage.isColumnEnabled(sectionPos));
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
