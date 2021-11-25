package net.minecraft;

import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.render.chunk.RenderedChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

@Environment(EnvType.CLIENT)
public class class_6850 {
	private final Long2ObjectMap<class_6850.class_6851> field_36314 = new Long2ObjectOpenHashMap<>();

	@Nullable
	public ChunkRendererRegion method_39969(World world, BlockPos blockPos, BlockPos blockPos2, int i) {
		int j = ChunkSectionPos.getSectionCoord(blockPos.getX() - i);
		int k = ChunkSectionPos.getSectionCoord(blockPos.getZ() - i);
		int l = ChunkSectionPos.getSectionCoord(blockPos2.getX() + i);
		int m = ChunkSectionPos.getSectionCoord(blockPos2.getZ() + i);
		class_6850.class_6851[][] lvs = new class_6850.class_6851[l - j + 1][m - k + 1];

		for (int n = j; n <= l; n++) {
			for (int o = k; o <= m; o++) {
				lvs[n - j][o - k] = this.field_36314
					.computeIfAbsent(
						ChunkPos.toLong(n, o),
						(Long2ObjectFunction<? extends class_6850.class_6851>)(lx -> new class_6850.class_6851(world.getChunk(ChunkPos.getPackedX(lx), ChunkPos.getPackedZ(lx))))
					);
			}
		}

		if (method_39970(blockPos, blockPos2, j, k, lvs)) {
			return null;
		} else {
			RenderedChunk[][] renderedChunks = new RenderedChunk[l - j + 1][m - k + 1];

			for (int o = j; o <= l; o++) {
				for (int p = k; p <= m; p++) {
					renderedChunks[o - j][p - k] = lvs[o - j][p - k].method_39972();
				}
			}

			return new ChunkRendererRegion(world, j, k, renderedChunks);
		}
	}

	private static boolean method_39970(BlockPos blockPos, BlockPos blockPos2, int i, int j, class_6850.class_6851[][] args) {
		int k = ChunkSectionPos.getSectionCoord(blockPos.getX());
		int l = ChunkSectionPos.getSectionCoord(blockPos.getZ());
		int m = ChunkSectionPos.getSectionCoord(blockPos2.getX());
		int n = ChunkSectionPos.getSectionCoord(blockPos2.getZ());

		for (int o = k; o <= m; o++) {
			for (int p = l; p <= n; p++) {
				WorldChunk worldChunk = args[o - i][p - j].method_39971();
				if (!worldChunk.areSectionsEmptyBetween(blockPos.getY(), blockPos2.getY())) {
					return false;
				}
			}
		}

		return true;
	}

	@Environment(EnvType.CLIENT)
	static final class class_6851 {
		private final WorldChunk field_36315;
		@Nullable
		private RenderedChunk field_36316;

		class_6851(WorldChunk worldChunk) {
			this.field_36315 = worldChunk;
		}

		public WorldChunk method_39971() {
			return this.field_36315;
		}

		public RenderedChunk method_39972() {
			if (this.field_36316 == null) {
				this.field_36316 = new RenderedChunk(this.field_36315);
			}

			return this.field_36316;
		}
	}
}
