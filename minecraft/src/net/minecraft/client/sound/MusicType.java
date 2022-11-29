package net.minecraft.client.sound;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class MusicType {
	private static final int MENU_MIN_DELAY = 20;
	private static final int MENU_MAX_DELAY = 600;
	private static final int GAME_MIN_DELAY = 12000;
	private static final int GAME_MAX_DELAY = 24000;
	private static final int END_MIN_DELAY = 6000;
	public static final MusicSound MENU = new MusicSound(SoundEvents.MUSIC_MENU, 20, 600, true);
	public static final MusicSound CREATIVE = new MusicSound(SoundEvents.MUSIC_CREATIVE, 12000, 24000, false);
	public static final MusicSound CREDITS = new MusicSound(SoundEvents.MUSIC_CREDITS, 0, 0, true);
	public static final MusicSound DRAGON = new MusicSound(SoundEvents.MUSIC_DRAGON, 0, 0, true);
	public static final MusicSound END = new MusicSound(SoundEvents.MUSIC_END, 6000, 24000, true);
	public static final MusicSound UNDERWATER = createIngameMusic(SoundEvents.MUSIC_UNDER_WATER);
	public static final MusicSound GAME = createIngameMusic(SoundEvents.MUSIC_GAME);

	public static MusicSound createIngameMusic(RegistryEntry<SoundEvent> sound) {
		return new MusicSound(sound, 12000, 24000, false);
	}
}
