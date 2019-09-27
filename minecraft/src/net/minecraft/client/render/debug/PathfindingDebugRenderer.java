package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PathfindingDebugRenderer implements DebugRenderer.Renderer {
	private final MinecraftClient client;
	private final Map<Integer, Path> paths = Maps.<Integer, Path>newHashMap();
	private final Map<Integer, Float> field_4617 = Maps.<Integer, Float>newHashMap();
	private final Map<Integer, Long> pathTimes = Maps.<Integer, Long>newHashMap();

	public PathfindingDebugRenderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	public void addPath(int i, Path path, float f) {
		this.paths.put(i, path);
		this.pathTimes.put(i, SystemUtil.getMeasuringTimeMs());
		this.field_4617.put(i, f);
	}

	@Override
	public void method_23109(long l) {
		if (!this.paths.isEmpty()) {
			long m = SystemUtil.getMeasuringTimeMs();

			for (Integer integer : this.paths.keySet()) {
				Path path = (Path)this.paths.get(integer);
				float f = (Float)this.field_4617.get(integer);
				method_23118(this.method_23120(), path, f, true, true);
			}

			for (Integer integer2 : (Integer[])this.pathTimes.keySet().toArray(new Integer[0])) {
				if (m - (Long)this.pathTimes.get(integer2) > 20000L) {
					this.paths.remove(integer2);
					this.pathTimes.remove(integer2);
				}
			}
		}
	}

	public static void method_23118(Camera camera, Path path, float f, boolean bl, boolean bl2) {
		RenderSystem.pushMatrix();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.color4f(0.0F, 1.0F, 0.0F, 0.75F);
		RenderSystem.disableTexture();
		RenderSystem.lineWidth(6.0F);
		method_23121(camera, path, f, bl, bl2);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
		RenderSystem.popMatrix();
	}

	private static void method_23121(Camera camera, Path path, float f, boolean bl, boolean bl2) {
		method_23117(camera, path);
		double d = camera.getPos().x;
		double e = camera.getPos().y;
		double g = camera.getPos().z;
		BlockPos blockPos = path.getTarget();
		if (method_23119(camera, blockPos) <= 40.0F) {
			DebugRenderer.method_23102(
				new Box(
						(double)((float)blockPos.getX() + 0.25F),
						(double)((float)blockPos.getY() + 0.25F),
						(double)blockPos.getZ() + 0.25,
						(double)((float)blockPos.getX() + 0.75F),
						(double)((float)blockPos.getY() + 0.75F),
						(double)((float)blockPos.getZ() + 0.75F)
					)
					.offset(-d, -e, -g),
				0.0F,
				1.0F,
				0.0F,
				0.5F
			);

			for (int i = 0; i < path.getLength(); i++) {
				PathNode pathNode = path.getNode(i);
				if (method_23119(camera, pathNode.method_22879()) <= 40.0F) {
					float h = i == path.getCurrentNodeIndex() ? 1.0F : 0.0F;
					float j = i == path.getCurrentNodeIndex() ? 0.0F : 1.0F;
					DebugRenderer.method_23102(
						new Box(
								(double)((float)pathNode.x + 0.5F - f),
								(double)((float)pathNode.y + 0.01F * (float)i),
								(double)((float)pathNode.z + 0.5F - f),
								(double)((float)pathNode.x + 0.5F + f),
								(double)((float)pathNode.y + 0.25F + 0.01F * (float)i),
								(double)((float)pathNode.z + 0.5F + f)
							)
							.offset(-d, -e, -g),
						h,
						0.0F,
						j,
						0.5F
					);
				}
			}
		}

		if (bl) {
			for (PathNode pathNode2 : path.method_22881()) {
				if (method_23119(camera, pathNode2.method_22879()) <= 40.0F) {
					DebugRenderer.method_23105(String.format("%s", pathNode2.type), (double)pathNode2.x + 0.5, (double)pathNode2.y + 0.75, (double)pathNode2.z + 0.5, -65536);
					DebugRenderer.method_23105(
						String.format(Locale.ROOT, "%.2f", pathNode2.penalty), (double)pathNode2.x + 0.5, (double)pathNode2.y + 0.25, (double)pathNode2.z + 0.5, -65536
					);
				}
			}

			for (PathNode pathNode2x : path.method_22880()) {
				if (method_23119(camera, pathNode2x.method_22879()) <= 40.0F) {
					DebugRenderer.method_23105(
						String.format("%s", pathNode2x.type), (double)pathNode2x.x + 0.5, (double)pathNode2x.y + 0.75, (double)pathNode2x.z + 0.5, -16776961
					);
					DebugRenderer.method_23105(
						String.format(Locale.ROOT, "%.2f", pathNode2x.penalty), (double)pathNode2x.x + 0.5, (double)pathNode2x.y + 0.25, (double)pathNode2x.z + 0.5, -16776961
					);
				}
			}
		}

		if (bl2) {
			for (int ix = 0; ix < path.getLength(); ix++) {
				PathNode pathNode = path.getNode(ix);
				if (method_23119(camera, pathNode.method_22879()) <= 40.0F) {
					DebugRenderer.method_23105(String.format("%s", pathNode.type), (double)pathNode.x + 0.5, (double)pathNode.y + 0.75, (double)pathNode.z + 0.5, -1);
					DebugRenderer.method_23105(
						String.format(Locale.ROOT, "%.2f", pathNode.penalty), (double)pathNode.x + 0.5, (double)pathNode.y + 0.25, (double)pathNode.z + 0.5, -1
					);
				}
			}
		}
	}

	public static void method_23117(Camera camera, Path path) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		double d = camera.getPos().x;
		double e = camera.getPos().y;
		double f = camera.getPos().z;
		bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);

		for (int i = 0; i < path.getLength(); i++) {
			PathNode pathNode = path.getNode(i);
			if (!(method_23119(camera, pathNode.method_22879()) > 40.0F)) {
				float g = (float)i / (float)path.getLength() * 0.33F;
				int j = i == 0 ? 0 : MathHelper.hsvToRgb(g, 0.9F, 0.9F);
				int k = j >> 16 & 0xFF;
				int l = j >> 8 & 0xFF;
				int m = j & 0xFF;
				bufferBuilder.vertex((double)pathNode.x - d + 0.5, (double)pathNode.y - e + 0.5, (double)pathNode.z - f + 0.5).color(k, l, m, 255).next();
			}
		}

		tessellator.draw();
	}

	private static float method_23119(Camera camera, BlockPos blockPos) {
		return (float)(
			Math.abs((double)blockPos.getX() - camera.getPos().x)
				+ Math.abs((double)blockPos.getY() - camera.getPos().y)
				+ Math.abs((double)blockPos.getZ() - camera.getPos().z)
		);
	}

	private Camera method_23120() {
		return this.client.gameRenderer.getCamera();
	}
}
