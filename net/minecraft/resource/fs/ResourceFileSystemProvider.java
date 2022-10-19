/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource.fs;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessDeniedException;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;
import java.nio.file.ReadOnlyFileSystemException;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.resource.fs.ResourceFile;
import net.minecraft.resource.fs.ResourcePath;
import org.jetbrains.annotations.Nullable;

class ResourceFileSystemProvider
extends FileSystemProvider {
    public static final String SCHEME = "x-mc-link";

    ResourceFileSystemProvider() {
    }

    @Override
    public String getScheme() {
        return SCHEME;
    }

    @Override
    public FileSystem newFileSystem(URI uri, Map<String, ?> env) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileSystem getFileSystem(URI uri) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Path getPath(URI uri) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?> ... attrs) throws IOException {
        if (options.contains(StandardOpenOption.CREATE_NEW) || options.contains(StandardOpenOption.CREATE) || options.contains(StandardOpenOption.APPEND) || options.contains(StandardOpenOption.WRITE)) {
            throw new UnsupportedOperationException();
        }
        Path path2 = ResourceFileSystemProvider.toResourcePath(path).toAbsolutePath().toPath();
        if (path2 == null) {
            throw new NoSuchFileException(path.toString());
        }
        return Files.newByteChannel(path2, options, attrs);
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, final DirectoryStream.Filter<? super Path> filter) throws IOException {
        final ResourceFile.Directory directory = ResourceFileSystemProvider.toResourcePath(dir).toAbsolutePath().toDirectory();
        if (directory == null) {
            throw new NotDirectoryException(dir.toString());
        }
        return new DirectoryStream<Path>(){

            @Override
            public Iterator<Path> iterator() {
                return directory.children().values().stream().filter(child -> {
                    try {
                        return filter.accept(child);
                    } catch (IOException iOException) {
                        throw new DirectoryIteratorException(iOException);
                    }
                }).map(child -> child).iterator();
            }

            @Override
            public void close() {
            }
        };
    }

    @Override
    public void createDirectory(Path dir, FileAttribute<?> ... attrs) {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public void delete(Path path) {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public void copy(Path source, Path target, CopyOption ... options) {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public void move(Path source, Path target, CopyOption ... options) {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public boolean isSameFile(Path path, Path path2) {
        return path instanceof ResourcePath && path2 instanceof ResourcePath && path.equals(path2);
    }

    @Override
    public boolean isHidden(Path path) {
        return false;
    }

    @Override
    public FileStore getFileStore(Path path) {
        return ResourceFileSystemProvider.toResourcePath(path).getFileSystem().getStore();
    }

    @Override
    public void checkAccess(Path path, AccessMode ... modes) throws IOException {
        if (modes.length == 0 && !ResourceFileSystemProvider.toResourcePath(path).isReadable()) {
            throw new NoSuchFileException(path.toString());
        }
        block4: for (AccessMode accessMode : modes) {
            switch (accessMode) {
                case READ: {
                    if (ResourceFileSystemProvider.toResourcePath(path).isReadable()) continue block4;
                    throw new NoSuchFileException(path.toString());
                }
                case EXECUTE: 
                case WRITE: {
                    throw new AccessDeniedException(accessMode.toString());
                }
            }
        }
    }

    @Override
    @Nullable
    public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption ... options) {
        ResourcePath resourcePath = ResourceFileSystemProvider.toResourcePath(path);
        if (type == BasicFileAttributeView.class) {
            return (V)resourcePath.getAttributeView();
        }
        return null;
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption ... options) throws IOException {
        ResourcePath resourcePath = ResourceFileSystemProvider.toResourcePath(path).toAbsolutePath();
        if (type == BasicFileAttributes.class) {
            return (A)resourcePath.getAttributes();
        }
        throw new UnsupportedOperationException("Attributes of type " + type.getName() + " not supported");
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption ... options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAttribute(Path path, String attribute, Object value, LinkOption ... options) {
        throw new ReadOnlyFileSystemException();
    }

    private static ResourcePath toResourcePath(@Nullable Path path) {
        if (path == null) {
            throw new NullPointerException();
        }
        if (path instanceof ResourcePath) {
            ResourcePath resourcePath = (ResourcePath)path;
            return resourcePath;
        }
        throw new ProviderMismatchException();
    }
}

