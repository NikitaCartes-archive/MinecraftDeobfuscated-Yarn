package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class class_384 extends class_382 {
	public class_384() {
		super(new Identifier(""), 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
	}

	@Override
	public void method_2025(TextureManager textureManager, boolean bl, float f, float g, VertexBuffer vertexBuffer, float h, float i, float j, float k) {
	}

	@Nullable
	@Override
	public Identifier method_2026() {
		return null;
	}
}
