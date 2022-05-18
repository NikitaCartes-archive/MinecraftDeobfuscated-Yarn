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
import javax.annotation.Nullable;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import org.slf4j.Logger;

public class NbtProvider implements DataProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final DataGenerator root;

	public NbtProvider(DataGenerator root) {
		this.root = root;
	}

	@Override
	public void run(DataWriter cache) throws IOException {
		Path path = this.root.getOutput();

		for (Path path2 : this.root.getInputs()) {
			Files.walk(path2).filter(pathx -> pathx.toString().endsWith(".nbt")).forEach(path3 -> convertNbtToSnbt(cache, path3, this.getLocation(path2, path3), path));
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
	public static Path convertNbtToSnbt(DataWriter dataWriter, Path path, String string, Path path2) {
		try {
			InputStream inputStream = Files.newInputStream(path);

			Path var6;
			try {
				Path path3 = path2.resolve(string + ".snbt");
				writeTo(dataWriter, path3, NbtHelper.toNbtProviderString(NbtIo.readCompressed(inputStream)));
				LOGGER.info("Converted {} from NBT to SNBT", string);
				var6 = path3;
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
			LOGGER.error("Couldn't convert {} from NBT to SNBT at {}", string, path, var9);
			return null;
		}
	}

	public static void writeTo(DataWriter dataWriter, Path path, String string) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		HashingOutputStream hashingOutputStream = new HashingOutputStream(Hashing.sha1(), byteArrayOutputStream);
		hashingOutputStream.write(string.getBytes(StandardCharsets.UTF_8));
		hashingOutputStream.write(10);
		dataWriter.write(path, byteArrayOutputStream.toByteArray(), hashingOutputStream.hash());
	}
}
