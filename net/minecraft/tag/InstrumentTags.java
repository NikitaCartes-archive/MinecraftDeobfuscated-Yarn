/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import net.minecraft.item.Instrument;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public interface InstrumentTags {
    public static final TagKey<Instrument> REGULAR_GOAT_HORNS = InstrumentTags.of("regular_goat_horns");
    public static final TagKey<Instrument> SCREAMING_GOAT_HORNS = InstrumentTags.of("screaming_goat_horns");
    public static final TagKey<Instrument> GOAT_HORNS = InstrumentTags.of("goat_horns");

    private static TagKey<Instrument> of(String id) {
        return TagKey.of(Registry.INSTRUMENT_KEY, new Identifier(id));
    }
}

