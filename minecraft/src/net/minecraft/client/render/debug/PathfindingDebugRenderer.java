package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PathfindingDebugRenderer implements DebugRenderer.Renderer {
	private final Map<Integer, Path> paths = Maps.<Integer, Path>newHashMap();
	private final Map<Integer, Float> field_4617 = Maps.<Integer, Float>newHashMap();
	private final Map<Integer, Long> pathTimes = Maps.<Integer, Long>newHashMap();

	public void addPath(int id, Path path, float f) {
		this.paths.put(id, path);
		this.pathTimes.put(id, Util.getMeasuringTimeMs());
		this.field_4617.put(id, f);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
		if (!this.paths.isEmpty()) {
			long l = Util.getMeasuringTimeMs();

			for (Integer integer : this.paths.keySet()) {
				Path path = (Path)this.paths.get(integer);
				float f = (Float)this.field_4617.get(integer);
				drawPath(path, f, true, true, cameraX, cameraY, cameraZ);
			}

			for (Integer integer2 : (Integer[])this.pathTimes.keySet().toArray(new Integer[0])) {
				if (l - (Long)this.pathTimes.get(integer2) > 5000L) {
					this.paths.remove(integer2);
					this.pathTimes.remove(integer2);
				}
			}
		}
	}

	public static void drawPath(Path path, float nodeSize, boolean bl, boolean drawLabels, double cameraX, double cameraY, double cameraZ) {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShaderColor(0.0F, 1.0F, 0.0F, 0.75F);
		RenderSystem.disableTexture();
		RenderSystem.lineWidth(6.0F);
		drawPathInternal(path, nodeSize, bl, drawLabels, cameraX, cameraY, cameraZ);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	private static void drawPathInternal(Path path, float nodeSize, boolean bl, boolean drawLabels, double cameraX, double cameraY, double cameraZ) {
		drawPathLines(path, cameraX, cameraY, cameraZ);
		BlockPos blockPos = path.getTarget();
		if (getManhattanDistance(blockPos, cameraX, cameraY, cameraZ) <= 80.0F) {
			DebugRenderer.drawBox(
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
				if (getManhattanDistance(pathNode.getPos(), cameraX, cameraY, cameraZ) <= 80.0F) {
					float f = i == path.getCurrentNodeIndex() ? 1.0F : 0.0F;
					float g = i == path.getCurrentNodeIndex() ? 0.0F : 1.0F;
					DebugRenderer.drawBox(
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

		if (bl) {
			for (PathNode pathNode2 : path.method_22881()) {
				if (getManhattanDistance(pathNode2.getPos(), cameraX, cameraY, cameraZ) <= 80.0F) {
					DebugRenderer.drawBox(
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

			for (PathNode pathNode2x : path.method_22880()) {
				if (getManhattanDistance(pathNode2x.getPos(), cameraX, cameraY, cameraZ) <= 80.0F) {
					DebugRenderer.drawBox(
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
			for (int ix = 0; ix < path.getLength(); ix++) {
				PathNode pathNode = path.getNode(ix);
				if (getManhattanDistance(pathNode.getPos(), cameraX, cameraY, cameraZ) <= 80.0F) {
					DebugRenderer.drawString(
						String.format("%s", pathNode.type), (double)pathNode.x + 0.5, (double)pathNode.y + 0.75, (double)pathNode.z + 0.5, -1, 0.02F, true, 0.0F, true
					);
					DebugRenderer.drawString(
						String.format(Locale.ROOT, "%.2f", pathNode.penalty),
						(double)pathNode.x + 0.5,
						(double)pathNode.y + 0.25,
						(double)pathNode.z + 0.5,
						-1,
						0.02F,
						true,
						0.0F,
						true
					);
				}
			}
		}
	}

	public static void drawPathLines(Path path, double cameraX, double cameraY, double cameraZ) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.LINE_STRIP, VertexFormats.POSITION_COLOR);

		for (int i = 0; i < path.getLength(); i++) {
			PathNode pathNode = path.getNode(i);
			if (!(getManhattanDistance(pathNode.getPos(), cameraX, cameraY, cameraZ) > 80.0F)) {
				float f = (float)i / (float)path.getLength() * 0.33F;
				int j = i == 0 ? 0 : MathHelper.hsvToRgb(f, 0.9F, 0.9F);
				int k = j >> 16 & 0xFF;
				int l = j >> 8 & 0xFF;
				int m = j & 0xFF;
				bufferBuilder.vertex((double)pathNode.x - cameraX + 0.5, (double)pathNode.y - cameraY + 0.5, (double)pathNode.z - cameraZ + 0.5).color(k, l, m, 255).next();
			}
		}

		tessellator.draw();
	}

	private static float getManhattanDistance(BlockPos pos, double x, double y, double z) {
		return (float)(Math.abs((double)pos.getX() - x) + Math.abs((double)pos.getY() - y) + Math.abs((double)pos.getZ() - z));
	}
}
