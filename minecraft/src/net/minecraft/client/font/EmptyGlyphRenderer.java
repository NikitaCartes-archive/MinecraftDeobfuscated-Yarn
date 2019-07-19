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
	public void draw(TextureManager textureManager, boolean italic, float x, float y, BufferBuilder buffer, float red, float green, float blue, float alpha) {
	}

	@Nullable
	@Override
	public Identifier getId() {
		return null;
	}
}
