/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import java.io.File;
import java.io.FileFilter;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ZipResourcePack;

public class FileResourcePackProvider
implements ResourcePackProvider {
    private static final FileFilter POSSIBLE_PACK = file -> {
        boolean bl = file.isFile() && file.getName().endsWith(".zip");
        boolean bl2 = file.isDirectory() && new File(file, "pack.mcmeta").isFile();
        return bl || bl2;
    };
    private final File packsFolder;
    private final ResourcePackSource source;

    public FileResourcePackProvider(File packsFolder, ResourcePackSource source) {
        this.packsFolder = packsFolder;
        this.source = source;
    }

    @Override
    public void register(Consumer<ResourcePackProfile> profileAdder, ResourcePackProfile.Factory factory) {
        File[] files;
        if (!this.packsFolder.isDirectory()) {
            this.packsFolder.mkdirs();
        }
        if ((files = this.packsFolder.listFiles(POSSIBLE_PACK)) == null) {
            return;
        }
        for (File file : files) {
            String string = "file/" + file.getName();
            ResourcePackProfile resourcePackProfile = ResourcePackProfile.of(string, false, this.createResourcePack(file), factory, ResourcePackProfile.InsertionPosition.TOP, this.source);
            if (resourcePackProfile == null) continue;
            profileAdder.accept(resourcePackProfile);
        }
    }

    private Supplier<ResourcePack> createResourcePack(File file) {
        if (file.isDirectory()) {
            return () -> new DirectoryResourcePack(file);
        }
        return () -> new ZipResourcePack(file);
    }
}

