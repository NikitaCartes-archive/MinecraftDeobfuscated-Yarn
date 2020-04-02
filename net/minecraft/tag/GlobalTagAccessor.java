/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * Allows access to tags from either client or server threads and propagates
 * tag changes through reloads/server tag sends.
 */
public class GlobalTagAccessor<T> {
    private TagContainer<T> currentContainer = new TagContainer(identifier -> Optional.empty(), "", "");
    private final List<CachedTag<T>> tags = Lists.newArrayList();

    public Tag.Identified<T> get(String id) {
        CachedTag cachedTag = new CachedTag(new Identifier(id));
        this.tags.add(cachedTag);
        return cachedTag;
    }

    public void setContainer(TagContainer<T> container) {
        this.currentContainer = container;
        this.tags.forEach(cachedTag -> cachedTag.updateContainer(container));
    }

    public TagContainer<T> getContainer() {
        return this.currentContainer;
    }

    static class CachedTag<T>
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

        void updateContainer(TagContainer<T> container) {
            this.currentTag = container.get(this.id);
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

