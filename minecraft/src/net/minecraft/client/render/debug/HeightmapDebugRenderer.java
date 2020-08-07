package net.minecraft.client.render.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.Chunk;

@Environment(EnvType.CLIENT)
public class HeightmapDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient client;

	public HeightmapDebugRenderer(MinecraftClient client) {
		this.client = client;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		WorldAccess worldAccess = this.client.world;
		RenderSystem.pushMatrix();
		RenderSystem.disableBlend();
		RenderSystem.disableTexture();
		RenderSystem.enableDepthTest();
		BlockPos blockPos = new BlockPos(cameraX, 0.0, cameraZ);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);

		for (int i = -32; i <= 32; i += 16) {
			for (int j = -32; j <= 32; j += 16) {
				Chunk chunk = worldAccess.getChunk(blockPos.add(i, 0, j));

				for (Entry<Heightmap.Type, Heightmap> entry : chunk.getHeightmaps()) {
					Heightmap.Type type = (Heightmap.Type)entry.getKey();
					ChunkPos chunkPos = chunk.getPos();
					Vector3f vector3f = this.method_27037(type);

					for (int k = 0; k < 16; k++) {
						for (int l = 0; l < 16; l++) {
							int m = chunkPos.x * 16 + k;
							int n = chunkPos.z * 16 + l;
							float f = (float)((double)((float)worldAccess.getTopY(type, m, n) + (float)type.ordinal() * 0.09375F) - cameraY);
							WorldRenderer.drawBox(
								bufferBuilder,
								(double)((float)m + 0.25F) - cameraX,
								(double)f,
								(double)((float)n + 0.25F) - cameraZ,
								(double)((float)m + 0.75F) - cameraX,
								(double)(f + 0.09375F),
								(double)((float)n + 0.75F) - cameraZ,
								vector3f.getX(),
								vector3f.getY(),
								vector3f.getZ(),
								1.0F
							);
						}
					}
				}
			}
		}

		tessellator.draw();
		RenderSystem.enableTexture();
		RenderSystem.popMatrix();
	}

	private Vector3f method_27037(Heightmap.Type type) {
		switch (type) {
			case field_13194:
				return new Vector3f(1.0F, 1.0F, 0.0F);
			case field_13195:
				return new Vector3f(1.0F, 0.0F, 1.0F);
			case field_13202:
				return new Vector3f(0.0F, 0.7F, 0.0F);
			case field_13200:
				return new Vector3f(0.0F, 0.0F, 0.5F);
			case field_13197:
				return new Vector3f(0.0F, 0.3F, 0.3F);
			case field_13203:
				return new Vector3f(0.0F, 0.5F, 0.5F);
			default:
				return new Vector3f(0.0F, 0.0F, 0.0F);
		}
	}
}
