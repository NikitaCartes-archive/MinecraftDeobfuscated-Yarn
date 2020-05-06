package net.minecraft.client.sound;

import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class MusicType {
	public static final MusicSound MENU = new MusicSound(SoundEvents.MUSIC_MENU, 20, 600, true);
	public static final MusicSound CREATIVE = new MusicSound(SoundEvents.MUSIC_CREATIVE, 12000, 24000, false);
	public static final MusicSound CREDITS = new MusicSound(SoundEvents.MUSIC_CREDITS, 0, 0, true);
	public static final MusicSound DRAGON = new MusicSound(SoundEvents.MUSIC_DRAGON, 0, 0, true);
	public static final MusicSound END = new MusicSound(SoundEvents.MUSIC_END, 6000, 24000, false);
	public static final MusicSound UNDERWATER = method_27283(SoundEvents.MUSIC_UNDER_WATER);
	public static final MusicSound GAME = method_27283(SoundEvents.MUSIC_GAME);

	public static MusicSound method_27283(SoundEvent soundEvent) {
		return new MusicSound(soundEvent, 12000, 24000, false);
	}
}
