package net.minecraft.client.texture.atlas;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AtlasSprite {
	private final Identifier id;
	private final Resource resource;
	private final AtomicReference<NativeImage> image = new AtomicReference();
	private final AtomicInteger regionCount;

	public AtlasSprite(Identifier id, Resource resource, int regionCount) {
		this.id = id;
		this.resource = resource;
		this.regionCount = new AtomicInteger(regionCount);
	}

	public NativeImage read() throws IOException {
		NativeImage nativeImage = (NativeImage)this.image.get();
		if (nativeImage == null) {
			synchronized (this) {
				nativeImage = (NativeImage)this.image.get();
				if (nativeImage == null) {
					try {
						InputStream inputStream = this.resource.getInputStream();

						try {
							nativeImage = NativeImage.read(inputStream);
							this.image.set(nativeImage);
						} catch (Throwable var8) {
							if (inputStream != null) {
								try {
									inputStream.close();
								} catch (Throwable var7) {
									var8.addSuppressed(var7);
								}
							}

							throw var8;
						}

						if (inputStream != null) {
							inputStream.close();
						}
					} catch (IOException var9) {
						throw new IOException("Failed to load image " + this.id, var9);
					}
				}
			}
		}

		return nativeImage;
	}

	public void close() {
		int i = this.regionCount.decrementAndGet();
		if (i <= 0) {
			NativeImage nativeImage = (NativeImage)this.image.getAndSet(null);
			if (nativeImage != null) {
				nativeImage.close();
			}
		}
	}
}
