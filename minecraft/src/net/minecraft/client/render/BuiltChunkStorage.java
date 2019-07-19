package net.minecraft.client.render;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.chunk.ChunkRenderer;
import net.minecraft.client.render.chunk.ChunkRendererFactory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BuiltChunkStorage {
	protected final WorldRenderer worldRenderer;
	protected final World world;
	protected int sizeY;
	protected int sizeX;
	protected int sizeZ;
	public ChunkRenderer[] renderers;

	public BuiltChunkStorage(World world, int i, WorldRenderer renderer, ChunkRendererFactory chunkRendererFactory) {
		this.worldRenderer = renderer;
		this.world = world;
		this.method_3325(i);
		this.createChunks(chunkRendererFactory);
	}

	protected void createChunks(ChunkRendererFactory chunkRendererFactory) {
		int i = this.sizeX * this.sizeY * this.sizeZ;
		this.renderers = new ChunkRenderer[i];

		for (int j = 0; j < this.sizeX; j++) {
			for (int k = 0; k < this.sizeY; k++) {
				for (int l = 0; l < this.sizeZ; l++) {
					int m = this.getChunkIndex(j, k, l);
					this.renderers[m] = chunkRendererFactory.create(this.world, this.worldRenderer);
					this.renderers[m].setOrigin(j * 16, k * 16, l * 16);
				}
			}
		}
	}

	public void clear() {
		for (ChunkRenderer chunkRenderer : this.renderers) {
			chunkRenderer.delete();
		}
	}

	private int getChunkIndex(int x, int y, int z) {
		return (z * this.sizeY + y) * this.sizeX + x;
	}

	protected void method_3325(int i) {
		int j = i * 2 + 1;
		this.sizeX = j;
		this.sizeY = 16;
		this.sizeZ = j;
	}

	public void updateCameraPosition(double x, double z) {
		int i = MathHelper.floor(x) - 8;
		int j = MathHelper.floor(z) - 8;
		int k = this.sizeX * 16;

		for (int l = 0; l < this.sizeX; l++) {
			int m = this.method_3328(i, k, l);

			for (int n = 0; n < this.sizeZ; n++) {
				int o = this.method_3328(j, k, n);

				for (int p = 0; p < this.sizeY; p++) {
					int q = p * 16;
					ChunkRenderer chunkRenderer = this.renderers[this.getChunkIndex(l, p, n)];
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

	public void scheduleRebuild(int x, int y, int z, boolean important) {
		int i = Math.floorMod(x, this.sizeX);
		int j = Math.floorMod(y, this.sizeY);
		int k = Math.floorMod(z, this.sizeZ);
		ChunkRenderer chunkRenderer = this.renderers[this.getChunkIndex(i, j, k)];
		chunkRenderer.scheduleRebuild(important);
	}

	@Nullable
	protected ChunkRenderer getChunkRenderer(BlockPos pos) {
		int i = MathHelper.floorDiv(pos.getX(), 16);
		int j = MathHelper.floorDiv(pos.getY(), 16);
		int k = MathHelper.floorDiv(pos.getZ(), 16);
		if (j >= 0 && j < this.sizeY) {
			i = MathHelper.floorMod(i, this.sizeX);
			k = MathHelper.floorMod(k, this.sizeZ);
			return this.renderers[this.getChunkIndex(i, j, k)];
		} else {
			return null;
		}
	}
}
