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
					Throwable var3 = null;

					StaticSound var9;
					try {
						InputStream inputStream = resource.getInputStream();
						Throwable var5 = null;

						try {
							OggAudioStream oggAudioStream = new OggAudioStream(inputStream);
							Throwable var7 = null;

							try {
								ByteBuffer byteBuffer = oggAudioStream.getBuffer();
								var9 = new StaticSound(byteBuffer, oggAudioStream.getFormat());
							} catch (Throwable var56) {
								var7 = var56;
								throw var56;
							} finally {
								if (oggAudioStream != null) {
									if (var7 != null) {
										try {
											oggAudioStream.close();
										} catch (Throwable var55) {
											var7.addSuppressed(var55);
										}
									} else {
										oggAudioStream.close();
									}
								}
							}
						} catch (Throwable var58) {
							var5 = var58;
							throw var58;
						} finally {
							if (inputStream != null) {
								if (var5 != null) {
									try {
										inputStream.close();
									} catch (Throwable var54) {
										var5.addSuppressed(var54);
									}
								} else {
									inputStream.close();
								}
							}
						}
					} catch (Throwable var60) {
						var3 = var60;
						throw var60;
					} finally {
						if (resource != null) {
							if (var3 != null) {
								try {
									resource.close();
								} catch (Throwable var53) {
									var3.addSuppressed(var53);
								}
							} else {
								resource.close();
							}
						}
					}

					return var9;
				} catch (IOException var62) {
					throw new CompletionException(var62);
				}
			}, Util.getServerWorkerExecutor()));
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
		}, Util.getServerWorkerExecutor());
	}

	public void close() {
		this.loadedSounds.values().forEach(completableFuture -> completableFuture.thenAccept(StaticSound::close));
		this.loadedSounds.clear();
	}

	public CompletableFuture<?> loadStatic(Collection<Sound> sounds) {
		return CompletableFuture.allOf((CompletableFuture[])sounds.stream().map(sound -> this.loadStatic(sound.getLocation())).toArray(CompletableFuture[]::new));
	}
}
