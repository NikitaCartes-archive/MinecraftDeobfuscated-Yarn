package net.minecraft.client.gl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SimpleFramebuffer extends Framebuffer {
	public SimpleFramebuffer(int width, int height, boolean useDepth, boolean getError) {
		super(useDepth);
		RenderSystem.assertOnRenderThreadOrInit();
		this.resize(width, height, getError);
	}
}
