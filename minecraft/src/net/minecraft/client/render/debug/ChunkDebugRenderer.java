package net.minecraft.client.render.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.ChunkRenderingDataPreparer;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Colors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Vector4f;

@Environment(EnvType.CLIENT)
public class ChunkDebugRenderer implements DebugRenderer.Renderer {
	public static final Direction[] DIRECTIONS = Direction.values();
	private final MinecraftClient client;

	public ChunkDebugRenderer(MinecraftClient client) {
		this.client = client;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		WorldRenderer worldRenderer = this.client.worldRenderer;
		if (this.client.debugChunkInfo || this.client.debugChunkOcclusion) {
			ChunkRenderingDataPreparer chunkRenderingDataPreparer = worldRenderer.getChunkRenderingDataPreparer();

			for (ChunkBuilder.BuiltChunk builtChunk : worldRenderer.getBuiltChunks()) {
				ChunkRenderingDataPreparer.ChunkInfo chunkInfo = chunkRenderingDataPreparer.method_52837(builtChunk);
				if (chunkInfo != null) {
					BlockPos blockPos = builtChunk.getOrigin();
					matrices.push();
					matrices.translate((double)blockPos.getX() - cameraX, (double)blockPos.getY() - cameraY, (double)blockPos.getZ() - cameraZ);
					Matrix4f matrix4f = matrices.peek().getPositionMatrix();
					if (this.client.debugChunkInfo) {
						VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
						int i = chunkInfo.propagationLevel == 0 ? 0 : MathHelper.hsvToRgb((float)chunkInfo.propagationLevel / 50.0F, 0.9F, 0.9F);
						int j = i >> 16 & 0xFF;
						int k = i >> 8 & 0xFF;
						int l = i & 0xFF;

						for (int m = 0; m < DIRECTIONS.length; m++) {
							if (chunkInfo.hasDirection(m)) {
								Direction direction = DIRECTIONS[m];
								vertexConsumer.vertex(matrix4f, 8.0F, 8.0F, 8.0F)
									.color(j, k, l, 255)
									.normal((float)direction.getOffsetX(), (float)direction.getOffsetY(), (float)direction.getOffsetZ());
								vertexConsumer.vertex(
										matrix4f, (float)(8 - 16 * direction.getOffsetX()), (float)(8 - 16 * direction.getOffsetY()), (float)(8 - 16 * direction.getOffsetZ())
									)
									.color(j, k, l, 255)
									.normal((float)direction.getOffsetX(), (float)direction.getOffsetY(), (float)direction.getOffsetZ());
							}
						}
					}

					if (this.client.debugChunkOcclusion && !builtChunk.getData().isEmpty()) {
						VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
						int i = 0;

						for (Direction direction2 : DIRECTIONS) {
							for (Direction direction3 : DIRECTIONS) {
								boolean bl = builtChunk.getData().isVisibleThrough(direction2, direction3);
								if (!bl) {
									i++;
									vertexConsumer.vertex(
											matrix4f, (float)(8 + 8 * direction2.getOffsetX()), (float)(8 + 8 * direction2.getOffsetY()), (float)(8 + 8 * direction2.getOffsetZ())
										)
										.color(255, 0, 0, 255)
										.normal((float)direction2.getOffsetX(), (float)direction2.getOffsetY(), (float)direction2.getOffsetZ());
									vertexConsumer.vertex(
											matrix4f, (float)(8 + 8 * direction3.getOffsetX()), (float)(8 + 8 * direction3.getOffsetY()), (float)(8 + 8 * direction3.getOffsetZ())
										)
										.color(255, 0, 0, 255)
										.normal((float)direction3.getOffsetX(), (float)direction3.getOffsetY(), (float)direction3.getOffsetZ());
								}
							}
						}

						if (i > 0) {
							VertexConsumer vertexConsumer2 = vertexConsumers.getBuffer(RenderLayer.getDebugQuads());
							float f = 0.5F;
							float g = 0.2F;
							vertexConsumer2.vertex(matrix4f, 0.5F, 15.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
							vertexConsumer2.vertex(matrix4f, 15.5F, 15.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
							vertexConsumer2.vertex(matrix4f, 15.5F, 15.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
							vertexConsumer2.vertex(matrix4f, 0.5F, 15.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
							vertexConsumer2.vertex(matrix4f, 0.5F, 0.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
							vertexConsumer2.vertex(matrix4f, 15.5F, 0.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
							vertexConsumer2.vertex(matrix4f, 15.5F, 0.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
							vertexConsumer2.vertex(matrix4f, 0.5F, 0.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
							vertexConsumer2.vertex(matrix4f, 0.5F, 15.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
							vertexConsumer2.vertex(matrix4f, 0.5F, 15.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
							vertexConsumer2.vertex(matrix4f, 0.5F, 0.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
							vertexConsumer2.vertex(matrix4f, 0.5F, 0.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
							vertexConsumer2.vertex(matrix4f, 15.5F, 0.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
							vertexConsumer2.vertex(matrix4f, 15.5F, 0.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
							vertexConsumer2.vertex(matrix4f, 15.5F, 15.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
							vertexConsumer2.vertex(matrix4f, 15.5F, 15.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
							vertexConsumer2.vertex(matrix4f, 0.5F, 0.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
							vertexConsumer2.vertex(matrix4f, 15.5F, 0.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
							vertexConsumer2.vertex(matrix4f, 15.5F, 15.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
							vertexConsumer2.vertex(matrix4f, 0.5F, 15.5F, 0.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
							vertexConsumer2.vertex(matrix4f, 0.5F, 15.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
							vertexConsumer2.vertex(matrix4f, 15.5F, 15.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
							vertexConsumer2.vertex(matrix4f, 15.5F, 0.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
							vertexConsumer2.vertex(matrix4f, 0.5F, 0.5F, 15.5F).color(0.9F, 0.9F, 0.0F, 0.2F);
						}
					}

					matrices.pop();
				}
			}
		}

		Frustum frustum = worldRenderer.getCapturedFrustum();
		if (frustum != null) {
			matrices.push();
			matrices.translate((float)(frustum.method_62343() - cameraX), (float)(frustum.method_62344() - cameraY), (float)(frustum.method_62345() - cameraZ));
			Matrix4f matrix4f2 = matrices.peek().getPositionMatrix();
			Vector4f[] vector4fs = frustum.method_62342();
			VertexConsumer vertexConsumer3 = vertexConsumers.getBuffer(RenderLayer.getDebugQuads());
			this.method_62347(vertexConsumer3, matrix4f2, vector4fs, 0, 1, 2, 3, 0, 1, 1);
			this.method_62347(vertexConsumer3, matrix4f2, vector4fs, 4, 5, 6, 7, 1, 0, 0);
			this.method_62347(vertexConsumer3, matrix4f2, vector4fs, 0, 1, 5, 4, 1, 1, 0);
			this.method_62347(vertexConsumer3, matrix4f2, vector4fs, 2, 3, 7, 6, 0, 0, 1);
			this.method_62347(vertexConsumer3, matrix4f2, vector4fs, 0, 4, 7, 3, 0, 1, 0);
			this.method_62347(vertexConsumer3, matrix4f2, vector4fs, 1, 5, 6, 2, 1, 0, 1);
			VertexConsumer vertexConsumer4 = vertexConsumers.getBuffer(RenderLayer.getLines());
			this.method_62346(vertexConsumer4, matrix4f2, vector4fs[0]);
			this.method_62346(vertexConsumer4, matrix4f2, vector4fs[1]);
			this.method_62346(vertexConsumer4, matrix4f2, vector4fs[1]);
			this.method_62346(vertexConsumer4, matrix4f2, vector4fs[2]);
			this.method_62346(vertexConsumer4, matrix4f2, vector4fs[2]);
			this.method_62346(vertexConsumer4, matrix4f2, vector4fs[3]);
			this.method_62346(vertexConsumer4, matrix4f2, vector4fs[3]);
			this.method_62346(vertexConsumer4, matrix4f2, vector4fs[0]);
			this.method_62346(vertexConsumer4, matrix4f2, vector4fs[4]);
			this.method_62346(vertexConsumer4, matrix4f2, vector4fs[5]);
			this.method_62346(vertexConsumer4, matrix4f2, vector4fs[5]);
			this.method_62346(vertexConsumer4, matrix4f2, vector4fs[6]);
			this.method_62346(vertexConsumer4, matrix4f2, vector4fs[6]);
			this.method_62346(vertexConsumer4, matrix4f2, vector4fs[7]);
			this.method_62346(vertexConsumer4, matrix4f2, vector4fs[7]);
			this.method_62346(vertexConsumer4, matrix4f2, vector4fs[4]);
			this.method_62346(vertexConsumer4, matrix4f2, vector4fs[0]);
			this.method_62346(vertexConsumer4, matrix4f2, vector4fs[4]);
			this.method_62346(vertexConsumer4, matrix4f2, vector4fs[1]);
			this.method_62346(vertexConsumer4, matrix4f2, vector4fs[5]);
			this.method_62346(vertexConsumer4, matrix4f2, vector4fs[2]);
			this.method_62346(vertexConsumer4, matrix4f2, vector4fs[6]);
			this.method_62346(vertexConsumer4, matrix4f2, vector4fs[3]);
			this.method_62346(vertexConsumer4, matrix4f2, vector4fs[7]);
			matrices.pop();
		}
	}

	private void method_62346(VertexConsumer vertexConsumer, Matrix4f matrix4f, Vector4f vector4f) {
		vertexConsumer.vertex(matrix4f, vector4f.x(), vector4f.y(), vector4f.z()).color(Colors.BLACK).normal(0.0F, 0.0F, -1.0F);
	}

	private void method_62347(VertexConsumer vertexConsumer, Matrix4f matrix4f, Vector4f[] vector4fs, int i, int j, int k, int l, int m, int n, int o) {
		float f = 0.25F;
		vertexConsumer.vertex(matrix4f, vector4fs[i].x(), vector4fs[i].y(), vector4fs[i].z()).color((float)m, (float)n, (float)o, 0.25F);
		vertexConsumer.vertex(matrix4f, vector4fs[j].x(), vector4fs[j].y(), vector4fs[j].z()).color((float)m, (float)n, (float)o, 0.25F);
		vertexConsumer.vertex(matrix4f, vector4fs[k].x(), vector4fs[k].y(), vector4fs[k].z()).color((float)m, (float)n, (float)o, 0.25F);
		vertexConsumer.vertex(matrix4f, vector4fs[l].x(), vector4fs[l].y(), vector4fs[l].z()).color((float)m, (float)n, (float)o, 0.25F);
	}
}
