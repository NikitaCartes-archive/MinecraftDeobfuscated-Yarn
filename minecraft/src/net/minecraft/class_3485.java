package net.minecraft;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3485 implements class_3302 {
	private static final Logger field_15514 = LogManager.getLogger();
	private final Map<class_2960, class_3499> field_15513 = Maps.<class_2960, class_3499>newHashMap();
	private final DataFixer field_15515;
	private final MinecraftServer field_15516;
	private final Path field_15512;

	public class_3485(MinecraftServer minecraftServer, File file, DataFixer dataFixer) {
		this.field_15516 = minecraftServer;
		this.field_15515 = dataFixer;
		this.field_15512 = file.toPath().resolve("generated").normalize();
		minecraftServer.method_3809().method_14477(this);
	}

	public class_3499 method_15091(class_2960 arg) {
		class_3499 lv = this.method_15094(arg);
		if (lv == null) {
			lv = new class_3499();
			this.field_15513.put(arg, lv);
		}

		return lv;
	}

	@Nullable
	public class_3499 method_15094(class_2960 arg) {
		return (class_3499)this.field_15513.computeIfAbsent(arg, argx -> {
			class_3499 lv = this.method_15092(argx);
			return lv != null ? lv : this.method_15088(argx);
		});
	}

	@Override
	public void method_14491(class_3300 arg) {
		this.field_15513.clear();
	}

	@Nullable
	private class_3499 method_15088(class_2960 arg) {
		class_2960 lv = new class_2960(arg.method_12836(), "structures/" + arg.method_12832() + ".nbt");

		try {
			class_3298 lv2 = this.field_15516.method_3809().method_14486(lv);
			Throwable var4 = null;

			class_3499 var5;
			try {
				var5 = this.method_15090(lv2.method_14482());
			} catch (Throwable var16) {
				var4 = var16;
				throw var16;
			} finally {
				if (lv2 != null) {
					if (var4 != null) {
						try {
							lv2.close();
						} catch (Throwable var15) {
							var4.addSuppressed(var15);
						}
					} else {
						lv2.close();
					}
				}
			}

			return var5;
		} catch (FileNotFoundException var18) {
			return null;
		} catch (Throwable var19) {
			field_15514.error("Couldn't load structure {}: {}", arg, var19.toString());
			return null;
		}
	}

	@Nullable
	private class_3499 method_15092(class_2960 arg) {
		if (!this.field_15512.toFile().isDirectory()) {
			return null;
		} else {
			Path path = this.method_15086(arg, ".nbt");

			try {
				InputStream inputStream = new FileInputStream(path.toFile());
				Throwable var4 = null;

				class_3499 var5;
				try {
					var5 = this.method_15090(inputStream);
				} catch (Throwable var16) {
					var4 = var16;
					throw var16;
				} finally {
					if (inputStream != null) {
						if (var4 != null) {
							try {
								inputStream.close();
							} catch (Throwable var15) {
								var4.addSuppressed(var15);
							}
						} else {
							inputStream.close();
						}
					}
				}

				return var5;
			} catch (FileNotFoundException var18) {
				return null;
			} catch (IOException var19) {
				field_15514.error("Couldn't load structure from {}", path, var19);
				return null;
			}
		}
	}

	private class_3499 method_15090(InputStream inputStream) throws IOException {
		class_2487 lv = class_2507.method_10629(inputStream);
		if (!lv.method_10573("DataVersion", 99)) {
			lv.method_10569("DataVersion", 500);
		}

		class_3499 lv2 = new class_3499();
		lv2.method_15183(class_2512.method_10688(this.field_15515, DataFixTypes.STRUCTURE, lv, lv.method_10550("DataVersion")));
		return lv2;
	}

	public boolean method_15093(class_2960 arg) {
		class_3499 lv = (class_3499)this.field_15513.get(arg);
		if (lv == null) {
			return false;
		} else {
			Path path = this.method_15086(arg, ".nbt");
			Path path2 = path.getParent();
			if (path2 == null) {
				return false;
			} else {
				try {
					Files.createDirectories(Files.exists(path2, new LinkOption[0]) ? path2.toRealPath() : path2);
				} catch (IOException var19) {
					field_15514.error("Failed to create parent directory: {}", path2);
					return false;
				}

				class_2487 lv2 = lv.method_15175(new class_2487());

				try {
					OutputStream outputStream = new FileOutputStream(path.toFile());
					Throwable var7 = null;

					try {
						class_2507.method_10634(lv2, outputStream);
					} catch (Throwable var18) {
						var7 = var18;
						throw var18;
					} finally {
						if (outputStream != null) {
							if (var7 != null) {
								try {
									outputStream.close();
								} catch (Throwable var17) {
									var7.addSuppressed(var17);
								}
							} else {
								outputStream.close();
							}
						}
					}

					return true;
				} catch (Throwable var21) {
					return false;
				}
			}
		}
	}

	private Path method_15085(class_2960 arg, String string) {
		try {
			Path path = this.field_15512.resolve(arg.method_12836());
			Path path2 = path.resolve("structures");
			return class_156.method_662(path2, arg.method_12832(), string);
		} catch (InvalidPathException var5) {
			throw new class_151("Invalid resource path: " + arg, var5);
		}
	}

	private Path method_15086(class_2960 arg, String string) {
		if (arg.method_12832().contains("//")) {
			throw new class_151("Invalid resource path: " + arg);
		} else {
			Path path = this.method_15085(arg, string);
			if (path.startsWith(this.field_15512) && class_156.method_653(path) && class_156.method_665(path)) {
				return path;
			} else {
				throw new class_151("Invalid resource path: " + path);
			}
		}
	}

	public void method_15087(class_2960 arg) {
		this.field_15513.remove(arg);
	}
}
