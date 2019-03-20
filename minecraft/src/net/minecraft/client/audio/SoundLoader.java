package net.minecraft.client.audio;

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
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class SoundLoader {
	private final ResourceManager field_18943;
	private final Map<Identifier, CompletableFuture<SoundData>> loadedSounds = Maps.<Identifier, CompletableFuture<SoundData>>newHashMap();

	public SoundLoader(ResourceManager resourceManager) {
		this.field_18943 = resourceManager;
	}

	public CompletableFuture<SoundData> method_19743(Identifier identifier) {
		return (CompletableFuture<SoundData>)this.loadedSounds.computeIfAbsent(identifier, identifierx -> CompletableFuture.supplyAsync(() -> {
				try {
					Resource resource = this.field_18943.getResource(identifierx);
					Throwable var3 = null;

					SoundData var9;
					try {
						InputStream inputStream = resource.getInputStream();
						Throwable var5 = null;

						try {
							AudioStream audioStream = new OggAudioStream(inputStream);
							Throwable var7 = null;

							try {
								ByteBuffer byteBuffer = audioStream.getBuffer();
								var9 = new SoundData(byteBuffer, audioStream.getFormat());
							} catch (Throwable var56) {
								var7 = var56;
								throw var56;
							} finally {
								if (audioStream != null) {
									if (var7 != null) {
										try {
											audioStream.close();
										} catch (Throwable var55) {
											var7.addSuppressed(var55);
										}
									} else {
										audioStream.close();
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
			}, SystemUtil.getServerWorkerExecutor()));
	}

	public CompletableFuture<AudioStream> method_19744(Identifier identifier) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Resource resource = this.field_18943.getResource(identifier);
				InputStream inputStream = resource.getInputStream();
				return new OggAudioStream(inputStream);
			} catch (IOException var4) {
				throw new CompletionException(var4);
			}
		}, SystemUtil.getServerWorkerExecutor());
	}

	public void method_19738() {
		this.loadedSounds.values().forEach(completableFuture -> completableFuture.thenAccept(SoundData::close));
		this.loadedSounds.clear();
	}

	public CompletableFuture<?> method_19741(Collection<Sound> collection) {
		return CompletableFuture.allOf(
			(CompletableFuture[])collection.stream().map(sound -> this.method_19743(sound.getLocation())).toArray(CompletableFuture[]::new)
		);
	}
}
