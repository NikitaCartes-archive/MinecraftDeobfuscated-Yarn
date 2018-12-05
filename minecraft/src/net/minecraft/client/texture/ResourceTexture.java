package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ResourceTexture extends AbstractTexture {
	private static final Logger LOGGER = LogManager.getLogger();
	protected final Identifier location;

	public ResourceTexture(Identifier identifier) {
		this.location = identifier;
	}

	@Override
	public void load(ResourceManager resourceManager) throws IOException {
		Resource resource = resourceManager.getResource(this.location);
		Throwable var3 = null;

		try (NativeImage nativeImage = NativeImage.fromInputStream(resource.getInputStream())) {
			boolean bl = false;
			boolean bl2 = false;
			if (resource.hasMetadata()) {
				try {
					TextureResourceMetadata textureResourceMetadata = resource.getMetadata(TextureResourceMetadata.READER);
					if (textureResourceMetadata != null) {
						bl = textureResourceMetadata.shouldBlur();
						bl2 = textureResourceMetadata.shouldClamp();
					}
				} catch (RuntimeException var32) {
					LOGGER.warn("Failed reading metadata of: {}", this.location, var32);
				}
			}

			this.bindTexture();
			TextureUtil.prepareImage(this.getGlId(), 0, nativeImage.getWidth(), nativeImage.getHeight());
			nativeImage.upload(0, 0, 0, 0, 0, nativeImage.getWidth(), nativeImage.getHeight(), bl, bl2, false);
		} catch (Throwable var35) {
			var3 = var35;
			throw var35;
		} finally {
			if (resource != null) {
				if (var3 != null) {
					try {
						resource.close();
					} catch (Throwable var30) {
						var3.addSuppressed(var30);
					}
				} else {
					resource.close();
				}
			}
		}
	}
}
