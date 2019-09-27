package net.minecraft.client.font;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4588;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EmptyGlyphRenderer extends GlyphRenderer {
	public EmptyGlyphRenderer() {
		super(new Identifier(""), 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
	}

	@Override
	public void draw(boolean bl, float f, float g, Matrix4f matrix4f, class_4588 arg, float h, float i, float j, float k, int l) {
	}

	@Nullable
	@Override
	public Identifier getId() {
		return null;
	}
}
