package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class PositionedSoundInstance extends AbstractSoundInstance {
	public PositionedSoundInstance(SoundEvent sound, SoundCategory category, float volume, float pitch, BlockPos pos) {
		this(sound, category, volume, pitch, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5);
	}

	public static PositionedSoundInstance master(SoundEvent sound, float pitch) {
		return master(sound, pitch, 0.25F);
	}

	public static PositionedSoundInstance master(SoundEvent sound, float pitch, float volume) {
		return new PositionedSoundInstance(sound.getId(), SoundCategory.MASTER, volume, pitch, false, 0, SoundInstance.AttenuationType.NONE, 0.0, 0.0, 0.0, true);
	}

	public static PositionedSoundInstance music(SoundEvent sound) {
		return new PositionedSoundInstance(sound.getId(), SoundCategory.MUSIC, 1.0F, 1.0F, false, 0, SoundInstance.AttenuationType.NONE, 0.0, 0.0, 0.0, true);
	}

	public static PositionedSoundInstance record(SoundEvent sound, double d, double e, double f) {
		return new PositionedSoundInstance(sound, SoundCategory.RECORDS, 4.0F, 1.0F, false, 0, SoundInstance.AttenuationType.LINEAR, d, e, f);
	}

	public static PositionedSoundInstance ambient(SoundEvent sound, float pitch, float volume) {
		return new PositionedSoundInstance(sound.getId(), SoundCategory.AMBIENT, volume, pitch, false, 0, SoundInstance.AttenuationType.NONE, 0.0, 0.0, 0.0, true);
	}

	public static PositionedSoundInstance ambient(SoundEvent sound) {
		return ambient(sound, 1.0F, 1.0F);
	}

	public static PositionedSoundInstance ambient(SoundEvent sound, double d, double e, double f) {
		return new PositionedSoundInstance(sound, SoundCategory.AMBIENT, 1.0F, 1.0F, false, 0, SoundInstance.AttenuationType.LINEAR, d, e, f);
	}

	public PositionedSoundInstance(SoundEvent sound, SoundCategory category, float volume, float pitch, double d, double e, double f) {
		this(sound, category, volume, pitch, false, 0, SoundInstance.AttenuationType.LINEAR, d, e, f);
	}

	private PositionedSoundInstance(
		SoundEvent sound,
		SoundCategory category,
		float volume,
		float pitch,
		boolean repeat,
		int repeatDelay,
		SoundInstance.AttenuationType attenuationType,
		double d,
		double e,
		double f
	) {
		this(sound.getId(), category, volume, pitch, repeat, repeatDelay, attenuationType, d, e, f, false);
	}

	public PositionedSoundInstance(
		Identifier id,
		SoundCategory category,
		float volume,
		float pitch,
		boolean repeat,
		int repeatDelay,
		SoundInstance.AttenuationType attenuationType,
		double d,
		double e,
		double f,
		boolean bl
	) {
		super(id, category);
		this.volume = volume;
		this.pitch = pitch;
		this.x = d;
		this.y = e;
		this.z = f;
		this.repeat = repeat;
		this.repeatDelay = repeatDelay;
		this.attenuationType = attenuationType;
		this.looping = bl;
	}
}
