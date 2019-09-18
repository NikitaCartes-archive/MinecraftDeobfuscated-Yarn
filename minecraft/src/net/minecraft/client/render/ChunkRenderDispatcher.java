package net.minecraft.client.render;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.chunk.ChunkBatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ChunkRenderDispatcher {
	protected final WorldRenderer renderer;
	protected final World world;
	protected int sizeY;
	protected int sizeX;
	protected int sizeZ;
	public ChunkBatcher.ChunkRenderer[] renderers;

	public ChunkRenderDispatcher(ChunkBatcher chunkBatcher, World world, int i, WorldRenderer worldRenderer) {
		this.renderer = worldRenderer;
		this.world = world;
		this.method_3325(i);
		this.createChunks(chunkBatcher);
	}

	protected void createChunks(ChunkBatcher chunkBatcher) {
		int i = this.sizeX * this.sizeY * this.sizeZ;
		this.renderers = new ChunkBatcher.ChunkRenderer[i];

		for (int j = 0; j < this.sizeX; j++) {
			for (int k = 0; k < this.sizeY; k++) {
				for (int l = 0; l < this.sizeZ; l++) {
					int m = this.getChunkIndex(j, k, l);
					this.renderers[m] = chunkBatcher.new ChunkRenderer();
					this.renderers[m].setOrigin(j * 16, k * 16, l * 16);
				}
			}
		}
	}

	public void delete() {
		for (ChunkBatcher.ChunkRenderer chunkRenderer : this.renderers) {
			chunkRenderer.delete();
		}
	}

	private int getChunkIndex(int i, int j, int k) {
		return (k * this.sizeY + j) * this.sizeX + i;
	}

	protected void method_3325(int i) {
		int j = i * 2 + 1;
		this.sizeX = j;
		this.sizeY = 16;
		this.sizeZ = j;
	}

	public void updateCameraPosition(double d, double e) {
		int i = MathHelper.floor(d) - 8;
		int j = MathHelper.floor(e) - 8;
		int k = this.sizeX * 16;

		for (int l = 0; l < this.sizeX; l++) {
			int m = this.method_3328(i, k, l);

			for (int n = 0; n < this.sizeZ; n++) {
				int o = this.method_3328(j, k, n);

				for (int p = 0; p < this.sizeY; p++) {
					int q = p * 16;
					ChunkBatcher.ChunkRenderer chunkRenderer = this.renderers[this.getChunkIndex(l, p, n)];
					chunkRenderer.setOrigin(m, q, o);
				}
			}
		}
	}

	private int method_3328(int i, int j, int k) {
		int l = k * 16;
		int m = l - i + j / 2;
		if (m < 0) {
			m -= j - 1;
		}

		return l - m / j * j;
	}

	public void scheduleChunkRender(int i, int j, int k, boolean bl) {
		int l = Math.floorMod(i, this.sizeX);
		int m = Math.floorMod(j, this.sizeY);
		int n = Math.floorMod(k, this.sizeZ);
		ChunkBatcher.ChunkRenderer chunkRenderer = this.renderers[this.getChunkIndex(l, m, n)];
		chunkRenderer.scheduleRebuild(bl);
	}

	@Nullable
	protected ChunkBatcher.ChunkRenderer getChunkRenderer(BlockPos blockPos) {
		int i = MathHelper.floorDiv(blockPos.getX(), 16);
		int j = MathHelper.floorDiv(blockPos.getY(), 16);
		int k = MathHelper.floorDiv(blockPos.getZ(), 16);
		if (j >= 0 && j < this.sizeY) {
			i = MathHelper.floorMod(i, this.sizeX);
			k = MathHelper.floorMod(k, this.sizeZ);
			return this.renderers[this.getChunkIndex(i, j, k)];
		} else {
			return null;
		}
	}
}
