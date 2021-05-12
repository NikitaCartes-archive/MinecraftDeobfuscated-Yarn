package net.minecraft.client.sound;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class SoundLoader {
	private final ResourceManager resourceManager;
	private final Map<Identifier, CompletableFuture<StaticSound>> loadedSounds = Maps.<Identifier, CompletableFuture<StaticSound>>newHashMap();

	public SoundLoader(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	public CompletableFuture<StaticSound> loadStatic(Identifier id) {
		return (CompletableFuture<StaticSound>)this.loadedSounds.computeIfAbsent(id, identifier -> CompletableFuture.supplyAsync(() -> {
				try {
					Resource resource = this.resourceManager.getResource(identifier);

					StaticSound var6;
					try {
						InputStream inputStream = resource.getInputStream();

						try {
							OggAudioStream oggAudioStream = new OggAudioStream(inputStream);

							try {
								ByteBuffer byteBuffer = oggAudioStream.getBuffer();
								var6 = new StaticSound(byteBuffer, oggAudioStream.getFormat());
							} catch (Throwable var10) {
								try {
									oggAudioStream.close();
								} catch (Throwable var9) {
									var10.addSuppressed(var9);
								}

								throw var10;
							}

							oggAudioStream.close();
						} catch (Throwable var11) {
							if (inputStream != null) {
								try {
									inputStream.close();
								} catch (Throwable var8) {
									var11.addSuppressed(var8);
								}
							}

							throw var11;
						}

						if (inputStream != null) {
							inputStream.close();
						}
					} catch (Throwable var12) {
						if (resource != null) {
							try {
								resource.close();
							} catch (Throwable var7) {
								var12.addSuppressed(var7);
							}
						}

						throw var12;
					}

					if (resource != null) {
						resource.close();
					}

					return var6;
				} catch (IOException var13) {
					throw new CompletionException(var13);
				}
			}, Util.getMainWorkerExecutor()));
	}

	public CompletableFuture<AudioStream> loadStreamed(Identifier id, boolean repeatInstantly) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Resource resource = this.resourceManager.getResource(id);
				InputStream inputStream = resource.getInputStream();
				return (AudioStream)(repeatInstantly ? new RepeatingAudioStream(OggAudioStream::new, inputStream) : new OggAudioStream(inputStream));
			} catch (IOException var5) {
				throw new CompletionException(var5);
			}
		}, Util.getMainWorkerExecutor());
	}

	public void close() {
		this.loadedSounds.values().forEach(completableFuture -> completableFuture.thenAccept(StaticSound::close));
		this.loadedSounds.clear();
	}

	public CompletableFuture<?> loadStatic(Collection<Sound> sounds) {
		return CompletableFuture.allOf((CompletableFuture[])sounds.stream().map(sound -> this.loadStatic(sound.getLocation())).toArray(CompletableFuture[]::new));
	}
}
