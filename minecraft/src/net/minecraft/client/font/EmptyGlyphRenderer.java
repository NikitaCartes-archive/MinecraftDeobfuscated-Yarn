package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class EmptyGlyphRenderer extends GlyphRenderer {
	public static final EmptyGlyphRenderer INSTANCE = new EmptyGlyphRenderer();

	public EmptyGlyphRenderer() {
		super(
			RenderLayer.getText(new Identifier("")),
			RenderLayer.getTextSeeThrough(new Identifier("")),
			RenderLayer.getTextPolygonOffset(new Identifier("")),
			0.0F,
			0.0F,
			0.0F,
			0.0F,
			0.0F,
			0.0F,
			0.0F,
			0.0F
		);
	}

	@Override
	public void draw(boolean bl, boolean bl2, float f, float g, Matrix4f matrix4f, VertexConsumer vertexConsumer, float h, float i, float j, float k, int l) {
	}
}
