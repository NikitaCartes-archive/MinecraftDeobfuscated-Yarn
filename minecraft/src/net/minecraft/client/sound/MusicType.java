package net.minecraft.client.sound;

import net.minecraft.class_5195;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class MusicType {
	public static final class_5195 field_5585 = new class_5195(SoundEvents.MUSIC_MENU, 20, 600, true);
	public static final class_5195 field_5581 = new class_5195(SoundEvents.MUSIC_CREATIVE, 12000, 24000, false);
	public static final class_5195 field_5578 = new class_5195(SoundEvents.MUSIC_CREDITS, 0, 0, true);
	public static final class_5195 field_5580 = new class_5195(SoundEvents.MUSIC_DRAGON, 0, 0, true);
	public static final class_5195 field_5583 = new class_5195(SoundEvents.MUSIC_END, 6000, 24000, false);
	public static final class_5195 field_5576 = method_27283(SoundEvents.MUSIC_UNDER_WATER);
	public static final class_5195 field_5586 = method_27283(SoundEvents.MUSIC_GAME);

	public static class_5195 method_27283(SoundEvent soundEvent) {
		return new class_5195(soundEvent, 12000, 24000, false);
	}
}
