package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4184;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PathfindingDebugRenderer implements DebugRenderer.DebugRenderer {
	private final MinecraftClient client;
	private final Map<Integer, Path> field_4616 = Maps.<Integer, Path>newHashMap();
	private final Map<Integer, Float> field_4617 = Maps.<Integer, Float>newHashMap();
	private final Map<Integer, Long> field_4615 = Maps.<Integer, Long>newHashMap();
	private class_4184 field_4618;
	private double field_4621;
	private double field_4620;
	private double field_4619;

	public PathfindingDebugRenderer(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	public void method_3869(int i, Path path, float f) {
		this.field_4616.put(i, path);
		this.field_4615.put(i, SystemUtil.getMeasuringTimeMs());
		this.field_4617.put(i, f);
	}

	@Override
	public void render(long l) {
		if (!this.field_4616.isEmpty()) {
			long m = SystemUtil.getMeasuringTimeMs();
			this.field_4618 = this.client.field_1773.method_19418();
			this.field_4621 = this.field_4618.method_19326().x;
			this.field_4620 = this.field_4618.method_19326().y;
			this.field_4619 = this.field_4618.method_19326().z;
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
			);
			GlStateManager.color4f(0.0F, 1.0F, 0.0F, 0.75F);
			GlStateManager.disableTexture();
			GlStateManager.lineWidth(6.0F);

			for (Integer integer : this.field_4616.keySet()) {
				Path path = (Path)this.field_4616.get(integer);
				float f = (Float)this.field_4617.get(integer);
				this.method_3868(path);
				PathNode pathNode = path.method_48();
				if (!(this.method_3867(pathNode) > 40.0F)) {
					WorldRenderer.drawBox(
						new BoundingBox(
								(double)((float)pathNode.x + 0.25F),
								(double)((float)pathNode.y + 0.25F),
								(double)pathNode.z + 0.25,
								(double)((float)pathNode.x + 0.75F),
								(double)((float)pathNode.y + 0.75F),
								(double)((float)pathNode.z + 0.75F)
							)
							.offset(-this.field_4621, -this.field_4620, -this.field_4619),
						0.0F,
						1.0F,
						0.0F,
						0.5F
					);

					for (int i = 0; i < path.getLength(); i++) {
						PathNode pathNode2 = path.getNode(i);
						if (!(this.method_3867(pathNode2) > 40.0F)) {
							float g = i == path.getCurrentNodeIndex() ? 1.0F : 0.0F;
							float h = i == path.getCurrentNodeIndex() ? 0.0F : 1.0F;
							WorldRenderer.drawBox(
								new BoundingBox(
										(double)((float)pathNode2.x + 0.5F - f),
										(double)((float)pathNode2.y + 0.01F * (float)i),
										(double)((float)pathNode2.z + 0.5F - f),
										(double)((float)pathNode2.x + 0.5F + f),
										(double)((float)pathNode2.y + 0.25F + 0.01F * (float)i),
										(double)((float)pathNode2.z + 0.5F + f)
									)
									.offset(-this.field_4621, -this.field_4620, -this.field_4619),
								g,
								0.0F,
								h,
								0.5F
							);
						}
					}
				}
			}

			for (Integer integerx : this.field_4616.keySet()) {
				Path path = (Path)this.field_4616.get(integerx);

				for (PathNode pathNode2 : path.method_37()) {
					if (!(this.method_3867(pathNode2) > 40.0F)) {
						DebugRenderer.method_3714(String.format("%s", pathNode2.type), (double)pathNode2.x + 0.5, (double)pathNode2.y + 0.75, (double)pathNode2.z + 0.5, -65536);
						DebugRenderer.method_3714(
							String.format(Locale.ROOT, "%.2f", pathNode2.field_43), (double)pathNode2.x + 0.5, (double)pathNode2.y + 0.25, (double)pathNode2.z + 0.5, -65536
						);
					}
				}

				for (PathNode pathNode2x : path.method_43()) {
					if (!(this.method_3867(pathNode2x) > 40.0F)) {
						DebugRenderer.method_3714(
							String.format("%s", pathNode2x.type), (double)pathNode2x.x + 0.5, (double)pathNode2x.y + 0.75, (double)pathNode2x.z + 0.5, -16776961
						);
						DebugRenderer.method_3714(
							String.format(Locale.ROOT, "%.2f", pathNode2x.field_43), (double)pathNode2x.x + 0.5, (double)pathNode2x.y + 0.25, (double)pathNode2x.z + 0.5, -16776961
						);
					}
				}

				for (int j = 0; j < path.getLength(); j++) {
					PathNode pathNode = path.getNode(j);
					if (!(this.method_3867(pathNode) > 40.0F)) {
						DebugRenderer.method_3714(String.format("%s", pathNode.type), (double)pathNode.x + 0.5, (double)pathNode.y + 0.75, (double)pathNode.z + 0.5, -1);
						DebugRenderer.method_3714(
							String.format(Locale.ROOT, "%.2f", pathNode.field_43), (double)pathNode.x + 0.5, (double)pathNode.y + 0.25, (double)pathNode.z + 0.5, -1
						);
					}
				}
			}

			for (Integer integer2 : (Integer[])this.field_4615.keySet().toArray(new Integer[0])) {
				if (m - (Long)this.field_4615.get(integer2) > 20000L) {
					this.field_4616.remove(integer2);
					this.field_4615.remove(integer2);
				}
			}

			GlStateManager.enableTexture();
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}

	public void method_3868(Path path) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		bufferBuilder.method_1328(3, VertexFormats.field_1576);

		for (int i = 0; i < path.getLength(); i++) {
			PathNode pathNode = path.getNode(i);
			if (!(this.method_3867(pathNode) > 40.0F)) {
				float f = (float)i / (float)path.getLength() * 0.33F;
				int j = i == 0 ? 0 : MathHelper.hsvToRgb(f, 0.9F, 0.9F);
				int k = j >> 16 & 0xFF;
				int l = j >> 8 & 0xFF;
				int m = j & 0xFF;
				bufferBuilder.vertex((double)pathNode.x - this.field_4621 + 0.5, (double)pathNode.y - this.field_4620 + 0.5, (double)pathNode.z - this.field_4619 + 0.5)
					.color(k, l, m, 255)
					.next();
			}
		}

		tessellator.draw();
	}

	private float method_3867(PathNode pathNode) {
		return (float)(
			Math.abs((double)pathNode.x - this.field_4618.method_19326().x)
				+ Math.abs((double)pathNode.y - this.field_4618.method_19326().y)
				+ Math.abs((double)pathNode.z - this.field_4618.method_19326().z)
		);
	}
}
