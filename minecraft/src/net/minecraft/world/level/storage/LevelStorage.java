package net.minecraft.world.level.storage;

import java.io.BufferedOutputStream;
import java.io.File;
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
import net.minecraft.util.ProgressListener;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.level.LevelProperties;

public interface LevelStorage {
	DateTimeFormatter field_200 = new DateTimeFormatterBuilder()
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

	@Environment(EnvType.CLIENT)
	default long method_237(String string) throws IOException {
		final Path path = this.resolvePath(string);
		String string2 = LocalDateTime.now().format(field_200) + "_" + string;
		int i = 0;
		Path path2 = this.method_236();

		try {
			Files.createDirectories(Files.exists(path2, new LinkOption[0]) ? path2.toRealPath() : path2);
		} catch (IOException var19) {
			throw new RuntimeException(var19);
		}

		Path path3;
		do {
			path3 = path2.resolve(string2 + (i++ > 0 ? "_" + i : "") + ".zip");
		} while (Files.exists(path3, new LinkOption[0]));

		final ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(path3.toFile())));
		Throwable var8 = null;

		try {
			final Path path4 = Paths.get(string);
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				public FileVisitResult method_246(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
					String string = path4.resolve(path.relativize(path)).toString();
					ZipEntry zipEntry = new ZipEntry(string);
					zipOutputStream.putNextEntry(zipEntry);
					com.google.common.io.Files.asByteSource(path.toFile()).copyTo(zipOutputStream);
					zipOutputStream.closeEntry();
					return FileVisitResult.CONTINUE;
				}
			});
			zipOutputStream.close();
		} catch (Throwable var18) {
			var8 = var18;
			throw var18;
		} finally {
			if (zipOutputStream != null) {
				if (var8 != null) {
					try {
						zipOutputStream.close();
					} catch (Throwable var17) {
						var8.addSuppressed(var17);
					}
				} else {
					zipOutputStream.close();
				}
			}
		}

		return Files.size(path3);
	}

	@Environment(EnvType.CLIENT)
	String getName();

	WorldSaveHandler method_242(String string, @Nullable MinecraftServer minecraftServer);

	@Environment(EnvType.CLIENT)
	List<LevelSummary> getAvailableLevels() throws LevelStorageException;

	@Environment(EnvType.CLIENT)
	void clearAll();

	@Nullable
	LevelProperties getLevelProperties(String string);

	@Environment(EnvType.CLIENT)
	boolean canCreate(String string);

	@Environment(EnvType.CLIENT)
	boolean delete(String string);

	@Environment(EnvType.CLIENT)
	void rename(String string, String string2);

	@Environment(EnvType.CLIENT)
	boolean isConvertible(String string);

	boolean requiresConversion(String string);

	boolean convertLevel(String string, ProgressListener progressListener);

	@Environment(EnvType.CLIENT)
	boolean exists(String string);

	File resolveFile(String string, String string2);

	@Environment(EnvType.CLIENT)
	Path resolvePath(String string);

	@Environment(EnvType.CLIENT)
	Path method_236();
}
