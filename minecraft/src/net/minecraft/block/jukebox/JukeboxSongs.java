package net.minecraft.block.jukebox;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public interface JukeboxSongs {
	RegistryKey<JukeboxSong> THIRTEEN = of("13");
	RegistryKey<JukeboxSong> CAT = of("cat");
	RegistryKey<JukeboxSong> BLOCKS = of("blocks");
	RegistryKey<JukeboxSong> CHIRP = of("chirp");
	RegistryKey<JukeboxSong> FAR = of("far");
	RegistryKey<JukeboxSong> MALL = of("mall");
	RegistryKey<JukeboxSong> MELLOHI = of("mellohi");
	RegistryKey<JukeboxSong> STAL = of("stal");
	RegistryKey<JukeboxSong> STRAD = of("strad");
	RegistryKey<JukeboxSong> WARD = of("ward");
	RegistryKey<JukeboxSong> ELEVEN = of("11");
	RegistryKey<JukeboxSong> WAIT = of("wait");
	RegistryKey<JukeboxSong> PIGSTEP = of("pigstep");
	RegistryKey<JukeboxSong> OTHERSIDE = of("otherside");
	RegistryKey<JukeboxSong> FIVE = of("5");
	RegistryKey<JukeboxSong> RELIC = of("relic");
	RegistryKey<JukeboxSong> PRECIPICE = of("precipice");
	RegistryKey<JukeboxSong> CREATOR = of("creator");
	RegistryKey<JukeboxSong> CREATOR_MUSIC_BOX = of("creator_music_box");

	private static RegistryKey<JukeboxSong> of(String id) {
		return RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.ofVanilla(id));
	}

	private static void register(
		Registerable<JukeboxSong> registry, RegistryKey<JukeboxSong> key, RegistryEntry.Reference<SoundEvent> soundEvent, int lengthInSeconds, int comparatorOutput
	) {
		registry.register(
			key, new JukeboxSong(soundEvent, Text.translatable(Util.createTranslationKey("jukebox_song", key.getValue())), (float)lengthInSeconds, comparatorOutput)
		);
	}

	static void bootstrap(Registerable<JukeboxSong> registry) {
		register(registry, THIRTEEN, SoundEvents.MUSIC_DISC_13, 178, 1);
		register(registry, CAT, SoundEvents.MUSIC_DISC_CAT, 185, 2);
		register(registry, BLOCKS, SoundEvents.MUSIC_DISC_BLOCKS, 345, 3);
		register(registry, CHIRP, SoundEvents.MUSIC_DISC_CHIRP, 185, 4);
		register(registry, FAR, SoundEvents.MUSIC_DISC_FAR, 174, 5);
		register(registry, MALL, SoundEvents.MUSIC_DISC_MALL, 197, 6);
		register(registry, MELLOHI, SoundEvents.MUSIC_DISC_MELLOHI, 96, 7);
		register(registry, STAL, SoundEvents.MUSIC_DISC_STAL, 150, 8);
		register(registry, STRAD, SoundEvents.MUSIC_DISC_STRAD, 188, 9);
		register(registry, WARD, SoundEvents.MUSIC_DISC_WARD, 251, 10);
		register(registry, ELEVEN, SoundEvents.MUSIC_DISC_11, 71, 11);
		register(registry, WAIT, SoundEvents.MUSIC_DISC_WAIT, 238, 12);
		register(registry, PIGSTEP, SoundEvents.MUSIC_DISC_PIGSTEP, 149, 13);
		register(registry, OTHERSIDE, SoundEvents.MUSIC_DISC_OTHERSIDE, 195, 14);
		register(registry, FIVE, SoundEvents.MUSIC_DISC_5, 178, 15);
		register(registry, RELIC, SoundEvents.MUSIC_DISC_RELIC, 218, 14);
		register(registry, PRECIPICE, SoundEvents.MUSIC_DISC_PRECIPICE, 299, 13);
		register(registry, CREATOR, SoundEvents.MUSIC_DISC_CREATOR, 176, 12);
		register(registry, CREATOR_MUSIC_BOX, SoundEvents.MUSIC_DISC_CREATOR_MUSIC_BOX, 73, 11);
	}
}
