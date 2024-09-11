package net.minecraft.data.dev;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.mojang.logging.LogUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.util.FixedBufferInputStream;
import net.minecraft.util.Util;
import org.slf4j.Logger;

public class NbtProvider implements DataProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Iterable<Path> paths;
	private final DataOutput output;

	public NbtProvider(DataOutput output, Collection<Path> paths) {
		this.paths = paths;
		this.output = output;
	}

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		Path path = this.output.getPath();
		List<CompletableFuture<?>> list = new ArrayList();

		for (Path path2 : this.paths) {
			list.add(
				CompletableFuture.supplyAsync(
						() -> {
							try {
								Stream<Path> stream = Files.walk(path2);

								CompletableFuture var4;
								try {
									var4 = CompletableFuture.allOf(
										(CompletableFuture[])stream.filter(pathxx -> pathxx.toString().endsWith(".nbt"))
											.map(pathxx -> CompletableFuture.runAsync(() -> convertNbtToSnbt(writer, pathxx, getLocation(path2, pathxx), path), Util.getIoWorkerExecutor()))
											.toArray(CompletableFuture[]::new)
									);
								} catch (Throwable var7) {
									if (stream != null) {
										try {
											stream.close();
										} catch (Throwable var6) {
											var7.addSuppressed(var6);
										}
									}

									throw var7;
								}

								if (stream != null) {
									stream.close();
								}

								return var4;
							} catch (IOException var8) {
								LOGGER.error("Failed to read structure input directory", (Throwable)var8);
								return CompletableFuture.completedFuture(null);
							}
						},
						Util.getMainWorkerExecutor().named("NbtToSnbt")
					)
					.thenCompose(future -> future)
			);
		}

		return CompletableFuture.allOf((CompletableFuture[])list.toArray(CompletableFuture[]::new));
	}

	@Override
	public final String getName() {
		return "NBT -> SNBT";
	}

	private static String getLocation(Path inputPath, Path filePath) {
		String string = inputPath.relativize(filePath).toString().replaceAll("\\\\", "/");
		return string.substring(0, string.length() - ".nbt".length());
	}

	@Nullable
	public static Path convertNbtToSnbt(DataWriter writer, Path inputPath, String filename, Path outputPath) {
		try {
			InputStream inputStream = Files.newInputStream(inputPath);

			Path var7;
			try {
				InputStream inputStream2 = new FixedBufferInputStream(inputStream);

				try {
					Path path = outputPath.resolve(filename + ".snbt");
					writeTo(writer, path, NbtHelper.toNbtProviderString(NbtIo.readCompressed(inputStream2, NbtSizeTracker.ofUnlimitedBytes())));
					LOGGER.info("Converted {} from NBT to SNBT", filename);
					var7 = path;
				} catch (Throwable var10) {
					try {
						inputStream2.close();
					} catch (Throwable var9) {
						var10.addSuppressed(var9);
					}

					throw var10;
				}

				inputStream2.close();
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

			return var7;
		} catch (IOException var12) {
			LOGGER.error("Couldn't convert {} from NBT to SNBT at {}", filename, inputPath, var12);
			return null;
		}
	}

	public static void writeTo(DataWriter writer, Path path, String content) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		HashingOutputStream hashingOutputStream = new HashingOutputStream(Hashing.sha1(), byteArrayOutputStream);
		hashingOutputStream.write(content.getBytes(StandardCharsets.UTF_8));
		hashingOutputStream.write(10);
		writer.write(path, byteArrayOutputStream.toByteArray(), hashingOutputStream.hash());
	}
}
