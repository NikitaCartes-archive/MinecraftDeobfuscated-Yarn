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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.TagHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3485 implements ResourceReloadListener {
	private static final Logger logger = LogManager.getLogger();
	private final Map<Identifier, class_3499> field_15513 = Maps.<Identifier, class_3499>newHashMap();
	private final DataFixer field_15515;
	private final MinecraftServer server;
	private final Path field_15512;

	public class_3485(MinecraftServer minecraftServer, File file, DataFixer dataFixer) {
		this.server = minecraftServer;
		this.field_15515 = dataFixer;
		this.field_15512 = file.toPath().resolve("generated").normalize();
		minecraftServer.getDataManager().addListener(this);
	}

	public class_3499 method_15091(Identifier identifier) {
		class_3499 lv = this.method_15094(identifier);
		if (lv == null) {
			lv = new class_3499();
			this.field_15513.put(identifier, lv);
		}

		return lv;
	}

	@Nullable
	public class_3499 method_15094(Identifier identifier) {
		return (class_3499)this.field_15513.computeIfAbsent(identifier, identifierx -> {
			class_3499 lv = this.method_15092(identifierx);
			return lv != null ? lv : this.method_15088(identifierx);
		});
	}

	@Override
	public void onResourceReload(ResourceManager resourceManager) {
		this.field_15513.clear();
	}

	@Nullable
	private class_3499 method_15088(Identifier identifier) {
		Identifier identifier2 = new Identifier(identifier.getNamespace(), "structures/" + identifier.getPath() + ".nbt");

		try {
			Resource resource = this.server.getDataManager().getResource(identifier2);
			Throwable var4 = null;

			class_3499 var5;
			try {
				var5 = this.method_15090(resource.getInputStream());
			} catch (Throwable var16) {
				var4 = var16;
				throw var16;
			} finally {
				if (resource != null) {
					if (var4 != null) {
						try {
							resource.close();
						} catch (Throwable var15) {
							var4.addSuppressed(var15);
						}
					} else {
						resource.close();
					}
				}
			}

			return var5;
		} catch (FileNotFoundException var18) {
			return null;
		} catch (Throwable var19) {
			logger.error("Couldn't load structure {}: {}", identifier, var19.toString());
			return null;
		}
	}

	@Nullable
	private class_3499 method_15092(Identifier identifier) {
		if (!this.field_15512.toFile().isDirectory()) {
			return null;
		} else {
			Path path = this.method_15086(identifier, ".nbt");

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
				logger.error("Couldn't load structure from {}", path, var19);
				return null;
			}
		}
	}

	private class_3499 method_15090(InputStream inputStream) throws IOException {
		CompoundTag compoundTag = NbtIo.readCompressed(inputStream);
		if (!compoundTag.containsKey("DataVersion", 99)) {
			compoundTag.putInt("DataVersion", 500);
		}

		class_3499 lv = new class_3499();
		lv.method_15183(TagHelper.update(this.field_15515, DataFixTypes.STRUCTURE, compoundTag, compoundTag.getInt("DataVersion")));
		return lv;
	}

	public boolean method_15093(Identifier identifier) {
		class_3499 lv = (class_3499)this.field_15513.get(identifier);
		if (lv == null) {
			return false;
		} else {
			Path path = this.method_15086(identifier, ".nbt");
			Path path2 = path.getParent();
			if (path2 == null) {
				return false;
			} else {
				try {
					Files.createDirectories(Files.exists(path2, new LinkOption[0]) ? path2.toRealPath() : path2);
				} catch (IOException var19) {
					logger.error("Failed to create parent directory: {}", path2);
					return false;
				}

				CompoundTag compoundTag = lv.method_15175(new CompoundTag());

				try {
					OutputStream outputStream = new FileOutputStream(path.toFile());
					Throwable var7 = null;

					try {
						NbtIo.writeCompressed(compoundTag, outputStream);
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

	private Path method_15085(Identifier identifier, String string) {
		try {
			Path path = this.field_15512.resolve(identifier.getNamespace());
			Path path2 = path.resolve("structures");
			return SystemUtil.method_662(path2, identifier.getPath(), string);
		} catch (InvalidPathException var5) {
			throw new InvalidIdentifierException("Invalid resource path: " + identifier, var5);
		}
	}

	private Path method_15086(Identifier identifier, String string) {
		if (identifier.getPath().contains("//")) {
			throw new InvalidIdentifierException("Invalid resource path: " + identifier);
		} else {
			Path path = this.method_15085(identifier, string);
			if (path.startsWith(this.field_15512) && SystemUtil.method_653(path) && SystemUtil.method_665(path)) {
				return path;
			} else {
				throw new InvalidIdentifierException("Invalid resource path: " + path);
			}
		}
	}

	public void method_15087(Identifier identifier) {
		this.field_15513.remove(identifier);
	}
}
