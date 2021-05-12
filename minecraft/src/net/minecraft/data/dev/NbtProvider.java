package net.minecraft.data.dev;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nullable;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NbtProvider implements DataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private final DataGenerator root;

	public NbtProvider(DataGenerator root) {
		this.root = root;
	}

	@Override
	public void run(DataCache cache) throws IOException {
		Path path = this.root.getOutput();

		for (Path path2 : this.root.getInputs()) {
			Files.walk(path2).filter(pathx -> pathx.toString().endsWith(".nbt")).forEach(path3 -> convertNbtToSnbt(path3, this.getLocation(path2, path3), path));
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
	public static Path convertNbtToSnbt(Path inputPath, String location, Path outputPath) {
		try {
			writeTo(outputPath.resolve(location + ".snbt"), NbtHelper.toPrettyPrintedString(NbtIo.readCompressed(Files.newInputStream(inputPath))));
			LOGGER.info("Converted {} from NBT to SNBT", location);
			return outputPath.resolve(location + ".snbt");
		} catch (IOException var4) {
			LOGGER.error("Couldn't convert {} from NBT to SNBT at {}", location, inputPath, var4);
			return null;
		}
	}

	public static void writeTo(Path file, String content) throws IOException {
		Files.createDirectories(file.getParent());
		BufferedWriter bufferedWriter = Files.newBufferedWriter(file);

		try {
			bufferedWriter.write(content);
			bufferedWriter.write(10);
		} catch (Throwable var6) {
			if (bufferedWriter != null) {
				try {
					bufferedWriter.close();
				} catch (Throwable var5) {
					var6.addSuppressed(var5);
				}
			}

			throw var6;
		}

		if (bufferedWriter != null) {
			bufferedWriter.close();
		}
	}
}
