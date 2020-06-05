package net.minecraft.client.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceManager;

@Environment(EnvType.CLIENT)
public class NativeImageBackedTexture extends AbstractTexture implements AutoCloseable {
	private NativeImage image;

	public NativeImageBackedTexture(NativeImage image) {
		this.image = image;
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(() -> {
				TextureUtil.allocate(this.getGlId(), this.image.getWidth(), this.image.getHeight());
				this.upload();
			});
		} else {
			TextureUtil.allocate(this.getGlId(), this.image.getWidth(), this.image.getHeight());
			this.upload();
		}
	}

	public NativeImageBackedTexture(int width, int height, boolean useStb) {
		RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
		this.image = new NativeImage(width, height, useStb);
		TextureUtil.allocate(this.getGlId(), this.image.getWidth(), this.image.getHeight());
	}

	@Override
	public void load(ResourceManager manager) throws IOException {
	}

	public void upload() {
		this.bindTexture();
		this.image.upload(0, 0, 0, false);
	}

	@Nullable
	public NativeImage getImage() {
		return this.image;
	}

	public void setImage(NativeImage image) throws Exception {
		this.image.close();
		this.image = image;
	}

	public void close() {
		this.image.close();
		this.clearGlId();
		this.image = null;
	}
}
