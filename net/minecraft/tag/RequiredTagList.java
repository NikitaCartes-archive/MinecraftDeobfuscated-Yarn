/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.tag.SetTag;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.tag.TagManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * Stores all required tags of a given type, so they can be updated to point to the new tag instances on datapack reload
 */
public class RequiredTagList<T> {
    private TagGroup<T> group = TagGroup.createEmpty();
    private final List<TagWrapper<T>> tags = Lists.newArrayList();
    private final Function<TagManager, TagGroup<T>> groupGetter;

    public RequiredTagList(Function<TagManager, TagGroup<T>> managerGetter) {
        this.groupGetter = managerGetter;
    }

    public Tag.Identified<T> add(String id) {
        TagWrapper tagWrapper = new TagWrapper(new Identifier(id));
        this.tags.add(tagWrapper);
        return tagWrapper;
    }

    @Environment(value=EnvType.CLIENT)
    public void clearAllTags() {
        this.group = TagGroup.createEmpty();
        SetTag tag2 = SetTag.empty();
        this.tags.forEach(tag -> tag.updateDelegate(id -> tag2));
    }

    public void updateTagManager(TagManager manager) {
        TagGroup tagGroup = this.groupGetter.apply(manager);
        this.group = tagGroup;
        this.tags.forEach(tag -> tag.updateDelegate(tagGroup::getTag));
    }

    public TagGroup<T> getGroup() {
        return this.group;
    }

    public List<? extends Tag.Identified<T>> getTags() {
        return this.tags;
    }

    /**
     * Gets the required tags which are not supplied by the current datapacks.
     */
    public Set<Identifier> getMissingTags(TagManager manager) {
        TagGroup<T> tagGroup = this.groupGetter.apply(manager);
        Set set = this.tags.stream().map(TagWrapper::getId).collect(Collectors.toSet());
        ImmutableSet<Identifier> immutableSet = ImmutableSet.copyOf(tagGroup.getTagIds());
        return Sets.difference(set, immutableSet);
    }

    static class TagWrapper<T>
    implements Tag.Identified<T> {
        @Nullable
        private Tag<T> delegate;
        protected final Identifier id;

        private TagWrapper(Identifier id) {
            this.id = id;
        }

        @Override
        public Identifier getId() {
            return this.id;
        }

        private Tag<T> get() {
            if (this.delegate == null) {
                throw new IllegalStateException("Tag " + this.id + " used before it was bound");
            }
            return this.delegate;
        }

        void updateDelegate(Function<Identifier, Tag<T>> tagFactory) {
            this.delegate = tagFactory.apply(this.id);
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

