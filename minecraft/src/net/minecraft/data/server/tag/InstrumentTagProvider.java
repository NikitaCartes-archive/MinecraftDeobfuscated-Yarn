package net.minecraft.data.server.tag;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.item.Instrument;
import net.minecraft.item.Instruments;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.InstrumentTags;

public class InstrumentTagProvider extends AbstractTagProvider<Instrument> {
	public InstrumentTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
		super(output, RegistryKeys.INSTRUMENT, registryLookupFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup lookup) {
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
