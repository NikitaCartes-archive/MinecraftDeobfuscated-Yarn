package net.minecraft.data;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.data.dev.NbtProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SnbtProvider implements DataProvider {
	@Nullable
	private static final Path field_24615 = null;
	private static final Logger LOGGER = LogManager.getLogger();
	private final DataGenerator root;
	private final List<SnbtProvider.Tweaker> write = Lists.<SnbtProvider.Tweaker>newArrayList();

	public SnbtProvider(DataGenerator dataGenerator) {
		this.root = dataGenerator;
	}

	public SnbtProvider addWriter(SnbtProvider.Tweaker tweaker) {
		this.write.add(tweaker);
		return this;
	}

	private CompoundTag write(String string, CompoundTag compoundTag) {
		CompoundTag compoundTag2 = compoundTag;

		for (SnbtProvider.Tweaker tweaker : this.write) {
			compoundTag2 = tweaker.write(string, compoundTag2);
		}

		return compoundTag2;
	}

	@Override
	public void run(DataCache cache) throws IOException {
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
			Throwable var4 = null;

			SnbtProvider.CompressedData var11;
			try {
				String string = IOUtils.toString(bufferedReader);
				CompoundTag compoundTag = this.write(name, NbtHelper.method_32260(string));
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				NbtIo.writeCompressed(compoundTag, byteArrayOutputStream);
				byte[] bs = byteArrayOutputStream.toByteArray();
				String string2 = SHA1.hashBytes(bs).toString();
				String string3;
				if (field_24615 != null) {
					string3 = NbtHelper.method_32271(compoundTag);
				} else {
					string3 = null;
				}

				var11 = new SnbtProvider.CompressedData(name, bs, string3, string2);
			} catch (Throwable var21) {
				var4 = var21;
				throw var21;
			} finally {
				if (bufferedReader != null) {
					if (var4 != null) {
						try {
							bufferedReader.close();
						} catch (Throwable var20) {
							var4.addSuppressed(var20);
						}
					} else {
						bufferedReader.close();
					}
				}
			}

			return var11;
		} catch (Throwable var23) {
			throw new SnbtProvider.class_5621(path, var23);
		}
	}

	private void write(DataCache dataCache, SnbtProvider.CompressedData compressedData, Path path) {
		if (compressedData.field_24616 != null) {
			Path path2 = field_24615.resolve(compressedData.name + ".snbt");

			try {
				NbtProvider.method_32234(path2, compressedData.field_24616);
			} catch (IOException var18) {
				LOGGER.error("Couldn't write structure SNBT {} at {}", compressedData.name, path2, var18);
			}
		}

		Path path2 = path.resolve(compressedData.name + ".nbt");

		try {
			if (!Objects.equals(dataCache.getOldSha1(path2), compressedData.sha1) || !Files.exists(path2, new LinkOption[0])) {
				Files.createDirectories(path2.getParent());
				OutputStream outputStream = Files.newOutputStream(path2);
				Throwable var6 = null;

				try {
					outputStream.write(compressedData.bytes);
				} catch (Throwable var17) {
					var6 = var17;
					throw var17;
				} finally {
					if (outputStream != null) {
						if (var6 != null) {
							try {
								outputStream.close();
							} catch (Throwable var16) {
								var6.addSuppressed(var16);
							}
						} else {
							outputStream.close();
						}
					}
				}
			}

			dataCache.updateSha1(path2, compressedData.sha1);
		} catch (IOException var20) {
			LOGGER.error("Couldn't write structure {} at {}", compressedData.name, path2, var20);
		}
	}

	static class CompressedData {
		private final String name;
		private final byte[] bytes;
		@Nullable
		private final String field_24616;
		private final String sha1;

		public CompressedData(String name, byte[] bytes, @Nullable String sha1, String string) {
			this.name = name;
			this.bytes = bytes;
			this.field_24616 = sha1;
			this.sha1 = string;
		}
	}

	@FunctionalInterface
	public interface Tweaker {
		CompoundTag write(String name, CompoundTag nbt);
	}

	static class class_5621 extends RuntimeException {
		public class_5621(Path path, Throwable throwable) {
			super(path.toAbsolutePath().toString(), throwable);
		}
	}
}
