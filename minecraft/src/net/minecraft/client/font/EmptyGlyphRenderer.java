package net.minecraft.client.font;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EmptyGlyphRenderer extends GlyphRenderer {
	public EmptyGlyphRenderer() {
		super(new Identifier(""), 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
	}

	@Override
	public void method_2025(TextureManager textureManager, boolean bl, float f, float g, BufferBuilder bufferBuilder, float h, float i, float j, float k) {
	}

	@Nullable
	@Override
	public Identifier getId() {
		return null;
	}
}
