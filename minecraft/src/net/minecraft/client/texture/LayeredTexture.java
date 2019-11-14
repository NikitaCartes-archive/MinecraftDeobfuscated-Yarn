package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class LayeredTexture extends AbstractTexture {
	private static final Logger LOGGER = LogManager.getLogger();
	public final List<String> locations;

	public LayeredTexture(String... locations) {
		this.locations = Lists.<String>newArrayList(locations);
		if (this.locations.isEmpty()) {
			throw new IllegalStateException("Layered texture with no layers.");
		}
	}

	@Override
	public void load(ResourceManager manager) throws IOException {
		Iterator<String> iterator = this.locations.iterator();
		String string = (String)iterator.next();

		try {
			Resource resource = manager.getResource(new Identifier(string));
			Throwable var5 = null;

			try {
				NativeImage nativeImage = NativeImage.read(resource.getInputStream());

				while (iterator.hasNext()) {
					String string2 = (String)iterator.next();
					if (string2 != null) {
						Resource resource2 = manager.getResource(new Identifier(string2));
						Throwable var9 = null;

						try (NativeImage nativeImage2 = NativeImage.read(resource2.getInputStream())) {
							for (int i = 0; i < nativeImage2.getHeight(); i++) {
								for (int j = 0; j < nativeImage2.getWidth(); j++) {
									nativeImage.blendPixel(j, i, nativeImage2.getPixelRgba(j, i));
								}
							}
						} catch (Throwable var61) {
							var9 = var61;
							throw var61;
						} finally {
							if (resource2 != null) {
								if (var9 != null) {
									try {
										resource2.close();
									} catch (Throwable var57) {
										var9.addSuppressed(var57);
									}
								} else {
									resource2.close();
								}
							}
						}
					}
				}

				if (!RenderSystem.isOnRenderThreadOrInit()) {
					RenderSystem.recordRenderCall(() -> this.method_22805(nativeImage));
				} else {
					this.method_22805(nativeImage);
				}
			} catch (Throwable var63) {
				var5 = var63;
				throw var63;
			} finally {
				if (resource != null) {
					if (var5 != null) {
						try {
							resource.close();
						} catch (Throwable var56) {
							var5.addSuppressed(var56);
						}
					} else {
						resource.close();
					}
				}
			}
		} catch (IOException var65) {
			LOGGER.error("Couldn't load layered image", (Throwable)var65);
		}
	}

	private void method_22805(NativeImage nativeImage) {
		TextureUtil.prepareImage(this.getGlId(), nativeImage.getWidth(), nativeImage.getHeight());
		nativeImage.upload(0, 0, 0, true);
	}
}
