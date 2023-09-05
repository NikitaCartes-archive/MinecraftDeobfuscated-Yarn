package net.minecraft.data;

import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class SnbtProvider implements DataProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final DataOutput output;
	private final Iterable<Path> paths;
	private final List<SnbtProvider.Tweaker> write = Lists.<SnbtProvider.Tweaker>newArrayList();

	public SnbtProvider(DataOutput output, Iterable<Path> paths) {
		this.output = output;
		this.paths = paths;
	}

	public SnbtProvider addWriter(SnbtProvider.Tweaker tweaker) {
		this.write.add(tweaker);
		return this;
	}

	private NbtCompound write(String key, NbtCompound compound) {
		NbtCompound nbtCompound = compound;

		for (SnbtProvider.Tweaker tweaker : this.write) {
			nbtCompound = tweaker.write(key, nbtCompound);
		}

		return nbtCompound;
	}

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		Path path = this.output.getPath();
		List<CompletableFuture<?>> list = Lists.<CompletableFuture<?>>newArrayList();

		for (Path path2 : this.paths) {
			list.add(
				CompletableFuture.supplyAsync(
						() -> {
							try {
								Stream<Path> stream = Files.walk(path2);

								CompletableFuture var5x;
								try {
									var5x = CompletableFuture.allOf(
										(CompletableFuture[])stream.filter(pathxx -> pathxx.toString().endsWith(".snbt")).map(pathxx -> CompletableFuture.runAsync(() -> {
												SnbtProvider.CompressedData compressedData = this.toCompressedNbt(pathxx, this.getFileName(path2, pathxx));
												this.write(writer, compressedData, path);
											}, Util.getMainWorkerExecutor())).toArray(CompletableFuture[]::new)
									);
								} catch (Throwable var8) {
									if (stream != null) {
										try {
											stream.close();
										} catch (Throwable var7) {
											var8.addSuppressed(var7);
										}
									}

									throw var8;
								}

								if (stream != null) {
									stream.close();
								}

								return var5x;
							} catch (Exception var9) {
								throw new RuntimeException("Failed to read structure input directory, aborting", var9);
							}
						},
						Util.getMainWorkerExecutor()
					)
					.thenCompose(future -> future)
			);
		}

		return Util.combine(list);
	}

	@Override
	public final String getName() {
		return "SNBT -> NBT";
	}

	private String getFileName(Path root, Path file) {
		String string = root.relativize(file).toString().replaceAll("\\\\", "/");
		return string.substring(0, string.length() - ".snbt".length());
	}

	private SnbtProvider.CompressedData toCompressedNbt(Path path, String name) {
		try {
			BufferedReader bufferedReader = Files.newBufferedReader(path);

			SnbtProvider.CompressedData var10;
			try {
				String string = IOUtils.toString(bufferedReader);
				NbtCompound nbtCompound = this.write(name, NbtHelper.fromNbtProviderString(string));
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				HashingOutputStream hashingOutputStream = new HashingOutputStream(Hashing.sha1(), byteArrayOutputStream);
				NbtIo.writeCompressed(nbtCompound, hashingOutputStream);
				byte[] bs = byteArrayOutputStream.toByteArray();
				HashCode hashCode = hashingOutputStream.hash();
				var10 = new SnbtProvider.CompressedData(name, bs, hashCode);
			} catch (Throwable var12) {
				if (bufferedReader != null) {
					try {
						bufferedReader.close();
					} catch (Throwable var11) {
						var12.addSuppressed(var11);
					}
				}

				throw var12;
			}

			if (bufferedReader != null) {
				bufferedReader.close();
			}

			return var10;
		} catch (Throwable var13) {
			throw new SnbtProvider.CompressionException(path, var13);
		}
	}

	private void write(DataWriter cache, SnbtProvider.CompressedData data, Path root) {
		Path path = root.resolve(data.name + ".nbt");

		try {
			cache.write(path, data.bytes, data.sha1);
		} catch (IOException var6) {
			LOGGER.error("Couldn't write structure {} at {}", data.name, path, var6);
		}
	}

	static record CompressedData(String name, byte[] bytes, HashCode sha1) {
	}

	static class CompressionException extends RuntimeException {
		public CompressionException(Path path, Throwable cause) {
			super(path.toAbsolutePath().toString(), cause);
		}
	}

	@FunctionalInterface
	public interface Tweaker {
		NbtCompound write(String name, NbtCompound nbt);
	}
}
