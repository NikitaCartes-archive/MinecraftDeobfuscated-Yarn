package net.minecraft.data;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Util;
import org.apache.commons.io.FileUtils;
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

	private NbtCompound write(String key, NbtCompound compound) {
		NbtCompound nbtCompound = compound;

		for (SnbtProvider.Tweaker tweaker : this.write) {
			nbtCompound = tweaker.write(key, nbtCompound);
		}

		return nbtCompound;
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

		((List)Util.combine(list).join()).stream().filter(Objects::nonNull).forEach(compressedData -> this.write(cache, compressedData, path));
	}

	@Override
	public String getName() {
		return "SNBT -> NBT";
	}

	private String getFileName(Path root, Path file) {
		String string = root.relativize(file).toString().replaceAll("\\\\", "/");
		return string.substring(0, string.length() - ".snbt".length());
	}

	@Nullable
	private SnbtProvider.CompressedData toCompressedNbt(Path path, String name) {
		try {
			BufferedReader bufferedReader = Files.newBufferedReader(path);
			Throwable var4 = null;

			SnbtProvider.CompressedData var11;
			try {
				String string = IOUtils.toString(bufferedReader);
				NbtCompound nbtCompound = this.write(name, StringNbtReader.parse(string));
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				NbtIo.writeCompressed(nbtCompound, byteArrayOutputStream);
				byte[] bs = byteArrayOutputStream.toByteArray();
				String string2 = SHA1.hashBytes(bs).toString();
				String string3;
				if (field_24615 != null) {
					string3 = nbtCompound.toText("    ", 0).getString() + "\n";
				} else {
					string3 = null;
				}

				var11 = new SnbtProvider.CompressedData(name, bs, string3, string2);
			} catch (Throwable var22) {
				var4 = var22;
				throw var22;
			} finally {
				if (bufferedReader != null) {
					if (var4 != null) {
						try {
							bufferedReader.close();
						} catch (Throwable var21) {
							var4.addSuppressed(var21);
						}
					} else {
						bufferedReader.close();
					}
				}
			}

			return var11;
		} catch (CommandSyntaxException var24) {
			LOGGER.error("Couldn't convert {} from SNBT to NBT at {} as it's invalid SNBT", name, path, var24);
		} catch (IOException var25) {
			LOGGER.error("Couldn't convert {} from SNBT to NBT at {}", name, path, var25);
		}

		return null;
	}

	private void write(DataCache cache, SnbtProvider.CompressedData data, Path root) {
		if (data.field_24616 != null) {
			Path path = field_24615.resolve(data.name + ".snbt");

			try {
				FileUtils.write(path.toFile(), data.field_24616, StandardCharsets.UTF_8);
			} catch (IOException var18) {
				LOGGER.error("Couldn't write structure SNBT {} at {}", data.name, path, var18);
			}
		}

		Path path = root.resolve(data.name + ".nbt");

		try {
			if (!Objects.equals(cache.getOldSha1(path), data.sha1) || !Files.exists(path, new LinkOption[0])) {
				Files.createDirectories(path.getParent());
				OutputStream outputStream = Files.newOutputStream(path);
				Throwable var6 = null;

				try {
					outputStream.write(data.bytes);
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

			cache.updateSha1(path, data.sha1);
		} catch (IOException var20) {
			LOGGER.error("Couldn't write structure {} at {}", data.name, path, var20);
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
		NbtCompound write(String name, NbtCompound nbt);
	}
}
