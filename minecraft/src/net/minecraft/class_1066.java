package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_1066 implements class_3285 {
	private static final Logger field_5298 = LogManager.getLogger();
	private static final Pattern field_5296 = Pattern.compile("^[a-fA-F0-9]{40}$");
	private final class_3268 field_5293;
	private final File field_5292;
	private final ReentrantLock field_5297 = new ReentrantLock();
	private final class_1064 field_16263;
	@Nullable
	private CompletableFuture<?> field_5294;
	@Nullable
	private class_1075 field_5295;

	public class_1066(File file, class_1064 arg) {
		this.field_5292 = file;
		this.field_16263 = arg;
		this.field_5293 = new class_1065(arg);
	}

	@Override
	public <T extends class_3288> void method_14453(Map<String, T> map, class_3288.class_3290<T> arg) {
		T lv = class_3288.method_14456("vanilla", true, () -> this.field_5293, arg, class_3288.class_3289.field_14281);
		if (lv != null) {
			map.put("vanilla", lv);
		}

		if (this.field_5295 != null) {
			map.put("server", this.field_5295);
		}

		File file = this.field_16263.method_4630(new class_2960("resourcepacks/programmer_art.zip"));
		if (file != null && file.isFile()) {
			T lv2 = class_3288.method_14456("programer_art", false, () -> new class_3258(file) {
					@Override
					public String method_14409() {
						return "Programmer Art";
					}
				}, arg, class_3288.class_3289.field_14280);
			if (lv2 != null) {
				map.put("programer_art", lv2);
			}
		}
	}

	public class_3268 method_4633() {
		return this.field_5293;
	}

	public static Map<String, String> method_4636() {
		Map<String, String> map = Maps.<String, String>newHashMap();
		map.put("X-Minecraft-Username", class_310.method_1551().method_1548().method_1676());
		map.put("X-Minecraft-UUID", class_310.method_1551().method_1548().method_1673());
		map.put("X-Minecraft-Version", class_155.method_16673().getName());
		map.put("X-Minecraft-Pack-Format", String.valueOf(class_155.method_16673().getPackVersion()));
		map.put("User-Agent", "Minecraft Java/" + class_155.method_16673().getName());
		return map;
	}

	public CompletableFuture<?> method_4640(String string, String string2) {
		String string3 = DigestUtils.sha1Hex(string);
		String string4 = field_5296.matcher(string2).matches() ? string2 : "";
		this.field_5297.lock();

		CompletableFuture var13;
		try {
			this.method_4642();
			this.method_4643();
			File file = new File(this.field_5292, string3);
			CompletableFuture<?> completableFuture;
			if (file.exists()) {
				completableFuture = CompletableFuture.completedFuture("");
			} else {
				class_435 lv = new class_435();
				Map<String, String> map = method_4636();
				class_310 lv2 = class_310.method_1551();
				lv2.method_19537(() -> lv2.method_1507(lv));
				completableFuture = class_3521.method_15301(file, string, map, 52428800, lv, lv2.method_1487());
			}

			this.field_5294 = completableFuture.thenCompose(
					object -> !this.method_4641(string4, file)
							? class_156.method_19483(new RuntimeException("Hash check failure for file " + file + ", see log"))
							: this.method_4638(file)
				)
				.whenComplete((void_, throwable) -> {
					if (throwable != null) {
						field_5298.warn("Pack application failed: {}, deleting file {}", throwable.getMessage(), file);
						method_19437(file);
					}
				});
			var13 = this.field_5294;
		} finally {
			this.field_5297.unlock();
		}

		return var13;
	}

	private static void method_19437(File file) {
		try {
			Files.delete(file.toPath());
		} catch (IOException var2) {
			field_5298.warn("Failed to delete file {}: {}", file, var2.getMessage());
		}
	}

	public void method_4642() {
		this.field_5297.lock();

		try {
			if (this.field_5294 != null) {
				this.field_5294.cancel(true);
			}

			this.field_5294 = null;
			if (this.field_5295 != null) {
				this.field_5295 = null;
				class_310.method_1551().method_1513();
			}
		} finally {
			this.field_5297.unlock();
		}
	}

	private boolean method_4641(String string, File file) {
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			Throwable var5 = null;

			String string2;
			try {
				string2 = DigestUtils.sha1Hex(fileInputStream);
			} catch (Throwable var15) {
				var5 = var15;
				throw var15;
			} finally {
				if (fileInputStream != null) {
					if (var5 != null) {
						try {
							fileInputStream.close();
						} catch (Throwable var14) {
							var5.addSuppressed(var14);
						}
					} else {
						fileInputStream.close();
					}
				}
			}

			if (string.isEmpty()) {
				field_5298.info("Found file {} without verification hash", file);
				return true;
			}

			if (string2.toLowerCase(Locale.ROOT).equals(string.toLowerCase(Locale.ROOT))) {
				field_5298.info("Found file {} matching requested hash {}", file, string);
				return true;
			}

			field_5298.warn("File {} had wrong hash (expected {}, found {}).", file, string, string2);
		} catch (IOException var17) {
			field_5298.warn("File {} couldn't be hashed.", file, var17);
		}

		return false;
	}

	private void method_4643() {
		try {
			List<File> list = Lists.<File>newArrayList(FileUtils.listFiles(this.field_5292, TrueFileFilter.TRUE, null));
			list.sort(LastModifiedFileComparator.LASTMODIFIED_REVERSE);
			int i = 0;

			for (File file : list) {
				if (i++ >= 10) {
					field_5298.info("Deleting old server resource pack {}", file.getName());
					FileUtils.deleteQuietly(file);
				}
			}
		} catch (IllegalArgumentException var5) {
			field_5298.error("Error while deleting old server resource pack : {}", var5.getMessage());
		}
	}

	public CompletableFuture<Void> method_4638(File file) {
		class_3272 lv = null;
		class_1011 lv2 = null;
		String string = null;

		try {
			class_3258 lv3 = new class_3258(file);
			Throwable var6 = null;

			try {
				lv = lv3.method_14407(class_3272.field_14202);

				try {
					InputStream inputStream = lv3.method_14410("pack.png");
					Throwable var8 = null;

					try {
						lv2 = class_1011.method_4309(inputStream);
					} catch (Throwable var35) {
						var8 = var35;
						throw var35;
					} finally {
						if (inputStream != null) {
							if (var8 != null) {
								try {
									inputStream.close();
								} catch (Throwable var34) {
									var8.addSuppressed(var34);
								}
							} else {
								inputStream.close();
							}
						}
					}
				} catch (IllegalArgumentException | IOException var37) {
					field_5298.info("Could not read pack.png: {}", var37.getMessage());
				}
			} catch (Throwable var38) {
				var6 = var38;
				throw var38;
			} finally {
				if (lv3 != null) {
					if (var6 != null) {
						try {
							lv3.close();
						} catch (Throwable var33) {
							var6.addSuppressed(var33);
						}
					} else {
						lv3.close();
					}
				}
			}
		} catch (IOException var40) {
			string = var40.getMessage();
		}

		if (string != null) {
			return class_156.method_19483(new RuntimeException(String.format("Invalid resourcepack at %s: %s", file, string)));
		} else {
			field_5298.info("Applying server pack {}", file);
			this.field_5295 = new class_1075(
				"server",
				true,
				() -> new class_3258(file),
				new class_2588("resourcePack.server.name"),
				lv.method_14423(),
				class_3281.method_14436(lv.method_14424()),
				class_3288.class_3289.field_14280,
				true,
				lv2
			);
			return class_310.method_1551().method_1513();
		}
	}
}
