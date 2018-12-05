package net.minecraft.client.render.chunk;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Renderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ChunkRenderDispatcher {
	protected final Renderer renderer;
	protected final World world;
	protected int field_4149;
	protected int field_4148;
	protected int field_4147;
	public ChunkRenderer[] renderers;

	public ChunkRenderDispatcher(World world, int i, Renderer renderer, ChunkRendererFactory chunkRendererFactory) {
		this.renderer = renderer;
		this.world = world;
		this.method_3325(i);
		this.method_3324(chunkRendererFactory);
	}

	protected void method_3324(ChunkRendererFactory chunkRendererFactory) {
		int i = this.field_4148 * this.field_4149 * this.field_4147;
		this.renderers = new ChunkRenderer[i];

		for (int j = 0; j < this.field_4148; j++) {
			for (int k = 0; k < this.field_4149; k++) {
				for (int l = 0; l < this.field_4147; l++) {
					int m = this.method_3326(j, k, l);
					this.renderers[m] = chunkRendererFactory.create(this.world, this.renderer);
					this.renderers[m].method_3653(j * 16, k * 16, l * 16);
				}
			}
		}
	}

	public void method_3327() {
		for (ChunkRenderer chunkRenderer : this.renderers) {
			chunkRenderer.method_3659();
		}
	}

	private int method_3326(int i, int j, int k) {
		return (k * this.field_4149 + j) * this.field_4148 + i;
	}

	protected void method_3325(int i) {
		int j = i * 2 + 1;
		this.field_4148 = j;
		this.field_4149 = 16;
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

				for (int p = 0; p < this.field_4149; p++) {
					int q = p * 16;
					ChunkRenderer chunkRenderer = this.renderers[this.method_3326(l, p, n)];
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

	public void method_16040(int i, int j, int k, boolean bl) {
		int l = Math.floorMod(i, this.field_4148);
		int m = Math.floorMod(j, this.field_4149);
		int n = Math.floorMod(k, this.field_4147);
		ChunkRenderer chunkRenderer = this.renderers[this.method_3326(l, m, n)];
		chunkRenderer.markRenderUpdate(bl);
	}

	@Nullable
	public ChunkRenderer method_3323(BlockPos blockPos) {
		int i = MathHelper.floorDiv(blockPos.getX(), 16);
		int j = MathHelper.floorDiv(blockPos.getY(), 16);
		int k = MathHelper.floorDiv(blockPos.getZ(), 16);
		if (j >= 0 && j < this.field_4149) {
			i = MathHelper.floorMOd(i, this.field_4148);
			k = MathHelper.floorMOd(k, this.field_4147);
			return this.renderers[this.method_3326(i, j, k)];
		} else {
			return null;
		}
	}
}
