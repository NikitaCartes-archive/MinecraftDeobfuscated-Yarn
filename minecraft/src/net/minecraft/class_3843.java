package net.minecraft;

import com.google.common.base.Charsets;
import com.mojang.datafixers.DataFixer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.apache.commons.io.IOUtils;

public class class_3843 implements class_2405 {
	private final class_2403 field_17038;

	public class_3843(class_2403 arg) {
		this.field_17038 = arg;
	}

	@Override
	public void method_10319(class_2408 arg) throws IOException {
		for (Path path : this.field_17038.method_10312()) {
			Path path2 = path.resolve("data/minecraft/structures/");
			if (Files.isDirectory(path2, new LinkOption[0])) {
				method_16879(class_3551.method_15450(), path2);
			}
		}
	}

	@Override
	public String method_10321() {
		return "Structure validator";
	}

	private static void method_16879(DataFixer dataFixer, Path path) throws IOException {
		Stream<Path> stream = Files.walk(path);
		Throwable var3 = null;

		try {
			stream.forEach(pathx -> {
				if (Files.isRegularFile(pathx, new LinkOption[0])) {
					method_16881(dataFixer, pathx);
				}
			});
		} catch (Throwable var12) {
			var3 = var12;
			throw var12;
		} finally {
			if (stream != null) {
				if (var3 != null) {
					try {
						stream.close();
					} catch (Throwable var11) {
						var3.addSuppressed(var11);
					}
				} else {
					stream.close();
				}
			}
		}
	}

	private static void method_16881(DataFixer dataFixer, Path path) {
		try {
			String string = path.getFileName().toString();
			if (string.endsWith(".snbt")) {
				method_16882(dataFixer, path);
			} else {
				if (!string.endsWith(".nbt")) {
					throw new IllegalArgumentException("Unrecognized format of file");
				}

				method_16883(dataFixer, path);
			}
		} catch (Exception var3) {
			throw new class_3843.class_3844(path, var3);
		}
	}

	private static void method_16882(DataFixer dataFixer, Path path) throws Exception {
		InputStream inputStream = Files.newInputStream(path);
		Throwable var4 = null;

		class_2487 lv;
		try {
			String string = IOUtils.toString(inputStream, Charsets.UTF_8);
			lv = class_2522.method_10718(string);
		} catch (Throwable var13) {
			var4 = var13;
			throw var13;
		} finally {
			if (inputStream != null) {
				if (var4 != null) {
					try {
						inputStream.close();
					} catch (Throwable var12) {
						var4.addSuppressed(var12);
					}
				} else {
					inputStream.close();
				}
			}
		}

		method_16878(dataFixer, method_16880(lv));
	}

	private static void method_16883(DataFixer dataFixer, Path path) throws Exception {
		InputStream inputStream = Files.newInputStream(path);
		Throwable var4 = null;

		class_2487 lv;
		try {
			lv = class_2507.method_10629(inputStream);
		} catch (Throwable var13) {
			var4 = var13;
			throw var13;
		} finally {
			if (inputStream != null) {
				if (var4 != null) {
					try {
						inputStream.close();
					} catch (Throwable var12) {
						var4.addSuppressed(var12);
					}
				} else {
					inputStream.close();
				}
			}
		}

		method_16878(dataFixer, method_16880(lv));
	}

	private static class_2487 method_16880(class_2487 arg) {
		if (!arg.method_10573("DataVersion", 99)) {
			arg.method_10569("DataVersion", 500);
		}

		return arg;
	}

	private static class_2487 method_16878(DataFixer dataFixer, class_2487 arg) {
		class_3499 lv = new class_3499();
		lv.method_15183(class_2512.method_10688(dataFixer, class_4284.field_19217, arg, arg.method_10550("DataVersion")));
		return lv.method_15175(new class_2487());
	}

	static class class_3844 extends RuntimeException {
		public class_3844(Path path, Throwable throwable) {
			super("Failed to process file: " + path.toAbsolutePath().toString(), throwable);
		}
	}
}
