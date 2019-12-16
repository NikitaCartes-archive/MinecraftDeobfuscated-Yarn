package net.minecraft.data;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SnbtProvider implements DataProvider {
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
	public void run(DataCache dataCache) throws IOException {
		Path path = this.root.getOutput();
		List<CompletableFuture<SnbtProvider.CompressedData>> list = Lists.<CompletableFuture<SnbtProvider.CompressedData>>newArrayList();

		for (Path path2 : this.root.getInputs()) {
			Files.walk(path2)
				.filter(pathx -> pathx.toString().endsWith(".snbt"))
				.forEach(
					path2x -> list.add(CompletableFuture.supplyAsync(() -> this.toCompressedNbt(path2x, this.getFileName(path2, path2x)), Util.getServerWorkerExecutor()))
				);
		}

		((List)Util.combine(list).join()).stream().filter(Objects::nonNull).forEach(compressedData -> this.write(dataCache, compressedData, path));
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

			SnbtProvider.CompressedData var9;
			try {
				String string = IOUtils.toString(bufferedReader);
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				NbtIo.writeCompressed(this.write(name, StringNbtReader.parse(string)), byteArrayOutputStream);
				byte[] bs = byteArrayOutputStream.toByteArray();
				String string2 = SHA1.hashBytes(bs).toString();
				var9 = new SnbtProvider.CompressedData(name, bs, string2);
			} catch (Throwable var20) {
				var4 = var20;
				throw var20;
			} finally {
				if (bufferedReader != null) {
					if (var4 != null) {
						try {
							bufferedReader.close();
						} catch (Throwable var19) {
							var4.addSuppressed(var19);
						}
					} else {
						bufferedReader.close();
					}
				}
			}

			return var9;
		} catch (CommandSyntaxException var22) {
			LOGGER.error("Couldn't convert {} from SNBT to NBT at {} as it's invalid SNBT", name, path, var22);
		} catch (IOException var23) {
			LOGGER.error("Couldn't convert {} from SNBT to NBT at {}", name, path, var23);
		}

		return null;
	}

	private void write(DataCache dataCache, SnbtProvider.CompressedData compressedData, Path path) {
		Path path2 = path.resolve(compressedData.name + ".nbt");

		try {
			if (!Objects.equals(dataCache.getOldSha1(path2), compressedData.sha1) || !Files.exists(path2, new LinkOption[0])) {
				Files.createDirectories(path2.getParent());
				OutputStream outputStream = Files.newOutputStream(path2);
				Throwable var6 = null;

				try {
					outputStream.write(compressedData.bytes);
				} catch (Throwable var16) {
					var6 = var16;
					throw var16;
				} finally {
					if (outputStream != null) {
						if (var6 != null) {
							try {
								outputStream.close();
							} catch (Throwable var15) {
								var6.addSuppressed(var15);
							}
						} else {
							outputStream.close();
						}
					}
				}
			}

			dataCache.updateSha1(path2, compressedData.sha1);
		} catch (IOException var18) {
			LOGGER.error("Couldn't write structure {} at {}", compressedData.name, path2, var18);
		}
	}

	static class CompressedData {
		private final String name;
		private final byte[] bytes;
		private final String sha1;

		public CompressedData(String name, byte[] bytes, String sha1) {
			this.name = name;
			this.bytes = bytes;
			this.sha1 = sha1;
		}
	}

	@FunctionalInterface
	public interface Tweaker {
		CompoundTag write(String name, CompoundTag nbt);
	}
}
