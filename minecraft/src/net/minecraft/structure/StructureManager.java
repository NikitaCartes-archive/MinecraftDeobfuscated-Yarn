package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
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
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.FileNameUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class StructureManager {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String STRUCTURES_DIRECTORY = "structures";
	private static final String GAME_TEST_STRUCTURES_DIRECTORY = "gameteststructures";
	private static final String NBT_FILE_EXTENSION = ".nbt";
	private static final String SNBT_FILE_EXTENSION = ".snbt";
	private final Map<Identifier, Optional<Structure>> structures = Maps.<Identifier, Optional<Structure>>newConcurrentMap();
	private final DataFixer dataFixer;
	private ResourceManager resourceManager;
	private final Path generatedPath;
	private final List<StructureManager.Provider> providers;

	public StructureManager(ResourceManager resourceManager, LevelStorage.Session session, DataFixer dataFixer) {
		this.resourceManager = resourceManager;
		this.dataFixer = dataFixer;
		this.generatedPath = session.getDirectory(WorldSavePath.GENERATED).normalize();
		Builder<StructureManager.Provider> builder = ImmutableList.builder();
		builder.add(new StructureManager.Provider(this::loadStructureFromFile, this::streamStructuresFromFile));
		if (SharedConstants.isDevelopment) {
			builder.add(new StructureManager.Provider(this::loadStructureFromGameTestFile, this::streamStructuresFromGameTestFile));
		}

		builder.add(new StructureManager.Provider(this::loadStructureFromResource, this::streamStructuresFromResource));
		this.providers = builder.build();
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
		return (Optional<Structure>)this.structures.computeIfAbsent(id, this::loadStructure);
	}

	public Stream<Identifier> streamStructures() {
		return this.providers.stream().flatMap(provider -> (Stream)provider.lister().get()).distinct();
	}

	private Optional<Structure> loadStructure(Identifier id) {
		for (StructureManager.Provider provider : this.providers) {
			try {
				Optional<Structure> optional = (Optional<Structure>)provider.loader().apply(id);
				if (optional.isPresent()) {
					return optional;
				}
			} catch (Exception var5) {
			}
		}

		return Optional.empty();
	}

	public void setResourceManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
		this.structures.clear();
	}

	private Optional<Structure> loadStructureFromResource(Identifier id) {
		Identifier identifier = new Identifier(id.getNamespace(), "structures/" + id.getPath() + ".nbt");
		return this.loadStructure(() -> this.resourceManager.open(identifier), throwable -> LOGGER.error("Couldn't load structure {}", id, throwable));
	}

	private Stream<Identifier> streamStructuresFromResource() {
		return this.resourceManager
			.findResources("structures", id -> true)
			.keySet()
			.stream()
			.map(id -> new Identifier(id.getNamespace(), id.getPath().substring("structures".length() + 1, id.getPath().length() - ".nbt".length())));
	}

	private Optional<Structure> loadStructureFromGameTestFile(Identifier id) {
		return this.loadStructureFromSnbt(id, Paths.get("gameteststructures"));
	}

	private Stream<Identifier> streamStructuresFromGameTestFile() {
		return this.streamStructures(Paths.get("gameteststructures"), "minecraft", ".snbt");
	}

	private Optional<Structure> loadStructureFromFile(Identifier id) {
		if (!Files.isDirectory(this.generatedPath, new LinkOption[0])) {
			return Optional.empty();
		} else {
			Path path = getAndCheckStructurePath(this.generatedPath, id, ".nbt");
			return this.loadStructure(() -> new FileInputStream(path.toFile()), throwable -> LOGGER.error("Couldn't load structure from {}", path, throwable));
		}
	}

	private Stream<Identifier> streamStructuresFromFile() {
		if (!Files.isDirectory(this.generatedPath, new LinkOption[0])) {
			return Stream.empty();
		} else {
			try {
				return Files.list(this.generatedPath).filter(path -> Files.isDirectory(path, new LinkOption[0])).flatMap(path -> this.streamStructures(path));
			} catch (IOException var2) {
				return Stream.empty();
			}
		}
	}

	private Stream<Identifier> streamStructures(Path namespaceDirectory) {
		Path path = namespaceDirectory.resolve("structures");
		return this.streamStructures(path, namespaceDirectory.getFileName().toString(), ".nbt");
	}

	private Stream<Identifier> streamStructures(Path structuresDirectoryPath, String namespace, String extension) {
		if (!Files.isDirectory(structuresDirectoryPath, new LinkOption[0])) {
			return Stream.empty();
		} else {
			int i = extension.length();
			Function<String, String> function = filename -> filename.substring(0, filename.length() - i);

			try {
				return Files.walk(structuresDirectoryPath).filter(path -> path.toString().endsWith(extension)).mapMulti((path, consumer) -> {
					try {
						consumer.accept(new Identifier(namespace, (String)function.apply(this.toRelativePath(structuresDirectoryPath, path))));
					} catch (InvalidIdentifierException var7x) {
						LOGGER.error("Invalid location while listing pack contents", (Throwable)var7x);
					}
				});
			} catch (IOException var7) {
				LOGGER.error("Failed to list folder contents", (Throwable)var7);
				return Stream.empty();
			}
		}
	}

	private String toRelativePath(Path root, Path path) {
		return root.relativize(path).toString().replace(File.separator, "/");
	}

	private Optional<Structure> loadStructureFromSnbt(Identifier id, Path path) {
		if (!Files.isDirectory(path, new LinkOption[0])) {
			return Optional.empty();
		} else {
			Path path2 = FileNameUtil.getResourcePath(path, id.getPath(), ".snbt");

			try {
				BufferedReader bufferedReader = Files.newBufferedReader(path2);

				Optional var6;
				try {
					String string = IOUtils.toString(bufferedReader);
					var6 = Optional.of(this.createStructure(NbtHelper.fromNbtProviderString(string)));
				} catch (Throwable var8) {
					if (bufferedReader != null) {
						try {
							bufferedReader.close();
						} catch (Throwable var7) {
							var8.addSuppressed(var7);
						}
					}

					throw var8;
				}

				if (bufferedReader != null) {
					bufferedReader.close();
				}

				return var6;
			} catch (NoSuchFileException var9) {
				return Optional.empty();
			} catch (CommandSyntaxException | IOException var10) {
				LOGGER.error("Couldn't load structure from {}", path2, var10);
				return Optional.empty();
			}
		}
	}

	private Optional<Structure> loadStructure(StructureManager.StructureFileOpener opener, Consumer<Throwable> exceptionConsumer) {
		try {
			InputStream inputStream = opener.open();

			Optional var4;
			try {
				var4 = Optional.of(this.readStructure(inputStream));
			} catch (Throwable var7) {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (Throwable var6) {
						var7.addSuppressed(var6);
					}
				}

				throw var7;
			}

			if (inputStream != null) {
				inputStream.close();
			}

			return var4;
		} catch (FileNotFoundException var8) {
			return Optional.empty();
		} catch (Throwable var9) {
			exceptionConsumer.accept(var9);
			return Optional.empty();
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
			Path path = getAndCheckStructurePath(this.generatedPath, id, ".nbt");
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
		return getStructurePath(this.generatedPath, id, extension);
	}

	public static Path getStructurePath(Path path, Identifier id, String extension) {
		try {
			Path path2 = path.resolve(id.getNamespace());
			Path path3 = path2.resolve("structures");
			return FileNameUtil.getResourcePath(path3, id.getPath(), extension);
		} catch (InvalidPathException var5) {
			throw new InvalidIdentifierException("Invalid resource path: " + id, var5);
		}
	}

	private static Path getAndCheckStructurePath(Path path, Identifier id, String extension) {
		if (id.getPath().contains("//")) {
			throw new InvalidIdentifierException("Invalid resource path: " + id);
		} else {
			Path path2 = getStructurePath(path, id, extension);
			if (path2.startsWith(path) && FileNameUtil.isNormal(path2) && FileNameUtil.isAllowedName(path2)) {
				return path2;
			} else {
				throw new InvalidIdentifierException("Invalid resource path: " + path2);
			}
		}
	}

	public void unloadStructure(Identifier id) {
		this.structures.remove(id);
	}

	static record Provider(Function<Identifier, Optional<Structure>> loader, Supplier<Stream<Identifier>> lister) {
	}

	@FunctionalInterface
	interface StructureFileOpener {
		InputStream open() throws IOException;
	}
}
