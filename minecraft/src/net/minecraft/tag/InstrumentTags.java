package net.minecraft.tag;

import net.minecraft.item.Instrument;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public interface InstrumentTags {
	TagKey<Instrument> REGULAR_GOAT_HORNS = of("regular_goat_horns");
	TagKey<Instrument> SCREAMING_GOAT_HORNS = of("screaming_goat_horns");
	TagKey<Instrument> GOAT_HORNS = of("goat_horns");

	private static TagKey<Instrument> of(String id) {
		return TagKey.of(Registry.INSTRUMENT_KEY, new Identifier(id));
	}
}
