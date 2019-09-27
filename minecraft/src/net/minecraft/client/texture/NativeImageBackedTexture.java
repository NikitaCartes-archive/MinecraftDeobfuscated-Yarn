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

	public NativeImageBackedTexture(NativeImage nativeImage) {
		this.image = nativeImage;
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(() -> {
				TextureUtil.prepareImage(this.getGlId(), this.image.getWidth(), this.image.getHeight());
				this.upload();
			});
		} else {
			TextureUtil.prepareImage(this.getGlId(), this.image.getWidth(), this.image.getHeight());
			this.upload();
		}
	}

	public NativeImageBackedTexture(int i, int j, boolean bl) {
		RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
		this.image = new NativeImage(i, j, bl);
		TextureUtil.prepareImage(this.getGlId(), this.image.getWidth(), this.image.getHeight());
	}

	@Override
	public void load(ResourceManager resourceManager) throws IOException {
	}

	public void upload() {
		this.method_23207();
		this.image.upload(0, 0, 0, false);
	}

	@Nullable
	public NativeImage getImage() {
		return this.image;
	}

	public void setImage(NativeImage nativeImage) throws Exception {
		this.image.close();
		this.image = nativeImage;
	}

	public void close() {
		this.image.close();
		this.clearGlId();
		this.image = null;
	}
}
