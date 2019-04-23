/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackContainer;
import net.minecraft.resource.ResourcePackCreator;
import net.minecraft.resource.ZipResourcePack;

public class FileResourcePackCreator
implements ResourcePackCreator {
    private static final FileFilter POSSIBLE_PACK = file -> {
        boolean bl = file.isFile() && file.getName().endsWith(".zip");
        boolean bl2 = file.isDirectory() && new File(file, "pack.mcmeta").isFile();
        return bl || bl2;
    };
    private final File packsFolder;

    public FileResourcePackCreator(File file) {
        this.packsFolder = file;
    }

    @Override
    public <T extends ResourcePackContainer> void registerContainer(Map<String, T> map, ResourcePackContainer.Factory<T> factory) {
        File[] files;
        if (!this.packsFolder.isDirectory()) {
            this.packsFolder.mkdirs();
        }
        if ((files = this.packsFolder.listFiles(POSSIBLE_PACK)) == null) {
            return;
        }
        for (File file : files) {
            String string = "file/" + file.getName();
            T resourcePackContainer = ResourcePackContainer.of(string, false, this.createResourcePack(file), factory, ResourcePackContainer.InsertionPosition.TOP);
            if (resourcePackContainer == null) continue;
            map.put(string, resourcePackContainer);
        }
    }

    private Supplier<ResourcePack> createResourcePack(File file) {
        if (file.isDirectory()) {
            return () -> new DirectoryResourcePack(file);
        }
        return () -> new ZipResourcePack(file);
    }
}

