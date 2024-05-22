package net.minecraft;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public interface class_9796 {
	RegistryKey<class_9793> field_52037 = method_60764("13");
	RegistryKey<class_9793> CAT = method_60764("cat");
	RegistryKey<class_9793> BLOCKS = method_60764("blocks");
	RegistryKey<class_9793> CHIRP = method_60764("chirp");
	RegistryKey<class_9793> FAR = method_60764("far");
	RegistryKey<class_9793> MALL = method_60764("mall");
	RegistryKey<class_9793> MELLOHI = method_60764("mellohi");
	RegistryKey<class_9793> STAL = method_60764("stal");
	RegistryKey<class_9793> STRAD = method_60764("strad");
	RegistryKey<class_9793> WARD = method_60764("ward");
	RegistryKey<class_9793> field_52047 = method_60764("11");
	RegistryKey<class_9793> WAIT = method_60764("wait");
	RegistryKey<class_9793> PIGSTEP = method_60764("pigstep");
	RegistryKey<class_9793> OTHERSIDE = method_60764("otherside");
	RegistryKey<class_9793> field_52051 = method_60764("5");
	RegistryKey<class_9793> RELIC = method_60764("relic");
	RegistryKey<class_9793> PRECIPICE = method_60764("precipice");
	RegistryKey<class_9793> CREATOR = method_60764("creator");
	RegistryKey<class_9793> CREATOR_MUSIC_BOX = method_60764("creator_music_box");

	private static RegistryKey<class_9793> method_60764(String string) {
		return RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.method_60656(string));
	}

	private static void method_60766(
		Registerable<class_9793> registerable, RegistryKey<class_9793> registryKey, RegistryEntry.Reference<SoundEvent> reference, int i, int j
	) {
		registerable.register(
			registryKey, new class_9793(reference, Text.translatable(Util.createTranslationKey("jukebox_song", registryKey.getValue())), (float)i, j)
		);
	}

	static void method_60765(Registerable<class_9793> registerable) {
		method_60766(registerable, field_52037, SoundEvents.MUSIC_DISC_13, 178, 1);
		method_60766(registerable, CAT, SoundEvents.MUSIC_DISC_CAT, 185, 2);
		method_60766(registerable, BLOCKS, SoundEvents.MUSIC_DISC_BLOCKS, 345, 3);
		method_60766(registerable, CHIRP, SoundEvents.MUSIC_DISC_CHIRP, 185, 4);
		method_60766(registerable, FAR, SoundEvents.MUSIC_DISC_FAR, 174, 5);
		method_60766(registerable, MALL, SoundEvents.MUSIC_DISC_MALL, 197, 6);
		method_60766(registerable, MELLOHI, SoundEvents.MUSIC_DISC_MELLOHI, 96, 7);
		method_60766(registerable, STAL, SoundEvents.MUSIC_DISC_STAL, 150, 8);
		method_60766(registerable, STRAD, SoundEvents.MUSIC_DISC_STRAD, 188, 9);
		method_60766(registerable, WARD, SoundEvents.MUSIC_DISC_WARD, 251, 10);
		method_60766(registerable, field_52047, SoundEvents.MUSIC_DISC_11, 71, 11);
		method_60766(registerable, WAIT, SoundEvents.MUSIC_DISC_WAIT, 238, 12);
		method_60766(registerable, PIGSTEP, SoundEvents.MUSIC_DISC_PIGSTEP, 149, 13);
		method_60766(registerable, OTHERSIDE, SoundEvents.MUSIC_DISC_OTHERSIDE, 195, 14);
		method_60766(registerable, field_52051, SoundEvents.MUSIC_DISC_5, 178, 15);
		method_60766(registerable, RELIC, SoundEvents.MUSIC_DISC_RELIC, 218, 14);
		method_60766(registerable, PRECIPICE, SoundEvents.MUSIC_DISC_PRECIPICE, 299, 13);
		method_60766(registerable, CREATOR, SoundEvents.MUSIC_DISC_CREATOR, 176, 12);
		method_60766(registerable, CREATOR_MUSIC_BOX, SoundEvents.MUSIC_DISC_CREATOR_MUSIC_BOX, 73, 11);
	}
}
