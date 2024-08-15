package net.minecraft.client.gl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.ClosableFactory;

@Environment(EnvType.CLIENT)
public record SimpleFramebufferFactory(int width, int height, boolean useDepth) implements ClosableFactory<Framebuffer> {
	public Framebuffer create() {
		return new SimpleFramebuffer(this.width, this.height, this.useDepth);
	}

	public void close(Framebuffer framebuffer) {
		framebuffer.delete();
	}
}
