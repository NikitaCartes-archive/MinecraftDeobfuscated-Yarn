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
public class ChunkRenderDispatcher {
	protected final WorldRenderer renderer;
	protected final World world;
	protected int sizeY;
	protected int field_4148;
	protected int field_4147;
	public ChunkRenderer[] renderers;

	public ChunkRenderDispatcher(World world, int i, WorldRenderer worldRenderer, ChunkRendererFactory chunkRendererFactory) {
		this.renderer = worldRenderer;
		this.world = world;
		this.method_3325(i);
		this.createChunks(chunkRendererFactory);
	}

	protected void createChunks(ChunkRendererFactory chunkRendererFactory) {
		int i = this.field_4148 * this.sizeY * this.field_4147;
		this.renderers = new ChunkRenderer[i];

		for (int j = 0; j < this.field_4148; j++) {
			for (int k = 0; k < this.sizeY; k++) {
				for (int l = 0; l < this.field_4147; l++) {
					int m = this.getChunkIndex(j, k, l);
					this.renderers[m] = chunkRendererFactory.create(this.world, this.renderer);
					this.renderers[m].method_3653(j * 16, k * 16, l * 16);
				}
			}
		}
	}

	public void delete() {
		for (ChunkRenderer chunkRenderer : this.renderers) {
			chunkRenderer.delete();
		}
	}

	private int getChunkIndex(int i, int j, int k) {
		return (k * this.sizeY + j) * this.field_4148 + i;
	}

	protected void method_3325(int i) {
		int j = i * 2 + 1;
		this.field_4148 = j;
		this.sizeY = 16;
		this.field_4147 = j;
	}

	public void method_3330(double d, double e) {
		int i = MathHelper.floor(d) - 8;
		int j = MathHelper.floor(e) - 8;
		int k = this.field_4148 * 16;

		for (int l = 0; l < this.field_4148; l++) {
			int m = this.method_3328(i, k, l);

			for (int n = 0; n < this.field_4147; n++) {
				int o = this.method_3328(j, k, n);

				for (int p = 0; p < this.sizeY; p++) {
					int q = p * 16;
					ChunkRenderer chunkRenderer = this.renderers[this.getChunkIndex(l, p, n)];
					chunkRenderer.method_3653(m, q, o);
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
		int l = Math.floorMod(i, this.field_4148);
		int m = Math.floorMod(j, this.sizeY);
		int n = Math.floorMod(k, this.field_4147);
		ChunkRenderer chunkRenderer = this.renderers[this.getChunkIndex(l, m, n)];
		chunkRenderer.scheduleRender(bl);
	}

	@Nullable
	protected ChunkRenderer getChunk(BlockPos blockPos) {
		int i = MathHelper.floorDiv(blockPos.getX(), 16);
		int j = MathHelper.floorDiv(blockPos.getY(), 16);
		int k = MathHelper.floorDiv(blockPos.getZ(), 16);
		if (j >= 0 && j < this.sizeY) {
			i = MathHelper.floorMod(i, this.field_4148);
			k = MathHelper.floorMod(k, this.field_4147);
			return this.renderers[this.getChunkIndex(i, j, k)];
		} else {
			return null;
		}
	}
}
