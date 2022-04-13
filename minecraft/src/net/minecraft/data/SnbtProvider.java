package net.minecraft.data;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.data.dev.NbtProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class SnbtProvider implements DataProvider {
	@Nullable
	private static final Path DEBUG_OUTPUT_DIRECTORY = null;
	private static final Logger LOGGER = LogUtils.getLogger();
	private final DataGenerator root;
	private final List<SnbtProvider.Tweaker> write = Lists.<SnbtProvider.Tweaker>newArrayList();

	public SnbtProvider(DataGenerator generator) {
		this.root = generator;
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
	public void run(DataWriter cache) throws IOException {
		Path path = this.root.getOutput();
		List<CompletableFuture<SnbtProvider.CompressedData>> list = Lists.<CompletableFuture<SnbtProvider.CompressedData>>newArrayList();

		for (Path path2 : this.root.getInputs()) {
			Files.walk(path2)
				.filter(pathx -> pathx.toString().endsWith(".snbt"))
				.forEach(
					path2x -> list.add(CompletableFuture.supplyAsync(() -> this.toCompressedNbt(path2x, this.getFileName(path2, path2x)), Util.getMainWorkerExecutor()))
				);
		}

		boolean bl = false;

		for (CompletableFuture<SnbtProvider.CompressedData> completableFuture : list) {
			try {
				this.write(cache, (SnbtProvider.CompressedData)completableFuture.get(), path);
			} catch (Exception var8) {
				LOGGER.error("Failed to process structure", (Throwable)var8);
				bl = true;
			}
		}

		if (bl) {
			throw new IllegalStateException("Failed to convert all structures, aborting");
		}
	}

	@Override
	public String getName() {
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
				NbtIo.writeCompressed(nbtCompound, byteArrayOutputStream);
				byte[] bs = byteArrayOutputStream.toByteArray();
				String string2 = SHA1.hashBytes(bs).toString();
				String string3;
				if (DEBUG_OUTPUT_DIRECTORY != null) {
					string3 = NbtHelper.toNbtProviderString(nbtCompound);
				} else {
					string3 = null;
				}

				var10 = new SnbtProvider.CompressedData(name, bs, string3, string2);
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
		if (data.snbtContent != null) {
			Path path = DEBUG_OUTPUT_DIRECTORY.resolve(data.name + ".snbt");

			try {
				NbtProvider.writeTo(path, data.snbtContent);
			} catch (IOException var7) {
				LOGGER.error("Couldn't write structure SNBT {} at {}", data.name, path, var7);
			}
		}

		Path path = root.resolve(data.name + ".nbt");

		try {
			cache.write(path, data.bytes, data.sha1);
		} catch (IOException var6) {
			LOGGER.error("Couldn't write structure {} at {}", data.name, path, var6);
		}
	}

	static class CompressedData {
		final String name;
		final byte[] bytes;
		@Nullable
		final String snbtContent;
		final String sha1;

		public CompressedData(String name, byte[] bytes, @Nullable String snbtContent, String sha1) {
			this.name = name;
			this.bytes = bytes;
			this.snbtContent = snbtContent;
			this.sha1 = sha1;
		}
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
