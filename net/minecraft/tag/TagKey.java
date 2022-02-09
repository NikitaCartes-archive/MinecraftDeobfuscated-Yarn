/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public record TagKey<T>(RegistryKey<? extends Registry<T>> registry, Identifier id) {
    private static final Interner<TagKey<?>> INTERNER = Interners.newStrongInterner();

    public static <T> Codec<TagKey<T>> identifierCodec(RegistryKey<? extends Registry<T>> registry) {
        return Identifier.CODEC.xmap(id -> TagKey.intern(registry, id), TagKey::id);
    }

    public static <T> Codec<TagKey<T>> stringCodec(RegistryKey<? extends Registry<T>> registry) {
        return Codec.STRING.comapFlatMap(string -> string.startsWith("#") ? Identifier.validate(string.substring(1)).map(id -> TagKey.intern(registry, id)) : DataResult.error("Not a tag id"), string -> "#" + string.id);
    }

    public static <T> TagKey<T> intern(RegistryKey<? extends Registry<T>> registry, Identifier id) {
        return INTERNER.intern(new TagKey<T>(registry, id));
    }

    @Override
    public String toString() {
        return "TagKey[" + this.registry.getValue() + " / " + this.id + "]";
    }
}

