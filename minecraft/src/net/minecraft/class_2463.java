package net.minecraft;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Objects;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2463 implements class_2405 {
	private static final Logger field_11454 = LogManager.getLogger();
	private final class_2403 field_11453;

	public class_2463(class_2403 arg) {
		this.field_11453 = arg;
	}

	@Override
	public void method_10319(class_2408 arg) throws IOException {
		Path path = this.field_11453.method_10313();

		for (Path path2 : this.field_11453.method_10312()) {
			Files.walk(path2).filter(pathx -> pathx.toString().endsWith(".snbt")).forEach(path3 -> this.method_10497(arg, path3, this.method_10500(path2, path3), path));
		}
	}

	@Override
	public String method_10321() {
		return "SNBT -> NBT";
	}

	private String method_10500(Path path, Path path2) {
		String string = path.relativize(path2).toString().replaceAll("\\\\", "/");
		return string.substring(0, string.length() - ".snbt".length());
	}

	private void method_10497(class_2408 arg, Path path, String string, Path path2) {
		try {
			Path path3 = path2.resolve(string + ".nbt");
			BufferedReader bufferedReader = Files.newBufferedReader(path);
			Throwable var7 = null;

			try {
				String string2 = IOUtils.toString(bufferedReader);
				String string3 = field_11280.hashUnencodedChars(string2).toString();
				if (!Objects.equals(arg.method_10323(path3), string3) || !Files.exists(path3, new LinkOption[0])) {
					Files.createDirectories(path3.getParent());
					OutputStream outputStream = Files.newOutputStream(path3);
					Throwable var11 = null;

					try {
						class_2507.method_10634(class_2522.method_10718(string2), outputStream);
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

				arg.method_10325(path3, string3);
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
			field_11454.error("Couldn't convert {} from SNBT to NBT at {} as it's invalid SNBT", string, path, var42);
		} catch (IOException var43) {
			field_11454.error("Couldn't convert {} from SNBT to NBT at {}", string, path, var43);
		}
	}
}
