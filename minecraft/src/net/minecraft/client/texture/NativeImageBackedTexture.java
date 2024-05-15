package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class NativeImageBackedTexture extends AbstractTexture implements DynamicTexture {
	private static final Logger LOGGER = LogUtils.getLogger();
	@Nullable
	private NativeImage image;

	public NativeImageBackedTexture(NativeImage image) {
		this.image = image;
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

	public NativeImageBackedTexture(int width, int height, boolean useStb) {
		this.image = new NativeImage(width, height, useStb);
		TextureUtil.prepareImage(this.getGlId(), this.image.getWidth(), this.image.getHeight());
	}

	@Override
	public void load(ResourceManager manager) {
	}

	public void upload() {
		if (this.image != null) {
			this.bindTexture();
			this.image.upload(0, 0, 0, false);
		} else {
			LOGGER.warn("Trying to upload disposed texture {}", this.getGlId());
		}
	}

	@Nullable
	public NativeImage getImage() {
		return this.image;
	}

	public void setImage(NativeImage image) {
		if (this.image != null) {
			this.image.close();
		}

		this.image = image;
	}

	@Override
	public void close() {
		if (this.image != null) {
			this.image.close();
			this.clearGlId();
			this.image = null;
		}
	}

	@Override
	public void save(Identifier id, Path path) throws IOException {
		if (this.image != null) {
			String string = id.toUnderscoreSeparatedString() + ".png";
			Path path2 = path.resolve(string);
			this.image.writeTo(path2);
		}
	}
}
