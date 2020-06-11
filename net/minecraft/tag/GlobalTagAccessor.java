/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5394;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * Allows access to tags from either client or server threads and propagates
 * tag changes through reloads/server tag sends.
 */
public class GlobalTagAccessor<T> {
    private final TagContainer<T> emptyContainer = new TagContainer(identifier -> Optional.empty(), "", "");
    private TagContainer<T> currentContainer = this.emptyContainer;
    private final List<CachedTag<T>> tags = Lists.newArrayList();

    public Tag.Identified<T> get(String id) {
        CachedTag cachedTag = new CachedTag(new Identifier(id));
        this.tags.add(cachedTag);
        return cachedTag;
    }

    @Environment(value=EnvType.CLIENT)
    public void markReady() {
        this.currentContainer = this.emptyContainer;
        class_5394 tag = class_5394.method_29898();
        this.tags.forEach(cachedTag -> cachedTag.updateContainer(identifier -> tag));
    }

    public void setContainer(TagContainer<T> container) {
        this.currentContainer = container;
        this.tags.forEach(cachedTag -> cachedTag.updateContainer(container::get));
    }

    public TagContainer<T> getContainer() {
        return this.currentContainer;
    }

    public List<CachedTag<T>> method_29902() {
        return this.tags;
    }

    public Set<Identifier> method_29224(TagContainer<T> tagContainer) {
        Set set = this.tags.stream().map(CachedTag::getId).collect(Collectors.toSet());
        ImmutableSet<Identifier> immutableSet = ImmutableSet.copyOf(tagContainer.getKeys());
        return Sets.difference(set, immutableSet);
    }

    public static class CachedTag<T>
    implements Tag.Identified<T> {
        @Nullable
        private Tag<T> currentTag;
        protected final Identifier id;

        private CachedTag(Identifier id) {
            this.id = id;
        }

        @Override
        public Identifier getId() {
            return this.id;
        }

        private Tag<T> get() {
            if (this.currentTag == null) {
                throw new IllegalStateException("Tag " + this.id + " used before it was bound");
            }
            return this.currentTag;
        }

        void updateContainer(Function<Identifier, Tag<T>> tagFactory) {
            this.currentTag = tagFactory.apply(this.id);
        }

        @Override
        public boolean contains(T entry) {
            return this.get().contains(entry);
        }

        @Override
        public List<T> values() {
            return this.get().values();
        }
    }
}

