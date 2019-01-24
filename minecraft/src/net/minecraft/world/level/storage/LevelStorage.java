package net.minecraft.world.level.storage;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixTypes;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.TagHelper;
import net.minecraft.world.OldWorldSaveHandler;
import net.minecraft.world.level.LevelProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LevelStorage {
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

	public LevelStorage(Path path, Path path2, DataFixer dataFixer) {
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
	public String getName() {
		return "Anvil";
	}

	@Environment(EnvType.CLIENT)
	public List<LevelSummary> getAvailableLevels() throws LevelStorageException {
		if (!Files.isDirectory(this.field_17666, new LinkOption[0])) {
			throw new LevelStorageException(new TranslatableTextComponent("selectWorld.load_folder_access").getString());
		} else {
			List<LevelSummary> list = Lists.<LevelSummary>newArrayList();
			File[] files = this.field_17666.toFile().listFiles();

			for (File file : files) {
				if (file.isDirectory()) {
					String string = file.getName();
					LevelProperties levelProperties = this.requiresConversion(string);
					if (levelProperties != null && (levelProperties.getVersion() == 19132 || levelProperties.getVersion() == 19133)) {
						boolean bl = levelProperties.getVersion() != this.method_17931();
						String string2 = levelProperties.getLevelName();
						if (StringUtils.isEmpty(string2)) {
							string2 = string;
						}

						long l = 0L;
						list.add(new LevelSummary(levelProperties, string, string2, 0L, bl));
					}
				}
			}

			return list;
		}
	}

	private int method_17931() {
		return 19133;
	}

	public OldWorldSaveHandler method_242(String string, @Nullable MinecraftServer minecraftServer) {
		return method_17929(this.field_17666, this.field_17668, string, minecraftServer);
	}

	protected static OldWorldSaveHandler method_17929(Path path, DataFixer dataFixer, String string, @Nullable MinecraftServer minecraftServer) {
		return new OldWorldSaveHandler(path.toFile(), string, minecraftServer, dataFixer);
	}

	public boolean isConvertible(String string) {
		LevelProperties levelProperties = this.requiresConversion(string);
		return levelProperties != null && levelProperties.getVersion() != this.method_17931();
	}

	public boolean method_17927(String string, ProgressListener progressListener) {
		return AnvilLevelStorage.convertLevel(this.field_17666, this.field_17668, string, progressListener);
	}

	@Nullable
	public LevelProperties requiresConversion(String string) {
		return method_17928(this.field_17666, this.field_17668, string);
	}

	@Nullable
	protected static LevelProperties method_17928(Path path, DataFixer dataFixer, String string) {
		File file = new File(path.toFile(), string);
		if (!file.exists()) {
			return null;
		} else {
			File file2 = new File(file, "level.dat");
			if (file2.exists()) {
				LevelProperties levelProperties = method_17926(file2, dataFixer);
				if (levelProperties != null) {
					return levelProperties;
				}
			}

			file2 = new File(file, "level.dat_old");
			return file2.exists() ? method_17926(file2, dataFixer) : null;
		}
	}

	@Nullable
	public static LevelProperties method_17926(File file, DataFixer dataFixer) {
		try {
			CompoundTag compoundTag = NbtIo.readCompressed(new FileInputStream(file));
			CompoundTag compoundTag2 = compoundTag.getCompound("Data");
			CompoundTag compoundTag3 = compoundTag2.containsKey("Player", 10) ? compoundTag2.getCompound("Player") : null;
			compoundTag2.remove("Player");
			int i = compoundTag2.containsKey("DataVersion", 99) ? compoundTag2.getInt("DataVersion") : -1;
			return new LevelProperties(TagHelper.update(dataFixer, DataFixTypes.LEVEL, compoundTag2, i), dataFixer, i, compoundTag3);
		} catch (Exception var6) {
			field_17665.error("Exception reading {}", file, var6);
			return null;
		}
	}

	@Environment(EnvType.CLIENT)
	public void rename(String string, String string2) {
		File file = new File(this.field_17666.toFile(), string);
		if (file.exists()) {
			File file2 = new File(file, "level.dat");
			if (file2.exists()) {
				try {
					CompoundTag compoundTag = NbtIo.readCompressed(new FileInputStream(file2));
					CompoundTag compoundTag2 = compoundTag.getCompound("Data");
					compoundTag2.putString("LevelName", string2);
					NbtIo.writeCompressed(compoundTag, new FileOutputStream(file2));
				} catch (Exception var7) {
					var7.printStackTrace();
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public boolean canCreate(String string) {
		File file = new File(this.field_17666.toFile(), string);
		if (file.exists()) {
			return false;
		} else {
			try {
				file.mkdir();
				file.delete();
				return true;
			} catch (Throwable var4) {
				field_17665.warn("Couldn't make new level", var4);
				return false;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public boolean delete(String string) {
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
	public boolean exists(String string) {
		return Files.isDirectory(this.field_17666.resolve(string), new LinkOption[0]);
	}

	public File resolveFile(String string, String string2) {
		return this.field_17666.resolve(string).resolve(string2).toFile();
	}

	@Environment(EnvType.CLIENT)
	private Path resolvePath(String string) {
		return this.field_17666.resolve(string);
	}

	@Environment(EnvType.CLIENT)
	public Path method_236() {
		return this.field_17667;
	}

	@Environment(EnvType.CLIENT)
	public long method_237(String string) throws IOException {
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
					String string = path4.resolve(path.relativize(path)).toString().replace('\\', '/');
					ZipEntry zipEntry = new ZipEntry(string);
					zipOutputStream.putNextEntry(zipEntry);
					com.google.common.io.Files.asByteSource(path.toFile()).copyTo(zipOutputStream);
					zipOutputStream.closeEntry();
					return FileVisitResult.CONTINUE;
				}
			});
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
}
