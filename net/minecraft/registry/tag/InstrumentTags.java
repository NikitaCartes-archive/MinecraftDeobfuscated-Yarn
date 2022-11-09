/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.registry.tag;

import net.minecraft.item.Instrument;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public interface InstrumentTags {
    public static final TagKey<Instrument> REGULAR_GOAT_HORNS = InstrumentTags.of("regular_goat_horns");
    public static final TagKey<Instrument> SCREAMING_GOAT_HORNS = InstrumentTags.of("screaming_goat_horns");
    public static final TagKey<Instrument> GOAT_HORNS = InstrumentTags.of("goat_horns");

    private static TagKey<Instrument> of(String id) {
        return TagKey.of(RegistryKeys.INSTRUMENT, new Identifier(id));
    }
}

