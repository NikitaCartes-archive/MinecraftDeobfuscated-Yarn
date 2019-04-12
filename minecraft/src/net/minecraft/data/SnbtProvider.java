package net.minecraft.data;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Objects;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringNbtReader;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SnbtProvider implements DataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private final DataGenerator root;

	public SnbtProvider(DataGenerator dataGenerator) {
		this.root = dataGenerator;
	}

	@Override
	public void run(DataCache dataCache) throws IOException {
		Path path = this.root.getOutput();

		for (Path path2 : this.root.getInputs()) {
			Files.walk(path2)
				.filter(pathx -> pathx.toString().endsWith(".snbt"))
				.forEach(path3 -> this.method_10497(dataCache, path3, this.method_10500(path2, path3), path));
		}
	}

	@Override
	public String getName() {
		return "SNBT -> NBT";
	}

	private String method_10500(Path path, Path path2) {
		String string = path.relativize(path2).toString().replaceAll("\\\\", "/");
		return string.substring(0, string.length() - ".snbt".length());
	}

	private void method_10497(DataCache dataCache, Path path, String string, Path path2) {
		try {
			Path path3 = path2.resolve(string + ".nbt");
			BufferedReader bufferedReader = Files.newBufferedReader(path);
			Throwable var7 = null;

			try {
				String string2 = IOUtils.toString(bufferedReader);
				String string3 = SHA1.hashUnencodedChars(string2).toString();
				if (!Objects.equals(dataCache.getOldSha1(path3), string3) || !Files.exists(path3, new LinkOption[0])) {
					Files.createDirectories(path3.getParent());
					OutputStream outputStream = Files.newOutputStream(path3);
					Throwable var11 = null;

					try {
						NbtIo.writeCompressed(StringNbtReader.parse(string2), outputStream);
					} catch (Throwable var38) {
						var11 = var38;
						throw var38;
					} finally {
						if (outputStream != null) {
							if (var11 != null) {
								try {
									outputStream.close();
								} catch (Throwable var37) {
									var11.addSuppressed(var37);
								}
							} else {
								outputStream.close();
							}
						}
					}
				}

				dataCache.updateSha1(path3, string3);
			} catch (Throwable var40) {
				var7 = var40;
				throw var40;
			} finally {
				if (bufferedReader != null) {
					if (var7 != null) {
						try {
							bufferedReader.close();
						} catch (Throwable var36) {
							var7.addSuppressed(var36);
						}
					} else {
						bufferedReader.close();
					}
				}
			}
		} catch (CommandSyntaxException var42) {
			LOGGER.error("Couldn't convert {} from SNBT to NBT at {} as it's invalid SNBT", string, path, var42);
		} catch (IOException var43) {
			LOGGER.error("Couldn't convert {} from SNBT to NBT at {}", string, path, var43);
		}
	}
}
