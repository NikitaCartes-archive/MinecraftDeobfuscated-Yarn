package net.minecraft.data.server.tag;

import net.minecraft.data.DataOutput;
import net.minecraft.item.Instrument;
import net.minecraft.item.Instruments;
import net.minecraft.tag.InstrumentTags;
import net.minecraft.util.registry.Registry;

public class InstrumentTagProvider extends AbstractTagProvider<Instrument> {
	public InstrumentTagProvider(DataOutput dataGenerator) {
		super(dataGenerator, Registry.INSTRUMENT);
	}

	@Override
	protected void configure() {
		this.getOrCreateTagBuilder(InstrumentTags.REGULAR_GOAT_HORNS)
			.add(Instruments.PONDER_GOAT_HORN)
			.add(Instruments.SING_GOAT_HORN)
			.add(Instruments.SEEK_GOAT_HORN)
			.add(Instruments.FEEL_GOAT_HORN);
		this.getOrCreateTagBuilder(InstrumentTags.SCREAMING_GOAT_HORNS)
			.add(Instruments.ADMIRE_GOAT_HORN)
			.add(Instruments.CALL_GOAT_HORN)
			.add(Instruments.YEARN_GOAT_HORN)
			.add(Instruments.DREAM_GOAT_HORN);
		this.getOrCreateTagBuilder(InstrumentTags.GOAT_HORNS).addTag(InstrumentTags.REGULAR_GOAT_HORNS).addTag(InstrumentTags.SCREAMING_GOAT_HORNS);
	}
}
