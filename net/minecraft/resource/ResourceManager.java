/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;

public interface ResourceManager {
    @Environment(value=EnvType.CLIENT)
    public Set<String> getAllNamespaces();

    public Resource getResource(Identifier var1) throws IOException;

    @Environment(value=EnvType.CLIENT)
    public boolean containsResource(Identifier var1);

    public List<Resource> getAllResources(Identifier var1) throws IOException;

    public Collection<Identifier> findResources(Identifier var1, Predicate<String> var2);

    public Collection<Identifier> findResources(String var1, Predicate<String> var2);

    @Environment(value=EnvType.CLIENT)
    public Stream<ResourcePack> streamResourcePacks();

    public static enum Empty implements ResourceManager
    {
        INSTANCE;


        @Override
        @Environment(value=EnvType.CLIENT)
        public Set<String> getAllNamespaces() {
            return ImmutableSet.of();
        }

        @Override
        public Resource getResource(Identifier id) throws IOException {
            throw new FileNotFoundException(id.toString());
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public boolean containsResource(Identifier id) {
            return false;
        }

        @Override
        public List<Resource> getAllResources(Identifier id) {
            return ImmutableList.of();
        }

        @Override
        public Collection<Identifier> findResources(Identifier resourceType, Predicate<String> predicate) {
            return ImmutableSet.of();
        }

        @Override
        public Collection<Identifier> findResources(String resourceType, Predicate<String> pathPredicate) {
            return ImmutableSet.of();
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public Stream<ResourcePack> streamResourcePacks() {
            return Stream.of(new ResourcePack[0]);
        }
    }
}

