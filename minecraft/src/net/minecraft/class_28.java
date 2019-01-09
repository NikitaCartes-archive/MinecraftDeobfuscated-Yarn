package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_28 implements class_32 {
	private static final Logger field_140 = LogManager.getLogger();
	protected final Path field_142;
	protected final Path field_141;
	protected final DataFixer field_143;

	public class_28(Path path, Path path2, DataFixer dataFixer) {
		this.field_143 = dataFixer;

		try {
			Files.createDirectories(Files.exists(path, new LinkOption[0]) ? path.toRealPath() : path);
		} catch (IOException var5) {
			throw new RuntimeException(var5);
		}

		this.field_142 = path;
		this.field_141 = path2;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public String method_232() {
		return "Old Format";
	}

	@Environment(EnvType.CLIENT)
	@Override
	public List<class_34> method_235() throws class_33 {
		List<class_34> list = Lists.<class_34>newArrayList();

		for (int i = 0; i < 5; i++) {
			String string = "World" + (i + 1);
			class_31 lv = this.method_238(string);
			if (lv != null) {
				list.add(new class_34(lv, string, "", lv.method_148(), false));
			}
		}

		return list;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_245() {
	}

	@Nullable
	@Override
	public class_31 method_238(String string) {
		File file = new File(this.field_142.toFile(), string);
		if (!file.exists()) {
			return null;
		} else {
			File file2 = new File(file, "level.dat");
			if (file2.exists()) {
				class_31 lv = method_126(file2, this.field_143);
				if (lv != null) {
					return lv;
				}
			}

			file2 = new File(file, "level.dat_old");
			return file2.exists() ? method_126(file2, this.field_143) : null;
		}
	}

	@Nullable
	public static class_31 method_126(File file, DataFixer dataFixer) {
		try {
			class_2487 lv = class_2507.method_10629(new FileInputStream(file));
			class_2487 lv2 = lv.method_10562("Data");
			class_2487 lv3 = lv2.method_10573("Player", 10) ? lv2.method_10562("Player") : null;
			lv2.method_10551("Player");
			int i = lv2.method_10573("DataVersion", 99) ? lv2.method_10550("DataVersion") : -1;
			return new class_31(class_2512.method_10688(dataFixer, DataFixTypes.LEVEL, lv2, i), dataFixer, i, lv3);
		} catch (Exception var6) {
			field_140.error("Exception reading {}", file, var6);
			return null;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_241(String string, String string2) {
		File file = new File(this.field_142.toFile(), string);
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
	@Override
	public boolean method_240(String string) {
		File file = new File(this.field_142.toFile(), string);
		if (file.exists()) {
			return false;
		} else {
			try {
				file.mkdir();
				file.delete();
				return true;
			} catch (Throwable var4) {
				field_140.warn("Couldn't make new level", var4);
				return false;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_233(String string) {
		File file = new File(this.field_142.toFile(), string);
		if (!file.exists()) {
			return true;
		} else {
			field_140.info("Deleting level {}", string);

			for (int i = 1; i <= 5; i++) {
				field_140.info("Attempt {}...", i);
				if (method_127(file.listFiles())) {
					break;
				}

				field_140.warn("Unsuccessful in deleting contents.");
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
	protected static boolean method_127(File[] files) {
		for (File file : files) {
			field_140.debug("Deleting {}", file);
			if (file.isDirectory() && !method_127(file.listFiles())) {
				field_140.warn("Couldn't delete directory {}", file);
				return false;
			}

			if (!file.delete()) {
				field_140.warn("Couldn't delete file {}", file);
				return false;
			}
		}

		return true;
	}

	@Override
	public class_30 method_242(String string, @Nullable MinecraftServer minecraftServer) {
		return new class_29(this.field_142.toFile(), string, minecraftServer, this.field_143);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_244(String string) {
		return false;
	}

	@Override
	public boolean method_231(String string) {
		return false;
	}

	@Override
	public boolean method_234(String string, class_3536 arg) {
		return false;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_230(String string) {
		return Files.isDirectory(this.field_142.resolve(string), new LinkOption[0]);
	}

	@Override
	public File method_239(String string, String string2) {
		return this.field_142.resolve(string).resolve(string2).toFile();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Path method_243(String string) {
		return this.field_142.resolve(string);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Path method_236() {
		return this.field_141;
	}
}
