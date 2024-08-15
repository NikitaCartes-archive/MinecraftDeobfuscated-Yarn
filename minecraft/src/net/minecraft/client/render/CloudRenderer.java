package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class CloudRenderer extends SinglePreparationResourceReloader<Optional<CloudRenderer.CloudCells>> implements AutoCloseable {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Identifier CLOUD_TEXTURE = Identifier.ofVanilla("textures/environment/clouds.png");
	private static final float field_53043 = 12.0F;
	private static final float field_53044 = 4.0F;
	private static final float field_53045 = 0.6F;
	private static final long field_53046 = 0L;
	private static final int field_53047 = 4;
	private static final int field_53048 = 3;
	private static final int field_53049 = 2;
	private static final int field_53050 = 1;
	private static final int field_53051 = 0;
	private boolean field_53052 = true;
	private int centerX = Integer.MIN_VALUE;
	private int centerZ = Integer.MIN_VALUE;
	private CloudRenderer.ViewMode viewMode = CloudRenderer.ViewMode.INSIDE_CLOUDS;
	@Nullable
	private CloudRenderMode renderMode;
	@Nullable
	private CloudRenderer.CloudCells cells;
	private final VertexBuffer buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
	private boolean renderClouds;

	protected Optional<CloudRenderer.CloudCells> prepare(ResourceManager resourceManager, Profiler profiler) {
		try {
			InputStream inputStream = resourceManager.open(CLOUD_TEXTURE);

			Optional var20;
			try (NativeImage nativeImage = NativeImage.read(inputStream)) {
				int i = nativeImage.getWidth();
				int j = nativeImage.getHeight();
				long[] ls = new long[i * j];

				for (int k = 0; k < j; k++) {
					for (int l = 0; l < i; l++) {
						int m = nativeImage.getColorArgb(l, k);
						if (isEmpty(m)) {
							ls[l + k * i] = 0L;
						} else {
							boolean bl = isEmpty(nativeImage.getColorArgb(l, Math.floorMod(k - 1, j)));
							boolean bl2 = isEmpty(nativeImage.getColorArgb(Math.floorMod(l + 1, j), k));
							boolean bl3 = isEmpty(nativeImage.getColorArgb(l, Math.floorMod(k + 1, j)));
							boolean bl4 = isEmpty(nativeImage.getColorArgb(Math.floorMod(l - 1, j), k));
							ls[l + k * i] = packCloudCell(m, bl, bl2, bl3, bl4);
						}
					}
				}

				var20 = Optional.of(new CloudRenderer.CloudCells(ls, i, j));
			} catch (Throwable var18) {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (Throwable var15) {
						var18.addSuppressed(var15);
					}
				}

				throw var18;
			}

			if (inputStream != null) {
				inputStream.close();
			}

			return var20;
		} catch (IOException var19) {
			LOGGER.error("Failed to load cloud texture", (Throwable)var19);
			return Optional.empty();
		}
	}

	protected void apply(Optional<CloudRenderer.CloudCells> optional, ResourceManager resourceManager, Profiler profiler) {
		this.cells = (CloudRenderer.CloudCells)optional.orElse(null);
		this.field_53052 = true;
	}

	private static boolean isEmpty(int color) {
		return ColorHelper.getAlpha(color) < 10;
	}

	private static long packCloudCell(int color, boolean borderNorth, boolean borderEast, boolean borderSouth, boolean borderWest) {
		return (long)color << 4
			| (long)((borderNorth ? 1 : 0) << 3)
			| (long)((borderEast ? 1 : 0) << 2)
			| (long)((borderSouth ? 1 : 0) << 1)
			| (long)((borderWest ? 1 : 0) << 0);
	}

	private static int unpackColor(long packed) {
		return (int)(packed >> 4 & 4294967295L);
	}

	private static boolean hasBorderNorth(long packed) {
		return (packed >> 3 & 1L) != 0L;
	}

	private static boolean hasBorderEast(long packed) {
		return (packed >> 2 & 1L) != 0L;
	}

	private static boolean hasBorderSouth(long packed) {
		return (packed >> 1 & 1L) != 0L;
	}

	private static boolean hasBorderWest(long packed) {
		return (packed >> 0 & 1L) != 0L;
	}

	public void renderClouds(
		int color, CloudRenderMode cloudRenderMode, float cloudHeight, Matrix4f positionMatrix, Matrix4f projectionMatrix, Vec3d cameraPos, float ticks
	) {
		if (this.cells != null) {
			float f = (float)((double)cloudHeight - cameraPos.y);
			float g = f + 4.0F;
			CloudRenderer.ViewMode viewMode;
			if (g < 0.0F) {
				viewMode = CloudRenderer.ViewMode.ABOVE_CLOUDS;
			} else if (f > 0.0F) {
				viewMode = CloudRenderer.ViewMode.BELOW_CLOUDS;
			} else {
				viewMode = CloudRenderer.ViewMode.INSIDE_CLOUDS;
			}

			double d = cameraPos.x + (double)(ticks * 0.030000001F);
			double e = cameraPos.z + 3.96F;
			double h = (double)this.cells.width * 12.0;
			double i = (double)this.cells.height * 12.0;
			d -= (double)MathHelper.floor(d / h) * h;
			e -= (double)MathHelper.floor(e / i) * i;
			int j = MathHelper.floor(d / 12.0);
			int k = MathHelper.floor(e / 12.0);
			float l = (float)(d - (double)((float)j * 12.0F));
			float m = (float)(e - (double)((float)k * 12.0F));
			RenderLayer renderLayer = cloudRenderMode == CloudRenderMode.FANCY ? RenderLayer.getFastClouds() : RenderLayer.getNoCullingClouds();
			this.buffer.bind();
			if (this.field_53052 || j != this.centerX || k != this.centerZ || viewMode != this.viewMode || cloudRenderMode != this.renderMode) {
				this.field_53052 = false;
				this.centerX = j;
				this.centerZ = k;
				this.viewMode = viewMode;
				this.renderMode = cloudRenderMode;
				BuiltBuffer builtBuffer = this.tessellateClouds(Tessellator.getInstance(), j, k, cloudRenderMode, viewMode, renderLayer);
				if (builtBuffer != null) {
					this.buffer.upload(builtBuffer);
					this.renderClouds = false;
				} else {
					this.renderClouds = true;
				}
			}

			if (!this.renderClouds) {
				RenderSystem.setShaderColor(
					ColorHelper.floatFromChannel(ColorHelper.getRed(color)),
					ColorHelper.floatFromChannel(ColorHelper.getGreen(color)),
					ColorHelper.floatFromChannel(ColorHelper.getBlue(color)),
					1.0F
				);
				if (cloudRenderMode == CloudRenderMode.FANCY) {
					this.renderClouds(RenderLayer.getFancyClouds(), positionMatrix, projectionMatrix, l, f, m);
				}

				this.renderClouds(renderLayer, positionMatrix, projectionMatrix, l, f, m);
				VertexBuffer.unbind();
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			}
		}
	}

	private void renderClouds(RenderLayer layer, Matrix4f positionMatrix, Matrix4f projectionMatrix, float x, float y, float z) {
		layer.startDrawing();
		ShaderProgram shaderProgram = RenderSystem.getShader();
		if (shaderProgram.modelOffset != null) {
			shaderProgram.modelOffset.set(-x, y, -z);
		}

		this.buffer.draw(positionMatrix, projectionMatrix, shaderProgram);
		layer.endDrawing();
	}

	@Nullable
	private BuiltBuffer tessellateClouds(Tessellator tessellator, int x, int z, CloudRenderMode renderMode, CloudRenderer.ViewMode viewMode, RenderLayer layer) {
		float f = 0.8F;
		int i = ColorHelper.fromFloats(0.8F, 1.0F, 1.0F, 1.0F);
		int j = ColorHelper.fromFloats(0.8F, 0.9F, 0.9F, 0.9F);
		int k = ColorHelper.fromFloats(0.8F, 0.7F, 0.7F, 0.7F);
		int l = ColorHelper.fromFloats(0.8F, 0.8F, 0.8F, 0.8F);
		BufferBuilder bufferBuilder = tessellator.begin(layer.getDrawMode(), layer.getVertexFormat());
		this.buildCloudCells(viewMode, bufferBuilder, x, z, k, i, j, l, renderMode == CloudRenderMode.FANCY);
		return bufferBuilder.endNullable();
	}

	private void buildCloudCells(
		CloudRenderer.ViewMode viewMode, BufferBuilder builder, int x, int z, int bottomColor, int topColor, int northSouthColor, int eastWestColor, boolean fancy
	) {
		if (this.cells != null) {
			int i = 32;
			long[] ls = this.cells.cells;
			int j = this.cells.width;
			int k = this.cells.height;

			for (int l = -32; l <= 32; l++) {
				for (int m = -32; m <= 32; m++) {
					int n = Math.floorMod(x + m, j);
					int o = Math.floorMod(z + l, k);
					long p = ls[n + o * j];
					if (p != 0L) {
						int q = unpackColor(p);
						if (fancy) {
							this.buildCloudCellFancy(
								viewMode,
								builder,
								ColorHelper.mix(bottomColor, q),
								ColorHelper.mix(topColor, q),
								ColorHelper.mix(northSouthColor, q),
								ColorHelper.mix(eastWestColor, q),
								m,
								l,
								p
							);
						} else {
							this.buildCloudCellFast(builder, ColorHelper.mix(topColor, q), m, l);
						}
					}
				}
			}
		}
	}

	private void buildCloudCellFast(BufferBuilder builder, int color, int x, int z) {
		float f = (float)x * 12.0F;
		float g = f + 12.0F;
		float h = (float)z * 12.0F;
		float i = h + 12.0F;
		builder.vertex(f, 0.0F, h).color(color);
		builder.vertex(f, 0.0F, i).color(color);
		builder.vertex(g, 0.0F, i).color(color);
		builder.vertex(g, 0.0F, h).color(color);
	}

	private void buildCloudCellFancy(
		CloudRenderer.ViewMode viewMode, BufferBuilder builder, int bottomColor, int topColor, int northSouthColor, int eastWestColor, int x, int z, long cell
	) {
		float f = (float)x * 12.0F;
		float g = f + 12.0F;
		float h = 0.0F;
		float i = 4.0F;
		float j = (float)z * 12.0F;
		float k = j + 12.0F;
		if (viewMode != CloudRenderer.ViewMode.BELOW_CLOUDS) {
			builder.vertex(f, 4.0F, j).color(topColor);
			builder.vertex(f, 4.0F, k).color(topColor);
			builder.vertex(g, 4.0F, k).color(topColor);
			builder.vertex(g, 4.0F, j).color(topColor);
		}

		if (viewMode != CloudRenderer.ViewMode.ABOVE_CLOUDS) {
			builder.vertex(g, 0.0F, j).color(bottomColor);
			builder.vertex(g, 0.0F, k).color(bottomColor);
			builder.vertex(f, 0.0F, k).color(bottomColor);
			builder.vertex(f, 0.0F, j).color(bottomColor);
		}

		if (hasBorderNorth(cell) && z > 0) {
			builder.vertex(f, 0.0F, j).color(eastWestColor);
			builder.vertex(f, 4.0F, j).color(eastWestColor);
			builder.vertex(g, 4.0F, j).color(eastWestColor);
			builder.vertex(g, 0.0F, j).color(eastWestColor);
		}

		if (hasBorderSouth(cell) && z < 0) {
			builder.vertex(g, 0.0F, k).color(eastWestColor);
			builder.vertex(g, 4.0F, k).color(eastWestColor);
			builder.vertex(f, 4.0F, k).color(eastWestColor);
			builder.vertex(f, 0.0F, k).color(eastWestColor);
		}

		if (hasBorderWest(cell) && x > 0) {
			builder.vertex(f, 0.0F, k).color(northSouthColor);
			builder.vertex(f, 4.0F, k).color(northSouthColor);
			builder.vertex(f, 4.0F, j).color(northSouthColor);
			builder.vertex(f, 0.0F, j).color(northSouthColor);
		}

		if (hasBorderEast(cell) && x < 0) {
			builder.vertex(g, 0.0F, j).color(northSouthColor);
			builder.vertex(g, 4.0F, j).color(northSouthColor);
			builder.vertex(g, 4.0F, k).color(northSouthColor);
			builder.vertex(g, 0.0F, k).color(northSouthColor);
		}

		boolean bl = Math.abs(x) <= 1 && Math.abs(z) <= 1;
		if (bl) {
			builder.vertex(g, 4.0F, j).color(topColor);
			builder.vertex(g, 4.0F, k).color(topColor);
			builder.vertex(f, 4.0F, k).color(topColor);
			builder.vertex(f, 4.0F, j).color(topColor);
			builder.vertex(f, 0.0F, j).color(bottomColor);
			builder.vertex(f, 0.0F, k).color(bottomColor);
			builder.vertex(g, 0.0F, k).color(bottomColor);
			builder.vertex(g, 0.0F, j).color(bottomColor);
			builder.vertex(g, 0.0F, j).color(eastWestColor);
			builder.vertex(g, 4.0F, j).color(eastWestColor);
			builder.vertex(f, 4.0F, j).color(eastWestColor);
			builder.vertex(f, 0.0F, j).color(eastWestColor);
			builder.vertex(f, 0.0F, k).color(eastWestColor);
			builder.vertex(f, 4.0F, k).color(eastWestColor);
			builder.vertex(g, 4.0F, k).color(eastWestColor);
			builder.vertex(g, 0.0F, k).color(eastWestColor);
			builder.vertex(f, 0.0F, j).color(northSouthColor);
			builder.vertex(f, 4.0F, j).color(northSouthColor);
			builder.vertex(f, 4.0F, k).color(northSouthColor);
			builder.vertex(f, 0.0F, k).color(northSouthColor);
			builder.vertex(g, 0.0F, k).color(northSouthColor);
			builder.vertex(g, 4.0F, k).color(northSouthColor);
			builder.vertex(g, 4.0F, j).color(northSouthColor);
			builder.vertex(g, 0.0F, j).color(northSouthColor);
		}
	}

	public void scheduleTerrainUpdate() {
		this.field_53052 = true;
	}

	public void close() {
		this.buffer.close();
	}

	@Environment(EnvType.CLIENT)
	public static record CloudCells(long[] cells, int width, int height) {
	}

	@Environment(EnvType.CLIENT)
	static enum ViewMode {
		ABOVE_CLOUDS,
		INSIDE_CLOUDS,
		BELOW_CLOUDS;
	}
}
