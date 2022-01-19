package net.minecraft.structure;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
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
import java.util.Optional;
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
import org.slf4j.Logger;

public class StructureManager {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String STRUCTURES_DIRECTORY = "structures";
	private static final String NBT_FILE_EXTENSION = ".nbt";
	private static final String SNBT_FILE_EXTENSION = ".snbt";
	private final Map<Identifier, Optional<Structure>> structures = Maps.<Identifier, Optional<Structure>>newConcurrentMap();
	private final DataFixer dataFixer;
	private ResourceManager resourceManager;
	private final Path generatedPath;

	public StructureManager(ResourceManager resourceManager, LevelStorage.Session session, DataFixer dataFixer) {
		this.resourceManager = resourceManager;
		this.dataFixer = dataFixer;
		this.generatedPath = session.getDirectory(WorldSavePath.GENERATED).normalize();
	}

	public Structure getStructureOrBlank(Identifier id) {
		Optional<Structure> optional = this.getStructure(id);
		if (optional.isPresent()) {
			return (Structure)optional.get();
		} else {
			Structure structure = new Structure();
			this.structures.put(id, Optional.of(structure));
			return structure;
		}
	}

	public Optional<Structure> getStructure(Identifier id) {
		return (Optional<Structure>)this.structures.computeIfAbsent(id, identifier -> {
			Optional<Structure> optional = this.loadStructureFromFile(identifier);
			return optional.isPresent() ? optional : this.loadStructureFromResource(identifier);
		});
	}

	public void setResourceManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
		this.structures.clear();
	}

	private Optional<Structure> loadStructureFromResource(Identifier id) {
		Identifier identifier = new Identifier(id.getNamespace(), "structures/" + id.getPath() + ".nbt");

		try {
			Resource resource = this.resourceManager.getResource(identifier);

			Optional var4;
			try {
				var4 = Optional.of(this.readStructure(resource.getInputStream()));
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
			return Optional.empty();
		} catch (Throwable var9) {
			LOGGER.error("Couldn't load structure {}: {}", id, var9.toString());
			return Optional.empty();
		}
	}

	private Optional<Structure> loadStructureFromFile(Identifier id) {
		if (!this.generatedPath.toFile().isDirectory()) {
			return Optional.empty();
		} else {
			Path path = this.getAndCheckStructurePath(id, ".nbt");

			try {
				InputStream inputStream = new FileInputStream(path.toFile());

				Optional var4;
				try {
					var4 = Optional.of(this.readStructure(inputStream));
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
				return Optional.empty();
			} catch (IOException var9) {
				LOGGER.error("Couldn't load structure from {}", path, var9);
				return Optional.empty();
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
		Optional<Structure> optional = (Optional<Structure>)this.structures.get(id);
		if (!optional.isPresent()) {
			return false;
		} else {
			Structure structure = (Structure)optional.get();
			Path path = this.getAndCheckStructurePath(id, ".nbt");
			Path path2 = path.getParent();
			if (path2 == null) {
				return false;
			} else {
				try {
					Files.createDirectories(Files.exists(path2, new LinkOption[0]) ? path2.toRealPath() : path2);
				} catch (IOException var13) {
					LOGGER.error("Failed to create parent directory: {}", path2);
					return false;
				}

				NbtCompound nbtCompound = structure.writeNbt(new NbtCompound());

				try {
					OutputStream outputStream = new FileOutputStream(path.toFile());

					try {
						NbtIo.writeCompressed(nbtCompound, outputStream);
					} catch (Throwable var11) {
						try {
							outputStream.close();
						} catch (Throwable var10) {
							var11.addSuppressed(var10);
						}

						throw var11;
					}

					outputStream.close();
					return true;
				} catch (Throwable var12) {
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
