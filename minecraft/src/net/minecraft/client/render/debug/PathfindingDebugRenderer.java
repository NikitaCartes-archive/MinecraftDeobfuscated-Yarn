package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.util.Colors;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PathfindingDebugRenderer implements DebugRenderer.Renderer {
	private final Map<Integer, Path> paths = Maps.<Integer, Path>newHashMap();
	private final Map<Integer, Float> nodeSizes = Maps.<Integer, Float>newHashMap();
	private final Map<Integer, Long> pathTimes = Maps.<Integer, Long>newHashMap();
	private static final long MAX_PATH_AGE = 5000L;
	private static final float RANGE = 80.0F;
	private static final boolean field_32908 = true;
	private static final boolean field_32909 = false;
	private static final boolean field_32910 = false;
	private static final boolean field_32911 = true;
	private static final boolean field_32912 = true;
	private static final float DRAWN_STRING_SIZE = 0.02F;

	public void addPath(int id, Path path, float nodeSize) {
		this.paths.put(id, path);
		this.pathTimes.put(id, Util.getMeasuringTimeMs());
		this.nodeSizes.put(id, nodeSize);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		if (!this.paths.isEmpty()) {
			long l = Util.getMeasuringTimeMs();

			for (Integer integer : this.paths.keySet()) {
				Path path = (Path)this.paths.get(integer);
				float f = (Float)this.nodeSizes.get(integer);
				drawPath(matrices, vertexConsumers, path, f, true, true, cameraX, cameraY, cameraZ);
			}

			for (Integer integer2 : (Integer[])this.pathTimes.keySet().toArray(new Integer[0])) {
				if (l - (Long)this.pathTimes.get(integer2) > 5000L) {
					this.paths.remove(integer2);
					this.pathTimes.remove(integer2);
				}
			}
		}
	}

	public static void drawPath(
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		Path path,
		float nodeSize,
		boolean drawDebugNodes,
		boolean drawLabels,
		double cameraX,
		double cameraY,
		double cameraZ
	) {
		drawPathLines(matrices, vertexConsumers.getBuffer(RenderLayer.getDebugLineStrip(6.0)), path, cameraX, cameraY, cameraZ);
		BlockPos blockPos = path.getTarget();
		if (getManhattanDistance(blockPos, cameraX, cameraY, cameraZ) <= 80.0F) {
			DebugRenderer.drawBox(
				matrices,
				vertexConsumers,
				new Box(
						(double)((float)blockPos.getX() + 0.25F),
						(double)((float)blockPos.getY() + 0.25F),
						(double)blockPos.getZ() + 0.25,
						(double)((float)blockPos.getX() + 0.75F),
						(double)((float)blockPos.getY() + 0.75F),
						(double)((float)blockPos.getZ() + 0.75F)
					)
					.offset(-cameraX, -cameraY, -cameraZ),
				0.0F,
				1.0F,
				0.0F,
				0.5F
			);

			for (int i = 0; i < path.getLength(); i++) {
				PathNode pathNode = path.getNode(i);
				if (getManhattanDistance(pathNode.getBlockPos(), cameraX, cameraY, cameraZ) <= 80.0F) {
					float f = i == path.getCurrentNodeIndex() ? 1.0F : 0.0F;
					float g = i == path.getCurrentNodeIndex() ? 0.0F : 1.0F;
					DebugRenderer.drawBox(
						matrices,
						vertexConsumers,
						new Box(
								(double)((float)pathNode.x + 0.5F - nodeSize),
								(double)((float)pathNode.y + 0.01F * (float)i),
								(double)((float)pathNode.z + 0.5F - nodeSize),
								(double)((float)pathNode.x + 0.5F + nodeSize),
								(double)((float)pathNode.y + 0.25F + 0.01F * (float)i),
								(double)((float)pathNode.z + 0.5F + nodeSize)
							)
							.offset(-cameraX, -cameraY, -cameraZ),
						f,
						0.0F,
						g,
						0.5F
					);
				}
			}
		}

		Path.DebugNodeInfo debugNodeInfo = path.getDebugNodeInfos();
		if (drawDebugNodes && debugNodeInfo != null) {
			for (PathNode pathNode2 : debugNodeInfo.closedSet()) {
				if (getManhattanDistance(pathNode2.getBlockPos(), cameraX, cameraY, cameraZ) <= 80.0F) {
					DebugRenderer.drawBox(
						matrices,
						vertexConsumers,
						new Box(
								(double)((float)pathNode2.x + 0.5F - nodeSize / 2.0F),
								(double)((float)pathNode2.y + 0.01F),
								(double)((float)pathNode2.z + 0.5F - nodeSize / 2.0F),
								(double)((float)pathNode2.x + 0.5F + nodeSize / 2.0F),
								(double)pathNode2.y + 0.1,
								(double)((float)pathNode2.z + 0.5F + nodeSize / 2.0F)
							)
							.offset(-cameraX, -cameraY, -cameraZ),
						1.0F,
						0.8F,
						0.8F,
						0.5F
					);
				}
			}

			for (PathNode pathNode2x : debugNodeInfo.openSet()) {
				if (getManhattanDistance(pathNode2x.getBlockPos(), cameraX, cameraY, cameraZ) <= 80.0F) {
					DebugRenderer.drawBox(
						matrices,
						vertexConsumers,
						new Box(
								(double)((float)pathNode2x.x + 0.5F - nodeSize / 2.0F),
								(double)((float)pathNode2x.y + 0.01F),
								(double)((float)pathNode2x.z + 0.5F - nodeSize / 2.0F),
								(double)((float)pathNode2x.x + 0.5F + nodeSize / 2.0F),
								(double)pathNode2x.y + 0.1,
								(double)((float)pathNode2x.z + 0.5F + nodeSize / 2.0F)
							)
							.offset(-cameraX, -cameraY, -cameraZ),
						0.8F,
						1.0F,
						1.0F,
						0.5F
					);
				}
			}
		}

		if (drawLabels) {
			for (int j = 0; j < path.getLength(); j++) {
				PathNode pathNode3 = path.getNode(j);
				if (getManhattanDistance(pathNode3.getBlockPos(), cameraX, cameraY, cameraZ) <= 80.0F) {
					DebugRenderer.drawString(
						matrices,
						vertexConsumers,
						String.valueOf(pathNode3.type),
						(double)pathNode3.x + 0.5,
						(double)pathNode3.y + 0.75,
						(double)pathNode3.z + 0.5,
						Colors.WHITE,
						0.02F,
						true,
						0.0F,
						true
					);
					DebugRenderer.drawString(
						matrices,
						vertexConsumers,
						String.format(Locale.ROOT, "%.2f", pathNode3.penalty),
						(double)pathNode3.x + 0.5,
						(double)pathNode3.y + 0.25,
						(double)pathNode3.z + 0.5,
						Colors.WHITE,
						0.02F,
						true,
						0.0F,
						true
					);
				}
			}
		}
	}

	public static void drawPathLines(MatrixStack matrices, VertexConsumer vertexConsumers, Path path, double cameraX, double cameraY, double cameraZ) {
		for (int i = 0; i < path.getLength(); i++) {
			PathNode pathNode = path.getNode(i);
			if (!(getManhattanDistance(pathNode.getBlockPos(), cameraX, cameraY, cameraZ) > 80.0F)) {
				float f = (float)i / (float)path.getLength() * 0.33F;
				int j = i == 0 ? 0 : MathHelper.hsvToRgb(f, 0.9F, 0.9F);
				int k = j >> 16 & 0xFF;
				int l = j >> 8 & 0xFF;
				int m = j & 0xFF;
				vertexConsumers.vertex(
						matrices.peek(), (float)((double)pathNode.x - cameraX + 0.5), (float)((double)pathNode.y - cameraY + 0.5), (float)((double)pathNode.z - cameraZ + 0.5)
					)
					.color(k, l, m, 255);
			}
		}
	}

	private static float getManhattanDistance(BlockPos pos, double x, double y, double z) {
		return (float)(Math.abs((double)pos.getX() - x) + Math.abs((double)pos.getY() - y) + Math.abs((double)pos.getZ() - z));
	}
}
