package net.minecraft.item;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public interface Instruments {
	int GOAT_HORN_RANGE = 256;
	float GOAT_HORN_USE_DURATION = 7.0F;
	RegistryKey<Instrument> PONDER_GOAT_HORN = of("ponder_goat_horn");
	RegistryKey<Instrument> SING_GOAT_HORN = of("sing_goat_horn");
	RegistryKey<Instrument> SEEK_GOAT_HORN = of("seek_goat_horn");
	RegistryKey<Instrument> FEEL_GOAT_HORN = of("feel_goat_horn");
	RegistryKey<Instrument> ADMIRE_GOAT_HORN = of("admire_goat_horn");
	RegistryKey<Instrument> CALL_GOAT_HORN = of("call_goat_horn");
	RegistryKey<Instrument> YEARN_GOAT_HORN = of("yearn_goat_horn");
	RegistryKey<Instrument> DREAM_GOAT_HORN = of("dream_goat_horn");

	private static RegistryKey<Instrument> of(String id) {
		return RegistryKey.of(RegistryKeys.INSTRUMENT, Identifier.ofVanilla(id));
	}

	static void bootstrap(Registerable<Instrument> registry) {
		register(registry, PONDER_GOAT_HORN, (RegistryEntry<SoundEvent>)SoundEvents.GOAT_HORN_SOUNDS.get(0), 7.0F, 256.0F);
		register(registry, SING_GOAT_HORN, (RegistryEntry<SoundEvent>)SoundEvents.GOAT_HORN_SOUNDS.get(1), 7.0F, 256.0F);
		register(registry, SEEK_GOAT_HORN, (RegistryEntry<SoundEvent>)SoundEvents.GOAT_HORN_SOUNDS.get(2), 7.0F, 256.0F);
		register(registry, FEEL_GOAT_HORN, (RegistryEntry<SoundEvent>)SoundEvents.GOAT_HORN_SOUNDS.get(3), 7.0F, 256.0F);
		register(registry, ADMIRE_GOAT_HORN, (RegistryEntry<SoundEvent>)SoundEvents.GOAT_HORN_SOUNDS.get(4), 7.0F, 256.0F);
		register(registry, CALL_GOAT_HORN, (RegistryEntry<SoundEvent>)SoundEvents.GOAT_HORN_SOUNDS.get(5), 7.0F, 256.0F);
		register(registry, YEARN_GOAT_HORN, (RegistryEntry<SoundEvent>)SoundEvents.GOAT_HORN_SOUNDS.get(6), 7.0F, 256.0F);
		register(registry, DREAM_GOAT_HORN, (RegistryEntry<SoundEvent>)SoundEvents.GOAT_HORN_SOUNDS.get(7), 7.0F, 256.0F);
	}

	static void register(Registerable<Instrument> registry, RegistryKey<Instrument> key, RegistryEntry<SoundEvent> sound, float useDuration, float range) {
		MutableText mutableText = Text.translatable(Util.createTranslationKey("instrument", key.getValue()));
		registry.register(key, new Instrument(sound, useDuration, range, mutableText));
	}
}
