package net.minecraft.client.render.debug;

import java.time.Duration;
import java.time.Instant;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.light.LightStorage;
import net.minecraft.world.chunk.light.LightingProvider;
import org.joml.Matrix4f;
import org.joml.Vector4f;

@Environment(EnvType.CLIENT)
public class LightDebugRenderer implements DebugRenderer.Renderer {
	private static final Duration UPDATE_INTERVAL = Duration.ofMillis(500L);
	private static final int RADIUS = 10;
	private static final Vector4f READY_SHAPE_COLOR = new Vector4f(1.0F, 1.0F, 0.0F, 0.25F);
	private static final Vector4f DEFAULT_SHAPE_COLOR = new Vector4f(0.25F, 0.125F, 0.0F, 0.125F);
	private final MinecraftClient client;
	private final LightType lightType;
	private Instant prevUpdateTime = Instant.now();
	@Nullable
	private LightDebugRenderer.Data data;

	public LightDebugRenderer(MinecraftClient client, LightType lightType) {
		this.client = client;
		this.lightType = lightType;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		Instant instant = Instant.now();
		if (this.data == null || Duration.between(this.prevUpdateTime, instant).compareTo(UPDATE_INTERVAL) > 0) {
			this.prevUpdateTime = instant;
			this.data = new LightDebugRenderer.Data(this.client.world.getLightingProvider(), ChunkSectionPos.from(this.client.player.getBlockPos()), 10, this.lightType);
		}

		drawEdges(matrices, this.data.readyShape, this.data.minSectionPos, vertexConsumers, cameraX, cameraY, cameraZ, READY_SHAPE_COLOR);
		drawEdges(matrices, this.data.shape, this.data.minSectionPos, vertexConsumers, cameraX, cameraY, cameraZ, DEFAULT_SHAPE_COLOR);
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugSectionQuads());
		drawFaces(matrices, this.data.readyShape, this.data.minSectionPos, vertexConsumer, cameraX, cameraY, cameraZ, READY_SHAPE_COLOR);
		drawFaces(matrices, this.data.shape, this.data.minSectionPos, vertexConsumer, cameraX, cameraY, cameraZ, DEFAULT_SHAPE_COLOR);
	}

	private static void drawFaces(
		MatrixStack matrices,
		VoxelSet shape,
		ChunkSectionPos sectionPos,
		VertexConsumer vertexConsumer,
		double cameraX,
		double cameraY,
		double cameraZ,
		Vector4f color
	) {
		shape.forEachDirection((direction, offsetX, offsetY, offsetZ) -> {
			int i = offsetX + sectionPos.getX();
			int j = offsetY + sectionPos.getY();
			int k = offsetZ + sectionPos.getZ();
			drawFace(matrices, vertexConsumer, direction, cameraX, cameraY, cameraZ, i, j, k, color);
		});
	}

	private static void drawEdges(
		MatrixStack matrices,
		VoxelSet shape,
		ChunkSectionPos sectionPos,
		VertexConsumerProvider vertexConsumers,
		double cameraX,
		double cameraY,
		double cameraZ,
		Vector4f color
	) {
		shape.forEachEdge((x1, y1, z1, x2, y2, z2) -> {
			int i = x1 + sectionPos.getX();
			int j = y1 + sectionPos.getY();
			int k = z1 + sectionPos.getZ();
			int l = x2 + sectionPos.getX();
			int m = y2 + sectionPos.getY();
			int n = z2 + sectionPos.getZ();
			VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugLineStrip(1.0));
			drawEdge(matrices, vertexConsumer, cameraX, cameraY, cameraZ, i, j, k, l, m, n, color);
		}, true);
	}

	private static void drawFace(
		MatrixStack matrices, VertexConsumer vertexConsumer, Direction direction, double cameraX, double cameraY, double cameraZ, int x, int y, int z, Vector4f color
	) {
		float f = (float)((double)ChunkSectionPos.getBlockCoord(x) - cameraX);
		float g = (float)((double)ChunkSectionPos.getBlockCoord(y) - cameraY);
		float h = (float)((double)ChunkSectionPos.getBlockCoord(z) - cameraZ);
		float i = f + 16.0F;
		float j = g + 16.0F;
		float k = h + 16.0F;
		float l = color.x();
		float m = color.y();
		float n = color.z();
		float o = color.w();
		Matrix4f matrix4f = matrices.peek().getPositionMatrix();
		switch (direction) {
			case DOWN:
				vertexConsumer.vertex(matrix4f, f, g, h).color(l, m, n, o).next();
				vertexConsumer.vertex(matrix4f, i, g, h).color(l, m, n, o).next();
				vertexConsumer.vertex(matrix4f, i, g, k).color(l, m, n, o).next();
				vertexConsumer.vertex(matrix4f, f, g, k).color(l, m, n, o).next();
				break;
			case UP:
				vertexConsumer.vertex(matrix4f, f, j, h).color(l, m, n, o).next();
				vertexConsumer.vertex(matrix4f, f, j, k).color(l, m, n, o).next();
				vertexConsumer.vertex(matrix4f, i, j, k).color(l, m, n, o).next();
				vertexConsumer.vertex(matrix4f, i, j, h).color(l, m, n, o).next();
				break;
			case NORTH:
				vertexConsumer.vertex(matrix4f, f, g, h).color(l, m, n, o).next();
				vertexConsumer.vertex(matrix4f, f, j, h).color(l, m, n, o).next();
				vertexConsumer.vertex(matrix4f, i, j, h).color(l, m, n, o).next();
				vertexConsumer.vertex(matrix4f, i, g, h).color(l, m, n, o).next();
				break;
			case SOUTH:
				vertexConsumer.vertex(matrix4f, f, g, k).color(l, m, n, o).next();
				vertexConsumer.vertex(matrix4f, i, g, k).color(l, m, n, o).next();
				vertexConsumer.vertex(matrix4f, i, j, k).color(l, m, n, o).next();
				vertexConsumer.vertex(matrix4f, f, j, k).color(l, m, n, o).next();
				break;
			case WEST:
				vertexConsumer.vertex(matrix4f, f, g, h).color(l, m, n, o).next();
				vertexConsumer.vertex(matrix4f, f, g, k).color(l, m, n, o).next();
				vertexConsumer.vertex(matrix4f, f, j, k).color(l, m, n, o).next();
				vertexConsumer.vertex(matrix4f, f, j, h).color(l, m, n, o).next();
				break;
			case EAST:
				vertexConsumer.vertex(matrix4f, i, g, h).color(l, m, n, o).next();
				vertexConsumer.vertex(matrix4f, i, j, h).color(l, m, n, o).next();
				vertexConsumer.vertex(matrix4f, i, j, k).color(l, m, n, o).next();
				vertexConsumer.vertex(matrix4f, i, g, k).color(l, m, n, o).next();
		}
	}

	private static void drawEdge(
		MatrixStack matrices,
		VertexConsumer vertexConsumer,
		double cameraX,
		double cameraY,
		double cameraZ,
		int x1,
		int y1,
		int z1,
		int x2,
		int y2,
		int z,
		Vector4f color
	) {
		float f = (float)((double)ChunkSectionPos.getBlockCoord(x1) - cameraX);
		float g = (float)((double)ChunkSectionPos.getBlockCoord(y1) - cameraY);
		float h = (float)((double)ChunkSectionPos.getBlockCoord(z1) - cameraZ);
		float i = (float)((double)ChunkSectionPos.getBlockCoord(x2) - cameraX);
		float j = (float)((double)ChunkSectionPos.getBlockCoord(y2) - cameraY);
		float k = (float)((double)ChunkSectionPos.getBlockCoord(z) - cameraZ);
		Matrix4f matrix4f = matrices.peek().getPositionMatrix();
		vertexConsumer.vertex(matrix4f, f, g, h).color(color.x(), color.y(), color.z(), 1.0F).next();
		vertexConsumer.vertex(matrix4f, i, j, k).color(color.x(), color.y(), color.z(), 1.0F).next();
	}

	@Environment(EnvType.CLIENT)
	static final class Data {
		final VoxelSet readyShape;
		final VoxelSet shape;
		final ChunkSectionPos minSectionPos;

		Data(LightingProvider lightingProvider, ChunkSectionPos sectionPos, int radius, LightType lightType) {
			int i = radius * 2 + 1;
			this.readyShape = new BitSetVoxelSet(i, i, i);
			this.shape = new BitSetVoxelSet(i, i, i);

			for (int j = 0; j < i; j++) {
				for (int k = 0; k < i; k++) {
					for (int l = 0; l < i; l++) {
						ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(
							sectionPos.getSectionX() + l - radius, sectionPos.getSectionY() + k - radius, sectionPos.getSectionZ() + j - radius
						);
						LightStorage.Status status = lightingProvider.getStatus(lightType, chunkSectionPos);
						if (status == LightStorage.Status.LIGHT_AND_DATA) {
							this.readyShape.set(l, k, j);
							this.shape.set(l, k, j);
						} else if (status == LightStorage.Status.LIGHT_ONLY) {
							this.shape.set(l, k, j);
						}
					}
				}
			}

			this.minSectionPos = ChunkSectionPos.from(sectionPos.getSectionX() - radius, sectionPos.getSectionY() - radius, sectionPos.getSectionZ() - radius);
		}
	}
}
