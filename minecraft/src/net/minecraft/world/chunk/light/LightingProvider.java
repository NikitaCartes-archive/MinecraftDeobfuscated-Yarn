package net.minecraft.world.chunk.light;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4076;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkPos;
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

	public boolean method_15561() {
		return this.skyLightProvider != null && this.skyLightProvider.method_15518()
			? true
			: this.blockLightProvider != null && this.blockLightProvider.method_15518();
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
	public void scheduleChunkLightUpdate(class_4076 arg, boolean bl) {
		if (this.blockLightProvider != null) {
			this.blockLightProvider.scheduleChunkLightUpdate(arg, bl);
		}

		if (this.skyLightProvider != null) {
			this.skyLightProvider.scheduleChunkLightUpdate(arg, bl);
		}
	}

	public void method_15557(ChunkPos chunkPos, boolean bl) {
		if (this.blockLightProvider != null) {
			this.blockLightProvider.method_15512(chunkPos, bl);
		}

		if (this.skyLightProvider != null) {
			this.skyLightProvider.method_15512(chunkPos, bl);
		}
	}

	public ChunkLightingView get(LightType lightType) {
		if (lightType == LightType.BLOCK) {
			return (ChunkLightingView)(this.blockLightProvider == null ? ChunkLightingView.Empty.field_15812 : this.blockLightProvider);
		} else {
			return (ChunkLightingView)(this.skyLightProvider == null ? ChunkLightingView.Empty.field_15812 : this.skyLightProvider);
		}
	}

	@Environment(EnvType.CLIENT)
	public String method_15564(LightType lightType, BlockPos blockPos) {
		if (lightType == LightType.BLOCK) {
			if (this.blockLightProvider != null) {
				return this.blockLightProvider.method_15520(class_4076.method_18691(blockPos.asLong()));
			}
		} else if (this.skyLightProvider != null) {
			return this.skyLightProvider.method_15520(class_4076.method_18691(blockPos.asLong()));
		}

		return "n/a";
	}

	public void setSection(LightType lightType, class_4076 arg, ChunkNibbleArray chunkNibbleArray) {
		if (lightType == LightType.BLOCK) {
			if (this.blockLightProvider != null) {
				this.blockLightProvider.setSection(arg.method_18694(), chunkNibbleArray);
			}
		} else if (this.skyLightProvider != null) {
			this.skyLightProvider.setSection(arg.method_18694(), chunkNibbleArray);
		}
	}
}
