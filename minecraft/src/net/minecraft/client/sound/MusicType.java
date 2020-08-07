package net.minecraft.client.sound;

import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class MusicType {
	public static final MusicSound MENU = new MusicSound(SoundEvents.field_15129, 20, 600, true);
	public static final MusicSound CREATIVE = new MusicSound(SoundEvents.field_14995, 12000, 24000, false);
	public static final MusicSound CREDITS = new MusicSound(SoundEvents.field_14755, 0, 0, true);
	public static final MusicSound DRAGON = new MusicSound(SoundEvents.field_14837, 0, 0, true);
	public static final MusicSound END = new MusicSound(SoundEvents.field_14631, 6000, 24000, true);
	public static final MusicSound UNDERWATER = createIngameMusic(SoundEvents.field_15198);
	public static final MusicSound GAME = createIngameMusic(SoundEvents.field_14681);

	public static MusicSound createIngameMusic(SoundEvent event) {
		return new MusicSound(event, 12000, 24000, false);
	}
}
