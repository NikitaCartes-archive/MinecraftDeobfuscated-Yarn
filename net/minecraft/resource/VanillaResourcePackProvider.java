/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public abstract class VanillaResourcePackProvider
implements ResourcePackProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String VANILLA_KEY = "vanilla";
    private final ResourceType type;
    private final DefaultResourcePack resourcePack;
    private final Identifier id;

    public VanillaResourcePackProvider(ResourceType type, DefaultResourcePack resourcePack, Identifier id) {
        this.type = type;
        this.resourcePack = resourcePack;
        this.id = id;
    }

    @Override
    public void register(Consumer<ResourcePackProfile> profileAdder) {
        ResourcePackProfile resourcePackProfile = this.createDefault(this.resourcePack);
        if (resourcePackProfile != null) {
            profileAdder.accept(resourcePackProfile);
        }
        this.forEachProfile(profileAdder);
    }

    @Nullable
    protected abstract ResourcePackProfile createDefault(ResourcePack var1);

    protected abstract Text getProfileName(String var1);

    public DefaultResourcePack getResourcePack() {
        return this.resourcePack;
    }

    private void forEachProfile(Consumer<ResourcePackProfile> consumer) {
        HashMap<String, Function> map = new HashMap<String, Function>();
        this.forEachProfile(map::put);
        map.forEach((fileName, packFactory) -> {
            ResourcePackProfile resourcePackProfile = (ResourcePackProfile)packFactory.apply(fileName);
            if (resourcePackProfile != null) {
                consumer.accept(resourcePackProfile);
            }
        });
    }

    protected void forEachProfile(BiConsumer<String, Function<String, ResourcePackProfile>> consumer) {
        this.resourcePack.forEachNamespacedPath(this.type, this.id, namespacedPath -> this.forEachProfile((Path)namespacedPath, consumer));
    }

    protected void forEachProfile(@Nullable Path namespacedPath, BiConsumer<String, Function<String, ResourcePackProfile>> consumer) {
        if (namespacedPath != null && Files.isDirectory(namespacedPath, new LinkOption[0])) {
            try {
                FileResourcePackProvider.forEachProfile(namespacedPath, (profilePath, factory) -> consumer.accept(VanillaResourcePackProvider.getFileName(profilePath), name -> this.create((String)name, (ResourcePackProfile.PackFactory)factory, this.getProfileName((String)name))));
            } catch (IOException iOException) {
                LOGGER.warn("Failed to discover packs in {}", (Object)namespacedPath, (Object)iOException);
            }
        }
    }

    private static String getFileName(Path path) {
        return StringUtils.removeEnd(path.getFileName().toString(), ".zip");
    }

    @Nullable
    protected abstract ResourcePackProfile create(String var1, ResourcePackProfile.PackFactory var2, Text var3);
}

