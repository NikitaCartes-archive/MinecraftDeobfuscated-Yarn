package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_32 {
	private static final Logger field_17665 = LogManager.getLogger();
	private static final DateTimeFormatter field_200 = new DateTimeFormatterBuilder()
		.appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
		.appendLiteral('-')
		.appendValue(ChronoField.MONTH_OF_YEAR, 2)
		.appendLiteral('-')
		.appendValue(ChronoField.DAY_OF_MONTH, 2)
		.appendLiteral('_')
		.appendValue(ChronoField.HOUR_OF_DAY, 2)
		.appendLiteral('-')
		.appendValue(ChronoField.MINUTE_OF_HOUR, 2)
		.appendLiteral('-')
		.appendValue(ChronoField.SECOND_OF_MINUTE, 2)
		.toFormatter();
	private final Path field_17666;
	private final Path field_17667;
	private final DataFixer field_17668;

	public class_32(Path path, Path path2, DataFixer dataFixer) {
		this.field_17668 = dataFixer;

		try {
			Files.createDirectories(Files.exists(path, new LinkOption[0]) ? path.toRealPath() : path);
		} catch (IOException var5) {
			throw new RuntimeException(var5);
		}

		this.field_17666 = path;
		this.field_17667 = path2;
	}

	@Environment(EnvType.CLIENT)
	public String method_232() {
		return "Anvil";
	}

	@Environment(EnvType.CLIENT)
	public List<class_34> method_235() throws class_33 {
		if (!Files.isDirectory(this.field_17666, new LinkOption[0])) {
			throw new class_33(new class_2588("selectWorld.load_folder_access").getString());
		} else {
			List<class_34> list = Lists.<class_34>newArrayList();
			File[] files = this.field_17666.toFile().listFiles();

			for (File file : files) {
				if (file.isDirectory()) {
					String string = file.getName();
					class_31 lv = this.method_231(string);
					if (lv != null && (lv.method_168() == 19132 || lv.method_168() == 19133)) {
						boolean bl = lv.method_168() != this.method_17931();
						String string2 = lv.method_150();
						if (StringUtils.isEmpty(string2)) {
							string2 = string;
						}

						long l = 0L;
						list.add(new class_34(lv, string, string2, 0L, bl));
					}
				}
			}

			return list;
		}
	}

	private int method_17931() {
		return 19133;
	}

	public class_29 method_242(String string, @Nullable MinecraftServer minecraftServer) {
		return method_17929(this.field_17666, this.field_17668, string, minecraftServer);
	}

	protected static class_29 method_17929(Path path, DataFixer dataFixer, String string, @Nullable MinecraftServer minecraftServer) {
		return new class_29(path.toFile(), string, minecraftServer, dataFixer);
	}

	public boolean method_244(String string) {
		class_31 lv = this.method_231(string);
		return lv != null && lv.method_168() != this.method_17931();
	}

	public boolean method_17927(String string, class_3536 arg) {
		return class_24.method_234(this.field_17666, this.field_17668, string, arg);
	}

	@Nullable
	public class_31 method_231(String string) {
		return method_17928(this.field_17666, this.field_17668, string);
	}

	@Nullable
	protected static class_31 method_17928(Path path, DataFixer dataFixer, String string) {
		File file = new File(path.toFile(), string);
		if (!file.exists()) {
			return null;
		} else {
			File file2 = new File(file, "level.dat");
			if (file2.exists()) {
				class_31 lv = method_17926(file2, dataFixer);
				if (lv != null) {
					return lv;
				}
			}

			file2 = new File(file, "level.dat_old");
			return file2.exists() ? method_17926(file2, dataFixer) : null;
		}
	}

	@Nullable
	public static class_31 method_17926(File file, DataFixer dataFixer) {
		try {
			class_2487 lv = class_2507.method_10629(new FileInputStream(file));
			class_2487 lv2 = lv.method_10562("Data");
			class_2487 lv3 = lv2.method_10573("Player", 10) ? lv2.method_10562("Player") : null;
			lv2.method_10551("Player");
			int i = lv2.method_10573("DataVersion", 99) ? lv2.method_10550("DataVersion") : -1;
			return new class_31(class_2512.method_10688(dataFixer, class_4284.field_19212, lv2, i), dataFixer, i, lv3);
		} catch (Exception var6) {
			field_17665.error("Exception reading {}", file, var6);
			return null;
		}
	}

	@Environment(EnvType.CLIENT)
	public void method_241(String string, String string2) {
		File file = new File(this.field_17666.toFile(), string);
		if (file.exists()) {
			File file2 = new File(file, "level.dat");
			if (file2.exists()) {
				try {
					class_2487 lv = class_2507.method_10629(new FileInputStream(file2));
					class_2487 lv2 = lv.method_10562("Data");
					lv2.method_10582("LevelName", string2);
					class_2507.method_10634(lv, new FileOutputStream(file2));
				} catch (Exception var7) {
					var7.printStackTrace();
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public boolean method_240(String string) {
		try {
			Path path = this.field_17666.resolve(string);
			Files.createDirectory(path);
			Files.deleteIfExists(path);
			return true;
		} catch (IOException var3) {
			return false;
		}
	}

	@Environment(EnvType.CLIENT)
	public boolean method_233(String string) {
		File file = new File(this.field_17666.toFile(), string);
		if (!file.exists()) {
			return true;
		} else {
			field_17665.info("Deleting level {}", string);

			for (int i = 1; i <= 5; i++) {
				field_17665.info("Attempt {}...", i);
				if (method_17930(file.listFiles())) {
					break;
				}

				field_17665.warn("Unsuccessful in deleting contents.");
				if (i < 5) {
					try {
						Thread.sleep(500L);
					} catch (InterruptedException var5) {
					}
				}
			}

			return file.delete();
		}
	}

	@Environment(EnvType.CLIENT)
	private static boolean method_17930(File[] files) {
		for (File file : files) {
			field_17665.debug("Deleting {}", file);
			if (file.isDirectory() && !method_17930(file.listFiles())) {
				field_17665.warn("Couldn't delete directory {}", file);
				return false;
			}

			if (!file.delete()) {
				field_17665.warn("Couldn't delete file {}", file);
				return false;
			}
		}

		return true;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_230(String string) {
		return Files.isDirectory(this.field_17666.resolve(string), new LinkOption[0]);
	}

	@Environment(EnvType.CLIENT)
	public Path method_19636() {
		return this.field_17666;
	}

	public File method_239(String string, String string2) {
		return this.field_17666.resolve(string).resolve(string2).toFile();
	}

	@Environment(EnvType.CLIENT)
	private Path method_243(String string) {
		return this.field_17666.resolve(string);
	}

	@Environment(EnvType.CLIENT)
	public Path method_236() {
		return this.field_17667;
	}

	@Environment(EnvType.CLIENT)
	public long method_237(String string) throws IOException {
		final Path path = this.method_243(string);
		String string2 = LocalDateTime.now().format(field_200) + "_" + string;
		Path path2 = this.method_236();

		try {
			Files.createDirectories(Files.exists(path2, new LinkOption[0]) ? path2.toRealPath() : path2);
		} catch (IOException var18) {
			throw new RuntimeException(var18);
		}

		Path path3 = path2.resolve(class_4239.method_19773(path2, string2, ".zip"));
		final ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(path3)));
		Throwable var7 = null;

		try {
			final Path path4 = Paths.get(string);
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				public FileVisitResult method_246(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
					String string = path4.resolve(path.relativize(path)).toString().replace('\\', '/');
					ZipEntry zipEntry = new ZipEntry(string);
					zipOutputStream.putNextEntry(zipEntry);
					com.google.common.io.Files.asByteSource(path.toFile()).copyTo(zipOutputStream);
					zipOutputStream.closeEntry();
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (Throwable var17) {
			var7 = var17;
			throw var17;
		} finally {
			if (zipOutputStream != null) {
				if (var7 != null) {
					try {
						zipOutputStream.close();
					} catch (Throwable var16) {
						var7.addSuppressed(var16);
					}
				} else {
					zipOutputStream.close();
				}
			}
		}

		return Files.size(path3);
	}
}
