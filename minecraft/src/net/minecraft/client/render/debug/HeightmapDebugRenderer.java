package net.minecraft.client.render.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.Chunk;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class HeightmapDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient client;
	private static final int CHUNK_RANGE = 2;
	private static final float BOX_HEIGHT = 0.09375F;

	public HeightmapDebugRenderer(MinecraftClient client) {
		this.client = client;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		WorldAccess worldAccess = this.client.world;
		RenderSystem.disableBlend();
		RenderSystem.disableTexture();
		RenderSystem.enableDepthTest();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		BlockPos blockPos = new BlockPos(cameraX, 0.0, cameraZ);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

		for (int i = -2; i <= 2; i++) {
			for (int j = -2; j <= 2; j++) {
				Chunk chunk = worldAccess.getChunk(blockPos.add(i * 16, 0, j * 16));

				for (Entry<Heightmap.Type, Heightmap> entry : chunk.getHeightmaps()) {
					Heightmap.Type type = (Heightmap.Type)entry.getKey();
					ChunkPos chunkPos = chunk.getPos();
					Vector3f vector3f = this.getColorForHeightmapType(type);

					for (int k = 0; k < 16; k++) {
						for (int l = 0; l < 16; l++) {
							int m = ChunkSectionPos.getOffsetPos(chunkPos.x, k);
							int n = ChunkSectionPos.getOffsetPos(chunkPos.z, l);
							float f = (float)((double)((float)worldAccess.getTopY(type, m, n) + (float)type.ordinal() * 0.09375F) - cameraY);
							WorldRenderer.drawBox(
								bufferBuilder,
								(double)((float)m + 0.25F) - cameraX,
								(double)f,
								(double)((float)n + 0.25F) - cameraZ,
								(double)((float)m + 0.75F) - cameraX,
								(double)(f + 0.09375F),
								(double)((float)n + 0.75F) - cameraZ,
								vector3f.x(),
								vector3f.y(),
								vector3f.z(),
								1.0F
							);
						}
					}
				}
			}
		}

		tessellator.draw();
		RenderSystem.enableTexture();
	}

	private Vector3f getColorForHeightmapType(Heightmap.Type type) {
		switch (type) {
			case WORLD_SURFACE_WG:
				return new Vector3f(1.0F, 1.0F, 0.0F);
			case OCEAN_FLOOR_WG:
				return new Vector3f(1.0F, 0.0F, 1.0F);
			case WORLD_SURFACE:
				return new Vector3f(0.0F, 0.7F, 0.0F);
			case OCEAN_FLOOR:
				return new Vector3f(0.0F, 0.0F, 0.5F);
			case MOTION_BLOCKING:
				return new Vector3f(0.0F, 0.3F, 0.3F);
			case MOTION_BLOCKING_NO_LEAVES:
				return new Vector3f(0.0F, 0.5F, 0.5F);
			default:
				return new Vector3f(0.0F, 0.0F, 0.0F);
		}
	}
}
