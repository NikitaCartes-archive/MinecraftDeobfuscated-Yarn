/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.RequiredTagList;
import net.minecraft.tag.TagGroup;
import net.minecraft.tag.TagManager;
import net.minecraft.util.Identifier;

public class RequiredTagListRegistry {
    private static final Map<Identifier, RequiredTagList<?>> REQUIRED_TAG_LISTS = Maps.newHashMap();

    public static <T> RequiredTagList<T> register(Identifier id, Function<TagManager, TagGroup<T>> containerGetter) {
        RequiredTagList<T> requiredTagList = new RequiredTagList<T>(containerGetter);
        RequiredTagList<T> requiredTagList2 = REQUIRED_TAG_LISTS.putIfAbsent(id, requiredTagList);
        if (requiredTagList2 != null) {
            throw new IllegalStateException("Duplicate entry for static tag collection: " + id);
        }
        return requiredTagList;
    }

    public static void updateTagManager(TagManager tagManager) {
        REQUIRED_TAG_LISTS.values().forEach(list -> list.updateTagManager(tagManager));
    }

    @Environment(value=EnvType.CLIENT)
    public static void clearAllTags() {
        REQUIRED_TAG_LISTS.values().forEach(RequiredTagList::clearAllTags);
    }

    public static Multimap<Identifier, Identifier> getMissingTags(TagManager tagManager) {
        HashMultimap<Identifier, Identifier> multimap = HashMultimap.create();
        REQUIRED_TAG_LISTS.forEach((id, list) -> multimap.putAll((Identifier)id, (Iterable<Identifier>)list.getMissingTags(tagManager)));
        return multimap;
    }

    public static void validateRegistrations() {
        RequiredTagList[] requiredTagLists = new RequiredTagList[]{BlockTags.REQUIRED_TAGS, ItemTags.REQUIRED_TAGS, FluidTags.REQUIRED_TAGS, EntityTypeTags.REQUIRED_TAGS};
        boolean bl = Stream.of(requiredTagLists).anyMatch(list -> !REQUIRED_TAG_LISTS.containsValue(list));
        if (bl) {
            throw new IllegalStateException("Missing helper registrations");
        }
    }
}

