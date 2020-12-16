/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.tag.RequiredTagListRegistry;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class TagManager {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final TagManager EMPTY = new TagManager(ImmutableMap.of());
    private final Map<RegistryKey<? extends Registry<?>>, TagGroup<?>> tagGroups;

    private TagManager(Map<RegistryKey<? extends Registry<?>>, TagGroup<?>> tagGroups) {
        this.tagGroups = tagGroups;
    }

    @Nullable
    private <T> TagGroup<T> getTagGroup(RegistryKey<? extends Registry<T>> registryKey) {
        return this.tagGroups.get(registryKey);
    }

    public <T> TagGroup<T> getOrCreateTagGroup(RegistryKey<? extends Registry<T>> registryKey) {
        return this.tagGroups.getOrDefault(registryKey, TagGroup.createEmpty());
    }

    public <T, E extends Exception> Tag<T> getTag(RegistryKey<? extends Registry<T>> registryKey, Identifier id, Function<Identifier, E> function) throws E {
        TagGroup<T> tagGroup = this.getTagGroup(registryKey);
        if (tagGroup == null) {
            throw (Exception)function.apply(id);
        }
        Tag<T> tag = tagGroup.getTag(id);
        if (tag == null) {
            throw (Exception)function.apply(id);
        }
        return tag;
    }

    public <T, E extends Exception> Identifier getTagId(RegistryKey<? extends Registry<T>> registryKey, Tag<T> tag, Supplier<E> supplier) throws E {
        TagGroup<T> tagGroup = this.getTagGroup(registryKey);
        if (tagGroup == null) {
            throw (Exception)supplier.get();
        }
        Identifier identifier = tagGroup.getUncheckedTagId(tag);
        if (identifier == null) {
            throw (Exception)supplier.get();
        }
        return identifier;
    }

    public void method_33161(class_5750 arg) {
        this.tagGroups.forEach((registryKey, tagGroup) -> TagManager.method_33162(arg, registryKey, tagGroup));
    }

    private static <T> void method_33162(class_5750 arg, RegistryKey<? extends Registry<?>> registryKey, TagGroup<?> tagGroup) {
        arg.method_33173(registryKey, tagGroup);
    }

    public void apply() {
        RequiredTagListRegistry.updateTagManager(this);
        Blocks.refreshShapeCache();
    }

    public Map<RegistryKey<? extends Registry<?>>, TagGroup.class_5748> toPacket(final DynamicRegistryManager dynamicRegistryManager) {
        final HashMap<RegistryKey<? extends Registry<?>>, TagGroup.class_5748> map = Maps.newHashMap();
        this.method_33161(new class_5750(){

            @Override
            public <T> void method_33173(RegistryKey<? extends Registry<T>> registryKey, TagGroup<T> tagGroup) {
                Optional optional = dynamicRegistryManager.getOptional(registryKey);
                if (optional.isPresent()) {
                    map.put(registryKey, tagGroup.toPacket(optional.get()));
                } else {
                    LOGGER.error("Unknown registry {}", (Object)registryKey);
                }
            }
        });
        return map;
    }

    @Environment(value=EnvType.CLIENT)
    public static TagManager fromPacket(DynamicRegistryManager dynamicRegistryManager, Map<RegistryKey<? extends Registry<?>>, TagGroup.class_5748> map) {
        class_5749 lv = new class_5749();
        map.forEach((registryKey, arg2) -> TagManager.method_33163(dynamicRegistryManager, lv, registryKey, arg2));
        return lv.method_33171();
    }

    @Environment(value=EnvType.CLIENT)
    private static <T> void method_33163(DynamicRegistryManager dynamicRegistryManager, class_5749 arg, RegistryKey<? extends Registry<? extends T>> registryKey, TagGroup.class_5748 arg2) {
        Optional optional = dynamicRegistryManager.getOptional(registryKey);
        if (optional.isPresent()) {
            arg.method_33172(registryKey, TagGroup.method_33155(arg2, optional.get()));
        } else {
            LOGGER.error("Unknown registry {}", (Object)registryKey);
        }
    }

    public static class class_5749 {
        private final ImmutableMap.Builder<RegistryKey<? extends Registry<?>>, TagGroup<?>> field_28310 = ImmutableMap.builder();

        public <T> class_5749 method_33172(RegistryKey<? extends Registry<? extends T>> registryKey, TagGroup<T> tagGroup) {
            this.field_28310.put(registryKey, tagGroup);
            return this;
        }

        public TagManager method_33171() {
            return new TagManager(this.field_28310.build());
        }
    }

    @FunctionalInterface
    static interface class_5750 {
        public <T> void method_33173(RegistryKey<? extends Registry<T>> var1, TagGroup<T> var2);
    }
}

