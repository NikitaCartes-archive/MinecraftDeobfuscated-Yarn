package net.minecraft;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2461 implements class_2405 {
	private static final Logger field_11450 = LogManager.getLogger();
	private final class_2403 field_11449;

	public class_2461(class_2403 arg) {
		this.field_11449 = arg;
	}

	@Override
	public void method_10319(class_2408 arg) throws IOException {
		Path path = this.field_11449.method_10313();

		for (Path path2 : this.field_11449.method_10312()) {
			Files.walk(path2).filter(pathx -> pathx.toString().endsWith(".nbt")).forEach(path3 -> this.method_10493(path3, this.method_10496(path2, path3), path));
		}
	}

	@Override
	public String method_10321() {
		return "NBT to SNBT";
	}

	private String method_10496(Path path, Path path2) {
		String string = path.relativize(path2).toString().replaceAll("\\\\", "/");
		return string.substring(0, string.length() - ".nbt".length());
	}

	private void method_10493(Path path, String string, Path path2) {
		try {
			class_2487 lv = class_2507.method_10629(Files.newInputStream(path));
			class_2561 lv2 = lv.method_10710("    ", 0);
			String string2 = lv2.getString();
			Path path3 = path2.resolve(string + ".snbt");
			Files.createDirectories(path3.getParent());
			BufferedWriter bufferedWriter = Files.newBufferedWriter(path3);
			Throwable var9 = null;

			try {
				bufferedWriter.write(string2);
			} catch (Throwable var19) {
				var9 = var19;
				throw var19;
			} finally {
				if (bufferedWriter != null) {
					if (var9 != null) {
						try {
							bufferedWriter.close();
						} catch (Throwable var18) {
							var9.addSuppressed(var18);
						}
					} else {
						bufferedWriter.close();
					}
				}
			}

			field_11450.info("Converted {} from NBT to SNBT", string);
		} catch (IOException var21) {
			field_11450.error("Couldn't convert {} from NBT to SNBT at {}", string, path, var21);
		}
	}
}
