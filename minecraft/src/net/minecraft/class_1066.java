package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
		File file = new File(this.field_5292, string3);
		this.field_5297.lock();

		try {
			this.method_4642();
			if (file.exists()) {
				if (this.method_4641(string4, file)) {
					return this.method_4638(file);
				}

				field_5298.warn("Deleting file {}", file);
				FileUtils.deleteQuietly(file);
			}

			this.method_4643();
			class_435 lv = new class_435();
			Map<String, String> map = method_4636();
			class_310 lv2 = class_310.method_1551();
			lv2.method_5382(() -> lv2.method_1507(lv)).join();
			this.field_5294 = class_3521.method_15301(file, string, map, 52428800, lv, lv2.method_1487()).whenComplete((object, throwable) -> {
				if (throwable == null) {
					if (this.method_4641(string4, file)) {
						this.method_4638(file);
					} else {
						field_5298.warn("Deleting file {}", file);
						FileUtils.deleteQuietly(file);
					}
				} else {
					FileUtils.deleteQuietly(file);
				}
			});
			return this.field_5294;
		} finally {
			this.field_5297.unlock();
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
			String string2 = DigestUtils.sha1Hex(new FileInputStream(file));
			if (string.isEmpty()) {
				field_5298.info("Found file {} without verification hash", file);
				return true;
			}

			if (string2.toLowerCase(Locale.ROOT).equals(string.toLowerCase(Locale.ROOT))) {
				field_5298.info("Found file {} matching requested hash {}", file, string);
				return true;
			}

			field_5298.warn("File {} had wrong hash (expected {}, found {}).", file, string, string2);
		} catch (IOException var4) {
			field_5298.warn("File {} couldn't be hashed.", file, var4);
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

	public CompletableFuture<Object> method_4638(File file) {
		class_3272 lv = null;
		class_1011 lv2 = null;

		try {
			class_3258 lv3 = new class_3258(file);
			Throwable var5 = null;

			try {
				lv = lv3.method_14407(class_3272.field_14202);

				try {
					InputStream inputStream = lv3.method_14410("pack.png");
					Throwable var7 = null;

					try {
						lv2 = class_1011.method_4309(inputStream);
					} catch (Throwable var34) {
						var7 = var34;
						throw var34;
					} finally {
						if (inputStream != null) {
							if (var7 != null) {
								try {
									inputStream.close();
								} catch (Throwable var33) {
									var7.addSuppressed(var33);
								}
							} else {
								inputStream.close();
							}
						}
					}
				} catch (IllegalArgumentException | IOException var36) {
				}
			} catch (Throwable var37) {
				var5 = var37;
				throw var37;
			} finally {
				if (lv3 != null) {
					if (var5 != null) {
						try {
							lv3.close();
						} catch (Throwable var32) {
							var5.addSuppressed(var32);
						}
					} else {
						lv3.close();
					}
				}
			}
		} catch (IOException var39) {
		}

		if (lv == null) {
			CompletableFuture<Object> completableFuture = new CompletableFuture();
			completableFuture.completeExceptionally(new RuntimeException("Invalid resourcepack"));
			return completableFuture;
		} else {
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
