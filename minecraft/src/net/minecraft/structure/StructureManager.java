package net.minecraft.structure;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
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
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.FileNameUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureManager {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final String field_31684 = "structures";
	private static final String field_31685 = ".nbt";
	private static final String field_31686 = ".snbt";
	private final Map<Identifier, Structure> structures = Maps.<Identifier, Structure>newHashMap();
	private final DataFixer dataFixer;
	private ResourceManager resourceManager;
	private final Path generatedPath;

	public StructureManager(ResourceManager resourceManager, LevelStorage.Session session, DataFixer dataFixer) {
		this.resourceManager = resourceManager;
		this.dataFixer = dataFixer;
		this.generatedPath = session.getDirectory(WorldSavePath.GENERATED).normalize();
	}

	public Structure getStructureOrBlank(Identifier id) {
		Structure structure = this.getStructure(id);
		if (structure == null) {
			structure = new Structure();
			this.structures.put(id, structure);
		}

		return structure;
	}

	@Nullable
	public Structure getStructure(Identifier id) {
		return (Structure)this.structures.computeIfAbsent(id, identifier -> {
			Structure structure = this.loadStructureFromFile(identifier);
			return structure != null ? structure : this.loadStructureFromResource(identifier);
		});
	}

	public void setResourceManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
		this.structures.clear();
	}

	@Nullable
	private Structure loadStructureFromResource(Identifier id) {
		Identifier identifier = new Identifier(id.getNamespace(), "structures/" + id.getPath() + ".nbt");

		try {
			Resource resource = this.resourceManager.getResource(identifier);

			Structure var4;
			try {
				var4 = this.readStructure(resource.getInputStream());
			} catch (Throwable var7) {
				if (resource != null) {
					try {
						resource.close();
					} catch (Throwable var6) {
						var7.addSuppressed(var6);
					}
				}

				throw var7;
			}

			if (resource != null) {
				resource.close();
			}

			return var4;
		} catch (FileNotFoundException var8) {
			return null;
		} catch (Throwable var9) {
			LOGGER.error("Couldn't load structure {}: {}", id, var9.toString());
			return null;
		}
	}

	@Nullable
	private Structure loadStructureFromFile(Identifier id) {
		if (!this.generatedPath.toFile().isDirectory()) {
			return null;
		} else {
			Path path = this.getAndCheckStructurePath(id, ".nbt");

			try {
				InputStream inputStream = new FileInputStream(path.toFile());

				Structure var4;
				try {
					var4 = this.readStructure(inputStream);
				} catch (Throwable var7) {
					try {
						inputStream.close();
					} catch (Throwable var6) {
						var7.addSuppressed(var6);
					}

					throw var7;
				}

				inputStream.close();
				return var4;
			} catch (FileNotFoundException var8) {
				return null;
			} catch (IOException var9) {
				LOGGER.error("Couldn't load structure from {}", path, var9);
				return null;
			}
		}
	}

	private Structure readStructure(InputStream structureInputStream) throws IOException {
		NbtCompound nbtCompound = NbtIo.readCompressed(structureInputStream);
		return this.createStructure(nbtCompound);
	}

	public Structure createStructure(NbtCompound nbt) {
		if (!nbt.contains("DataVersion", NbtElement.NUMBER_TYPE)) {
			nbt.putInt("DataVersion", 500);
		}

		Structure structure = new Structure();
		structure.readNbt(NbtHelper.update(this.dataFixer, DataFixTypes.STRUCTURE, nbt, nbt.getInt("DataVersion")));
		return structure;
	}

	public boolean saveStructure(Identifier id) {
		Structure structure = (Structure)this.structures.get(id);
		if (structure == null) {
			return false;
		} else {
			Path path = this.getAndCheckStructurePath(id, ".nbt");
			Path path2 = path.getParent();
			if (path2 == null) {
				return false;
			} else {
				try {
					Files.createDirectories(Files.exists(path2, new LinkOption[0]) ? path2.toRealPath() : path2);
				} catch (IOException var12) {
					LOGGER.error("Failed to create parent directory: {}", path2);
					return false;
				}

				NbtCompound nbtCompound = structure.writeNbt(new NbtCompound());

				try {
					OutputStream outputStream = new FileOutputStream(path.toFile());

					try {
						NbtIo.writeCompressed(nbtCompound, outputStream);
					} catch (Throwable var10) {
						try {
							outputStream.close();
						} catch (Throwable var9) {
							var10.addSuppressed(var9);
						}

						throw var10;
					}

					outputStream.close();
					return true;
				} catch (Throwable var11) {
					return false;
				}
			}
		}
	}

	public Path getStructurePath(Identifier id, String extension) {
		try {
			Path path = this.generatedPath.resolve(id.getNamespace());
			Path path2 = path.resolve("structures");
			return FileNameUtil.getResourcePath(path2, id.getPath(), extension);
		} catch (InvalidPathException var5) {
			throw new InvalidIdentifierException("Invalid resource path: " + id, var5);
		}
	}

	private Path getAndCheckStructurePath(Identifier id, String extension) {
		if (id.getPath().contains("//")) {
			throw new InvalidIdentifierException("Invalid resource path: " + id);
		} else {
			Path path = this.getStructurePath(id, extension);
			if (path.startsWith(this.generatedPath) && FileNameUtil.isNormal(path) && FileNameUtil.isAllowedName(path)) {
				return path;
			} else {
				throw new InvalidIdentifierException("Invalid resource path: " + path);
			}
		}
	}

	public void unloadStructure(Identifier id) {
		this.structures.remove(id);
	}
}
