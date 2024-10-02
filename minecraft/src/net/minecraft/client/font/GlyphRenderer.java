package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class GlyphRenderer {
	private final TextRenderLayerSet textRenderLayers;
	private final float minU;
	private final float maxU;
	private final float minV;
	private final float maxV;
	private final float minX;
	private final float maxX;
	private final float minY;
	private final float maxY;

	public GlyphRenderer(TextRenderLayerSet textRenderLayers, float minU, float maxU, float minV, float maxV, float minX, float maxX, float minY, float maxY) {
		this.textRenderLayers = textRenderLayers;
		this.minU = minU;
		this.maxU = maxU;
		this.minV = minV;
		this.maxV = maxV;
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
	}

	public void draw(boolean italic, float x, float y, Matrix4f matrix, VertexConsumer vertexConsumer, int color, int light) {
		float f = x + this.minX;
		float g = x + this.maxX;
		float h = y + this.minY;
		float i = y + this.maxY;
		float j = italic ? 1.0F - 0.25F * this.minY : 0.0F;
		float k = italic ? 1.0F - 0.25F * this.maxY : 0.0F;
		vertexConsumer.vertex(matrix, f + j, h, 0.0F).color(color).texture(this.minU, this.minV).light(light);
		vertexConsumer.vertex(matrix, f + k, i, 0.0F).color(color).texture(this.minU, this.maxV).light(light);
		vertexConsumer.vertex(matrix, g + k, i, 0.0F).color(color).texture(this.maxU, this.maxV).light(light);
		vertexConsumer.vertex(matrix, g + j, h, 0.0F).color(color).texture(this.maxU, this.minV).light(light);
	}

	public void drawRectangle(GlyphRenderer.Rectangle rectangle, Matrix4f matrix, VertexConsumer vertexConsumer, int light) {
		vertexConsumer.vertex(matrix, rectangle.minX, rectangle.minY, rectangle.zIndex).color(rectangle.color).texture(this.minU, this.minV).light(light);
		vertexConsumer.vertex(matrix, rectangle.maxX, rectangle.minY, rectangle.zIndex).color(rectangle.color).texture(this.minU, this.maxV).light(light);
		vertexConsumer.vertex(matrix, rectangle.maxX, rectangle.maxY, rectangle.zIndex).color(rectangle.color).texture(this.maxU, this.maxV).light(light);
		vertexConsumer.vertex(matrix, rectangle.minX, rectangle.maxY, rectangle.zIndex).color(rectangle.color).texture(this.maxU, this.minV).light(light);
	}

	public RenderLayer getLayer(TextRenderer.TextLayerType layerType) {
		return this.textRenderLayers.getRenderLayer(layerType);
	}

	@Environment(EnvType.CLIENT)
	public static class Rectangle {
		protected final float minX;
		protected final float minY;
		protected final float maxX;
		protected final float maxY;
		protected final float zIndex;
		protected final int color;

		public Rectangle(float minX, float minY, float maxX, float maxY, float zIndex, int color) {
			this.minX = minX;
			this.minY = minY;
			this.maxX = maxX;
			this.maxY = maxY;
			this.zIndex = zIndex;
			this.color = color;
		}
	}
}
