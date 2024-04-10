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
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class SoundLoader {
	private final ResourceFactory resourceFactory;
	private final Map<Identifier, CompletableFuture<StaticSound>> loadedSounds = Maps.<Identifier, CompletableFuture<StaticSound>>newHashMap();

	public SoundLoader(ResourceFactory resourceFactory) {
		this.resourceFactory = resourceFactory;
	}

	public CompletableFuture<StaticSound> loadStatic(Identifier id) {
		return (CompletableFuture<StaticSound>)this.loadedSounds.computeIfAbsent(id, id2 -> CompletableFuture.supplyAsync(() -> {
				try {
					InputStream inputStream = this.resourceFactory.open(id2);

					StaticSound var5;
					try {
						NonRepeatingAudioStream nonRepeatingAudioStream = new OggAudioStream(inputStream);

						try {
							ByteBuffer byteBuffer = nonRepeatingAudioStream.readAll();
							var5 = new StaticSound(byteBuffer, nonRepeatingAudioStream.getFormat());
						} catch (Throwable var8) {
							try {
								nonRepeatingAudioStream.close();
							} catch (Throwable var7) {
								var8.addSuppressed(var7);
							}

							throw var8;
						}

						nonRepeatingAudioStream.close();
					} catch (Throwable var9) {
						if (inputStream != null) {
							try {
								inputStream.close();
							} catch (Throwable var6) {
								var9.addSuppressed(var6);
							}
						}

						throw var9;
					}

					if (inputStream != null) {
						inputStream.close();
					}

					return var5;
				} catch (IOException var10) {
					throw new CompletionException(var10);
				}
			}, Util.getDownloadWorkerExecutor()));
	}

	public CompletableFuture<AudioStream> loadStreamed(Identifier id, boolean repeatInstantly) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				InputStream inputStream = this.resourceFactory.open(id);
				return (AudioStream)(repeatInstantly ? new RepeatingAudioStream(OggAudioStream::new, inputStream) : new OggAudioStream(inputStream));
			} catch (IOException var4) {
				throw new CompletionException(var4);
			}
		}, Util.getDownloadWorkerExecutor());
	}

	public void close() {
		this.loadedSounds.values().forEach(soundFuture -> soundFuture.thenAccept(StaticSound::close));
		this.loadedSounds.clear();
	}

	public CompletableFuture<?> loadStatic(Collection<Sound> sounds) {
		return CompletableFuture.allOf((CompletableFuture[])sounds.stream().map(sound -> this.loadStatic(sound.getLocation())).toArray(CompletableFuture[]::new));
	}
}
