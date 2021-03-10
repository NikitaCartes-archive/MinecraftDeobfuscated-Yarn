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
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;

/**
 * Provides resource loading capabilities to Minecraft.
 */
public interface ResourceManager
extends ResourceFactory {
    /**
     * Gets a set of all namespaces offered by the resource packs loaded by this manager.
     */
    @Environment(value=EnvType.CLIENT)
    public Set<String> getAllNamespaces();

    /**
     * Checks whether any of the currently-loaded resource packs contain an entry for the given id.
     * 
     * <p>Starts by querying the resource pack with the highest priority to lowest until it finds one that
     * responds to the requested identifier.
     * 
     * @param id the resource identifier to search for
     */
    @Environment(value=EnvType.CLIENT)
    public boolean containsResource(Identifier var1);

    /**
     * Gets all of the available resources to the corresponding resource identifier.
     * 
     * <p>Resources are returned in load order, or ascending order of priority, so the last element in the returned
     * list is what would be returned normally by {@link #getResource}
     * 
     * @throws java.io.FileNotFoundException if no matching resources could be found (i.e. if the list would be empty)
     * @throws IOException if resources were found, but any one of them could not be opened to be read.
     * 
     * @param id the resource identifier to search for
     */
    public List<Resource> getAllResources(Identifier var1) throws IOException;

    /**
     * Returns a sorted list of identifiers matching a path predicate.
     * 
     * <p>Scanning begins in {@code startingPath} and each candidate file present under that directory
     * will be offered up to the predicate to decide whether it should be included or not.
     * 
     * <p>Elements in the returned list may not, necessarily be unique. Additional effort is advised to ensure that
     * duplicates in the returned list are discarded before loading.
     * 
     * @return the list matching identifiers
     * 
     * @param startingPath the starting path to begin scanning from
     * @param pathPredicate a predicate to determine whether a path should be included or not
     */
    public Collection<Identifier> findResources(String var1, Predicate<String> var2);

    /**
     * Gets a stream of loaded resource packs in increasing order of priority.
     */
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
        public Collection<Identifier> findResources(String startingPath, Predicate<String> pathPredicate) {
            return ImmutableSet.of();
        }

        @Override
        @Environment(value=EnvType.CLIENT)
        public Stream<ResourcePack> streamResourcePacks() {
            return Stream.of(new ResourcePack[0]);
        }
    }
}

