package net.minecraft.item;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public interface Instruments {
	int GOAT_HORN_RANGE = 256;
	int GOAT_HORN_USE_DURATION = 140;
	RegistryKey<Instrument> PONDER_GOAT_HORN = of("ponder_goat_horn");
	RegistryKey<Instrument> SING_GOAT_HORN = of("sing_goat_horn");
	RegistryKey<Instrument> SEEK_GOAT_HORN = of("seek_goat_horn");
	RegistryKey<Instrument> FEEL_GOAT_HORN = of("feel_goat_horn");
	RegistryKey<Instrument> ADMIRE_GOAT_HORN = of("admire_goat_horn");
	RegistryKey<Instrument> CALL_GOAT_HORN = of("call_goat_horn");
	RegistryKey<Instrument> YEARN_GOAT_HORN = of("yearn_goat_horn");
	RegistryKey<Instrument> DREAM_GOAT_HORN = of("dream_goat_horn");

	private static RegistryKey<Instrument> of(String id) {
		return RegistryKey.of(Registry.INSTRUMENT_KEY, new Identifier(id));
	}

	static Instrument registerAndGetDefault(Registry<Instrument> registry) {
		Registry.register(registry, PONDER_GOAT_HORN, new Instrument((SoundEvent)SoundEvents.GOAT_HORN_SOUNDS.get(0), 140, 256.0F));
		Registry.register(registry, SING_GOAT_HORN, new Instrument((SoundEvent)SoundEvents.GOAT_HORN_SOUNDS.get(1), 140, 256.0F));
		Registry.register(registry, SEEK_GOAT_HORN, new Instrument((SoundEvent)SoundEvents.GOAT_HORN_SOUNDS.get(2), 140, 256.0F));
		Registry.register(registry, FEEL_GOAT_HORN, new Instrument((SoundEvent)SoundEvents.GOAT_HORN_SOUNDS.get(3), 140, 256.0F));
		Registry.register(registry, ADMIRE_GOAT_HORN, new Instrument((SoundEvent)SoundEvents.GOAT_HORN_SOUNDS.get(4), 140, 256.0F));
		Registry.register(registry, CALL_GOAT_HORN, new Instrument((SoundEvent)SoundEvents.GOAT_HORN_SOUNDS.get(5), 140, 256.0F));
		Registry.register(registry, YEARN_GOAT_HORN, new Instrument((SoundEvent)SoundEvents.GOAT_HORN_SOUNDS.get(6), 140, 256.0F));
		return Registry.register(registry, DREAM_GOAT_HORN, new Instrument((SoundEvent)SoundEvents.GOAT_HORN_SOUNDS.get(7), 140, 256.0F));
	}
}
