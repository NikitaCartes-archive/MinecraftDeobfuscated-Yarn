/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
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
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.command.CommandRegistryWrapper;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.PathUtil;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class StructureTemplateManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String STRUCTURES_DIRECTORY = "structures";
    private static final String GAME_TEST_STRUCTURES_DIRECTORY = "gameteststructures";
    private static final String NBT_FILE_EXTENSION = ".nbt";
    private static final String SNBT_FILE_EXTENSION = ".snbt";
    private final Map<Identifier, Optional<StructureTemplate>> templates = Maps.newConcurrentMap();
    private final DataFixer dataFixer;
    private ResourceManager resourceManager;
    private final Path generatedPath;
    private final List<Provider> providers;
    private final CommandRegistryWrapper<Block> blockRegistryWrapper;
    private static final ResourceFinder NBT_FINDER = new ResourceFinder("structures", ".nbt");

    public StructureTemplateManager(ResourceManager resourceManager, LevelStorage.Session session, DataFixer dataFixer, CommandRegistryWrapper<Block> blockRegistryWrapper) {
        this.resourceManager = resourceManager;
        this.dataFixer = dataFixer;
        this.generatedPath = session.getDirectory(WorldSavePath.GENERATED).normalize();
        this.blockRegistryWrapper = blockRegistryWrapper;
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.add(new Provider(this::loadTemplateFromFile, this::streamTemplatesFromFile));
        if (SharedConstants.isDevelopment) {
            builder.add(new Provider(this::loadTemplateFromGameTestFile, this::streamTemplatesFromGameTestFile));
        }
        builder.add(new Provider(this::loadTemplateFromResource, this::streamTemplatesFromResource));
        this.providers = builder.build();
    }

    public StructureTemplate getTemplateOrBlank(Identifier id) {
        Optional<StructureTemplate> optional = this.getTemplate(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        StructureTemplate structureTemplate = new StructureTemplate();
        this.templates.put(id, Optional.of(structureTemplate));
        return structureTemplate;
    }

    public Optional<StructureTemplate> getTemplate(Identifier id) {
        return this.templates.computeIfAbsent(id, this::loadTemplate);
    }

    public Stream<Identifier> streamTemplates() {
        return this.providers.stream().flatMap(provider -> provider.lister().get()).distinct();
    }

    private Optional<StructureTemplate> loadTemplate(Identifier id) {
        for (Provider provider : this.providers) {
            try {
                Optional<StructureTemplate> optional = provider.loader().apply(id);
                if (!optional.isPresent()) continue;
                return optional;
            } catch (Exception exception) {
            }
        }
        return Optional.empty();
    }

    public void setResourceManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        this.templates.clear();
    }

    private Optional<StructureTemplate> loadTemplateFromResource(Identifier id) {
        Identifier identifier = NBT_FINDER.toResourcePath(id);
        return this.loadTemplate(() -> this.resourceManager.open(identifier), throwable -> LOGGER.error("Couldn't load structure {}", (Object)id, throwable));
    }

    private Stream<Identifier> streamTemplatesFromResource() {
        return NBT_FINDER.findResources(this.resourceManager).keySet().stream().map(NBT_FINDER::toResourceId);
    }

    private Optional<StructureTemplate> loadTemplateFromGameTestFile(Identifier id) {
        return this.loadTemplateFromSnbt(id, Paths.get(GAME_TEST_STRUCTURES_DIRECTORY, new String[0]));
    }

    private Stream<Identifier> streamTemplatesFromGameTestFile() {
        return this.streamTemplates(Paths.get(GAME_TEST_STRUCTURES_DIRECTORY, new String[0]), "minecraft", SNBT_FILE_EXTENSION);
    }

    private Optional<StructureTemplate> loadTemplateFromFile(Identifier id) {
        if (!Files.isDirectory(this.generatedPath, new LinkOption[0])) {
            return Optional.empty();
        }
        Path path = StructureTemplateManager.getAndCheckTemplatePath(this.generatedPath, id, NBT_FILE_EXTENSION);
        return this.loadTemplate(() -> new FileInputStream(path.toFile()), throwable -> LOGGER.error("Couldn't load structure from {}", (Object)path, throwable));
    }

    private Stream<Identifier> streamTemplatesFromFile() {
        if (!Files.isDirectory(this.generatedPath, new LinkOption[0])) {
            return Stream.empty();
        }
        try {
            return Files.list(this.generatedPath).filter(path -> Files.isDirectory(path, new LinkOption[0])).flatMap(path -> this.streamTemplates((Path)path));
        } catch (IOException iOException) {
            return Stream.empty();
        }
    }

    private Stream<Identifier> streamTemplates(Path namespaceDirectory) {
        Path path = namespaceDirectory.resolve(STRUCTURES_DIRECTORY);
        return this.streamTemplates(path, namespaceDirectory.getFileName().toString(), NBT_FILE_EXTENSION);
    }

    private Stream<Identifier> streamTemplates(Path structuresDirectoryPath, String namespace, String extension) {
        if (!Files.isDirectory(structuresDirectoryPath, new LinkOption[0])) {
            return Stream.empty();
        }
        int i = extension.length();
        Function<String, String> function = filename -> filename.substring(0, filename.length() - i);
        try {
            return Files.walk(structuresDirectoryPath, new FileVisitOption[0]).filter(path -> path.toString().endsWith(extension)).mapMulti((path, consumer) -> {
                try {
                    consumer.accept(new Identifier(namespace, (String)function.apply(this.toRelativePath(structuresDirectoryPath, (Path)path))));
                } catch (InvalidIdentifierException invalidIdentifierException) {
                    LOGGER.error("Invalid location while listing pack contents", invalidIdentifierException);
                }
            });
        } catch (IOException iOException) {
            LOGGER.error("Failed to list folder contents", iOException);
            return Stream.empty();
        }
    }

    private String toRelativePath(Path root, Path path) {
        return root.relativize(path).toString().replace(File.separator, "/");
    }

    private Optional<StructureTemplate> loadTemplateFromSnbt(Identifier id, Path path) {
        Optional<StructureTemplate> optional;
        block10: {
            if (!Files.isDirectory(path, new LinkOption[0])) {
                return Optional.empty();
            }
            Path path2 = PathUtil.getResourcePath(path, id.getPath(), SNBT_FILE_EXTENSION);
            BufferedReader bufferedReader = Files.newBufferedReader(path2);
            try {
                String string = IOUtils.toString(bufferedReader);
                optional = Optional.of(this.createTemplate(NbtHelper.fromNbtProviderString(string)));
                if (bufferedReader == null) break block10;
            } catch (Throwable throwable) {
                try {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                } catch (NoSuchFileException noSuchFileException) {
                    return Optional.empty();
                } catch (CommandSyntaxException | IOException exception) {
                    LOGGER.error("Couldn't load structure from {}", (Object)path2, (Object)exception);
                    return Optional.empty();
                }
            }
            bufferedReader.close();
        }
        return optional;
    }

    private Optional<StructureTemplate> loadTemplate(TemplateFileOpener opener, Consumer<Throwable> exceptionConsumer) {
        Optional<StructureTemplate> optional;
        block9: {
            InputStream inputStream = opener.open();
            try {
                optional = Optional.of(this.readTemplate(inputStream));
                if (inputStream == null) break block9;
            } catch (Throwable throwable) {
                try {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                } catch (FileNotFoundException fileNotFoundException) {
                    return Optional.empty();
                } catch (Throwable throwable3) {
                    exceptionConsumer.accept(throwable3);
                    return Optional.empty();
                }
            }
            inputStream.close();
        }
        return optional;
    }

    private StructureTemplate readTemplate(InputStream templateIInputStream) throws IOException {
        NbtCompound nbtCompound = NbtIo.readCompressed(templateIInputStream);
        return this.createTemplate(nbtCompound);
    }

    public StructureTemplate createTemplate(NbtCompound nbt) {
        if (!nbt.contains("DataVersion", NbtElement.NUMBER_TYPE)) {
            nbt.putInt("DataVersion", 500);
        }
        StructureTemplate structureTemplate = new StructureTemplate();
        structureTemplate.readNbt(this.blockRegistryWrapper, NbtHelper.update(this.dataFixer, DataFixTypes.STRUCTURE, nbt, nbt.getInt("DataVersion")));
        return structureTemplate;
    }

    public boolean saveTemplate(Identifier id) {
        Optional<StructureTemplate> optional = this.templates.get(id);
        if (!optional.isPresent()) {
            return false;
        }
        StructureTemplate structureTemplate = optional.get();
        Path path = StructureTemplateManager.getAndCheckTemplatePath(this.generatedPath, id, NBT_FILE_EXTENSION);
        Path path2 = path.getParent();
        if (path2 == null) {
            return false;
        }
        try {
            Files.createDirectories(Files.exists(path2, new LinkOption[0]) ? path2.toRealPath(new LinkOption[0]) : path2, new FileAttribute[0]);
        } catch (IOException iOException) {
            LOGGER.error("Failed to create parent directory: {}", (Object)path2);
            return false;
        }
        NbtCompound nbtCompound = structureTemplate.writeNbt(new NbtCompound());
        try (FileOutputStream outputStream = new FileOutputStream(path.toFile());){
            NbtIo.writeCompressed(nbtCompound, outputStream);
        } catch (Throwable throwable) {
            return false;
        }
        return true;
    }

    public Path getTemplatePath(Identifier id, String extension) {
        return StructureTemplateManager.getTemplatePath(this.generatedPath, id, extension);
    }

    public static Path getTemplatePath(Path path, Identifier id, String extension) {
        try {
            Path path2 = path.resolve(id.getNamespace());
            Path path3 = path2.resolve(STRUCTURES_DIRECTORY);
            return PathUtil.getResourcePath(path3, id.getPath(), extension);
        } catch (InvalidPathException invalidPathException) {
            throw new InvalidIdentifierException("Invalid resource path: " + id, invalidPathException);
        }
    }

    private static Path getAndCheckTemplatePath(Path path, Identifier id, String extension) {
        if (id.getPath().contains("//")) {
            throw new InvalidIdentifierException("Invalid resource path: " + id);
        }
        Path path2 = StructureTemplateManager.getTemplatePath(path, id, extension);
        if (!(path2.startsWith(path) && PathUtil.isNormal(path2) && PathUtil.isAllowedName(path2))) {
            throw new InvalidIdentifierException("Invalid resource path: " + path2);
        }
        return path2;
    }

    public void unloadTemplate(Identifier id) {
        this.templates.remove(id);
    }

    record Provider(Function<Identifier, Optional<StructureTemplate>> loader, Supplier<Stream<Identifier>> lister) {
    }

    @FunctionalInterface
    static interface TemplateFileOpener {
        public InputStream open() throws IOException;
    }
}

