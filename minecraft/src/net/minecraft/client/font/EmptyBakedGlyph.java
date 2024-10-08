package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class EmptyBakedGlyph extends BakedGlyph {
	public static final EmptyBakedGlyph INSTANCE = new EmptyBakedGlyph();

	public EmptyBakedGlyph() {
		super(TextRenderLayerSet.of(Identifier.ofVanilla("")), 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
	}

	@Override
	public void draw(BakedGlyph.DrawnGlyph glyph, Matrix4f matrices, VertexConsumer vertexConsumers, int light) {
	}
}
