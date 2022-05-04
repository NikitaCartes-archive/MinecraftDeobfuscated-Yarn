/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.ResourceNotFoundException;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.Util;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class DirectoryResourcePack
extends AbstractFileResourcePack {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final boolean IS_WINDOWS = Util.getOperatingSystem() == Util.OperatingSystem.WINDOWS;
    private static final CharMatcher BACKSLASH_MATCHER = CharMatcher.is('\\');

    public DirectoryResourcePack(File file) {
        super(file);
    }

    public static boolean isValidPath(File file, String filename) throws IOException {
        String string = file.getCanonicalPath();
        if (IS_WINDOWS) {
            string = BACKSLASH_MATCHER.replaceFrom((CharSequence)string, '/');
        }
        return string.endsWith(filename);
    }

    @Override
    protected InputStream openFile(String name) throws IOException {
        File file = this.getFile(name);
        if (file == null) {
            throw new ResourceNotFoundException(this.base, name);
        }
        return new FileInputStream(file);
    }

    @Override
    protected boolean containsFile(String name) {
        return this.getFile(name) != null;
    }

    @Nullable
    private File getFile(String name) {
        try {
            File file = new File(this.base, name);
            if (file.isFile() && DirectoryResourcePack.isValidPath(file, name)) {
                return file;
            }
        } catch (IOException iOException) {
            // empty catch block
        }
        return null;
    }

    @Override
    public Set<String> getNamespaces(ResourceType type) {
        HashSet<String> set = Sets.newHashSet();
        File file = new File(this.base, type.getDirectory());
        File[] files = file.listFiles(DirectoryFileFilter.DIRECTORY);
        if (files != null) {
            for (File file2 : files) {
                String string = DirectoryResourcePack.relativize(file, file2);
                if (string.equals(string.toLowerCase(Locale.ROOT))) {
                    set.add(string.substring(0, string.length() - 1));
                    continue;
                }
                this.warnNonLowerCaseNamespace(string);
            }
        }
        return set;
    }

    @Override
    public void close() {
    }

    @Override
    public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, Predicate<Identifier> allowedPathPredicate) {
        File file = new File(this.base, type.getDirectory());
        ArrayList<Identifier> list = Lists.newArrayList();
        this.findFiles(new File(new File(file, namespace), prefix), namespace, list, prefix + "/", allowedPathPredicate);
        return list;
    }

    private void findFiles(File file, String namespace, List<Identifier> foundIds, String rootDirectory, Predicate<Identifier> allowedPathPredicate) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File file2 : files) {
                if (file2.isDirectory()) {
                    this.findFiles(file2, namespace, foundIds, rootDirectory + file2.getName() + "/", allowedPathPredicate);
                    continue;
                }
                if (file2.getName().endsWith(".mcmeta")) continue;
                try {
                    String string = rootDirectory + file2.getName();
                    Identifier identifier = Identifier.of(namespace, string);
                    if (identifier == null) {
                        LOGGER.warn("Invalid path in datapack: {}:{}, ignoring", (Object)namespace, (Object)string);
                        continue;
                    }
                    if (!allowedPathPredicate.test(identifier)) continue;
                    foundIds.add(identifier);
                } catch (InvalidIdentifierException invalidIdentifierException) {
                    LOGGER.error(invalidIdentifierException.getMessage());
                }
            }
        }
    }
}

