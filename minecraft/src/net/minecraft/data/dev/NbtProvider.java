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
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import org.slf4j.Logger;

public class NbtProvider implements DataProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Iterable<Path> field_40660;
	private final DataOutput field_40661;

	public NbtProvider(DataOutput dataOutput, Collection<Path> collection) {
		this.field_40660 = collection;
		this.field_40661 = dataOutput;
	}

	@Override
	public void run(DataWriter writer) throws IOException {
		Path path = this.field_40661.getPath();

		for (Path path2 : this.field_40660) {
			Files.walk(path2).filter(pathx -> pathx.toString().endsWith(".nbt")).forEach(pathx -> convertNbtToSnbt(writer, pathx, this.getLocation(path2, pathx), path));
		}
	}

	@Override
	public String getName() {
		return "NBT to SNBT";
	}

	private String getLocation(Path targetPath, Path rootPath) {
		String string = targetPath.relativize(rootPath).toString().replaceAll("\\\\", "/");
		return string.substring(0, string.length() - ".nbt".length());
	}

	@Nullable
	public static Path convertNbtToSnbt(DataWriter writer, Path inputPath, String filename, Path outputPath) {
		try {
			InputStream inputStream = Files.newInputStream(inputPath);

			Path var6;
			try {
				Path path = outputPath.resolve(filename + ".snbt");
				writeTo(writer, path, NbtHelper.toNbtProviderString(NbtIo.readCompressed(inputStream)));
				LOGGER.info("Converted {} from NBT to SNBT", filename);
				var6 = path;
			} catch (Throwable var8) {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (Throwable var7) {
						var8.addSuppressed(var7);
					}
				}

				throw var8;
			}

			if (inputStream != null) {
				inputStream.close();
			}

			return var6;
		} catch (IOException var9) {
			LOGGER.error("Couldn't convert {} from NBT to SNBT at {}", filename, inputPath, var9);
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
